package it.sharengo.eteria.ui.base.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.injection.components.DaggerMvpFragmentComponent;
import it.sharengo.eteria.injection.components.MvpFragmentComponent;
import it.sharengo.eteria.injection.modules.MvpFragmentModule;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.ui.base.presenters.MvpView;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.utils.UiUtils;

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

}
