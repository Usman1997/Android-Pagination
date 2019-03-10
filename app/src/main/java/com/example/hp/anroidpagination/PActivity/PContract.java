package com.example.hp.anroidpagination.PActivity;

import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;

import java.util.ArrayList;

public interface PContract {

    interface Presenter{
          void onDestroy();

          void loadFirstPage(int currentPage,String keywoard);
          void loadNextPage(int currentPage,String keyword);

    }


    interface PView{
             void showProgress();
             void hideProgress();
            void hideErrorView();
            void showErrorView(Throwable throwable);
            void setData(Brand brand);
            void updatePagesFirstLoad();
            void updatePagesNextLoad();
            void updateTotalPage(Brand brand);
            void removeLoadingFooter();
            void showRetry(Throwable throwable);
            void clearAdapterandList();
            void resetCurrentPage();

    }


    interface BrandIntractor{
        interface OnFinishedListener {
            void onSuccess(Brand brand,int flag,String keyword);
            void onFailure(Throwable t,int flag,String keyword);

        }
        void getBrandArrayList(OnFinishedListener onFinishedListener,int page,int flag,String keyword);

    }
    }

