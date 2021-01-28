package com.baidu.shop.entity;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spec_group")
@Data
public class SpecGroupEntity {
    @Id
    private Integer id;//规格组Id

    private Integer cid;//商品分类id

    private String name;//规格组的名称
}
