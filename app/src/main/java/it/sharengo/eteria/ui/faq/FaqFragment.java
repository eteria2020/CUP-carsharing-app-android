package it.sharengo.eteria.ui.faq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.base.webview.MyWebView;


public class FaqFragment extends BaseMvpFragment<FaqPresenter> implements FaqMvpView {

    private static final String TAG = FaqFragment.class.getSimpleName();

    //@BindView(R.id.faqWebView)
    MyWebView webview;
    private String supportURL = "http://support.sharengo.it/home";

    public static FaqFragment newInstance() {
        FaqFragment fragment = new FaqFragment();
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
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        webview = (MyWebView) view.findViewById(R.id.faqWebView);

        try{
            supportURL = getResources().getString(R.string.endpointSupport);
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
        webview.getSettings().setDomStorageEnabled(true);
        //webview.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");

        /*webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if(getActivity() != null)
                    getActivity().setProgress(progress * 1000);
            }
        });*/
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                            getString(R.string.error_msg_network_general),
                            getString(R.string.faq_tutorialalert_action),
                            getString(R.string.faq_backalert_action));
                    cdd.show();
                    cdd.yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cdd.dismissAlert();
                            Navigator.launchTutorial(FaqFragment.this);
                            getActivity().overridePendingTransition(R.anim.slide_modal, R.anim.no_anim);
                            getActivity().finish();
                        }
                    });
                    cdd.no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cdd.dismissAlert();
                            getActivity().finish();
                        }
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webview.setIgnoreUrls(supportURL);
        webview.loadUrl(supportURL);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show tutorial view.
     */

    @OnClick(R.id.tutorialButton)
    public void onTutorialClick(){
        Navigator.launchTutorial(this);
        getActivity().overridePendingTransition(R.anim.slide_modal, R.anim.no_anim);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



}
