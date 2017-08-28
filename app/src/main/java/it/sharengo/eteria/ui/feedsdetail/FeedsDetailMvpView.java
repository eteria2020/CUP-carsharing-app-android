package it.sharengo.eteria.ui.feedsdetail;

import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface FeedsDetailMvpView extends MvpView {

    void showInformations(Feed feed);

}
