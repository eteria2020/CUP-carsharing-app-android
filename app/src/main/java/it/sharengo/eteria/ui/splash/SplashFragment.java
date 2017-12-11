package it.sharengo.eteria.ui.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;

import static android.content.Context.MODE_PRIVATE;

public class SplashFragment extends BaseMvpFragment<SplashPresenter> implements SplashMvpView {

    private static final String TAG = SplashFragment.class.getSimpleName();
    private boolean handled=false;
    
    @BindView(R.id.splash_content)
    View mSplashContent;
    private static final int mRequestPermission = 1;

    public static SplashFragment newInstance() {
        SplashFragment instance = new SplashFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        handled = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSplashContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSplashContent.startAnimation(anim);

        mPresenter.loadData(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE), getContext());
    }

    @Override
    public void navigateToHome(String lang) {

        if(!handled){
            handled=true;
        }else{
            return;
        }
        

        //Se è il primo accesso, mostro il login
        if(mPresenter.isFirstAccess(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE))){
            mPresenter.setFirstAccess(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
            getActivity().finish();
            Navigator.launchLogin(this, Navigator.REQUEST_LOGIN_START);
            //Navigator.launchOnboarding(this);
            //Navigator.launchLongIntro(this);
        }else{
            getActivity().finish();
            Navigator.launchMapGoogle(this,Navigator.REQUEST_MAP_DEFAULT);
            //Navigator.launchOnboarding(this); //TODO remove
            Navigator.launchShortIntro(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(!mPresenter.isViewAttached()) {
            mPresenter.attachView(this, false);
        }

        switch (requestCode) {
            case mRequestPermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED ) {
                        mPresenter.permissionChecked();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mPresenter.askPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void checkMapPermission(){

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setMessage(getActivity().getString(R.string.msg_rational_location_permission))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissions, mRequestPermission);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .create()
                        .show();

            } else {
                requestPermissions(permissions, mRequestPermission);
            }
        }else{
            mPresenter.permissionChecked();
        }
    }

    @Override
    public void navigateToLogin() {
        Navigator.launchLogin(this, Navigator.REQUEST_LOGIN_START);
        getActivity().finish();
    }
}
