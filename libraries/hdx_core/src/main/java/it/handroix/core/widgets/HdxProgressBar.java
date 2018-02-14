package it.handroix.core.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by andrealucibello on 22/10/15.
 */
public class HdxProgressBar extends FrameLayout {

    public HdxProgressBar(Context context) {
        super(context);
        init(context);
    }

    public HdxProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HdxProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HdxProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView(context);
        addView(v, frameParams);
    }


    protected View makeControllerView(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ProgressBar progressBar = new ProgressBar(context);
            return progressBar;
        } else {
            MaterialProgressBar materialProgressBar = new MaterialProgressBar(context);
            materialProgressBar.setIndeterminate(true);
            return materialProgressBar;
        }
    }
}
