package it.sharengo.development.ui.profile;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class ProfilePresenter extends BasePresenter<ProfileMvpView> {

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    public ProfilePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);

        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.PROFILE);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



