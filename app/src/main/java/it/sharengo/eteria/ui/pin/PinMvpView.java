package it.sharengo.eteria.ui.pin;

import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface PinMvpView extends MvpView {

    void showPinInfo(UserInfo userInfo);

}
