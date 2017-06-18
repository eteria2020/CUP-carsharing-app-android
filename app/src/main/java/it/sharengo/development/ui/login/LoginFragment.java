package it.sharengo.development.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class LoginFragment extends BaseMvpFragment<LoginPresenter> implements LoginMvpView {

    private static final String TAG = LoginFragment.class.getSimpleName();

    @BindView(R.id.emailEditText)
    EditText emailEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.forgotButton)
    TextView forgotButton;

    @BindView(R.id.loginButton)
    Button loginButton;

    @BindView(R.id.registerButton)
    Button registerButton;

    @BindView(R.id.continueButton)
    TextView continueButton;


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
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


}
