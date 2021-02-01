package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.BaseApiService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.aspectj.weaver.ast.Var;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {
    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;
    //商品查询
    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {
        //分页插件
        if (ObjectUtils.isNotNull(spuDTO.getPage()) && ObjectUtils.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        //上下架
        if (ObjectUtils.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());

        //条件查询
        if(!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%"+spuDTO.getTitle()+"%");

        //排序
        if(!StringUtils.isEmpty(spuDTO.getSort()))example.setOrderByClause(spuDTO.getOrder());

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        //分页
        List<SpuDTO> spuDtoList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDto1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //通过分类id集合查询数据(分类)
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
            // 遍历集合并且将分类名称用 / 拼接
            String collect = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDto1.setCategoryName(collect);

            //品牌
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDto1.setBrandName(brandEntity.getName());

            return spuDto1;
        }).collect(Collectors.toList());


        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);

//        return this.setResultSuccess(spuEntityPageInfo);
        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal() + "",spuDtoList);
    }

    //商品新增
    @Override
    @Transactional
    public Result<JsonObject> addGoods(SpuDTO spuDTO) {
       final Date date = new Date();
        //新增spu,新增返回主键, 给必要字段赋默认值
        //转spuEntity
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //新增spuDetail
        //从spuDto中获取SpuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku
        //从spuDto中获取sku
        this.saveSkuAndStockInfo(spuDTO,spuEntity.getId(),date);
        return this.setResultSuccess();
    }

    //修改商品信息
    @Override
    public Result<JsonObject> editGoods(SpuDTO spuDTO) {
        //修改spu
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改SpuDetail
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));

        //修改sku
        //先通过spuid删除sku
        //然后新增数据
        this.delSkuAndStrock(spuEntity.getId());


        //修改stock
        //删除stock
        //但是sku在上面已经被删除掉了
        //所以应该先查询出被删除的skuid
        //新增stock
        this.saveSkuAndStockInfo(spuDTO,spuEntity.getId(),date);
        return this.setResultSuccess();
    }

    //删除商品
    @Transactional
    @Override
    public Result<JsonObject> delGoods(Integer spuId) {
        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);
        //刪除sspuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);

        //先通过spuId删除sku
        this.delSkuAndStrock(spuId);
        return this.setResultSuccess();
    }

    //通过spuId查询SpuDetail信息
    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    //通过spuId查询sku信息
    @Override
    public Result<List<SpuDTO>> getSkuById(Integer spuId) {
        List<SkuDTO> skusAndStockBySpuId = skuMapper.getSkusAndStockBySpuId(spuId);
        return this.setResultSuccess(skusAndStockBySpuId);
    }
    //修改和删除整合
    private void delSkuAndStrock(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skucollect = skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skucollect);
    }

    //修改和新增的整合（sku增加）
    private void saveSkuAndStockInfo(SpuDTO spuDTO, Integer spuId, Date date){
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stcok
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
