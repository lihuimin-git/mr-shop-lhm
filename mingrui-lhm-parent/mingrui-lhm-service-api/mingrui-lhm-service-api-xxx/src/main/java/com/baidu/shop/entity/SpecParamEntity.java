package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spec_param")
@Data
public class SpecParamEntity {
    @Id
    private Integer id;//规格参数Id

    private Integer cid;//商品分类id

    private Integer groupId;

    private String name;//参数名

    @Column(name = "`numeric`")
    private Boolean numeric;//是否是数字类型参数

    private String unit;//数字类型参数的单位

    private Boolean generic;//是否是sku通用属性

    private Boolean searching;//是否用于搜索过滤

    private String segments;//数值类型参数
}
