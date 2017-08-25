package it.sharengo.development.ui.base.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.handroix.core.utils.HdxUiUtility;
import it.sharengo.development.R;

public abstract class BaseToolbarActivity extends BaseActivity {

    View mToolBarView;
    LinearLayout mToolBarContainer;
    Toolbar mToolBar;
    TextView mToolBarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1 - Inizializzo la view_toolbar_title
        mToolBarView = getLayoutInflater().inflate(R.layout.view_toolbar_title, null);
        mToolBarContainer = (LinearLayout) mToolBarView.findViewById(R.id.toolbar_container);

        mToolBar = (Toolbar) mToolBarView.findViewById(R.id.toolbar);
        mToolBarTitle = (TextView) mToolBar.findViewById(R.id.toolbar_title_tv);

        //3 - Imposto la view_toolbar_title come support ActionBar
        setSupportActionBar(mToolBar);
    }

    public void setToolbarTitle(String title) {
        mToolBarTitle.setPadding(0, mToolBarTitle.getPaddingTop(), mToolBarTitle.getPaddingRight(), mToolBarTitle.getPaddingBottom());
        mToolBarTitle.setText(Html.fromHtml(title));
        mToolBarTitle.setVisibility(View.VISIBLE);
        resizeToolbarTitle();
    }

    private void resizeToolbarTitle() {
        mToolBarTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mToolBarTitle.getWidth() > 0) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        // before Jelly Bean:
                        mToolBarTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        // for Jelly Bean and later:
                        mToolBarTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    Context context = BaseToolbarActivity.this;

                    int toolbarSpace = (BaseToolbarActivity.this instanceof BaseBackActivity || BaseToolbarActivity.this instanceof BaseDrawerActivity) ?
                            context.getResources().getDimensionPixelSize(R.dimen.toolbar_left_space_drawer_back) :
                            context.getResources().getDimensionPixelSize(R.dimen.toolbar_left_space);

                    int paddingLeft = (HdxUiUtility.getScreenWidth(context) / 2) - (mToolBarTitle.getWidth() / 2) - toolbarSpace;
                    mToolBarTitle.setPadding(paddingLeft, mToolBarTitle.getPaddingTop(), mToolBarTitle.getPaddingRight(), mToolBarTitle.getPaddingBottom());
                    mToolBarTitle.requestLayout();
                }
            }
        });
    }

    public void setToolbarBackground(int color) {
        mToolBarContainer.setBackgroundColor(color);
    }
}
