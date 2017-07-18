package it.sharengo.development.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    private Activity mActivity;
    
    private List<MenuItem> mData;

    public interface OnItemActionListener {
        void onItemClick(MenuItem menuItem);
    }

    public MenuAdapter(OnItemActionListener listener, Activity activity) {
        mListener = listener;
        mActivity = activity;
        mData = new ArrayList<>();
    }

    public void setData(List<MenuItem> pData) {
        mData = pData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_menu_item, parent, false);


        v.setLayoutParams(new RecyclerView.LayoutParams(parent.getWidth(), (parent.getHeight() / 9)));
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

        @BindView(R.id.icon)
        ImageView icon;

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
            /*if(menuItem.selected) {
                text.setTypeface(null, Typeface.BOLD);
            }
            else {
                text.setTypeface(null, Typeface.NORMAL);
            }*/

            icon.setImageDrawable(ResourceProvider.getMenuItemIcon(
                    icon.getContext(),
                    menuItem
            ));
            //icon.setBackground(ContextCompat.getDrawable(icon.getContext(), R.drawable.btn_bkg_darkjunglegreen_bordered));
        }

        @OnClick(R.id.text)
        void onClick() {
            mListener.onItemClick(mData.get(getAdapterPosition()));
        }
    }
}
