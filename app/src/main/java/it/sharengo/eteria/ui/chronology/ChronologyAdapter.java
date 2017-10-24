package it.sharengo.eteria.ui.chronology;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Trip;


public class ChronologyAdapter extends RecyclerView.Adapter<ChronologyAdapter.ViewHolder> {

    private OnItemActionListener mListener;

    private Activity mActivity;

    private List<Trip> mData;

    private ViewGroup detailViewSelected;
    private ImageView arrowImageViewSelected;

    private float discount_rate = 0.0f;


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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDiscountRate(float discount_rate){
        this.discount_rate = discount_rate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chronView)
        ViewGroup chronView;

        @BindView(R.id.bookingNTextView)
        TextView bookingNTextView;

        @BindView(R.id.minutesTextView)
        TextView minutesTextView;

        @BindView(R.id.dateTextView)
        TextView dateTextView;

        @BindView(R.id.addressStartTextView)
        TextView addressStartTextView;

        @BindView(R.id.endTextView)
        TextView endTextView;

        @BindView(R.id.addressEndTextView)
        TextView addressEndTextView;

        /*@BindView(R.id.kmTextView)
        TextView kmTextView;*/

        @BindView(R.id.carTextView)
        TextView carTextView;

        @BindView(R.id.detailView)
        ViewGroup detailView;

        @BindView(R.id.arrowImageView)
        ImageView arrowImageView;

        @BindView(R.id.rateMinTextView)
        TextView rateMinTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        /**
         * Render in view the info of user's trip.
         *
         * @param  trip  trip of user to render in view.
         */
        public void render(Trip trip) {
            //Numero prenotazione
            bookingNTextView.setText(String.format(mActivity.getString(R.string.chronology_bookingn_label), trip.id+""));

            //Costo e Minuti
            String sCost = "";
            if(trip.cost_computed && trip.total_cost != 0){
                sCost = String.format("%.2f", trip.total_cost / 100);
                sCost = " - € " + sCost.replace(",00","").replace(".00","");
            }
            int diffTime = (int) (trip.timestamp_end - trip.timestamp_start);
            float timeRide = diffTime/60.0f;
            double dec = timeRide - Math.floor(timeRide);
            diffTime = diffTime/60;
            if(dec > 0.5) diffTime++;
            minutesTextView.setText(String.format(mActivity.getString(R.string.chronology_minutes_label), diffTime+"", sCost));

            //Giorno e ora
            Date date = new Date(trip.timestamp_start*1000L);
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");
            String formattedDay = sdfDay.format(date);
            String formattedH = sdfH.format(date);
            dateTextView.setText(String.format(mActivity.getString(R.string.chronology_date_label), formattedDay, formattedH));

            if(getAdapterPosition() % 2 == 0){
                chronView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.whitesmoke));
            }else{
                chronView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            }

            //Indirizzo di partenza
            addressStartTextView.setText(getAddress(trip.latitude, trip.longitude));

            //Fine
            date = new Date(trip.timestamp_end*1000L);
            formattedDay = sdfDay.format(date);
            formattedH = sdfH.format(date);
            endTextView.setText(String.format(mActivity.getString(R.string.chronology_end_label), formattedDay, formattedH));

            //Indirizzo di arrivo
            addressEndTextView.setText(getAddress(trip.latitude_end, trip.longitude_end));

            //Km percorsi
            /*int km = trip.km_end - trip.km_start;
            kmTextView.setText(String.format(mActivity.getString(R.string.chronology_km_label), km+""));*/

            //Tariffa al minuto
            float baseRates = (float) (0.28 - (0.28 * discount_rate/100));
            String sBase = String.format("%.2f", baseRates);
            sBase = sBase.replace(",00","").replace(".00","");
            rateMinTextView.setText(String.format(mActivity.getString(R.string.chronology_ratesmin_label), ""+sBase));

            //Macchina
            carTextView.setText(String.format(mActivity.getString(R.string.chronology_sharengo_label), trip.plate));
        }

        /**
         * Open detail view for showing details of user's trip.
         */
        @OnClick(R.id.chronView)
        void onClick() {

            if(detailViewSelected != null && detailView != detailViewSelected){
                detailViewSelected.setVisibility(View.GONE);
                arrowImageViewSelected.setImageDrawable(getIcon(R.drawable.ic_arrow_drop_down));
            }

            detailViewSelected = detailView;
            arrowImageViewSelected = arrowImageView;

            if (detailView.getVisibility() == View.VISIBLE) {
                detailView.setVisibility(View.GONE);
                arrowImageView.setImageDrawable(getIcon(R.drawable.ic_arrow_drop_down));
            } else {
                detailView.setVisibility(View.VISIBLE);
                arrowImageView.setImageDrawable(getIcon(R.drawable.ic_arrow_drop_up));
            }

            if(getAdapterPosition() >= 0) mListener.onItemClick(mData.get(getAdapterPosition()));
        }


        private String getAddress(float latitude, float longitude){
            String address = "";

            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);


                if(!addresses.isEmpty() && addresses.get(0) != null) {

                    String street = addresses.get(0).getThoroughfare(); //Nome della via
                    String number = addresses.get(0).getSubThoroughfare(); //Numero civico

                    if(street != null) address = street;
                    if(address.length() > 0 && number != null) address += ", ";
                    if(number != null) address += number;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            return address;
        }

        private BitmapDrawable getIcon(int icon){
            BitmapDrawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (BitmapDrawable) mActivity.getDrawable(icon);
            }else{
                drawable = (BitmapDrawable) mActivity.getResources().getDrawable(icon);
            }

            return drawable;
        }
    }
}
