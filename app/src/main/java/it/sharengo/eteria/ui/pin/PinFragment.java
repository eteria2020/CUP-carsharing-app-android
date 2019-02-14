package it.sharengo.eteria.ui.pin;

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
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;


public class PinFragment extends BaseMvpFragment<PinPresenter> implements PinMvpView {

    private static final String TAG = PinFragment.class.getSimpleName();

    @BindView(R.id.ratesTitleTextView)
    TextView ratesTitleTextView;

    @BindView(R.id.ratesH1TextView)
    TextView ratesH1TextView;

    @BindView(R.id.hourTextView)
    TextView hourTextView;

    @BindView(R.id.bonusTextView)
    TextView bonusTextView;

    public static PinFragment newInstance() {
        PinFragment fragment = new PinFragment();
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
        View view = inflater.inflate(R.layout.fragment_pin , container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initInterface();

        return view;
    }

    private void initInterface(){

        //Titolo
        if(mPresenter.isAuth()){
            ratesTitleTextView.setText(R.string.menu_pin);
        }else{
            ratesTitleTextView.setText(R.string.menu_pin);
        }

        //H1
        if(mPresenter.isAuth()){
            ratesH1TextView.setText(R.string.booking_userpin_label);
        }else{
            ratesH1TextView.setText("");
        }

        bonusTextView.setText(""+(mPresenter.getPinInfo()));

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showPinInfo(UserInfo userInfo){

        //bonusTextView.setText( userInfo.pin);


    }


}
