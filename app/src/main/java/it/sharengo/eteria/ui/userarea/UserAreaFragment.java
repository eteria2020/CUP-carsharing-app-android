package it.sharengo.eteria.ui.userarea;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.base.webview.MyWebView;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.signup.SignupFragment;


public class UserAreaFragment extends BaseMvpFragment<UserAreaPresenter> implements UserAreaMvpView {

    private static final String TAG = UserAreaFragment.class.getSimpleName();

    public static final String ARG_TYPE = "ARG_TYPE";

    private String baseURL="";
    private String loginURL = "https://www.sharengo.it/user/login";
    private String userAreaURLMobile = "https://www.sharengo.it/area-utente/mobile";
    private String userAreaURL = "https://www.sharengo.it/area-utente";

    private boolean isLogin;


    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    //@BindView(R.id.userareaWebView)
    MyWebView webview;

    public static UserAreaFragment newInstance(UserAreaActivity.InnerRoute route) {
        UserAreaFragment fragment = new UserAreaFragment();
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

        if(getArguments() != null){
            UserAreaActivity.InnerRoute route = UserAreaActivity.InnerRoute.valueOf(getArguments().getString(ARG_TYPE));

            switch (route) {
                case HOME:
                    baseURL = "";
                    break;
                case PAYMENTS:
                    baseURL = "/dati-pagamento";
                    break;
                case DRIVER_LICENSE:
                    baseURL = "/patente";
                    break;
                case RATES:
                    baseURL = "/tariffe";
                    break;
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_area, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        webview = (MyWebView) view.findViewById(R.id.userareaWebView);

        try{
            loginURL = getResources().getString(R.string.endpointSite) + getString(R.string.routeLogin);
            userAreaURLMobile = getResources().getString(R.string.endpointSite) + getString(R.string.routeUserArea) + getString(R.string.routeMobile);
            userAreaURL = getResources().getString(R.string.endpointSite) + getString(R.string.routeUserArea);

        }catch (Exception e) {
            Log.e(TAG, "onCreateView: Exception", e);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((BaseActivity) getActivity()).showLoadingChronology();

        webview.setIgnoreUrls(userAreaURLMobile);
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

        String postData = null;
        try {
            postData = "identity=" + URLEncoder.encode(username, "UTF-8") + "&credential=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webview.setWebChromeClient(new MyChromeClient());

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
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
        webview.postUrl(loginURL,postData.getBytes());

    }

    private void loadUrl(WebView view, String mobileUrl){
        if (StringUtils.equals(mobileUrl, userAreaURL)) {
            mobileUrl = mobileUrl + baseURL+"/mobile?lang=" + mPresenter.getLang();
            Log.d("BOMB","Webview is loading: "+mobileUrl);
            view.loadUrl(mobileUrl);
        }
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
                Navigator.launchMapGoogle(UserAreaFragment.this,Navigator.REQUEST_MAP_DEFAULT);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    private class MyChromeClient extends WebChromeClient{
        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
        }
        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
        public void openFileChooser(ValueCallback uploadMsg, String acceptType){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
           startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FCR);
        }
        //For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), UserAreaFragment.FCR);
        }
        //For Android 5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams){
            if(mUMA != null){
                mUMA.onReceiveValue(null);
            }
            mUMA = filePathCallback;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getContext().getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCM);
                }catch(IOException ex){
                    Log.e("BOMB", "Image file creation failed", ex);
                }
                if(photoFile != null){
                    mCM = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }else{
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            Intent[] intentArray;
            if(takePictureIntent != null){
                intentArray = new Intent[]{takePictureIntent};
            }else{
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, FCR);
            return true;
        }
    }

    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

}
