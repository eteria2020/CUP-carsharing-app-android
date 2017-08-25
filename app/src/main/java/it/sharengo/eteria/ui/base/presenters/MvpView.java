package it.sharengo.development.ui.base.presenters;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface MvpView {

    void showStandardLoading();
    void hideStandardLoading();

    void hideSoftKeyboard();
    void showInfo(String infoMessage);
    void showError();
    void showError(Throwable errorResponse);
    void showError(String errorMessage);
    void showUserError(Throwable e);
}
