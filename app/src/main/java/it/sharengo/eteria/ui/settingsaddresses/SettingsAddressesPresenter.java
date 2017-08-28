package it.sharengo.eteria.ui.settingsaddresses;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.List;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class SettingsAddressesPresenter extends BasePresenter<SettingsAddressesMvpView> {

    private static final String TAG = SettingsAddressesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;
    private final PreferencesRepository mPreferencesRepository;

    private Observable<List<SearchItem>> mFindSearchRequest;
    private List<SearchItem> historicItems;

    private boolean hideLoading;

    private Context mContext;
    private SharedPreferences pref;


    public SettingsAddressesPresenter(SchedulerProvider schedulerProvider,
                                      AppRepository appRepository,
                                      PreferencesRepository preferencesRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        this.mUserRepository = userRepository;
        mPreferencesRepository = preferencesRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }

    public void setData(Context context, SharedPreferences mPrefs){
        mContext = context;
        pref = mPrefs;
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

        getHistoric(mContext, pref);

    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    /**
     * Load historic data.
     *
     * @param  context context of application
     * @param  mPrefs  shared preference of app
     */
    public void loadData(Context context, SharedPreferences mPrefs){

        hideLoading = true;

        getHistoric(context, mPrefs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET historic
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server historic data.
     *
     * @param  context  context of application
     * @param  mPrefs   shared preference of app
     */
    public void getHistoric(Context context, SharedPreferences mPrefs) {
        hideLoading = true;

        if( mFindSearchRequest == null) {
            mFindSearchRequest = buildHistoricRequest("", context, mPrefs);
            addSubscription(mFindSearchRequest.unsafeSubscribe(getHistoricSubscriber(context, mPrefs)));
        }
    }

    private Observable<List<SearchItem>> buildHistoricRequest(String searchText, final Context context, SharedPreferences mPrefs) {


        return mFindSearchRequest = mPreferencesRepository.getHistoricSearch(searchText, mPrefs, null)
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

        boolean find = false;
        for(SearchItem ss : historicItems){
            if(ss.type.equals("favorite")) find = true;
        }

        if(historicItems == null || historicItems.size() == 0|| !find){
            historicItems.add(new SearchItem(context.getString(R.string.settingsaddressnew_searchempty_label), "settings"));

            getMvpView().showEmptyResult();
        }else{
            getMvpView().showList(historicItems);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              EDIT  favourite
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Edit result search of favourite.
     *
     * @param  mPref       shared preference of app
     * @param  searchItem  item to search
     * @param  name        new value for name
     * @param  address     new value for address
     */
    public void editFavourite(SharedPreferences mPref, SearchItem searchItem, String name, String address){
        mPreferencesRepository.editSearchResultOnFavourites(mPref, searchItem, name, address);
        getHistoric(mContext, pref);
    }

    /**
     * Add result search of favourite.
     *
     * @param  mPref       shared preference of app
     * @param  searchItem  item to search
     * @param  name        new value for name
     * @param  address     new value for address
     */
    public void addFavourite(SharedPreferences mPref, SearchItem searchItem, String name, String address){
        mPreferencesRepository.addSearchResultOnFavourites(mPref, searchItem, name, address);
        getHistoric(mContext, pref);
    }

    /**
     * Delete result search of favourite.
     *
     * @param  mPref       shared preference of app
     * @param  searchItem  item to search
     */
    public void deleteFavourite(SharedPreferences mPref, SearchItem searchItem){
        mPreferencesRepository.deleteSearchResultOnFavourites(mPref, searchItem);
        getHistoric(mContext, pref);
    }
}