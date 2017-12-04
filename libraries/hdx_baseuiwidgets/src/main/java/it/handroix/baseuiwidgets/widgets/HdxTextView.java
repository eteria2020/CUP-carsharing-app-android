package it.handroix.baseuiwidgets.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import it.handroix.baseuiwidgets.fontmanager.HdxFontManager;
import it.handroix.baseuiwidgets.widgets.R;


public class HdxTextView extends TextView {
	private static final String TAG = "HdxTextView";
	private Context mContext=null;

    public HdxTextView(Context context) {
    	
        super(context);
        mContext=context;
    }

    public HdxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        mContext=context;
    }

    public HdxTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.HdxTextView);
        String customFont = a.getString(R.styleable.HdxTextView_customFont);
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
}
