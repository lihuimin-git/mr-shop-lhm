package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.status.BaseApiService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.jsf.FacesContextUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {
    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;
    //品牌查询
    @Override
    public Result<List<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {
        //mybatis如何自定义分页插件 --》 mybatis执行器
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        if (!StringUtils.isEmpty(brandDTO.getSort()))PageHelper.orderBy(brandDTO.getOrder());

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        Example example = new Example(BrandEntity.class);
        //条件查询
        example.createCriteria().andLike("name","%" + brandEntity.getName() + "%");

        List<BrandEntity> brandEntities = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandEntities);
        return this.setResultSuccess(pageInfo);
    }

    //品牌新增
    @Transactional
    @Override
    public Result<JsonObject> addBrand(BrandDTO brandDTO) {
        //新增返回主键?
        //两种方式实现 select-key insert加两个属性
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);
        //处理品牌首字母
        brandDTO.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)),PinyinUtil.TO_FUUL_PINYIN).charAt(0));
        brandMapper.insertSelective(brandEntity);
        //维护中间表数据
        this.insetrCategoryBrandList(brandDTO.getCategories(),brandEntity.getId());
        return this.setResultSuccess();
    }

    //品牌修改
    @Transactional
    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().toCharArray()[0]),false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandEntity);
        //先通过brandId删除中间表的数据
        this.deleteCategoryByBrandId(brandEntity.getId());

        this.insetrCategoryBrandList(brandDTO.getCategories(),brandEntity.getId());
        return this.setResultSuccess();
    }

    //删除品牌
    @Transactional
    @Override
    public Result<JsonObject> delBrand(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
        this.deleteCategoryByBrandId(id);
        return this.setResultSuccess();
    }

    //修改删除整合
    private void deleteCategoryByBrandId(Integer brandId){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",brandId);
        categoryBrandMapper.deleteByExample(example);
    }
    //新增修改整合
    private void insetrCategoryBrandList (String categories,Integer brandId){
        if (StringUtils.isEmpty(categories))throw new RuntimeException("分类信息不能为空");

        //判断分类集合字符串中是否包含,
        if(categories.contains(",")){
            categoryBrandMapper.insertList(
                    //数组转list
                    Arrays.asList(categories.split(","))
                            //获取stream流，流：对一次数据进行操作
                            .stream()
                            //遍历list中所有数据
                            .map(categoryById->new CategoryBrandEntity(Integer.valueOf(categoryById),brandId))
                            //最终转换成list
                            .collect(Collectors.toList())
            );
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandEntity.setBrandId(brandId);

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }

    //通过分类id获取品牌
    @Override
    public Result<List<BrandEntity>> getBrandInfoByCategoryById(Integer cid) {
        List<BrandEntity> brandInfoByCategoryById = brandMapper.getBrandInfoByCategoryById(cid);
        return this.setResultSuccess(brandInfoByCategoryById);
    }
}
