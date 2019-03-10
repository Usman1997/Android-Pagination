package com.example.hp.anroidpagination.model;

import java.lang.ref.SoftReference;

public class BrandData {
    private String image;
    private String reference_id;
    private String description;
    private String created_at;
    private int equipment;
    private String title;
    private String type;
    private String deleted_at;
    private String brand_id;
    private String current_stock;
    private String updated_at;
    private String id;
    private BrandDataProduct_brand product_brand;
    private String customer_id;

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getReference_id() {
        return this.reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getEquipment() {
        return this.equipment;
    }

    public void setEquipment(int equipment) {
        this.equipment = equipment;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getDeleted_at() {
        return this.deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getBrand_id() {
        return this.brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCurrent_stock() {
        return this.current_stock;
    }

    public void setCurrent_stock(String current_stock) {
        this.current_stock = current_stock;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BrandDataProduct_brand getProduct_brand() {
        return this.product_brand;
    }

    public void setProduct_brand(BrandDataProduct_brand product_brand) {
        this.product_brand = product_brand;
    }

    public String getCustomer_id() {
        return this.customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
