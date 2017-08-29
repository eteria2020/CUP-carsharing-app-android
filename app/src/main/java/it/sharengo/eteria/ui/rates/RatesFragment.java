package it.sharengo.eteria.ui.rates;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.UserInfo;
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

    @BindView(R.id.bonusView)
    ViewGroup bonusView;

    @BindView(R.id.bonusTextView)
    TextView bonusTextView;

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
        baseTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_base_label), 0.28)));

        //Tariffa oraria
        hourTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_hour_label), 12f)));

        //Tariffa giornaliera
        dayTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_day_label), 50f)));

        //Tariffa di prenotazione
        bookingTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_booking_label), 0f)));

        //Iscriviti subito
        if(mPresenter.isAuth()){
            ratesSignupButton.setVisibility(View.GONE);
        }else{
            ratesSignupButton.setVisibility(View.VISIBLE);
        }

        //Bonus
        if(mPresenter.isAuth()){
            bonusView.setVisibility(View.VISIBLE);
        }else{
            bonusView.setVisibility(View.GONE);
        }

        //Tariffe
        if(mPresenter.isAuth())
            mPresenter.getRatesInfo();
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
    public void showRatesInfo(UserInfo userInfo){

        float discount_rate = userInfo.discount_rate;

        //Tariffa base
        float baseRates = (float) (0.28 - (0.28 * discount_rate/100));
        baseTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_base_label), baseRates)));

        //Tariffa oraria
        float hourRates = (float) (12.00 - (12.00 * discount_rate/100));
        hourTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_hour_label), hourRates)));

        //Tariffa giornaliera
        float dayRates = (float) (50.00 - (50.00 * discount_rate/100));
        /*float result = x - (int)x;
        if (result != 0)
        {
            //If the value of `result` is not equal to zero, then, you have a decimal portion which is not equal to 0.
        }*/
        dayTextView.setText(Html.fromHtml(String.format(getString(R.string.rates_day_label), dayRates)));

        /*
        * TARIFFA MINUTO = 0,28
            0,28 - (0,28 X 20/100) = 0,22

            TARIFFA ORARIA = 12,00
            12,00 - (12,00 X 20/100) = 9,60.

            TARIFFA GIORNALIERA = 50,00
            50,00 - (50,00 X 20/100) = 40,00.
        *
        * */

        //Bonus
        bonusTextView.setText(String.format(getString(R.string.rates_bonusmin_label), ""+userInfo.bonus));
    }


}
