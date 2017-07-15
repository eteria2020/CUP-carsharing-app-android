package it.sharengo.development.ui.feeds;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.utils.ImageUtils;

public class FeedsCategoriesAdapter extends RecyclerView.Adapter<FeedsCategoriesAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;

    private List<FeedCategory> mData;
    private int height;

    public interface OnItemActionListener {
        void onItemClick(FeedCategory categoryItem);
    }

    public FeedsCategoriesAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
    }

    public void setData(List<FeedCategory> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_feeds_categories_item, parent, false);
        height = parent.getMeasuredHeight() / 3;
        v.setMinimumHeight(height);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FeedCategory categoryItem = mData.get(position);
        holder.render(categoryItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameTextview)
        TextView nameTextview;

        @BindView(R.id.bkgImageView)
        ImageView bkgImageView;

        @BindView(R.id.dividerRightView)
        View dividerRightView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(FeedCategory category) {
            nameTextview.setText(category.name);

            //Icona
            ImageUtils.loadImage(bkgImageView, category.media.images.icon.uri);
            bkgImageView.setColorFilter(Color.argb(255, 255, 255, 255));

            //Sfondo icona
            GradientDrawable backgroundShape = (GradientDrawable) bkgImageView.getBackground();
            backgroundShape.setColor(Color.parseColor(category.appearance.color.rgb));

            //Divider
            dividerRightView.setMinimumHeight(height);

            //Verifico se la categoria è disabilitata
            if(!category.status.published.equals("1")){
                bkgImageView.setColorFilter(Color.parseColor("#97918B"));
                backgroundShape.setColor(Color.parseColor("#B4ACA4"));
            }
        }

        @OnClick(R.id.categoryView)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
