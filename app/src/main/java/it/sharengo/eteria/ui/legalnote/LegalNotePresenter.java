package it.sharengo.eteria.ui.legalnote;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.User;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class LegalNotePresenter extends BasePresenter<LegalNoteMvpView> {

    private static final String TAG = LegalNotePresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;



    public LegalNotePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.LEGAL_NOTE);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public User getUserInfo(){
        return mUserRepository.getCachedUser();
    }

    public String getLang(){
        return mAppRepository.getLang();
    }


}



