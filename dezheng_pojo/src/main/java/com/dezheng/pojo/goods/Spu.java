package com.dezheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_spu")
public class Spu implements Serializable {

    @Id
    private String id;
    private String sn;
    private String name;
    private Integer brandId;
    private Integer category1Id;
    private Integer category2Id;
    private Integer category3Id;
    private String introduction;
    private String specItems;
    private String isMarketable;
    private String isDelete;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategory1Id() {
        return category1Id;
    }

    public void setCategory1Id(Integer category1Id) {
        this.category1Id = category1Id;
    }

    public Integer getCategory2Id() {
        return category2Id;
    }

    public void setCategory2Id(Integer category2Id) {
        this.category2Id = category2Id;
    }

    public Integer getCategory3Id() {
        return category3Id;
    }

    public void setCategory3Id(Integer category3Id) {
        this.category3Id = category3Id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSpecItems() {
        return specItems;
    }

    public void setSpecItems(String specItems) {
        this.specItems = specItems;
    }

    public String getIsMarketable() {
        return isMarketable;
    }

    public void setIsMarketable(String isMarketable) {
        this.isMarketable = isMarketable;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Spu{" +
                "id='" + id + '\'' +
                ", sn='" + sn + '\'' +
                ", name='" + name + '\'' +
                ", brandId='" + brandId + '\'' +
                ", category1Id=" + category1Id +
                ", category2Id=" + category2Id +
                ", category3Id=" + category3Id +
                ", introduction='" + introduction + '\'' +
                ", specItems='" + specItems + '\'' +
                ", isMarketable='" + isMarketable + '\'' +
                ", isDelete='" + isDelete + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
