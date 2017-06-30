package it.sharengo.development.ui.settingcities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

import static it.sharengo.development.R.id.profileButton;
import static it.sharengo.development.R.id.scoreTextView;
import static it.sharengo.development.R.id.welcomeTextView;


public class SettingsCitiesFragment extends BaseMvpFragment<SettingsCitiesPresenter> implements SettingsCitiesMvpView {

    private static final String TAG = SettingsCitiesFragment.class.getSimpleName();

    private SettingsCitiesAdapter mAdapter;

    @BindView(R.id.citiesRecyclerView)
    RecyclerView mRv;

    public static SettingsCitiesFragment newInstance() {
        SettingsCitiesFragment fragment = new SettingsCitiesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new SettingsCitiesAdapter(mActionListener, getActivity());
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
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showList(List<City> citiesList) {
        mAdapter.setData(citiesList);
    }

}
