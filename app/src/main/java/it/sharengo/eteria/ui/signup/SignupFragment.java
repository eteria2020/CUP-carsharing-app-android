package it.sharengo.eteria.ui.signup;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.base.webview.MyWebView;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.userarea.UserAreaFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class SignupFragment extends BaseMvpFragment<SignupPresenter> implements SignupMvpView {

    private static final String TAG = SignupFragment.class.getSimpleName();

    @BindView(R.id.signupWebView)
    MyWebView webview;
    private String currentUrl = "http://www.sharengo.it/signup/mobile";
    private String signupURL = "http://www.sharengo.it/signup/mobile";

    private String mCM;
    private ValueCallback<Uri> valueCallback;
    private ValueCallback<Uri[]> valueCallbackArray;
    private final static int requestCodeFile =1;
    private static Intent ChooserIntent;
    private static Intent[] cameraIntent;
    private static final int PERM_REQ_CODE = 1010;

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

        try{
            signupURL = getResources().getString(R.string.endpointSite) + getString(R.string.routeSignup) + "?lang=" + mPresenter.getLang() ;
            currentUrl = signupURL;
        }catch (Exception e) {
            Log.e(TAG, "onCreateView: Exception", e);
        }

//        loadWebView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //  ((BaseActivity) getActivity()).showLoadingChronology();

        webview.setIgnoreUrls(signupURL);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == requestCodeFile){
                    if(valueCallbackArray == null){
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
            valueCallbackArray.onReceiveValue(results);
            valueCallbackArray = null;
        }else{
            if(requestCode == requestCodeFile){
                if(null == valueCallback) return;
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                valueCallback.onReceiveValue(result);
                valueCallback = null;
            }
        }
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
        webview.setWebChromeClient(new MyChromeClient());
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description,final String failingUrl) {
                //showError(getString(R.string.error_generic_msg));

                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                            getString(R.string.error_msg_network_general)+" \n Error code: "+ errorCode,
                            getString(R.string.ok),
                            null);
                    cdd.show();
                    cdd.yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cdd.dismissAlert();
                            webview.loadUrl(failingUrl);
                            webview.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, final WebResourceRequest request, WebResourceError error) {
                if(getActivity() != null) {
                    webview.setVisibility(View.GONE);
                    final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                            getString(R.string.error_msg_network_general)+" \n Error code: "+ error.getErrorCode(),
                            getString(R.string.ok),
                            null);
                    cdd.show();
                    cdd.yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cdd.dismissAlert();
                            //Navigator.launchSlideshow(SignupFragment.this);
                            //getActivity().finish();
                            webview.loadUrl(currentUrl);
                            webview.setVisibility(View.VISIBLE);
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
                view.loadUrl(needEmbeddedPdf(url));
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                if (url.equals("http://www.sharengo.it/signup-3/mobile")) {
                    loadLogin();
                }
                url = needEmbeddedPdf(url);

                webview.loadUrl( url);
                return true;
            }
            private String needEmbeddedPdf(@NonNull String url){

                if(url.split("[?]")[0].endsWith(".pdf")){
                    url = "https://docs.google.com/gview?url="+url+"&embedded=true";
                }
                return url;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                currentUrl = url;
            }
        });

        webview.loadUrl(signupURL);

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


    private class MyChromeClient extends WebChromeClient{
        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg){
            valueCallback = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i,"File Chooser"), requestCodeFile);
        }
        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
        public void openFileChooser(ValueCallback uploadMsg, String acceptType){
            valueCallback = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), requestCodeFile);
        }
        //For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            valueCallback = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), requestCodeFile);
        }
        //For Android 5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams){
            openChooser(filePathCallback);
//            startActivityForResult(chooserIntent, requestCodeFile);
            return true;
        }
    }

    private void openChooser(ValueCallback<Uri[]> filePathCallback){
//        if(valueCallbackArray != null){
//            valueCallbackArray.onReceiveValue(null);
//        }
        valueCallbackArray = filePathCallback;
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
        String[] perms = {};
        if(takePictureIntent != null){
//            if(EasyPermissions.hasPermissions(getActivity(), perms)) {
            intentArray = new Intent[]{takePictureIntent};
//            }else {
//                cameraIntent = new Intent[]{takePictureIntent};
//                intentArray = new Intent[0];
//            }
        }else{
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, requestCodeFile);
    }

    @AfterPermissionGranted(PERM_REQ_CODE)
    private void checkPermission() {
        String[] perms = {};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...

            ChooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntent);
            startActivityForResult(ChooserIntent, requestCodeFile);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, PERM_REQ_CODE, perms)
                    .setRationale(R.string.msg_rational_location_permission)
                    .setPositiveButtonText(R.string.ok)
                    .setTheme(R.style.AppTheme_AlertDialog)
                    .build());
        }
    }

    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
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
