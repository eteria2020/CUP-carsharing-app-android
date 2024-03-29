package it.sharengo.eteria.ui.settingcities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.utils.ImageUtils;
import it.sharengo.eteria.utils.ResourceProvider;

public class SettingsCitiesAdapter extends RecyclerView.Adapter<SettingsCitiesAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;

    private List<City> mData;

    public interface OnItemActionListener {
        void onItemClick(City cityItem);
    }

    public SettingsCitiesAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
    }

    public void setData(List<City> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_settings_cities_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        City cityItem = mData.get(position);
        holder.render(cityItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameTextview)
        TextView nameTextview;

        @BindView(R.id.iconImageView)
        ImageView iconImageView;

        @BindView(R.id.cityView)
        ViewGroup cityView;

        @BindView(R.id.checkImageView)
        ImageView checkImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        /**
         * Render image, visibility and color of city's object received as param.
         *
         * @param  city  city object to render
         */
        public void render(City city) {
            nameTextview.setText(city.name);
            //ImageUtils.loadImage(iconImageView, city.media.images.icon.uri);

            Drawable cityIcon = null;
            switch (city.id){
                case "5":
                    cityIcon = ResourceProvider.getDrawable(mActivity, R.drawable.ic_milano);
                    break;
                case "8":
                    cityIcon = ResourceProvider.getDrawable(mActivity, R.drawable.ic_modena);
                    break;
                case "7":
                    cityIcon = ResourceProvider.getDrawable(mActivity, R.drawable.ic_roma);
                    break;
                case "6":
                    cityIcon = ResourceProvider.getDrawable(mActivity, R.drawable.ic_firenze);
                    break;
            }

            if(cityIcon != null)
                iconImageView.setImageDrawable(cityIcon);

            if(city.favourites){
                checkImageView.setVisibility(View.VISIBLE);
            }else{
                checkImageView.setVisibility(View.GONE);
            }

            if(getAdapterPosition() % 2 == 0){
                cityView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }else{
                cityView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gainsboro));
            }
        }

        /**
         * Manage click on list item.
         */
        @OnClick(R.id.cityView)
        void onClick() {
            if(getAdapterPosition() >= 0)
                mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
