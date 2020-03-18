package com.dezheng.pojo.user;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_address")
public class Address implements Serializable {

    @Id
    private String id;
    private String username;
    private String contact;
    private String phone;
    private String address;
    private String detail;
    private String isDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", contact='" + contact + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isDefault='" + isDefault + '\'' +
                '}';
    }
}
