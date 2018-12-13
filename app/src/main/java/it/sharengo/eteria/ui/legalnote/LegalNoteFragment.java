package it.sharengo.eteria.ui.legalnote;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.base.webview.MyWebView;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class LegalNoteFragment extends BaseMvpFragment<LegalNotePresenter> implements LegalNoteMvpView {

    private static final String TAG = LegalNoteFragment.class.getSimpleName();

    public static final String ARG_TYPE = "ARG_TYPE";

    private String fileNameURL="";
    private String legalNoteURl =" https://site.sharengo.it/note-legali-app/?app";

    private boolean isLogin;

    //@BindView(R.id.userareaWebView)
    MyWebView webview;

    public static LegalNoteFragment newInstance(LegalNoteActivity.InnerRoute route) {
        LegalNoteFragment fragment = new LegalNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, route.name());
        fragment.setArguments(args);
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
        webview = (MyWebView) view.findViewById(R.id.userareaWebView);

        try{
            if( Locale.getDefault().getLanguage().equals("it") )
                legalNoteURl = getResources().getString(R.string.endpointSiteWP) + getString(R.string.routeLegalNote);
            else
                 legalNoteURl = getResources().getString(R.string.endpointSiteWP) +  getString(R.string.routeLegalNoteEN);

        }catch (Exception e) {
            Log.e(TAG, "onCreateView: Exception", e);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


      //  ((BaseActivity) getActivity()).showLoadingChronology();

        webview.setIgnoreUrls(legalNoteURl);
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

        webview.setWebChromeClient(new WebChromeClient());

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   // view.evaluateJavascript("alert(\"mhh\");", null);
                } else {
                   // view.loadUrl("javascript:alert(\"mhh\");");
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    hideWebView();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(LegalNoteFragment.this.legalNoteURl.equalsIgnoreCase(url)) {

                    webview.loadUrl(url);
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(LegalNoteFragment.this.legalNoteURl.equalsIgnoreCase(request.getUrl().toString())) {

                    webview.loadUrl( request.getUrl().toString());
                }
                return true;
            }


        });
        Log.d("link: ", legalNoteURl+fileNameURL);

        webview.loadUrl(legalNoteURl);
        webview.setVisibility(View.VISIBLE);

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
                Navigator.launchMapGoogle(LegalNoteFragment.this,Navigator.REQUEST_MAP_DEFAULT);
                getActivity().finish();
            }
        });
    }



}
