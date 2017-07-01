package it.sharengo.development.ui.splash;

import it.sharengo.development.ui.base.presenters.MvpView;

public interface SplashMvpView extends MvpView {

        void navigateToHome(String lang);
        void navigateToLogin();
}
