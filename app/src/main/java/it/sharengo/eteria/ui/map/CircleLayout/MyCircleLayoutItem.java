package it.sharengo.development.ui.map.CircleLayout;

import android.content.Context;
import android.util.AttributeSet;

import com.example.x.circlelayout.CircleLayout;
import com.example.x.circlelayout.CircularLayoutItem;


public class MyCircleLayoutItem extends CircularLayoutItem {

    private OnItemActionListener mListener;

    public interface OnItemActionListener {
        void onItemClick(int i);
    }

    public MyCircleLayoutItem(Context context, OnItemActionListener listener) {
        super(context);initialize(); mListener = listener;
    }

    public MyCircleLayoutItem(Context context, CircleLayout cl) {
        super(context, cl);
        initialize();
    }

    public MyCircleLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MyCircleLayoutItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void initialize()
    {


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick() {
                mListener.onItemClick(getIndex());
            }
        });

        this.setOnFocusListener(new OnFocusListener()
        {
            @Override
            public void onFocus() {
//                Toast.makeText(getContext(),"Item number: "+getIndex()+ " is now on focus ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnFocus() {

            }
        });
    }



}
