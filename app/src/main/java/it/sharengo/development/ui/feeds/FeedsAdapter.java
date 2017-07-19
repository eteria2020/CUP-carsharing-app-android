package it.sharengo.development.ui.feeds;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.utils.ImageUtils;
import jp.wasabeef.glide.transformations.MaskTransformation;

import static android.content.Context.MODE_PRIVATE;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;
    private SharedPreferences mPref;

    private List<Feed> mData;
    private int height;

    public interface OnItemActionListener {
        void onItemClick(Feed categoryItem);
    }

    public FeedsAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
        mPref = mActivity.getSharedPreferences(mActivity.getString(R.string.preference_file_key), MODE_PRIVATE);
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

        @BindView(R.id.feedTrapezoidImageView)
        ImageView feedTrapezoidImageView;

        @BindView(R.id.feedAdvantageTextView)
        TextView feedAdvantageTextView;

        @BindView(R.id.feedTriangleView)
        ViewGroup feedTriangleView;

        @BindView(R.id.feedTriangleImageView)
        ImageView feedTriangleImageView;

        @BindView(R.id.feedIconImageView)
        ImageView feedIconImageView;

        @BindView(R.id.feedDateTextView)
        TextView feedDateTextView;

        @BindView(R.id.feedLaunchTitleTextView)
        TextView feedLaunchTitleTextView;

        @BindView(R.id.feedAbstractTextView)
        TextView feedAbstractTextView;

        @BindView(R.id.feedLocationTextView)
        TextView feedLocationTextView;

        @BindView(R.id.feedAdvantageBottomTextView)
        TextView feedAdvantageBottomTextView;

        @BindView(R.id.feedIntersImageView)
        ImageView feedIntersImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(Feed feed) {

            //Immagine copertina
            Glide.with(feedImageView.getContext())
                    .load(feed.media.images.image.uri)
                    .crossFade()
                    .bitmapTransform(new CenterCrop(feedImageView.getContext()),
                            new MaskTransformation(feedImageView.getContext(), R.drawable.trapezoid1))
                    .into(feedImageView);

            //Overlay copertina
            feedTrapezoidImageView.setColorFilter(Color.parseColor(feed.appearance.color.rgb));

            //Advantage
            feedTriangleImageView.setColorFilter(Color.parseColor(feed.appearance.color.rgb));
            if(feed.informations.advantage_top.isEmpty()) {
                feedTriangleView.setVisibility(View.GONE);
            }else {
                feedTriangleView.setVisibility(View.VISIBLE);
                feedAdvantageTextView.setText(feed.informations.advantage_top);
            }

            //Icona
            ImageUtils.loadImage(feedIconImageView, feed.category.media.images.icon.uri);
            GradientDrawable backgroundShape = (GradientDrawable) feedIconImageView.getBackground();
            backgroundShape.setColor(Color.parseColor(feed.appearance.color.rgb));

            //Data
            feedDateTextView.setText(feed.informations.date.friendly);

            //Launch title
            if(feed.informations.launch_title.isEmpty()){
                feedLaunchTitleTextView.setVisibility(View.GONE);
            }else{
                feedLaunchTitleTextView.setText(feed.informations.launch_title);
                feedLaunchTitleTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
            }

            //Titolo
            feedTitleTextView.setText(feed.title);

            //Abstract
            if(feed.informations.abstract_text.isEmpty()){
                feedAbstractTextView.setVisibility(View.GONE);
            }else{
                feedAbstractTextView.setText(feed.informations.abstract_text);
            }

            //Location
            feedLocationTextView.setText(feed.informations.location + ", " +  feed.informations.address.friendly +  ", " + feed.informations.city.name);

            //Advantage bottom
            if(feed.informations.advantage_bottom.isEmpty()){
                feedAdvantageBottomTextView.setVisibility(View.GONE);
            }else{
                feedAdvantageBottomTextView.setText(feed.informations.advantage_bottom);

                if(feed.appearance.color.enforce.equals("true"))
                    feedAdvantageBottomTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
            }

            //Interessi
            SharedPreferences.Editor prefsEditor = mPref.edit();

            Type fooType = new TypeToken<List<String>>() {}.getType();
            Gson gson = new Gson();
            List<String> feedsInterested = (ArrayList<String>) gson.fromJson(mPref.getString(mActivity.getString(R.string.preference_feeds), "[]"), fooType);
            boolean isInters = false;
            for (String feed_id : feedsInterested){
                if(feed_id.equals(feed.id)){
                    isInters = true;
                }
            }

            if(isInters){
                feedIntersImageView.setVisibility(View.VISIBLE);
            }else{
                feedIntersImageView.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.feedView)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
