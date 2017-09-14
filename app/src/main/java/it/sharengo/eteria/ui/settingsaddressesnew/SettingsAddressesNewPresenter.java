package it.sharengo.eteria.ui.settingsaddressesnew;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.GooglePlace;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.data.repositories.AddressRepository;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class SettingsAddressesNewPresenter extends BasePresenter<SettingsAddressesNewMvpView> {

    private static final String TAG = SettingsAddressesNewPresenter.class.getSimpleName();

    public final AppRepository mAppRepository;
    private final AddressRepository mAddressRepository;
    private final PreferencesRepository mPreferencesRepository;
    private final UserRepository mUserRepository;

    //private Observable<List<Address>> mFindAddressRequest;
    private Observable<List<SearchItem>> mFindSearchRequest;
    private Observable<ResponseGooglePlace> mFindPlacesRequest;

    private boolean hideLoading;
    private List<Address> mAddress;
    private List<SearchItem> mSearchItems;
    private List<SearchItem> historicItems;
    private ResponseGooglePlace mGooglePlace;


    public SettingsAddressesNewPresenter(SchedulerProvider schedulerProvider,
                                         AppRepository appRepository,
                                         AddressRepository addressRepository,
                                         PreferencesRepository preferencesRepository,
                                         UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mAddressRepository = addressRepository;
        mPreferencesRepository = preferencesRepository;
        this.mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SAVE new favourite
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Save search result on preference.
     *
     * @param  mPref       shared preference of app
     * @param  searchItem  item to search
     */
    public void saveFavourite(SharedPreferences mPref, SearchItem searchItem){
        mPreferencesRepository.saveSearchResultOnFavourites(mPref, searchItem);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET historic
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from search historic data by text searched.
     *
     * @param  searchText  text to search
     * @param  context     context of application
     * @param  mPrefs      shared preference of app
     */
    public void getHistoric(String searchText, Context context, SharedPreferences mPrefs) {
        hideLoading = true;

        if( mFindSearchRequest == null) {
            mFindSearchRequest = buildHistoricRequest(searchText, context, mPrefs);
            addSubscription(mFindSearchRequest.unsafeSubscribe(getHistoricSubscriber(context, mPrefs)));
        }
    }

    private Observable<List<SearchItem>> buildHistoricRequest(String searchText, final Context context, SharedPreferences mPrefs) {


        return mFindSearchRequest = mPreferencesRepository.getHistoricSearch(searchText, mPrefs, "favorite")
                .first()
                .compose(this.<List<SearchItem>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        showEmptyList(context);
                    }
                });
    }

    private Subscriber<List<SearchItem>> getHistoricSubscriber(final Context context, final SharedPreferences mPrefs){
        return new Subscriber<List<SearchItem>>() {
            @Override
            public void onCompleted() {
                mFindSearchRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindSearchRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<SearchItem> searchItemList) {

                historicItems = searchItemList;

                Collections.reverse(historicItems);

                historicItems = historicItems.subList(0, Math.min(historicItems.size(), 15));

            }
        };
    }

    private void showEmptyList(Context context){

        if(historicItems == null || historicItems.size() == 0){
            historicItems.add(new SearchItem(context.getString(R.string.settingsaddressnew_searchempty_label), "settings"));
        }

        getMvpView().showSearchResult(historicItems);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Address
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*public void findAddress(String searchText) {
        hideLoading = true;

        if( mFindAddressRequest == null) {
            mFindAddressRequest = buildFindAddressRequest(searchText);
            addSubscription(mFindAddressRequest.unsafeSubscribe(getFindAddressSubscriber()));
        }
    }


    private Observable<List<Address>> buildFindAddressRequest(String searchText) {
        return mFindAddressRequest = mAddressRepository.searchAddress(searchText,"json")
                .first()
                .compose(this.<List<Address>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkAddressResult();
                    }
                });
    }

    private Subscriber<List<Address>> getFindAddressSubscriber(){
        return new Subscriber<List<Address>>() {
            @Override
            public void onCompleted() {
                mFindAddressRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindAddressRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Address> addressList) {
                mAddress = addressList;
            }
        };
    }

    private void checkAddressResult(){

        mSearchItems = new ArrayList<SearchItem>();

        for (Address address : mAddress){

            String type = "address";

            if(historicItems != null)
                for (SearchItem sI : historicItems) {
                    if (address.display_name.equals(sI.display_name)) type = "historic";
                }

            mSearchItems.add(new SearchItem(address.display_name, type, address.longitude, address.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);

    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Address
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find place with Google Place API by text search by user.
     *
     * @param  searchText  context of application.
     */
    public void searchPlace(Context context, String searchText, Location location, String lang) {
        hideLoading = true;

        if( mFindPlacesRequest == null) {
            mFindPlacesRequest = buildFindPlacesRequest(context, searchText, location, lang);
            addSubscription(mFindPlacesRequest.unsafeSubscribe(getFindPlacesSubscriber()));
        }
    }


    private Observable<ResponseGooglePlace> buildFindPlacesRequest(Context context, String searchText, Location location, String lang) {
        return mFindPlacesRequest = mAddressRepository.searchPlace(searchText, location.getLatitude()+", "+location.getLongitude(), lang, context.getString(R.string.google_place_api_key))
                .first()
                .compose(this.<ResponseGooglePlace>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkPlacesResult();
                    }
                });
    }

    private Subscriber<ResponseGooglePlace> getFindPlacesSubscriber(){
        return new Subscriber<ResponseGooglePlace>() {
            @Override
            public void onCompleted() {
                mFindPlacesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindPlacesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseGooglePlace addressList) {
                //mAddress = addressList; TODO
                mGooglePlace = addressList;
            }
        };
    }

    private void checkPlacesResult(){



        mSearchItems = new ArrayList<SearchItem>();

        if(mGooglePlace != null && mGooglePlace.results != null){

            for(int i = 0; i < mGooglePlace.results.size(); i++){

                GooglePlace gPlace = mGooglePlace.results.get(i);
                String type = "address";

                if(historicItems != null)
                    for (SearchItem sI : historicItems) {
                        if (gPlace.address.equals(sI.display_name)) type = "historic";
                    }

                mSearchItems.add(new SearchItem(gPlace.address, type, gPlace.geometry.location.longitude, gPlace.geometry.location.latitude));
            }
        }

        getMvpView().showSearchResult(mSearchItems);

    }

}



