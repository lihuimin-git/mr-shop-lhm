package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BrandService {
    @GetMapping(value = "brand/list")
    @ApiOperation(value = "查询商品列表")
    Result<List<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @PostMapping(value = "brand/save")
    @ApiOperation(value = "新增品牌")
    Result<JsonObject> addBrand(@RequestBody BrandDTO brandDTO);

    @PutMapping(value = "brand/save")
    @ApiOperation(value = "修改品牌")
    Result<JsonObject> editBrand(@RequestBody BrandDTO brandDTO);

    @DeleteMapping(value = "brand/del")
    @ApiOperation(value = "删除品牌")
    Result<JsonObject> delBrand(Integer id);

    @GetMapping(value = "brand/getBrandInfoByCategoryById")
    @ApiOperation(value = "通过分类id获取品牌")
    Result<List<BrandEntity>> getBrandInfoByCategoryById(Integer cid);
}
