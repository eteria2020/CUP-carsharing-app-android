package it.sharengo.development.ui.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import it.sharengo.development.R;
import it.sharengo.development.data.common.ErrorResponse;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.home.HomeFragment;
import it.sharengo.development.ui.map.MapFragment;

import static android.content.Context.MODE_PRIVATE;
import static it.sharengo.development.data.common.ErrorResponse.ErrorType.HTTP;


public class LoginFragment extends BaseMvpFragment<LoginPresenter> implements LoginMvpView {

    private static final String TAG = LoginFragment.class.getSimpleName();

    public static final String ARG_TYPE = "ARG_TYPE";

    private int type = 0;

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


    public static LoginFragment newInstance(int type) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //TODO
        emailEditText.setText("francesco.galatro@gmail.com");
        passwordEditText.setText("AppTest2017");

        return view;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.loginButton)
    public void onLoginClick(){
        hideSoftKeyboard();
        checkFormLogin();
    }

    @OnClick(R.id.continueButton)
    public void onContinue(){
        Navigator.launchHome(getActivity());
        getActivity().finish();
    }

    @OnClick(R.id.registerButton)
    public void onRegister(){
        Navigator.launchSlideshow(this);
        getActivity().finish();
    }

    @OnClick(R.id.forgotButton)
    public void onRecoveryPassword(){
        Navigator.launchPasswordRecovery(this);
        getActivity().finish();
    }

    @OnEditorAction(R.id.passwordEditText)
    public boolean OnEditorAction(){
        onLoginClick();
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showLoginError(Throwable throwable){

        if(!(throwable instanceof ErrorResponse)) {
            mPresenter.showError(throwable);
            return;
        }

        ErrorResponse errorResponse = (ErrorResponse) throwable;


        if(errorResponse.errorType.equals(HTTP)){

            int errorString = 0;

            /*
                User INESISTENTE

                errorCode: : 404
                erroreMessage: : {"status":404,"code":"not_found"}

                User ESISTENTE, Password ERRATA

                errorCode: : 406
                erroreMessage: : {"msg":"invalid_credentials"}

                User ESISTENTE, Password CORRETTA ma il profilo è disattivato in quanto la patente non è valido

                errorCode: : 405
                erroreMessage: : {"msg":"user_disabled"}
            */

            switch (errorResponse.httpStatus){
                case 404:
                    errorString = R.string.login_wrongemail_alert;
                    break;
                case 405:
                    errorString = R.string.login_userdisabled_alert;
                    break;
                case 406:
                    errorString = R.string.login_wrongpassword_alert;
                    break;
                default:
                    errorString = R.string.login_userdisabled_alert;
                    break;
            }

            //Mostro un messaggio di errore
            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                    getString(errorString),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });

        }else{
            mPresenter.showError(throwable);
        }
    }

    public void showEnabledError(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.login_enabled_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });
    }

    public void loginCompleted(String username, String password){

        //Salvo nelle preferenze e prelevo i dati dell'utente
        mPresenter.saveCredentials(username, password, getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));

    }

    public void navigateTo(){

        switch (type){
            case Navigator.REQUEST_LOGIN_START:
                Navigator.launchHome(getActivity());
                getActivity().finish();
                break;
            case Navigator.REQUEST_LOGIN_PROFILE:
                Navigator.launchProfile(LoginFragment.this);
                getActivity().finish();
                break;
            case Navigator.REQUEST_LOGIN_MAPS:
                Navigator.launchMapGoogle(LoginFragment.this, Navigator.REQUEST_MAP_DEFAULT);
                getActivity().finish();
                break;
            case Navigator.REQUEST_LOGIN_FEEDS:
                //Verifico se la città preferita è stata impostata
                SharedPreferences mPref = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), MODE_PRIVATE);
                if (mPref.getString(getActivity().getString(R.string.preference_citiesfavourites), "").isEmpty()) {
                    //Apro i settings
                    Navigator.launchSettingsCities(LoginFragment.this, true);
                    getActivity().finish();
                } else {
                    //Apro i feed
                    Navigator.launchFeeds(LoginFragment.this, "0", "");
                    getActivity().finish();
                }
                break;
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Login
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkFormLogin(){

        String email        = emailEditText.getText().toString().trim();
        String password     = passwordEditText.getText().toString().trim();

        //Verifico che tutti i campi siano stati compilati
        if(email.isEmpty() || password.isEmpty()){

            //Mostro un messaggio di errore
            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                    getString(R.string.login_emptyfields_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
            return;
        }

        //Continuo con il login
        mPresenter.login(email, password);

    }

}
