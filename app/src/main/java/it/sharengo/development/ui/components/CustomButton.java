package it.sharengo.development.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import it.handroix.baseuiwidgets.widgets.HdxButton;
import it.handroix.baseuiwidgets.widgets.HdxTextView;
import it.sharengo.development.R;
import it.sharengo.development.utils.TypeFactory;

/**
 * Created by greta on 29/05/17.
 */

public class CustomButton extends HdxButton {

    private int typefaceType;
    private TypeFactory mFontFactory;

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    public CustomButton(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {


        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomTextView,
                0, 0);
        try {
            typefaceType = array.getInteger(R.styleable.CustomTextView_font_name, 0);
        } finally {
            array.recycle();
        }
        if (!isInEditMode()) {
            setTypeface(getTypeFace(typefaceType));
        }

    }

    public Typeface getTypeFace(int type) {
        if (mFontFactory == null)
            mFontFactory = new TypeFactory(getContext());

        switch (type) {
            case Constants.P_BOLD:
                return mFontFactory.poppinsBold;

            case Constants.P_LIGHT:
                return mFontFactory.poppinsLight;

            case Constants.P_REGULAR:
                return mFontFactory.poppinsRegular;

            case Constants.P_MEDIUM:
                return mFontFactory.poppinsMedium;

            case Constants.P_SEMIBOLD:
                return mFontFactory.poppinsSemiBold;

            default:
                return mFontFactory.poppinsBold;
        }
    }

    public interface Constants {
        int P_BOLD = 1,
                P_LIGHT = 2,
                P_REGULAR = 3,
                P_MEDIUM = 4,
                P_SEMIBOLD=5;
    }
}
