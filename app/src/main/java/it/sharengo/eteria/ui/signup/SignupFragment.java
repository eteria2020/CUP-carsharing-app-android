package it.sharengo.eteria.ui.signup;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class SignupFragment extends BaseMvpFragment<SignupPresenter> implements SignupMvpView {

    private static final String TAG = SignupFragment.class.getSimpleName();

    @BindView(R.id.signupWebView)
    WebView webview;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        loadWebView();

        return view;
    }

    private void loadWebView(){

        webview.getSettings().setJavaScriptEnabled(true);

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
                //showError(getString(R.string.error_generic_msg));

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
                            Navigator.launchSlideshow(SignupFragment.this);
                            getActivity().finish();
                        }
                    });
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("http://www.sharengo.it/signup-3/mobile")) {
                    loadLogin();
                }
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                if (url.equals("http://www.sharengo.it/signup-3/mobile")) {
                    loadLogin();
                }
                return false;
            }
        });

        webview.loadUrl("http://www.sharengo.it/signup/mobile");

    }

    private void loadLogin(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Navigator.launchLogin(SignupFragment.this, Navigator.REQUEST_LOGIN_START);
                getActivity().finish();

            }
        }, 4000);
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