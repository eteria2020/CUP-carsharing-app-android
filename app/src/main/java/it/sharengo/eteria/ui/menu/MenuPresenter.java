package it.sharengo.eteria.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.util.List;
import java.util.Locale;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MenuPresenter extends BasePresenter<MenuMvpView> {

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<List<MenuItem>> mMenuRequest;

    private List<MenuItem> mMenuItemList;

    public MenuPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        this.mAppRepository = appRepository;
        this.mUserRepository = userRepository;
    }

    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mMenuItemList != null) {
            getMvpView().showList(mMenuItemList);
        }
    }

    @Override
    protected void subscribeRequestsOnResume() {
        if (mMenuRequest != null) {
            addSubscription(mMenuRequest.subscribe(getMenuSubscriber()));
        }
    }

    @Override
    protected boolean showCustomLoading() {
        return true;
    }

    @Override
    protected boolean hideCustomLoading() {
        return true;
    }

    /**
     * Load menu with different value if user is authenticated or not.
     *
     * @param  sectionString  sectionString to selected.
     */
    public void loadMenu(String sectionString) {

        final MenuItem.Section selectedSection = MenuItem.Section.toSection(sectionString);
        if(mMenuRequest == null) {

            //Mostro un menu diverso in base se l'utente è loggato oppure no
            //Utente non loggato
            if(mUserRepository.getCachedUser() == null || mUserRepository.getCachedUser().username.isEmpty()) {

                mMenuRequest = mAppRepository.getMenu()
                        .compose(this.<List<MenuItem>>handleDataRequest())
                        .flatMap(new Func1<List<MenuItem>, Observable<List<MenuItem>>>() {
                            @Override
                            public Observable<List<MenuItem>> call(List<MenuItem> menuItemList) {
                                return Observable.from(menuItemList)
                                        .doOnNext(new Action1<MenuItem>() {
                                            @Override
                                            public void call(MenuItem menuItem) {
                                                menuItem.selected = menuItem.section == selectedSection;
                                            }
                                        })
                                        .toList();
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().showList(mMenuItemList);
                            }
                        });

                addSubscription(mMenuRequest.subscribe(getMenuSubscriber()));
            }else{

                //Utente loggato
                mMenuRequest = mAppRepository.getAuthMenu()
                        .compose(this.<List<MenuItem>>handleDataRequest())
                        .flatMap(new Func1<List<MenuItem>, Observable<List<MenuItem>>>() {
                            @Override
                            public Observable<List<MenuItem>> call(List<MenuItem> menuItemList) {
                                return Observable.from(menuItemList)
                                        .doOnNext(new Action1<MenuItem>() {
                                            @Override
                                            public void call(MenuItem menuItem) {
                                                menuItem.selected = menuItem.section == selectedSection;
                                            }
                                        })
                                        .toList();
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().showList(mMenuItemList);
                            }
                        });

                addSubscription(mMenuRequest.subscribe(getMenuSubscriber()));
            }
        }
    }

    private Subscriber<List<MenuItem>> getMenuSubscriber(){
        return new Subscriber<List<MenuItem>>() {
            @Override
            public void onCompleted() {
                mMenuRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mMenuRequest = null;
            }

            @Override
            public void onNext(List<MenuItem> menuItemList) {
                mMenuItemList = menuItemList;
            }
        };
    }

    /**
     * Check actual status of authentication of user.
     *
     * @return      status of authentication of user.
     * @see         boolean
     */
    public boolean isAuth(){
        if(mUserRepository.getCachedUser() != null && mUserRepository.getCachedUser().username != null && !mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

    /**
     * Retrieve from cache the user info.
     *
     * @return      object with user info.
     * @see         UserInfo
     */
    public UserInfo getUserInfo(){
        return mUserRepository.getCachedUser().userInfo;
    }

    /**
     * Return actual item selected from menu.
     *
     * @return      actual menu item selected.
     * @see         MenuItem.Section
     */
    public MenuItem.Section getMenuSelection(){
        return mAppRepository.getSelectMenuItem();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LOGOUT
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Execute logout of user from app. Delete any information from cache.
     *
     * @param  context  context of application
     * @param  mPref    shared preference of app
     */
    public void  logout(Context context, SharedPreferences mPref){

        /*SharedPreferences.Editor editor = mPref.edit();
        editor.putString(context.getString(R.string.preference_lang), Locale.getDefault().getLanguage());
        editor.commit();*/

        mAppRepository.putLang(Locale.getDefault().getLanguage());

        mUserRepository.logoutUser(mPref);

        try {
            getMvpView().logoutUser();
        }catch (NullPointerException e){}
    }

    public void alertLogout(Activity context, final SharedPreferences mPref){
        final CustomDialogClass cdd=new CustomDialogClass(context,
                context.getResources().getString(R.string.general_logout_alert),
                context.getResources().getString(R.string.ok),
                context.getResources().getString(R.string.cancel));
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                logout(null,mPref);
            }
        });
    }
}
