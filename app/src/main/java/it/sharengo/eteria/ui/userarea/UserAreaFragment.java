package it.sharengo.eteria.ui.userarea;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.signup.SignupFragment;


public class UserAreaFragment extends BaseMvpFragment<UserAreaPresenter> implements UserAreaMvpView {

    private static final String TAG = UserAreaFragment.class.getSimpleName();

    private boolean isLogin;

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


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((BaseActivity) getActivity()).showLoadingChronology();

        //Pulisco la sessione
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    loadWebView();
                }
            });
        }else{
            cookieManager.removeSessionCookie();
            loadWebView();
        }

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
    private void loadWebView(){

        isLogin = false;

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);


        String username = mPresenter.getUserInfo().username;
        String password = mPresenter.getUserInfo().password;

        String url = "https://www.sharengo.it/user/login";
        String postData = null;
        try {
            postData = "identity=" + URLEncoder.encode(username, "UTF-8") + "&credential=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    hideWebView();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(getActivity() != null) {

                    loadUrl(view, url);
                }
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(getActivity() != null) {
                    String mobileUrl = request.getUrl().toString();
                    loadUrl(view, mobileUrl);
                }
                return false;
            }
        });
        webview.postUrl(url,postData.getBytes());

    }

    private void loadUrl(WebView view, String mobileUrl){
        if (StringUtils.equals(mobileUrl, "https://www.sharengo.it/area-utente")) {
            mobileUrl = mobileUrl + "/mobile";
        }
        view.loadUrl(mobileUrl);
        ((BaseActivity) getActivity()).hideLoadingChronology();
    }

    private void hideWebView(){
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
