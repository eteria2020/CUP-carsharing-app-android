package it.sharengo.eteria.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;


public class SettingsFragment extends BaseMvpFragment<SettingsPresenter> implements SettingsMvpView {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Open setting's city.
     */
    @OnClick(R.id.cityButton)
    public void onCityClick(){
        Navigator.launchSettingsCities(this, false);
        //getActivity().finish();
    }

    /**
     * Open setting's address.
     */
    @OnClick(R.id.addressButton)
    public void onAddressClick(){
        Navigator.launchSettingsAddresses(this);
        //getActivity().finish();
    }

    /**
     * Open setting's language.
     */
    @OnClick(R.id.langButton)
    public void onLangClick(){
        Navigator.launchSettingsLang(this);
        //getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}