package com.example.hp.anroidpagination.PActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hp.anroidpagination.MainActivity;
import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;
import com.example.hp.anroidpagination.model.BrandPagination;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PPresenter implements PContract.BrandIntractor.OnFinishedListener,PContract.Presenter {

    private PContract.PView pView;
    private PContract.BrandIntractor brandIntractor;
    public PPresenter(PContract.PView pView,PContract.BrandIntractor brandIntractor){
        this.pView = pView;
        this.brandIntractor = brandIntractor;
    }

    @Override
    public void onDestroy() {
        pView = null;
    }


    @Override
    public void loadFirstPage(int currentPage,String keyword){
        pView.clearAdapterandList();
        pView.showProgress();
        pView.hideErrorView();
        brandIntractor.getBrandArrayList(this,currentPage,0,keyword);
    }

    @Override
    public void loadNextPage(int currentPage,String keyword) {
       brandIntractor.getBrandArrayList(this,currentPage,1,keyword);
    }

    @Override
    public void onSuccess(Brand brand,int flag,String keyword){
        if(flag==0){
                pView.updateTotalPage(brand);
                pView.hideProgress();
                pView.setData(brand);
                pView.updatePagesFirstLoad();

        }else{
                pView.removeLoadingFooter();
                pView.updateTotalPage(brand);
                pView.setData(brand);
                pView.updatePagesNextLoad();


        }
    }

    @Override
    public void onFailure(Throwable throwable,int flag,String keyword){
         if(flag==0){
             pView.showErrorView(throwable);
             }else{
             pView.showRetry(throwable);
         }
    }





}
