package it.sharengo.development.ui.passwordrecovery;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;


public class PasswordRecoveryFragment extends BaseMvpFragment<PasswordRecoveryPresenter> implements PasswordRecoveryMvpView {

    private static final String TAG = PasswordRecoveryFragment.class.getSimpleName();

    @BindView(R.id.passwordRecoveryWebView)
    WebView webview;

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

        loadWebView();

        return view;
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
                webview.setVisibility(View.GONE);
                final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
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

        webview.loadUrl("https://www.sharengo.it/forgot-password/mobile");

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
                        Navigator.launchLogin(PasswordRecoveryFragment.this, Navigator.REQUEST_LOGIN_START);
                        getActivity().finish();
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
