package it.sharengo.eteria.ui.chronology;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.models.Trip;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class ChronologyPresenter extends BasePresenter<ChronologyMvpView> {

    private static final String TAG = ChronologyPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseTrip> mTripsRequest;
    private Observable<ResponseUser> mUserRequest;

    private ResponseTrip mResponseTrip;
    private ResponseUser responseUser;

    private float discount_rate = 0.28f;

    private boolean hideLoading;
    private boolean background = false;
    public Context mContext;

    public ChronologyPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.HISTORIC);


    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mResponseTrip != null && mResponseTrip.trips != null) {
            getMvpView().showList(mResponseTrip.trips, discount_rate);
        }else{
            getTrips();
        }
    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }


    @Override
    protected void subscribeRequestsOnResume() {

        getTrips();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server user's trips list.
     */
    public void getTrips(){
        hideLoading = true;
        //getMvpView().showStandardLoading();

        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest();
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequest() {
        Log.w("background",": "+background);

        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, false, background)
                .first()
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkTripsResult();
                    }
                });
    }

    private Subscriber<ResponseTrip> getTripsSubscriber(){
        return new Subscriber<ResponseTrip>() {
            @Override
            public void onCompleted() {
                mTripsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mTripsRequest = null;
                getMvpView().showChronError(e);
            }

            @Override
            public void onNext(ResponseTrip response) {
                mResponseTrip = response;
            }
        };
    }

    private void checkTripsResult(){
        if(mResponseTrip.reason.isEmpty() && mResponseTrip.trips != null && mResponseTrip.trips.size() > 0){

            getRatesInfo();
        }else{

            getMvpView().showEmptyResult();
        }
    }

    public void hideLoading(){
        getMvpView().hideStandardLoading();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getRatesInfo(){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber()));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {
        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, 0, 0)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                        if(responseUser.reason != null && responseUser.reason.isEmpty() && responseUser.user != null){
                            discount_rate = responseUser.user.discount_rate;
                        }

                        renderChronList();
                    }
                });
    }

    private Subscriber<ResponseUser> getUserSubscriber(){
        return new Subscriber<ResponseUser>() {
            @Override
            public void onCompleted() {
                mUserRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mUserRequest = null;
                getMvpView().showList(mResponseTrip.trips, 0.28f);
            }

            @Override
            public void onNext(ResponseUser response) {

                responseUser = response;
            }
        };
    }

    private void renderChronList(){

        /*for(Trip trip : mResponseTrip.trips){

            int diffTime = (int) (trip.timestamp_end - trip.timestamp_start);
            String sCost = "";
            if(trip.cost_computed){
                sCost = String.format("%.2f", trip.total_cost / 100);
                sCost = " - € " + sCost.replace(",00","").replace(".00","");
            }

            trip.diffTime = diffTime / 60;
            trip.sCost = sCost;

            //Giorno e ora
            Date date = new Date(trip.timestamp_start*1000L);
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");
            String formattedDay = sdfDay.format(date);
            String formattedH = sdfH.format(date);
            trip.formattedDay = formattedDay;
            trip.formattedH = formattedH;

            //Fine
            date = new Date(trip.timestamp_end*1000L);
            trip.formattedEndDay = sdfDay.format(date);
            trip.formattedEndH = sdfH.format(date);

            //Tariffa al minuto
            float baseRates = (float) (0.28 - (0.28 * discount_rate/100));
            String sBase = String.format("%.2f", baseRates);
            sBase = sBase.replace(",00","").replace(".00","");
            trip.sBase = sBase;

            //Indirizzo partenza
            trip.addressStart = getAddress(trip.latitude, trip.longitude);

            //Indirizzo arrivo
            trip.addressEnd = getAddress(trip.latitude_end, trip.longitude_end);
        }*/

        getMvpView().showList(mResponseTrip.trips, discount_rate);

        if(!background) {
            background = true;
            getTrips();
        }
    }

    private String getAddress(float latitude, float longitude){
        String address = "";

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
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
        }

        return address;
    }

}



