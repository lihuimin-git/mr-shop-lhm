package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "规格组接口")
public interface SpecGroupService {
    @ApiModelProperty(value = "规格组查询")
    @GetMapping(value = "specGroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "规格组新增")
    @PostMapping(value = "specGroup/save")
    Result<JsonObject> addSpecGroup(@RequestBody  SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "规格组修改")
    @PutMapping(value = "specGroup/save")
    Result<JsonObject> editSpecGroup(@RequestBody  SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "规格组删除")
    @DeleteMapping(value = "specGroup/delete")
    Result<JsonObject> delSpecGroupById(Integer id);
}
