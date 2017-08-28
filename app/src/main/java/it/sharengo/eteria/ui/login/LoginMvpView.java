package it.sharengo.eteria.ui.login;

import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface LoginMvpView extends MvpView {

    void showLoginError(Throwable e);
    void showEnabledError();
    void loginCompleted(String username, String password, UserInfo mCachedUser);
    void navigateTo();
}
