package it.sharengo.eteria.ui.base.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.drawer.DrawerArrowDrawable;
import it.sharengo.eteria.ui.base.drawer.DrawerSlideListener;
import it.sharengo.eteria.ui.menu.MenuFragment;

public abstract class BaseDrawerActivity extends BaseToolbarActivity implements DrawerSlideListener {

    public static final String EXTRA_MENU_ITEM = "EXTRA_MENU_ITEM";

    public DrawerLayout mDrawerLayout;
    
    public DrawerArrowDrawable mDrawerArrowDrawable;

    public ImageView menuButton;
    
//    public FrameLayout mRightFrame;
//    public FrameLayout mLeftFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setScrimColor(Color.parseColor("#956fac54"));

//        addLeftFrame();
//        addRightFrame();

        if (mToolBarView != null) {
            RelativeLayout contentContainer = (RelativeLayout) findViewById(R.id.content_container);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            contentContainer.addView(mToolBarView, params);

            menuButton = (ImageView) mToolBar.findViewById(R.id.menuButton);

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionBarInteraction();
                }
            });
        }

        mDrawerArrowDrawable = new DrawerArrowDrawable(getResources());
        mDrawerArrowDrawable.setStrokeColor(ContextCompat.getColor(this, android.R.color.white));
        mDrawerArrowDrawable.setFlip(true);
        //mToolBar.setNavigationIcon(mDrawerArrowDrawable);
        mToolBar.setNavigationOnClickListener(mNavigationOnClickListener);

        mToolBar.findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.launchHome(BaseDrawerActivity.this);
                BaseDrawerActivity.this.finish();
            }
        });


        String sectionString = getIntent().getStringExtra(EXTRA_MENU_ITEM);
//        setLeftFragment(MenuFragment.newInstance(sectionString), savedInstanceState);

        MenuFragment menuFragment = MenuFragment.newInstance(sectionString);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.menu_frame, menuFragment)
                    .commit();
        }


        final ViewGroup notificationView = (ViewGroup) findViewById(R.id.notificationView);
        notificationView.setVisibility(View.VISIBLE);
        notificationView.setY(-500f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnValue = false;
        if (item.getItemId() == R.id.rightSideButton) {
            actionBarInteraction();
            if (mDrawerLayout != null) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
            returnValue = true;
        }

        return returnValue;
    }

    @Override
    public void onDrawerSlide(float v) {

    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            super.onBackPressed();
        }
    }

    public Boolean closeDrawer() {
        Boolean drawerClosed = Boolean.FALSE;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerClosed = Boolean.TRUE;
            closeRightDrawerFrame();
        }
        return drawerClosed;
    }

//    public void setLeftFragment(Fragment pFragment, Bundle savedInstanceState) {
//        setDrawerFrame(mLeftFrame, pFragment, savedInstanceState);
//    }
//
//    public void setRightFragment(Fragment pFragment, Bundle savedInstanceState) {
//        setDrawerFrame(mRightFrame, pFragment, savedInstanceState);
//    }



//    public void closeLeftDrawerFrame() {
//        mDrawerLayout.closeDrawer(GravityCompat.START);
//    }
//    public void openLeftDrawerFrame() {
//        mDrawerLayout.openDrawer(GravityCompat.START);
//    }
    public void closeRightDrawerFrame() {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }
    public void openRightDrawerFrame() {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }
//    public void toggleRightDrawerFrame() {
//        if (mDrawerLayout != null) {
//            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
//                mDrawerLayout.closeDrawers();
//            } else {
//                mDrawerLayout.openDrawer(GravityCompat.END);
//            }
//        }
//    }
//
//
//
//    private FrameLayout addLeftFrame() {
//
//        if (mDrawerLayout != null) {
//            mLeftFrame = new FrameLayout(this);
//            mLeftFrame.setId(R.id.left_frame);
//            int panelWith = getResources().getDimensionPixelSize(R.dimen.drawer_width);
//            DrawerLayout.LayoutParams layoutparams = new DrawerLayout.LayoutParams(panelWith, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutparams.gravity = Gravity.START;
//            mLeftFrame.setLayoutParams(layoutparams);
//            mDrawerLayout.addView(mLeftFrame);
//            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mLeftFrame);
//        } else {
//            mLeftFrame = null;
//        }
//
//        return mLeftFrame;
//    }
//
//    private FrameLayout addRightFrame() {
//        if (mDrawerLayout != null) {
//            mRightFrame = new FrameLayout(this);
//            mRightFrame.setId(R.id.right_frame);
//            int panelWith = getResources().getDimensionPixelSize(R.dimen.drawer_width);
//            DrawerLayout.LayoutParams layoutparams = new DrawerLayout.LayoutParams(panelWith, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutparams.gravity = Gravity.END;
//            mRightFrame.setLayoutParams(layoutparams);
//            mDrawerLayout.addView(mRightFrame);
//            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mRightFrame);
//        } else {
//            mRightFrame = null;
//        }
//
//        return mRightFrame;
//    }

//    private void setDrawerFrame(FrameLayout frame, Fragment pFragment, Bundle savedInstanceState) {
//        if (pFragment != null) {
//            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightFrame);
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction().add(frame.getId(), pFragment).commit();
//            }
//        }
//    }

    private View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDrawerArrowDrawable != null && mDrawerArrowDrawable.isFlipped()) {
                //DRAWER MODE  open/close drawer
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    closeRightDrawerFrame();
                } else {

                    try {
                        //FORZO LA CHIUSURA DELLA TASTIERA
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        openRightDrawerFrame();
                        actionBarInteraction();

                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(), e.getMessage());
                    }
                }
            } else {
                //BACK ARROW
            }
        }
    };

    public void actionBarInteraction(){

    }

    public void showNotification(String notification, View.OnClickListener mNotificationListener){

        ((TextView) findViewById(R.id.notificationTextView)).setText(notification);
        final ViewGroup notificationView = (ViewGroup) findViewById(R.id.notificationView);
        notificationView.setVisibility(View.VISIBLE);
        notificationView.setOnClickListener(mNotificationListener);

        notificationView.animate().translationY(0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationView.animate().translationY(-500);
                notificationView.setOnClickListener(null);
                notificationView.setVisibility(View.GONE);
            }
        }, 10000);
    }
}
