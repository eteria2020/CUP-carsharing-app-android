package it.handroix.baseuiwidgets.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import it.handroix.baseuiwidgets.fontmanager.HdxFontManager;
import it.handroix.baseuiwidgets.widgets.R;

public class HdxEditText extends TextInputEditText {
    private static final String TAG = "HdxEditText";
    private Context mContext=null;

    public interface ExtraButtonListener {
        void onExtraButtonPressed();
    }
    private ExtraButtonListener mListener;

    public HdxEditText(Context context) {

        super(context);
        mContext=context;
        readStyleParameters(context, null);
    }

    public HdxEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        setCustomFont(context, attrs);
        readStyleParameters(context, attrs);
    }

    public HdxEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
        setCustomFont(context, attrs);
        readStyleParameters(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.HdxEditText);
        String customFont = a.getString(R.styleable.HdxEditText_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        tf = HdxFontManager.getInstance().getFont(ctx,asset);

        if(tf !=null){
            setTypeface(tf);
            return true;
        }else{
            return false;
        }
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        boolean enableCancelButton = false;
        Drawable cancelDrawable;
        int cancelDrawableColor;
        Drawable extraButtonDrawable;

        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.HdxEditText);
        try {
            enableCancelButton = a.getBoolean(R.styleable.HdxEditText_enableCancelButton, false);
            cancelDrawable = a.getDrawable(R.styleable.HdxEditText_cancelButtonDrawable);
            cancelDrawableColor = a.getColor(R.styleable.HdxEditText_cancelButtonColor, getColor(getContext(), R.color.colorAccent));
            extraButtonDrawable = a.getDrawable(R.styleable.HdxEditText_extraButtonDrawable);
        } finally {
            a.recycle();
        }

        if (enableCancelButton) {

            if (cancelDrawable == null) {
                cancelDrawable = getDrawable(getContext(), R.drawable.ic_add_circle_outline_black_24px);
            }

            cancelDrawable.mutate().setColorFilter(cancelDrawableColor, PorterDuff.Mode.SRC_ATOP);

            setCompoundDrawablesWithIntrinsicBounds(null, null, cancelDrawable, null);
            setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.et_cancel_icon_padding));


            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN && getCompoundDrawables()[2] != null) {
                        if (event.getX() > v.getWidth() - v.getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth()) {

                            setText("");

                            if (mListener != null) {
                                mListener.onExtraButtonPressed();
                            }
                        }
                    }
                    v.requestFocus();
                    return false;
                }
            });
        }


        if (extraButtonDrawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, extraButtonDrawable, null);
            setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.et_cancel_icon_padding));

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN && getCompoundDrawables()[2] != null) {
                        if (event.getX() > v.getWidth() - v.getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth()) {

                            if (mListener != null) {
                                mListener.onExtraButtonPressed();
                            }
                        }
                    }
                    v.requestFocus();
                    return false;
                }
            });
        }
    }


    public void setExtraButtonListener(ExtraButtonListener listener) {
        this.mListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getDrawable(Context context, int resourceId) {
        return Build.VERSION.SDK_INT >= 23?context.getResources().getDrawable(resourceId, (Resources.Theme)null):context.getResources().getDrawable(resourceId);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static int getColor(Context context, int resourceId) {
        return Build.VERSION.SDK_INT >= 23?context.getResources().getColor(resourceId, (Resources.Theme)null):context.getResources().getColor(resourceId);
    }
}
