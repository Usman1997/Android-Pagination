package com.example.hp.anroidpagination;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.anroidpagination.Adapter.PaginationAdapter;
import com.example.hp.anroidpagination.Api.BrandService;
import com.example.hp.anroidpagination.Api.RetrofitApiClient;
import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;
import com.example.hp.anroidpagination.model.BrandPagination;
import com.example.hp.anroidpagination.utils.PaginationAdapterCallback;
import com.example.hp.anroidpagination.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback,PaginationAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    private BrandService brandService;

   public static ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
   public static int selectedPosition=-1;
    int previousPosition;
    boolean isSelect = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        adapter = new PaginationAdapter(this);
        adapter.setClickListener(this);

       GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        rv.setLayoutManager(layoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);


        rv.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });





    brandService = RetrofitApiClient.getClient().create(BrandService.class);
        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();


        brandCall().enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                // Got data. Send it to adapter

                hideErrorView();

                BrandPagination brandPagination = fetchPagination(response);
                TOTAL_PAGES = brandPagination.getLast();
                Toast.makeText(MainActivity.this,String.valueOf(TOTAL_PAGES),Toast.LENGTH_SHORT).show();
                Log.d(TAG, String.valueOf(response));
                List<BrandData> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }



    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        brandCall().enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                BrandPagination brandPagination = fetchPagination(response);
                TOTAL_PAGES = brandPagination.getLast();
                List<BrandData> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES)
                    adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private BrandPagination fetchPagination(Response<Brand> response) {
         Brand brand = response.body();
        return brand.getPagination();
    }



    private List<BrandData> fetchResults(Response<Brand> response) {
        Brand brand = response.body();
        return brand.getData();
    }

    private Call<Brand> brandCall() {
        return brandService.getBrands(
                true,currentPage,null
        );
    }

    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }


    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void onItemClick(View view, int position) {

        GridLayoutManager gridLayoutManager = ((GridLayoutManager) rv.getLayoutManager());
        int firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();
        if (isSelect) {
            if (previousPosition >= firstVisiblePosition && previousPosition <= lastVisiblePosition) {
                RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(previousPosition);
                ImageView imageView1 = viewHolder.itemView.findViewById(R.id.brand_image);
                imageView1.setBackground(getDrawable(R.drawable.restaurant_choose_item_background));
            } else {
                selectedIndexes.add(previousPosition);
            }
            ImageView imageView = view.findViewById(R.id.brand_image);
            imageView.setBackground(getDrawable(R.drawable.restaurant_choose_item_selected_backround));


        } else {
            ImageView imageView = view.findViewById(R.id.brand_image);
            imageView.setBackground(getDrawable(R.drawable.restaurant_choose_item_selected_backround));
        }
        previousPosition = position;
        selectedPosition = position;
        isSelect = true;


    }



}
