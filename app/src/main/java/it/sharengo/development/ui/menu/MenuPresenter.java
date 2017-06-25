package it.sharengo.development.ui.menu;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.User;
import it.sharengo.development.data.models.UserInfo;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
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
        super(schedulerProvider);
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

    public void loadMenu(String sectionString) {

        final MenuItem.Section selectedSection = MenuItem.Section.toSection(sectionString);
        if(mMenuRequest == null) {

            //Mostro un menu diverso in base se l'utente è loggato oppure no
            //Utente non loggato
            if(mUserRepository.getCachedUser().username.isEmpty()) {

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

    public boolean isAuth(){
        if(!mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

    public UserInfo getUserInfo(){
        return mUserRepository.getCachedUser().userInfo;
    }

    public MenuItem.Section getMenuSelection(){
        return mAppRepository.getSelectMenuItem();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LOGOUT
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void logout(SharedPreferences mPref){
        mUserRepository.logoutUser(mPref);
        getMvpView().logoutUser();
    }
}
