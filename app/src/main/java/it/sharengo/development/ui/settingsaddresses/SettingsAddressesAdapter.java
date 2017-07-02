package it.sharengo.development.ui.settingsaddresses;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<SearchItem> mData;

    public interface OnItemActionListener {
        void onItemClick(SearchItem searchItem);
    }

    public SettingsAddressesAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
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

        @BindView(R.id.nameTextView)
        TextView nameTextView;

        @BindView(R.id.addressTextView)
        TextView addressTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(SearchItem searchItem) {

            if(searchItem.name != null)
                nameTextView.setText(searchItem.name);

            addressTextView.setText(searchItem.display_name);
        }

        @OnClick(R.id.addressView)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
