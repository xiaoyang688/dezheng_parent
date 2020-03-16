package com.dezheng.pojo.order;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_order")
public class Order implements Serializable {

    @Id
    private String id;
    private Integer totalNum;
    private Integer totalMoney;
    private Integer payMoney;
    private Integer preMoney;
    private Date createTime;
    private Date updateTime;
    private Date payTime;
    private Date consignTime;
    private Date endTime;
    private Date closeTime;
    private String username;
    private String buyerMessage;
    private String consignee;
    private String consigneePhone;
    private String consigneeAddress;
    private String logisticsId;
    private String logisticsName;
    private String transactionId;
    private String orderStatus;
    private String payStatus;
    private String consignStatus;
    private String isDelete;

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getPreMoney() {
        return preMoney;
    }

    public void setPreMoney(Integer preMoney) {
        this.preMoney = preMoney;
    }

    public String getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(String logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Integer getPerMoney() {
        return preMoney;
    }

    public void setPerMoney(Integer perMoney) {
        this.preMoney = perMoney;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public String getLogisticId() {
        return logisticsId;
    }

    public void setLogisticId(String logisticId) {
        this.logisticsId = logisticId;
    }

    public String getLogisticName() {
        return logisticsName;
    }

    public void setLogisticName(String logisticName) {
        this.logisticsName = logisticName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getConsignStatus() {
        return consignStatus;
    }

    public void setConsignStatus(String consignStatus) {
        this.consignStatus = consignStatus;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", totalNum=" + totalNum +
                ", totalMoney=" + totalMoney +
                ", perMoney=" + preMoney +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", payTime=" + payTime +
                ", consignTime=" + consignTime +
                ", endTime=" + endTime +
                ", closeTime=" + closeTime +
                ", username='" + username + '\'' +
                ", buyerMessage='" + buyerMessage + '\'' +
                ", consignee='" + consignee + '\'' +
                ", consigneePhone='" + consigneePhone + '\'' +
                ", consigneeAddress='" + consigneeAddress + '\'' +
                ", logisticId='" + logisticsId + '\'' +
                ", logisticName='" + logisticsName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", consignStatus='" + consignStatus + '\'' +
                ", isDelete='" + isDelete + '\'' +
                '}';
    }
}
