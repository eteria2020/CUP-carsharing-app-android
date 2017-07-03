package it.sharengo.development.ui.settingsaddresses;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
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
import it.sharengo.development.data.models.SearchItem;


public class SettingsAddressesAdapter extends RecyclerView.Adapter<SettingsAddressesAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;
    private SettingsAddressesFragment mFragment;

    private List<SearchItem> mData;

    public interface OnItemActionListener {
        void onItemClick(SearchItem searchItem);
    }

    public SettingsAddressesAdapter(OnItemActionListener listener, Activity activity, SettingsAddressesFragment fragment) {
        mListener = listener;
        mActivity = activity;
        mFragment = fragment;
        mData = new ArrayList<>();
    }

    public void setData(List<SearchItem> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_settings_addresses_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchItem searchItem = mData.get(position);
        holder.render(searchItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iconImageView)
        ImageView iconImageView;

        @BindView(R.id.nameTextView)
        TextView nameTextView;

        @BindView(R.id.addressTextView)
        TextView addressTextView;

        @BindView(R.id.addressView)
        ViewGroup addressView;

        @BindView(R.id.favoriteButton)
        ImageView favoriteButton;

        @BindView(R.id.editButton)
        ImageView editButton;

        @BindView(R.id.deleteButton)
        ImageView deleteButton;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(SearchItem searchItem) {

            if(searchItem.name != null && !searchItem.name.isEmpty()) {
                nameTextView.setText(searchItem.name);
                nameTextView.setVisibility(View.VISIBLE);
            }else{
                nameTextView.setVisibility(View.GONE);
            }

            addressTextView.setText(searchItem.display_name);

            //Background
            if(getAdapterPosition() % 2 == 0){
                addressView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }else{
                addressView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gainsboro));
            }

            //Icona
            int typeDrawable = 0;
            if(searchItem.type.equals("plate"))
                typeDrawable = R.drawable.ic_targa_ricerca;
            else if(searchItem.type.equals("address"))
                typeDrawable = R.drawable.ic_indirizzo_ricerca;
            else if(searchItem.type.equals("none") || searchItem.type.equals("favorite"))
                typeDrawable = R.drawable.ic_favourites;
            else if(searchItem.type.equals("historic"))
                typeDrawable = R.drawable.ic_clock;

            if(typeDrawable > 0) {
                Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), typeDrawable);
                iconImageView.setImageDrawable(drawable);
                iconImageView.setVisibility(View.VISIBLE);
            }else{
                iconImageView.setVisibility(View.GONE);
            }

            //Pulsanti
            if(searchItem.type.equals("favorite")){
                favoriteButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }else{
                favoriteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.addressView)
        void onClick() {
            //mListener.onItemClick(mData.get(getAdapterPosition()));
        }

        @OnClick(R.id.favoriteButton)
        void onFavoriteClick(){
            mFragment.setAddFavorite(mData.get(getAdapterPosition()));
        }

        @OnClick(R.id.editButton)
        void onEditClick(){
            mFragment.setEditFavorite(mData.get(getAdapterPosition()));
        }

        @OnClick(R.id.deleteButton)
        void onDeleteClick(){
            mFragment.setDeleteFavorite(mData.get(getAdapterPosition()));
        }
    }
}
