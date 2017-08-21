package it.sharengo.development.ui.settingslang;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class SettingsLangFragment extends BaseMvpFragment<SettingsLangPresenter> implements SettingsLangMvpView {

    private static final String TAG = SettingsLangFragment.class.getSimpleName();

    @BindView(R.id.itCheckImageView)
    ImageView itCheckImageView;

    @BindView(R.id.enCheckImageView)
    ImageView enCheckImageView;

    public static SettingsLangFragment newInstance() {
        SettingsLangFragment fragment = new SettingsLangFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings_lang, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.loadData(getContext());
    }

    /**
     * Show list by language's setting.
     *
     * @param  lang  lang tapped
     */
    public void showList(String lang){
        if(lang.equals("it")){
            itCheckImageView.setVisibility(View.VISIBLE);
            enCheckImageView.setVisibility(View.GONE);
        }else if(lang.equals("en")){
            itCheckImageView.setVisibility(View.GONE);
            enCheckImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Reload language's setting view.
     */
    public void reloadApp(){
        Navigator.launchSettingsLang(this);
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Back previous view.
     */
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        Navigator.launchSettings(this);
        getActivity().finish();
    }

    /**
     * Set it language.
     */
    @OnClick(R.id.itButton)
    public void onITClick(){
        mPresenter.setLang(getContext(), "it");
    }

    /**
     * Set en language.
     */
    @OnClick(R.id.enButton)
    public void onENClick(){
        mPresenter.setLang(getContext(), "en");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
