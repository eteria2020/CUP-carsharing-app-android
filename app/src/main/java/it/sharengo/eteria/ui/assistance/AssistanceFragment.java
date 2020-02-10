package it.sharengo.eteria.ui.assistance;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.MotionEvent;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.userarea.UserAreaFragment;


public class AssistanceFragment extends BaseMvpFragment<AssistancePresenter> implements AssistanceMvpView {

    private static final String TAG = AssistanceFragment.class.getSimpleName();

    @BindView (R.id.sosButton)
    Button sosButton;

    @BindView (R.id.callAssistanceButton)
    Button callAssistanceButton;

    @BindView (R.id.sosAssistanceText)
    TextView sosAssistanceText;

    @BindDrawable(R.drawable.ic_grey_phone_icon)
    Drawable background;

    @BindView (R.id.mailWebView)
    WebView webview;
    private boolean isLogin;


    public static AssistanceFragment newInstance() {
        AssistanceFragment fragment = new AssistanceFragment();
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
        View view = inflater.inflate(R.layout.fragment_assistance, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        ViewGroup.LayoutParams vc=webview.getLayoutParams();
        vc.height=1;
        webview.setLayoutParams(vc);
        webview.setVisibility(View.GONE);
        initInterface();
        return view;
    }

    /**
     *  Just for italian app disabled callAssistanceButton every morning from 1 to 6
     */
    private void initInterface(){

        if(BuildConfig.FLAVOR.equalsIgnoreCase("prod")){

            //set visible sosButton
            sosButton.setVisibility(View.VISIBLE);
            //get current hour
            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            if ((currentHour > 0) && (currentHour < 7)) {
                callAssistanceButton.setBackground(background);
                callAssistanceButton.setEnabled(false);
                sosAssistanceText.setVisibility(View.VISIBLE);
            }

        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check if device can use telephony module. Answer is show to user by a dialog.
     *
     */

    @OnClick(R.id.mailAssistanceButton)
    public void onAssistanceMailClick(){
        String[] addressDest  = {getString(R.string.endpointSupportEmail)};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, addressDest);
        intent.putExtra(Intent.EXTRA_SUBJECT, mPresenter.getUsername());
        intent.putExtra(Intent.EXTRA_TEXT,"");
        startActivity(Intent.createChooser(intent, "Send Email"));
        /*final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                getString(R.string.assistance_mail_label),
                getString(R.string.assistance_write_action),
                getString(R.string.assistance_cancelmail_action));
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                //showInfo("MAIL");
                ViewGroup.LayoutParams vc=webview.getLayoutParams();
                vc.height=2000;
                webview.setLayoutParams(vc);
                webview.setVisibility(View.VISIBLE);
//-------------------PROVA-----------------------
                isLogin = false;

                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setDomStorageEnabled(true);


                String username = "UTENTE";
                String password = "TEST1";

                String url = "https://www.sharengo.it/user/contatti";
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
        });*/
    }

    private void loadUrl(WebView view, String mobileUrl){
        if (StringUtils.equals(mobileUrl, "https://www.sharengo.it/contatti")) {
            //mobileUrl = mobileUrl + "/mobile";
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
                Navigator.launchMapGoogle(AssistanceFragment.this,Navigator.REQUEST_MAP_DEFAULT);
                getActivity().finish();
            }
        });
    }
//-------------------------FINE PROVA---------------------------

    @OnClick(R.id.sosButton)
    public void onSosClick(){

        //Verifico se il device è supportato per le chiamate telefoniche
        if(isTelephonyEnabled()) {
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    mPresenter.getCallCenterNumber(),
                    getString(R.string.assistance_alertcall_action),
                    getString(R.string.assistance_cancelcall_action));
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPresenter.getCallCenterNumber())));
                }
            });
        }else{
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    getString(R.string.assistance_devicenotenabled_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
        }
    }

    @OnClick(R.id.callAssistanceButton)
    public void onAssistanceClick(){

        //Verifico se il device è supportato per le chiamate telefoniche
        if(isTelephonyEnabled()) {
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    mPresenter.getCallCenterNumber(),
                    getString(R.string.assistance_alertcall_action),
                    getString(R.string.assistance_cancelcall_action));
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPresenter.getCallCenterNumber())));
                }
            });
        }else{
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    getString(R.string.assistance_devicenotenabled_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
        }
    }

    private boolean isTelephonyEnabled(){
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getSimState()==TelephonyManager.SIM_STATE_READY;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
