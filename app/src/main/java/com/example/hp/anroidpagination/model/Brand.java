package com.example.hp.anroidpagination.model;

import java.util.ArrayList;
import java.util.List;

public class Brand {
    private BrandPagination pagination;
    private ArrayList<BrandData> data;

    public BrandPagination getPagination() {
        return this.pagination;
    }

    public void setPagination(BrandPagination pagination) {
        this.pagination = pagination;
    }

    public ArrayList<BrandData> getData() {
        return this.data;
    }

    public void setData(ArrayList<BrandData> data) {
        this.data = data;
    }
}
