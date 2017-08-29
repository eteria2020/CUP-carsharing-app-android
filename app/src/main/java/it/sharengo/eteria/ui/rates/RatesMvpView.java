package it.sharengo.eteria.ui.rates;

import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface RatesMvpView extends MvpView {

    void showRatesInfo(UserInfo userInfo);

}
