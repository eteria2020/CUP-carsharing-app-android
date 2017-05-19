package it.sharengo.eteria.ui.menu;

import java.util.List;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MenuPresenter extends BasePresenter<MenuMvpView> {

    private final AppRepository mAppRepository;

    private Observable<List<MenuItem>> mMenuRequest;

    private List<MenuItem> mMenuItemList;

    public MenuPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);
        this.mAppRepository = appRepository;
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
}
