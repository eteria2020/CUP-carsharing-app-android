package it.sharengo.eteria.ui.buyminutes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;


public class BuyMinutesFragment extends BaseMvpFragment<BuyMinutesPresenter> implements BuyMinutesMvpView {

    private static final String TAG = BuyMinutesFragment.class.getSimpleName();

    @BindView(R.id.buyWebView)
    WebView webview;
    private String supportURL = "http://support.sharengo.it/home";

    public static BuyMinutesFragment newInstance() {
        BuyMinutesFragment fragment = new BuyMinutesFragment();
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
        View view = inflater.inflate(R.layout.fragment_buy_minutes, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        try{
            supportURL = getResources().getString(R.string.endpointSupport) + getString(R.string.routeSupport);
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
                        Navigator.launchMapGoogle(BuyMinutesFragment.this,Navigator.REQUEST_MAP_DEFAULT);
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webview.loadUrl(supportURL);

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
