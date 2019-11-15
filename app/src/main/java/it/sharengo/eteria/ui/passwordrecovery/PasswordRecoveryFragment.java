package it.sharengo.eteria.ui.passwordrecovery;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class PasswordRecoveryFragment extends BaseMvpFragment<PasswordRecoveryPresenter> implements PasswordRecoveryMvpView {

    private static final String TAG = PasswordRecoveryFragment.class.getSimpleName();

    @BindView(R.id.passwordRecoveryWebView)
    WebView webview;
    private String passwordRecoveryURL = "https://www.sharengo.it/forgot-password/mobile";

    public static PasswordRecoveryFragment newInstance() {
        PasswordRecoveryFragment fragment = new PasswordRecoveryFragment();
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
        View view = inflater.inflate(R.layout.fragment_password_recovery, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        try{
            passwordRecoveryURL = getResources().getString(R.string.endpointSite) + getString(R.string.routeForgotPassword) + "?lang=" + mPresenter.getLang();
        }catch (Exception e) {
            Log.e(TAG, "onCreateView: Exception", e);
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadWebView();
    }

    private void loadWebView(){

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if(getActivity() != null)
                    getActivity().setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                            getString(R.string.error_msg_network_general),
                            getString(R.string.ok),
                            null);
                    cdd.show();
                    cdd.yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cdd.dismissAlert();
                            Navigator.launchLogin(PasswordRecoveryFragment.this, Navigator.REQUEST_LOGIN_START);
                            getActivity().finish();
                        }
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                StringBuilder sb = new StringBuilder();
                sb.append("document.getElementsByTagName('form')[0].onsubmit = function () {");
                sb.append("var objAccount;var str = '';");
                sb.append("var inputs = document.getElementsByTagName('input');");
                sb.append("for (var i = 0; i < inputs.length; i++) {");
                sb.append("if (inputs[i].name.toLowerCase() === 'email') {objAccount = inputs[i];}");
                sb.append("}");
                sb.append("if (objAccount != null) {str += objAccount.value;}");
                sb.append("window.MYOBJECT.processHTML(str);");
                sb.append("return true;");
                sb.append("};");

                view.loadUrl("javascript:" + sb.toString());
            }
        });

        webview.loadUrl(passwordRecoveryURL);

    }

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        public void processHTML(String html)
        {
            //called by javascript
            if(!html.isEmpty()){
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        if(getActivity() != null) {
                            Navigator.launchLogin(PasswordRecoveryFragment.this, Navigator.REQUEST_LOGIN_START);
                            getActivity().finish();
                        }
                    }
                }, 3000);
            }
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


}
