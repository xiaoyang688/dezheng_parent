package com.dezheng.pojo.user;

import java.io.Serializable;
import java.math.BigDecimal;

public class FaceResult implements Serializable {
    private BigDecimal confidence;
    private String userName;

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

