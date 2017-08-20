package it.sharengo.development.ui.base.map;


import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.ui.base.presenters.Presenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onResume() and onPause(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public abstract class BaseMapPresenter<T extends MvpMapView> extends BasePresenter<T> implements Presenter<T> {

    private final UserRepository mUserRepository;

    public BaseMapPresenter(SchedulerProvider schedulerProvider, UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mUserRepository = userRepository;
    }
}

