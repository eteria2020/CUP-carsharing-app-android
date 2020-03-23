package it.sharengo.eteria.ui.legalnote;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import butterknife.ButterKnife;
import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.base.webview.MyWebView;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class LegalNoteFragment extends BaseMvpFragment<LegalNotePresenter> implements LegalNoteMvpView {

    private static final String TAG = LegalNoteFragment.class.getSimpleName();

    public static final String ARG_TYPE = "ARG_TYPE";

    private String fileNameURL="";
    private String legalNoteURL =" https://site.sharengo.it/note-legali-app/?app";

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

            if( mPresenter.getLang().equals("it") )
                legalNoteURL = getResources().getString(R.string.endpointSiteWP) + getString(R.string.routeLegalNote);
            else
                legalNoteURL = getResources().getString(R.string.endpointSiteWP) +  getString(R.string.routeLegalNoteEN);

            if(BuildConfig.FLAVOR.equalsIgnoreCase("slovakia"))
                legalNoteURL = getResources().getString(R.string.endpointSite) +   getString(R.string.routeLegalNoteSK) ;

            if(BuildConfig.FLAVOR.equalsIgnoreCase("slovenia"))
                legalNoteURL = getResources().getString(R.string.endpointSiteWP) + getString(R.string.routeLegalNoteSI);

            if(BuildConfig.FLAVOR.equalsIgnoreCase("olanda"))
                legalNoteURL = getResources().getString(R.string.endpointSite) +  getString(R.string.routeLegalNoteNL);

        }catch (Exception e) {
            Log.e(TAG, "onCreateView: Exception", e);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


      //  ((BaseActivity) getActivity()).showLoadingChronology();

        webview.setIgnoreUrls(legalNoteURL);
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
        webview.getSettings().setAllowContentAccess(true);

//        webview.getSettings().setDomStorageEnabled(true);

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
                url = needEmbeddedPdf(url);
                if(needEmbeddedPdf(LegalNoteFragment.this.legalNoteURL).equalsIgnoreCase(url)) {

                    view.loadUrl( url);
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = needEmbeddedPdf(request.getUrl().toString());
                if(needEmbeddedPdf(LegalNoteFragment.this.legalNoteURL).equalsIgnoreCase(url)) {

                    view.loadUrl(url);
                }
                return true;
            }

            private String needEmbeddedPdf(@NonNull String url){

                if(url.split("[?]")[0].endsWith(".pdf")){
                    url = "https://docs.google.com/gview?url="+url+"&embedded=true";
                }
                if(url.split("[?]")[0].contains("docukey")){
                    url = "https://docs.google.com/gview?url="+url+"&embedded=true";
                }
                return url;
            }

        });
        Log.d("link: ", legalNoteURL +fileNameURL);

        webview.loadUrl(needEmbeddedPdf(legalNoteURL));
        webview.setVisibility(View.VISIBLE);

    }
    private String needEmbeddedPdf(@NonNull String url){

        if(url.split("[?]")[0].endsWith(".pdf")){
            url = "https://docs.google.com/gview?url="+url+"&embedded=true";
        }
        if(url.split("[?]")[0].contains("docukey")){
            url = "https://docs.google.com/gview?url="+url+"&embedded=true";
        }
        return url;
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
