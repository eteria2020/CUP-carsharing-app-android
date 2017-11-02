package it.sharengo.eteria.ui.feeds;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.FeedCategory;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;

import static android.content.Context.MODE_PRIVATE;


public class FeedsFragment extends BaseMvpFragment<FeedsPresenter> implements FeedsMvpView {

    private static final String TAG = FeedsFragment.class.getSimpleName();
    private static final int mRequestPermission = 1;

    private FeedsCategoriesAdapter mCategoriesAdapter;
    private FeedsAdapter mFeedsAdapter;

    private boolean isEmpty;
    private int tabSelected;

    public static final String ARG_CATEGORY = "ARG_CATEGORY";
    public static final String ARG_CATEGORY_NAME = "ARG_CATEGORY_NAME";

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

    @BindView(R.id.aroundMeButton)
    Button aroundMeButton;

    @BindView(R.id.feedsEmptyView)
    ViewGroup feedsEmptyView;

    @BindView(R.id.feedsEmptyTextView)
    TextView feedsEmptyTextView;

    @BindView(R.id.feedTabButton)
    ViewGroup feedTabButton;

    @BindView(R.id.headerView)
    ViewGroup headerView;

    @BindView(R.id.feedHeaderTextView)
    TextView feedHeaderTextView;

    public static FeedsFragment newInstance(String mCategoryId, String mCategoryName) {
        FeedsFragment fragment = new FeedsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, mCategoryId);
        args.putString(ARG_CATEGORY_NAME, mCategoryName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            mPresenter.category_id = getArguments().getString(ARG_CATEGORY);
            mPresenter.category_name = getArguments().getString(ARG_CATEGORY_NAME);
        }

        mCategoriesAdapter = new FeedsCategoriesAdapter(mCategoriesActionListener, getActivity());
        mFeedsAdapter = new FeedsAdapter(mFeedsActionListener, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        isEmpty = false;

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

        //Se è stata selezionata una categoria, nascondo i tab
        if(!mPresenter.category_id.equals("0")){
            feedTabButton.setVisibility(View.GONE);
            aroundMeButton.setVisibility(View.GONE);

            headerView.setVisibility(View.VISIBLE);
            feedHeaderTextView.setText(mPresenter.category_name);
        }

        tabSelected = 0;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Preference
        SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        //Setto la città in uso
        mPresenter.city_id = mPref.getString(getString(R.string.preference_citiesfavourites),"0");

        //Carico le info
        mPresenter.loadCategoriesList(getContext());
    }

    private void showFeedsList(){
        //Stile dei pulsanti
        feedTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.aureolin));
        categoriesTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.manatee));
        feedBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.aureolin));
        categoriesBorderView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.manatee));

        //Mostro la lista
        if(isEmpty){
            aroundMeButton.setVisibility(View.GONE);
            feedsEmptyView.setVisibility(View.VISIBLE);
        }else{
            aroundMeButton.setVisibility(View.VISIBLE);
            feedsListView.setVisibility(View.VISIBLE);
            feedsEmptyView.setVisibility(View.GONE);
        }

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
        feedsEmptyView.setVisibility(View.GONE);
        feedsCategoriesView.setVisibility(View.VISIBLE);

    }


    private void checkMapPermission(){

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setMessage(getActivity().getString(R.string.msg_rational_location_permission))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissions, mRequestPermission);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();

            } else {
                requestPermissions(permissions, mRequestPermission);
            }
        }else{
            launchMap();
        }
    }

    private void launchMap(){
        getActivity().finish();
        Navigator.launchMapGoogle(this, Navigator.REQUEST_MAP_FEEDS);
    }

    /**
     * Launch map only if permission set true. Invoke check if set to false.
     *
     * @param  requestCode  int of requestCode for set permission
     * @param  permissions  array of permission
     * @param  grantResults array of permission status
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case mRequestPermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    launchMap();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    checkMapPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

                Navigator.launchFeeds(FeedsFragment.this, feedCategory.id, feedCategory.name);
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

            Navigator.launchFeedsDetail(FeedsFragment.this, feed);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show list of feed available.
     */
    @OnClick(R.id.feedButton)
    public void onFeedClick(){
        tabSelected = 0;
        showFeedsList();
    }

    /**
     * Show feed categories available.
     */
    @OnClick(R.id.categoriesButton)
    public void onCategoriesClick(){
        tabSelected = 1;
        showFeedsCategories();
    }

    /**
     * Go Back section.
     */
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        getActivity().finish();
    }

    /**
     * Check map permission.
     */
    @OnClick(R.id.aroundMeButton)
    public void onAroungMeClick(){
        checkMapPermission();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show list of feeds category available.
     *
     * @param  feedsCategoryList  list of feeds category
     */
    public void showCategoriesList(List<FeedCategory> feedsCategoryList){
        mCategoriesAdapter.setData(feedsCategoryList);
    }

    /**
     * Show list all type of feeds.
     *
     * @param  feedsList  list of feeds
     */
    public void showAllFeedsList(List<Feed> feedsList){
        isEmpty = false;
        mFeedsAdapter.setData(feedsList);
    }

    /**
     * Show dialog if not present any feed for this city.
     */
    public void showEmptyMessage(){

        isEmpty = true;

        aroundMeButton.setVisibility(View.GONE);
        if(tabSelected == 0) feedsEmptyView.setVisibility(View.VISIBLE);

        if(mPresenter.category_id.equals("0")){
            feedsEmptyTextView.setText(getString(R.string.feeds_empty_label));
        }else{
            feedsEmptyTextView.setText(getString(R.string.feeds_emptycategory_label));
        }
    }

}