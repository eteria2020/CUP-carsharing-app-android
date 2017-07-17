package it.sharengo.development.ui.feeds;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Feed;
import jp.wasabeef.glide.transformations.MaskTransformation;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;

    private List<Feed> mData;
    private int height;

    public interface OnItemActionListener {
        void onItemClick(Feed categoryItem);
    }

    public FeedsAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
    }

    public void setData(List<Feed> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_feeds_item, parent, false);
        height = parent.getMeasuredHeight() / 3;
        v.setMinimumHeight(height);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feed feedItem = mData.get(position);
        holder.render(feedItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feedTitleTextView)
        TextView feedTitleTextView;

        @BindView(R.id.feedImageView)
        ImageView feedImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(Feed feed) {

            //Titolo
            feedTitleTextView.setText(feed.title);

            //Immagine copertina
            //ImageUtils.loadImage(feedImageView, feed.media.images.image.uri);
            Glide.with(feedImageView.getContext())
                    .load(feed.media.images.image.uri)
                    .crossFade()
                    .bitmapTransform(new CenterCrop(feedImageView.getContext()),
                            new MaskTransformation(feedImageView.getContext(), R.drawable.trapezoid1))
                    .into(feedImageView);

        }

        @OnClick(R.id.feedView)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
