package it.sharengo.development.ui.menu;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
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
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.utils.ResourceProvider;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private OnItemActionListener mListener;
    
    private List<MenuItem> mData;

    public interface OnItemActionListener {
        void onItemClick(MenuItem menuItem);
    }

    public MenuAdapter(OnItemActionListener listener) {
        mListener = listener;
        mData = new ArrayList<>();
    }

    public void setData(List<MenuItem> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItem menuItem = mData.get(position);
        holder.render(menuItem);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView text;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void render(MenuItem menuItem) {
            text.setText(ResourceProvider.getMenuItemLabel(
                    text.getContext(),
                    menuItem
            ));
            
            text.setSelected(menuItem.selected);
//            text.setEnabled(!menuItem.selected);
//            text.setClickable(!menuItem.selected);
            if(menuItem.selected) {
                text.setTypeface(null, Typeface.BOLD);
            }
            else {
                text.setTypeface(null, Typeface.NORMAL);
            }
        }

        @OnClick(R.id.text)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
