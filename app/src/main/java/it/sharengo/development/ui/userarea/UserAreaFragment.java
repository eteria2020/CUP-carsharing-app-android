package it.sharengo.development.ui.userarea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;


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
                        Navigator.launchHome(UserAreaFragment.this);
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webview.loadUrl("https://www.sharengo.it/area-utente/mobile");

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
