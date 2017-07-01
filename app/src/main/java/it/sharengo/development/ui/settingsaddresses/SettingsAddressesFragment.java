package it.sharengo.development.ui.settingsaddresses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class SettingsAddressesFragment extends BaseMvpFragment<SettingsAddressesPresenter> implements SettingsAddressesMvpView {

    private static final String TAG = SettingsAddressesFragment.class.getSimpleName();

    public static SettingsAddressesFragment newInstance() {
        SettingsAddressesFragment fragment = new SettingsAddressesFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_addresses, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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


}
