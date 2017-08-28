package it.sharengo.eteria.ui.home;

import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface HomeMvpView extends MvpView {

    void openNotification(int start, int end);
    void openReservationNotification();
    
}
