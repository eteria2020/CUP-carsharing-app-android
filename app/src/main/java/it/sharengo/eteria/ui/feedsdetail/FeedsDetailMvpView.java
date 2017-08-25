package it.sharengo.development.ui.feedsdetail;

import it.sharengo.development.data.models.Feed;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface FeedsDetailMvpView extends MvpView {

    void showInformations(Feed feed);

}
