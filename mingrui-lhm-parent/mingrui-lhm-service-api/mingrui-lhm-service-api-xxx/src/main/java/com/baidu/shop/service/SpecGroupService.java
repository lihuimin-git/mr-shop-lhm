package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Api(value = "规格组接口")
public interface SpecGroupService {
    @ApiModelProperty(value = "规格组查询")
    @GetMapping(value = "specGroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO);
}
