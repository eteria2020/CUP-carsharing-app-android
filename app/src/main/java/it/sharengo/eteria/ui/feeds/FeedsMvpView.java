package it.sharengo.eteria.ui.feeds;

import java.util.List;

import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.FeedCategory;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface FeedsMvpView extends MvpView {

    void showCategoriesList(List<FeedCategory> feedCategoryList);
    void showAllFeedsList(List<Feed> feedsList);
    void showEmptyMessage();

}
