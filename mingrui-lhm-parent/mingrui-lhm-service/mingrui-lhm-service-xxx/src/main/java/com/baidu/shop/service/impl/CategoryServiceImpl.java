package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.status.BaseApiService;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    //查询商品分类
    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    //新增商品分类
    @Transactional
    @Override
    public Result<JsonObject> addCategory(CategoryEntity categoryEntity) {
        CategoryEntity parentCategoryEntity= new CategoryEntity();
        parentCategoryEntity.setId(categoryEntity.getId());
        parentCategoryEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(parentCategoryEntity);

        categoryMapper.insertSelective(categoryEntity);
        return this.setResultSuccess();
    }
    //修改商品分类
    @Transactional
    @Override
    public Result<JsonObject> editCategory(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess();
    }

    //删除商品分类
    @Override
    @Transactional
    public Result<JsonObject> delCategory(Integer id) {
        //判断页面传递过来的id是否合法
        if (null != id && id > 0){
            //通过id查询当前节点信息
            CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

            //判断当前节点是否为父节点
            if (categoryEntity.getParentId() == 1)return this.setResultError("当前节点为父节点不能删除");//return之后的代码不会执行

            //通过当前节点的父节点id 查询 当前节点(将要被删除的节点)的父节点下是否还有其他子节点
            Example example = new Example(CategoryEntity.class);
            example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
            List<CategoryEntity> categoryEntities = categoryMapper.selectByExample(example);

            //如果size <= 1 --> 如果当前节点被删除的话 当前节点的父节点下没有节点了 --> 将当前节点的父节点状态改为叶子节点
            if (categoryEntities.size() <= 1){
                CategoryEntity updateCategoryEntity = new CategoryEntity();
                updateCategoryEntity.setParentId(0);
                updateCategoryEntity.setId(categoryEntity.getId());

                categoryMapper.updateByPrimaryKeySelective(updateCategoryEntity);
            }
            categoryMapper.deleteByPrimaryKey(id);
            return this.setResultSuccess();
        }
        return this.setResultError("id不合理");
    }
}
