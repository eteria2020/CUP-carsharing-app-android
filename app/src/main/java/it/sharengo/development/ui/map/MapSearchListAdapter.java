package it.sharengo.development.ui.map;

import android.graphics.drawable.Drawable;
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

public class MapSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    public static final String TAG = MapSearchListAdapter.class.getSimpleName();

    protected List<SearchItem> mData;
    private OnItemActionListener mListener;

    public interface OnItemActionListener {
        void onItemClick(SearchItem searchItem);
    }

    public MapSearchListAdapter(OnItemActionListener listener) {
        mListener = listener;
        mData = new ArrayList<>();
    }

    public void setData(List<SearchItem> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchItem searchItem = mData.get(position);
        ((ViewHolder) holder).render(searchItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.typeImageView)
        ImageView typeImageView;
        @BindView(R.id.displayNameTextView)
        TextView displayNameTextView;
        @BindView(R.id.subNameTextView)
        TextView subNameTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(SearchItem searchItem) {

            //Icona
            int typeDrawable = 0;
            if(searchItem.type.equals("plate"))
                typeDrawable = R.drawable.ic_targa_ricerca;
            else if(searchItem.type.equals("address"))
                typeDrawable = R.drawable.ic_indirizzo_ricerca;
            else if(searchItem.type.equals("none"))
                typeDrawable = R.drawable.ic_favourites;
            else if(searchItem.type.equals("historic"))
                typeDrawable = R.drawable.ic_clock;

            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), typeDrawable);
            typeImageView.setImageDrawable(drawable);

            //Nome
            displayNameTextView.setText(searchItem.display_name);

            //In caso di targa
            if(searchItem.type.equals("plate"))
                subNameTextView.setVisibility(View.VISIBLE);
            else
                subNameTextView.setVisibility(View.GONE);

        }

        @OnClick(R.id.container)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }

    }
}
