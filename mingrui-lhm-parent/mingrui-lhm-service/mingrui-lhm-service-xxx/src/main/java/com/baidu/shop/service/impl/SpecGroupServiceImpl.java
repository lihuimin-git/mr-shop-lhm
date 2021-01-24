package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.service.SpecGroupService;
import com.baidu.shop.status.BaseApiService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SpecGroupServiceImpl extends BaseApiService implements SpecGroupService {
    @Resource
    private SpecGroupMapper specGroupMapper;
    @Override
    @Transactional
    public Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO) {
        Example example = new Example(SpecGroupEntity.class);

        if (ObjectUtils.isNotNull(specGroupDTO.getCid()))
                    example
                    .createCriteria()
//                    .andEqualTo("cid", BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
                    .andEqualTo("cid",specGroupDTO.getCid());
        List<SpecGroupEntity> specGroupEntities = specGroupMapper.selectByExample(example);
        return this.setResultSuccess(specGroupEntities);
    }
}
