package com.example.hp.anroidpagination.Api;

import com.example.hp.anroidpagination.model.Brand;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface BrandService {

    @Headers("Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdXN0b21lcl9pZCI6MTQ0MDYsInN1YiI6MjQxLCJpc3MiOiJodHRwczovL3N0YWdpbmcudGVjaHBsYW5uZXIuY29tL2FwaS92MS91c2VyL2xvZ2luL2N1c3RvbWVyIiwiaWF0IjoxNTUxNTIwMTU3LCJleHAiOjE1NTIxMjQ5NTcsIm5iZiI6MTU1MTUyMDE1NywianRpIjoiMjRvZnhQZmM4UjJMZ2d1SyJ9.Gdk3NRtyE8W9855uLtIcMJ62h3KPwnN3mHgsx4bDj9U")
    @GET("product/list")
    Call<Brand> getBrands(
            @Query("pagination") boolean flag,
            @Query("page") int page,
            @Query("keyword") String keyword

    );

}
