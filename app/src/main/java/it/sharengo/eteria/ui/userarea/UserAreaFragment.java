package it.sharengo.eteria.ui.userarea;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class UserAreaFragment extends BaseMvpFragment<UserAreaPresenter> implements UserAreaMvpView {

    private static final String TAG = UserAreaFragment.class.getSimpleName();

    @BindView(R.id.userareaWebView)
    WebView webview;

    public static UserAreaFragment newInstance() {
        UserAreaFragment fragment = new UserAreaFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_area, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        loadWebView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((BaseActivity) getActivity()).showLoadingChronology();
        //mPresenter.checkUserLogin(getActivity());
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
    @Override
    public void loadWebView(){



        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        //webview.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");

        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.setVisibility(View.GONE);
                hideWebView();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String username = mPresenter.getUserInfo().username;
                String password = mPresenter.getUserInfo().password;
                view.loadUrl("javascript:document.getElementsByName('identity')[0].value = '" + username + "';javascript:document.getElementsByName('credential')[0].value = '" + password + "';javascript:document.getElementsByTagName('form')[0].submit();");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).hideLoadingChronology();
                    }
                }, 5000);

            }
        });

        webview.loadUrl("https://www.sharengo.it/area-utente/mobile");

    }

    @Override
    public void hideWebView(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.error_msg_network_general),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                Navigator.launchHome(UserAreaFragment.this);
                getActivity().finish();
            }
        });
    }



}
