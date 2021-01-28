package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_spu")
@Data
public class SpuEntity {
    @Id
    private Integer id;//spu id

    private String title;//标题

    private String subTitle;//子标题

    private Integer cid1;//1级类目id

    private Integer cid2;//2级类目id

    private Integer cid3;//3级类目id

    private Integer brandId;//商品所属品牌id

    private Integer saleable;//是否上架，0下架，1上架

    private Integer valid;//是否有效，0已删除，1有效

    private Date createTime;//添加时间

    private Date lastUpdateTime;//最后修改时间
}
