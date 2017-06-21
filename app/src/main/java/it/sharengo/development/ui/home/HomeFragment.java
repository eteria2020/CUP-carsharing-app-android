package it.sharengo.development.ui.home;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.map.MapFragment;

public class HomeFragment extends BaseMvpFragment<HomePresenter> implements HomeMvpView {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int mRequestPermission = 1;

    @BindView(R.id.circleView)
    View circleView;

    @BindView(R.id.homeView)
    View homeView;

    @BindView(R.id.searchCarsButton)
    ImageView searchCarsButton;

    @BindView(R.id.profileUserButton)
    ImageView profileUserButton;

    @BindView(R.id.unknownButton)
    ImageView unknownButton;

    @BindView(R.id.welcomeTextView)
    TextView welcomeTextView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mPresenter.viewCreated();

        //Scritta di benvenuto
        if(mPresenter.isAuth()){
            welcomeTextView.setText(String.format(getString(R.string.home_welcomeauth_label), mPresenter.getUserInfo().userInfo.name));
        }else{
            welcomeTextView.setText(getString(R.string.home_welcome_label));
        }

        //Animazioni
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setAnimations();
            }
        }, 800);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    private void setAnimations(){

        circleView.animate().alpha(1.0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setCircleAnimatio();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //(int) (200 * getResources().getDisplayMetrics().density)
    }

    private void setCircleAnimatio(){
        ResizeAnimation resizeAnimation = new ResizeAnimation(circleView, homeView.getWidth() - 350, homeView.getWidth() - 350);
        resizeAnimation.setDuration(600);
        circleView.startAnimation(resizeAnimation);
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setButtonAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        searchCarsButton.setX((float) (circleView.getX()+searchCarsButton.getWidth()*0.35));
        searchCarsButton.setY((float) (circleView.getY()-(homeView.getWidth() - 350)/2+searchCarsButton.getHeight()*0.35));

        profileUserButton.setX((float) (circleView.getX()+(homeView.getWidth() - 350)*0.435+profileUserButton.getWidth()*0.35));
        profileUserButton.setY((float) (circleView.getY()+(homeView.getWidth() - 350)*0.225 + profileUserButton.getHeight()*0.35));

        unknownButton.setX((float) (circleView.getX()-(homeView.getWidth() - 350)*0.435+unknownButton.getWidth()*0.35));
        unknownButton.setY((float) (circleView.getY()+(homeView.getWidth() - 350)*0.225 + unknownButton.getHeight()*0.35));
    }

    private void setButtonAnimation(){
        profileUserButton.animate().alpha(1.0f).setDuration(500).start();
        searchCarsButton.animate().alpha(1.0f).setDuration(500).start();
        unknownButton.animate().alpha(1.0f).setDuration(500).start();

        welcomeTextView.animate().alpha(1.0f).setDuration(500).start();
    }

    private void checkMapPermission(){

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
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();

            } else {
                requestPermissions(permissions, mRequestPermission);
            }
        }else{
            getActivity().finish();
            Navigator.launchMap(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case mRequestPermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    Navigator.launchMap(this);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    checkMapPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    ////////////////////////////////////
    //
    //         BUTTERKNIFE
    //
    ////////////////////////////////////
    @OnClick(R.id.searchCarsButton)
    public void onSendClick() {
        checkMapPermission();
    }

    @OnClick(R.id.profileUserButton)
    public void onProfileClick() {

        if(mPresenter.isAuth()) {

            //Apro il profilo
            Navigator.launchProfile(this);
            getActivity().finish();

        }else{

            //Mostro il login
            Navigator.launchLogin(HomeFragment.this, Navigator.REQUEST_LOGIN_PROFILE);
            getActivity().finish();
        }

    }

    @OnClick(R.id.unknownButton)
    public void onUnknownClick(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.general_notenabled_alert),
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



    ////////////////////////////////////
    //
    //         MVP
    //
    ////////////////////////////////////



    public class ResizeAnimation extends Animation {
        final int startWidth;
        final int targetWidth;
        final int startHeight;
        final int targetHeight;
        View view;

        public ResizeAnimation(View view, int targetWidth, int targetHeight) {
            this.view = view;
            this.targetWidth = targetWidth;
            startWidth = view.getWidth();
            this.targetHeight = targetHeight;
            startHeight = view.getHeight();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
