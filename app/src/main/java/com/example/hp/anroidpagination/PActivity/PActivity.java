package com.example.hp.anroidpagination.PActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.anroidpagination.Adapter.PaginationAdapter;
import com.example.hp.anroidpagination.MainActivity;
import com.example.hp.anroidpagination.R;
import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;
import com.example.hp.anroidpagination.model.BrandPagination;
import com.example.hp.anroidpagination.utils.PaginationAdapterCallback;
import com.example.hp.anroidpagination.utils.PaginationScrollListener;
import com.example.hp.anroidpagination.utils.RecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Response;

public class PActivity extends AppCompatActivity implements PContract.PView,PaginationAdapterCallback,PaginationAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";

    PaginationAdapter adapter;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    private PContract.Presenter presenter;

    ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
    int previousPosition;
    boolean isSelect = false;

    ImageView cancel_icon;
    EditText search_text;
    List<BrandData> results;

    String search_result="";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = new PPresenter(this,new GetBrandIntractor());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        cancel_icon = (ImageView)findViewById(R.id.cancel_icon);
        search_text = (EditText)findViewById(R.id.search);
        adapter = new PaginationAdapter(this);
        adapter.setClickListener(this);


        results = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        rv.setLayoutManager(layoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cancel_icon.setVisibility(View.VISIBLE);
            }
        });



        cancel_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCurrentPage();
                search_text.setText("");
                search_result="";
                cancel_icon.setVisibility(View.INVISIBLE);
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(search_text.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                presenter.loadFirstPage(currentPage,search_result);

            }
        });


          search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
              @Override
              public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                  resetCurrentPage();
                  search_result = search_text.getText().toString();
                  presenter.loadFirstPage(currentPage,search_result);
                  InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                  in.hideSoftInputFromWindow(search_text.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                  return true;
              }

          });



        rv.addOnScrollListener(new PaginationScrollListener(layoutManager) {


            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                presenter.loadNextPage(currentPage,search_result);
                Log.d("adbcbcb","ad");
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





        presenter.loadFirstPage(currentPage,search_result);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.loadFirstPage(currentPage,search_result);
            }
        });
    }



    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void retryPageLoad() {
    presenter.loadNextPage(currentPage,search_text.getText().toString());
    }

    @Override
    public void showErrorView(Throwable throwable) {
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void setData(Brand brand) {
        results = fetchResults(brand);
        Toast.makeText(PActivity.this,String.valueOf(brand),Toast.LENGTH_SHORT).show();
        adapter.addAll(results);
    }

    @Override
    public void updatePagesFirstLoad() {
        if (currentPage <= TOTAL_PAGES)
            adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void updatePagesNextLoad() {
        if (currentPage != TOTAL_PAGES)
            adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void updateTotalPage(Brand brand) {
         BrandPagination brandPagination = fetchPagination(brand);
         TOTAL_PAGES = brandPagination.getLast();
    }

    @Override
    public void removeLoadingFooter() {
            adapter.removeLoadingFooter();
            isLoading = false;
    }

    @Override
    public void showRetry(Throwable t) {
        adapter.showRetry(true,fetchErrorMessage(t));
    }

    @Override
    public void clearAdapterandList() {
        results.clear();
        adapter.clear();

    }

    @Override
    public void resetCurrentPage() {
        isLoading = false;
        isLastPage = false;
        TOTAL_PAGES = 0;
        currentPage = PAGE_START;
    }


    private BrandPagination fetchPagination(Brand brand) {
        return brand.getPagination();
        //return null;
    }



    private List<BrandData> fetchResults(Brand brand) {
        return brand.getData();
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
            }else {
                selectedIndexes.add(previousPosition);

            }

            ImageView imageView = view.findViewById(R.id.brand_image);
            imageView.setBackground(getDrawable(R.drawable.restaurant_choose_item_selected_backround));


        } else {
            ImageView imageView = view.findViewById(R.id.brand_image);
            imageView.setBackground(getDrawable(R.drawable.restaurant_choose_item_selected_backround));
        }
        previousPosition = position;
        isSelect = true;

    }


}
