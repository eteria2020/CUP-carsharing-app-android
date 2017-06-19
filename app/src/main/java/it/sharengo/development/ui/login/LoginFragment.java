package it.sharengo.development.ui.login;

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
import it.sharengo.development.R;
import it.sharengo.development.data.common.ErrorResponse;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;

import static android.content.Context.MODE_PRIVATE;
import static it.sharengo.development.data.common.ErrorResponse.ErrorType.HTTP;


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

            switch (errorResponse.httpStatus){
                case 404:
                    errorString = R.string.login_wrongemail_alert;
                    break;
                case 406:
                    errorString = R.string.login_wrongpassword_alert;
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

    public void loginCompleted(String username, String password){

        //Salvo nelle preferenze
        mPresenter.saveCredentials(username, password, getActivity().getPreferences(MODE_PRIVATE));

        Navigator.launchHome(getActivity());
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
