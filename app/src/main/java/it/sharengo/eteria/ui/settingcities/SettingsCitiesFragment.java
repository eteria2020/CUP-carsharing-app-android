package it.sharengo.development.ui.settingcities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.City;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

import static android.content.Context.MODE_PRIVATE;


public class SettingsCitiesFragment extends BaseMvpFragment<SettingsCitiesPresenter> implements SettingsCitiesMvpView {

    private static final String TAG = SettingsCitiesFragment.class.getSimpleName();

    public static final String ARG_FEEDS = "ARG_FEEDS";

    private boolean feeds;

    private SettingsCitiesAdapter mAdapter;

    @BindView(R.id.citiesRecyclerView)
    RecyclerView mRv;

    @BindView(R.id.backImageView)
    ImageView backImageView;

    public static SettingsCitiesFragment newInstance(boolean feeds) {
        SettingsCitiesFragment fragment = new SettingsCitiesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FEEDS, feeds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new SettingsCitiesAdapter(mActionListener, getActivity());

        if(getArguments() != null){
            feeds = getArguments().getBoolean(ARG_FEEDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_cities, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRv.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lm.setSmoothScrollbarEnabled(false);
        mRv.setLayoutManager(lm);
        mRv.setAdapter(mAdapter);
        //mRv.addItemDecoration(new DividerItemDecoration(mRv.getContext(), lm.getOrientation()));

        //Se provengo dalla Home per i feeds rimuovo il pulsante back
        if(feeds){
            backImageView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.loadList(getContext());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LISTENERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private SettingsCitiesAdapter.OnItemActionListener mActionListener = new SettingsCitiesAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(City city) {

            SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(getString(R.string.preference_citiesfavourites), city.id);
            editor.commit();

            if(feeds) {
                Navigator.launchFeeds(SettingsCitiesFragment.this, "0", "");
                getActivity().finish();
            }else
                mPresenter.loadList(getContext());
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close actual view and open Settings.
     */
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        Navigator.launchSettings(this);
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set the list of city and notify if data is changed.
     *
     * @param  citiesList  list of city
     */
    @Override
    public void showList(List<City> citiesList) {
        mAdapter.setData(citiesList);
        mAdapter.notifyDataSetChanged();
    }

}
