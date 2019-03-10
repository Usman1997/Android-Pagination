package com.example.hp.anroidpagination.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hp.anroidpagination.MainActivity;
import com.example.hp.anroidpagination.PActivity.PActivity;
import com.example.hp.anroidpagination.R;
import com.example.hp.anroidpagination.model.Brand;
import com.example.hp.anroidpagination.model.BrandData;
import com.example.hp.anroidpagination.utils.PaginationAdapterCallback;
import com.example.hp.anroidpagination.utils.RecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.hp.anroidpagination.R.drawable.restaurant_choose_item_selected_backround;

/**
 * Created by Suleiman on 19/10/16.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LOADING = 1;
    private static final int ITEM = 0;

    private List<BrandData> brandResult;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private String errorMsg;

   private PaginationAdapterCallback mCallback;

    private ItemClickListener mClickListener;



    public PaginationAdapter(Context context) {
        this.context = context;
       this.mCallback = (PaginationAdapterCallback) context;
        brandResult = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case ITEM:
                View viewHero = inflater.inflate(R.layout.brand_item, parent, false);
                viewHolder = new BrandVH(viewHero);
                 break;

            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BrandData brandData = brandResult.get(position); // Movie

        switch (getItemViewType(position)){
            case ITEM:
                final BrandVH brandVH = (BrandVH) holder;
                brandVH.brand_name.setText(brandData.getTitle());

                brandVH.no_of_equipments.setText(String.valueOf(brandData.getEquipment()));

                break;

                case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }

        }

    @Override
    public int getItemCount() {
        return brandResult == null ? 0 : brandResult.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM;
        } else {
            return (position == brandResult.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    /*
        Helpers - bind Views
   _________________________________________________________________________________________________
    */

    /**
     * @param result
     * @return [releasedate] | [2letterlangcode]
     */
//    private String formatYearLabel(Result result) {
//        return result.getReleaseDate().substring(0, 4)  // we want the year only
//                + " | "
//                + result.getOriginalLanguage().toUpperCase();
//    }

    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
     *

     * @return Glide builder
     */
//    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
//        return Glide
//                .with(context)
//                .load(BASE_URL_IMG + posterPath)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
//                .centerCrop()
//                .crossFade();
//    }


    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(BrandData r) {
        brandResult.add(r);
        notifyItemInserted(brandResult.size() - 1);
    }

    public void addAll(List<BrandData> brandResult) {
        for (BrandData result : brandResult) {
            add(result);
        }
    }

    public void remove(BrandData r) {
        int position = brandResult.indexOf(r);
        if (position > -1) {
            brandResult.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new BrandData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = brandResult.size() - 1;
        BrandData result = getItem(position);

        if (result != null) {
            brandResult.remove(position);
            notifyItemRemoved(position);
        }
    }

    public BrandData getItem(int position) {
        return brandResult.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(brandResult.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Header ViewHolder
     */
    protected class BrandVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView brand_name;
        private ImageView brand_image;
        private TextView no_of_equipments;


        public BrandVH(View itemView) {
            super(itemView);
            brand_name = (TextView) itemView.findViewById(R.id.brand_name);
            brand_image = (ImageView) itemView.findViewById(R.id.brand_image);
            no_of_equipments = (TextView)itemView.findViewById(R.id.no_of_equipment);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }



    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }







}
