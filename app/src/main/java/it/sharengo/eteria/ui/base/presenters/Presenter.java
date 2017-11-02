package it.sharengo.eteria.ui.base.presenters;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {

    void attachView(V mvpView, boolean recreation);
    void detachView();
    boolean isViewAttached();
    
}