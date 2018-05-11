package it.sharengo.eteria.ui.shortintro;


import java.util.List;

import it.sharengo.eteria.data.models.Config;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.ConfigRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class ShortIntroPresenter extends BasePresenter<ShortIntroMvpView> {

    private static final String TAG = ShortIntroPresenter.class.getSimpleName();

    private final UserRepository mUserRepository;
    private final ConfigRepository mConfigRepository;

    public ShortIntroPresenter(SchedulerProvider schedulerProvider, UserRepository userRepository, ConfigRepository configRepository) {
        super(schedulerProvider, userRepository);

        this.mUserRepository = userRepository;
        this.mConfigRepository = configRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

        mConfigRepository.updateConfig();

    }

}



