package it.sharengo.development.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import it.handroix.baseuiwidgets.widgets.HdxTextView;
import it.sharengo.development.R;
import it.sharengo.development.utils.TypeFactory;

/**
 * Created by greta on 29/05/17.
 */

public class MenuRadiusButton extends RelativeLayout {

    int flagTouch = 0;

    public MenuRadiusButton(Context context) {
        this(context, null, 0);
    }

    public MenuRadiusButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuRadiusButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_MOVE: {
                flagTouch = flagTouch + 2;
            }
            case MotionEvent.ACTION_DOWN: {
                flagTouch = 0;
            }
            case MotionEvent.ACTION_UP: {
                flagTouch = flagTouch + 1;
            }
        }
        if (flagTouch == 1)
        {
            return super.onTouchEvent(event);
        }
        else
        {
            return false;
        }
    }
}
