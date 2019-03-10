package com.example.hp.anroidpagination.PActivity;

import android.util.Log;
import android.widget.Toast;

import com.example.hp.anroidpagination.Api.BrandService;
import com.example.hp.anroidpagination.Api.RetrofitApiClient;
import com.example.hp.anroidpagination.MainActivity;
import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;
import com.example.hp.anroidpagination.model.BrandPagination;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetBrandIntractor implements PContract.BrandIntractor {


    @Override
    public void getBrandArrayList(final OnFinishedListener onFinishedListener, int currentPage, final int flag, final String keyword) {
        final BrandService brandService= RetrofitApiClient.getClient().create(BrandService.class);
        Call<Brand> call = brandService.getBrands(true,currentPage,keyword);
        call.enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if(flag==0){
                    onFinishedListener.onSuccess(response.body(),0,keyword);
                }else{
                    onFinishedListener.onSuccess(response.body(),1,keyword);
                    }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                if(flag==0){
                    onFinishedListener.onFailure(t,0,keyword);
                }else{
                    onFinishedListener.onFailure(t,1,keyword);

                }
            }
        });

    }







}
