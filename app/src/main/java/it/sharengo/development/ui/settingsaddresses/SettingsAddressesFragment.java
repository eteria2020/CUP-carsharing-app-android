package it.sharengo.development.ui.settingsaddresses;

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
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAddressesFragment extends BaseMvpFragment<SettingsAddressesPresenter> implements SettingsAddressesMvpView {

    private static final String TAG = SettingsAddressesFragment.class.getSimpleName();

    private SettingsAddressesAdapter mAdapter;

    @BindView(R.id.addressLayout)
    ViewGroup addressLayout;

    @BindView(R.id.addressListLayout)
    ViewGroup addressListLayout;

    @BindView(R.id.addressesRecyclerView)
    RecyclerView mRv;

    public static SettingsAddressesFragment newInstance() {
        SettingsAddressesFragment fragment = new SettingsAddressesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new SettingsAddressesAdapter(null, getActivity());

        mPresenter.setData(getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_addresses, container, false);
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

        mPresenter.loadData(getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        Navigator.launchSettings(this);
    }

    @OnClick(R.id.newAddressButton)
    public void onAddressClick(){
        Navigator.launchSettingsAddressesNew(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showEmptyResult(){
        addressLayout.setVisibility(View.VISIBLE);
        addressListLayout.setVisibility(View.GONE);
    }

    public void showList(List<SearchItem> searchItems){
        mAdapter.setData(searchItems);
        addressLayout.setVisibility(View.GONE);
        addressListLayout.setVisibility(View.VISIBLE);
    }

}
