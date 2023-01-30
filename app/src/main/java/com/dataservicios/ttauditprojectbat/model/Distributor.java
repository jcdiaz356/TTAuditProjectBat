package com.dataservicios.ttauditprojectbat.model;

import com.j256.ormlite.field.DatabaseField;

public class Distributor {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String code;
    @DatabaseField
    private String fullName;
    @DatabaseField
    private int active;
    @DatabaseField
    private String vendorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    @Override
    public String toString () {
        return fullName;
    }
}
