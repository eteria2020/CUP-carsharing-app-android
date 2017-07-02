package it.sharengo.development.ui.settingsaddressesnew;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.sharengo.development.R;
import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.repositories.AddressRepository;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class SettingsAddressesNewPresenter extends BasePresenter<SettingsAddressesNewMvpView> {

    private static final String TAG = SettingsAddressesNewPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final AddressRepository mAddressRepository;
    private final PreferencesRepository mPreferencesRepository;

    private Observable<List<Address>> mFindAddressRequest;
    private Observable<List<SearchItem>> mFindSearchRequest;

    private boolean hideLoading;
    private List<Address> mAddress;
    private List<SearchItem> mSearchItems;
    private List<SearchItem> historicItems;


    public SettingsAddressesNewPresenter(SchedulerProvider schedulerProvider,
                                         AppRepository appRepository,
                                         AddressRepository addressRepository,
                                         PreferencesRepository preferencesRepository) {
        super(schedulerProvider);
        mAppRepository = appRepository;
        mAddressRepository = addressRepository;
        mPreferencesRepository = preferencesRepository;

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
    //                                              GET historic
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void getHistoric(String searchText, Context context, SharedPreferences mPrefs) {
        hideLoading = true;

        if( mFindSearchRequest == null) {
            mFindSearchRequest = buildHistoricRequest(searchText, context, mPrefs);
            addSubscription(mFindSearchRequest.unsafeSubscribe(getHistoricSubscriber(context, mPrefs)));
        }
    }

    private Observable<List<SearchItem>> buildHistoricRequest(String searchText, final Context context, SharedPreferences mPrefs) {


        return mFindSearchRequest = mPreferencesRepository.getHistoricSearch(searchText, mPrefs)
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

    public void findAddress(String searchText) {
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

        Log.w("SEARCH","checkAddressResult");

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

    }

}



