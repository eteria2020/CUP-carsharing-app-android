package it.sharengo.development.ui.feeds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;


public class FeedsFragment extends BaseMvpFragment<FeedsPresenter> implements FeedsMvpView {

    private static final String TAG = FeedsFragment.class.getSimpleName();

    private FeedsCategoriesAdapter mCategoriesAdapter;
    private FeedsAdapter mFeedsAdapter;

    @BindView(R.id.feedTextView)
    TextView feedTextView;

    @BindView(R.id.categoriesTextView)
    TextView categoriesTextView;

    @BindView(R.id.feedBorderView)
    View feedBorderView;

    @BindView(R.id.categoriesBorderView)
    View categoriesBorderView;

    @BindView(R.id.feedsListView)
    ViewGroup feedsListView;

    @BindView(R.id.feedsCategoriesView)
    ViewGroup feedsCategoriesView;

    @BindView(R.id.categoriesRecyclerView)
    RecyclerView mCategoriesRv;

    @BindView(R.id.feedsRecyclerView)
    RecyclerView mFeedsRv;

    public static FeedsFragment newInstance() {
        FeedsFragment fragment = new FeedsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        mCategoriesAdapter = new FeedsCategoriesAdapter(mCategoriesActionListener, getActivity());
        mFeedsAdapter = new FeedsAdapter(mFeedsActionListener, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //Mostro la lista
        feedsListView.setVisibility(View.VISIBLE);

        //Categorie
        mCategoriesRv.setHasFixedSize(true);
        final GridLayoutManager lm = new GridLayoutManager(mContext, 2);
        lm.setSmoothScrollbarEnabled(false);
        mCategoriesRv.setLayoutManager(lm);
        mCategoriesRv.setAdapter(mCategoriesAdapter);

        //Feed
        mFeedsRv.setHasFixedSize(true);
        final LinearLayoutManager lmFeed = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lmFeed.setSmoothScrollbarEnabled(false);
        mFeedsRv.setLayoutManager(lmFeed);
        mFeedsRv.setAdapter(mFeedsAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.loadCategoriesList(getContext());
    }

    private void showFeedsList(){
        //Stile dei pulsanti
        feedTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.aureolin));
        categoriesTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.manatee));
        feedBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.aureolin));
        categoriesBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.manatee));

        //Mostro la lista
        feedsListView.setVisibility(View.VISIBLE);
        feedsCategoriesView.setVisibility(View.GONE);
    }

    private void showFeedsCategories(){
        //Stile dei pulsanti
        feedTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.manatee));
        categoriesTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.aureolin));
        feedBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.manatee));
        categoriesBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.aureolin));

        //Mostro le categorie
        feedsListView.setVisibility(View.GONE);
        feedsCategoriesView.setVisibility(View.VISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LISTENERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private FeedsCategoriesAdapter.OnItemActionListener mCategoriesActionListener = new FeedsCategoriesAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(FeedCategory feedCategory) {

            if(feedCategory.status.published.equals("1")){

                //TODO
            }else{

                //Alert categoria disabilitata
                final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                        getString(R.string.feeds_categorydisabled_alert),
                        getString(R.string.ok),
                        null);
                cdd.show();
                cdd.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cdd.dismissAlert();
                    }
                });
            }
        }
    };

    private FeedsAdapter.OnItemActionListener mFeedsActionListener = new FeedsAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(Feed feed) {

            //TODO
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.feedButton)
    public void onFeedClick(){
        showFeedsList();
    }

    @OnClick(R.id.categoriesButton)
    public void onCategoriesClick(){
        showFeedsCategories();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showCategoriesList(List<FeedCategory> feedsCategoryList){
        mCategoriesAdapter.setData(feedsCategoryList);
    }

    public void showAllFeedsList(List<Feed> feedsList){
        mFeedsAdapter.setData(feedsList);
    }

}
