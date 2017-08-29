package it.sharengo.eteria.ui.rates;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.utils.StringsUtils;


public class RatesFragment extends BaseMvpFragment<RatesPresenter> implements RatesMvpView {

    private static final String TAG = RatesFragment.class.getSimpleName();

    @BindView(R.id.ratesTitleTextView)
    TextView ratesTitleTextView;

    @BindView(R.id.ratesH1TextView)
    TextView ratesH1TextView;

    @BindView(R.id.baseTextView)
    TextView baseTextView;

    @BindView(R.id.hourTextView)
    TextView hourTextView;

    @BindView(R.id.dayTextView)
    TextView dayTextView;

    @BindView(R.id.bookingTextView)
    TextView bookingTextView;

    @BindView(R.id.ratesSignupButton)
    Button ratesSignupButton;

    public static RatesFragment newInstance() {
        RatesFragment fragment = new RatesFragment();
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
        View view = inflater.inflate(R.layout.fragment_rates, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initInterface();

        return view;
    }

    private void initInterface(){

        //Titolo
        if(mPresenter.isAuth()){
            ratesTitleTextView.setText(getString(R.string.rates_titlenoauth_label));
        }else{
            ratesTitleTextView.setText(getString(R.string.rates_titleauth_label));
        }

        //H1
        if(mPresenter.isAuth()){
            ratesH1TextView.setText(getString(R.string.rates_yourrates_label));
        }else{
            ratesH1TextView.setText(getString(R.string.rates_rates_label));
        }

        //Tariffa base
        baseTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_base_label), "")));

        //Tariffa oraria
        hourTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_hour_label), "")));

        //Tariffa giornaliera
        dayTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_day_label), "")));

        //Tariffa di prenotazione
        bookingTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_booking_label), "")));

        //Iscriviti subito
        if(mPresenter.isAuth()){
            ratesSignupButton.setVisibility(View.GONE);
        }else{
            ratesSignupButton.setVisibility(View.VISIBLE);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.ratesSignupButton)
    public void onSignUp(){
        Navigator.launchSignup(this);
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
