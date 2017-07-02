package it.sharengo.development.ui.chronology;

import android.app.Activity;
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
import it.sharengo.development.R;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.utils.ImageUtils;

public class ChronologyAdapter extends RecyclerView.Adapter<ChronologyAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;

    private List<Trip> mData;

    public interface OnItemActionListener {
        void onItemClick(Trip cityItem);
    }

    public ChronologyAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
    }

    public void setData(List<Trip> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chronology_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip tripItem = mData.get(position);
        holder.render(tripItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chronView)
        ViewGroup chronView;

        @BindView(R.id.bookingNTextView)
        TextView bookingNTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(Trip trip) {
            bookingNTextView.setText("BUBA");


            if(getAdapterPosition() % 2 == 0){
                chronView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gainsboro));
            }else{
                chronView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }
        }

        @OnClick(R.id.chronView)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
