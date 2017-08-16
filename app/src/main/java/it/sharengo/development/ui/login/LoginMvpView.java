package it.sharengo.development.ui.login;

import java.util.List;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.UserInfo;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface LoginMvpView extends MvpView {

    void showLoginError(Throwable e);
    void showEnabledError();
    void loginCompleted(String username, String password, UserInfo mCachedUser);
    void navigateTo();
}
