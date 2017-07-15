package it.sharengo.development.ui.feeds;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface FeedsMvpView extends MvpView {

    void showCategoriesList(List<FeedCategory> feedCategoryList);

}
