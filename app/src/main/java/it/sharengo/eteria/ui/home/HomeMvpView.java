package it.sharengo.development.ui.home;

import it.sharengo.development.ui.base.presenters.MvpView;

public interface HomeMvpView extends MvpView {

    void openNotification(int start, int end);
    void openReservationNotification();
    
}
