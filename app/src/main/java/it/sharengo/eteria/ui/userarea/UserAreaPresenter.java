package it.sharengo.eteria.ui.userarea;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.User;
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class UserAreaPresenter extends BasePresenter<UserAreaMvpView> {

    private static final String TAG = UserAreaPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public UserAreaPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.PROFILE);
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


}



