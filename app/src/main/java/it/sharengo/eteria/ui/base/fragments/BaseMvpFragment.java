package it.sharengo.eteria.ui.base.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.common.ErrorResponse;
import it.sharengo.eteria.injection.components.DaggerMvpFragmentComponent;
import it.sharengo.eteria.injection.components.MvpFragmentComponent;
import it.sharengo.eteria.injection.modules.MvpFragmentModule;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.ui.base.presenters.MvpView;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.utils.UiUtils;

import static android.content.Context.MODE_PRIVATE;
import static it.sharengo.eteria.data.common.ErrorResponse.ErrorType.HTTP;

public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseFragment implements MvpView {

    @Inject
    protected T mPresenter;
    @Inject
    PresenterManager mPresenterManager;

    private Bundle mBundle;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBundle = savedInstanceState;
        if(!mPresenter.isViewAttached()) {
            mPresenter.attachView(this, mBundle != null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mPresenter.isViewAttached()) {
            mPresenter.attachView(this, mBundle != null);
        }

        if(mPresenter.isAuth())
            mPresenter.getUpdateUser(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.detachView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPresenterManager==null) {
            return;
        }
        mPresenterManager.savePresenter(mPresenter, outState);
    }

    public MvpFragmentComponent getMvpFragmentComponent(Bundle bundle){
        return  DaggerMvpFragmentComponent.builder()
                .mvpFragmentModule(new MvpFragmentModule(bundle))
                .applicationComponent(App.get(getActivity()).getComponent())
                .build();
    }

    @Override
    public void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showStandardLoading() {
        ((BaseActivity) getActivity()).showLoading();
    }

    @Override
    public void hideStandardLoading() {
        ((BaseActivity) getActivity()).hideLoading();
    }

    @Override
    public void showInfo(String infoMessage) {
        UiUtils.showInfoMsg(getActivity(), infoMessage);
    }

    @Override
    public void showError() {
        UiUtils.showGenericErrorMsg(getActivity());
    }

    @Override
    public void showError(String errorMessage) {
        UiUtils.showErrorMsg(getActivity(), errorMessage);
    }

    @Override
    public void showError(Throwable throwable) {
        UiUtils.showErrorMsg(getActivity(), throwable);
    }

    @Override
    public void showUserError(Throwable throwable){
        /*
        * if response.status == 404, let code = response.code {
        if code == "not_found" {
        LOGOUT!
        if let msg = response.msg {
        if msg == "invalid_credentials" {
        LOGOUT
        if msg == "user_disabled" {
        LOGOUT
        * */

        if(!(throwable instanceof ErrorResponse)) {
            return;
        }

        ErrorResponse errorResponse = (ErrorResponse) throwable;


        if(errorResponse.errorType.equals(HTTP)){//TODO Handle errore responde type -> switch(errorResponde.httpstatus)

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
                    getString(R.string.btn_login),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.logout(getActivity(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
                    cdd.dismissAlert();

                    Navigator.launchLogin(getActivity(),Navigator.REQUEST_LOGIN_MAPS);
                    getActivity().finish();
                }
            });
        }
    }

}
