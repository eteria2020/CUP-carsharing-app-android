package it.handroix.baseuiwidgets.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

/**
 * Created by luca on 20/08/14.
 */
public class HdxGridView extends GridView{

    public static interface Callbacks {
        public void onScrollChanged(int scrollY);
    }

    private int mCellHeight;
    private Callbacks mCallbacks;

    public HdxGridView(Context context) {
        super(context);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public HdxGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public HdxGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public void setCallbacks(Callbacks listener) {
        mCallbacks = listener;
    }


    private OnScrollListener makeScrollListener() {
        return new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int pScrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                if(getAdapter()!=null && mCallbacks!=null){
                    mCallbacks.onScrollChanged(getComputedScrollY());
                }
            }
        };
    }

    public void setCellHeight(int height){
        mCellHeight = height;
    }

    public int getComputedScrollY() {
        int pos;
        int nScrollY = 0;
        int nItemY;
        View view = null;
        pos = getFirstVisiblePosition();
        view = getChildAt(0);
        if(view!=null) {
            nItemY = view.getTop();

            if(mCellHeight==-1){
                throw new IllegalArgumentException("Cell Height not initialized, please call setCellHeight(int height)");
            }
            nScrollY = mCellHeight * pos - nItemY;
        }
        return nScrollY;
    }

}
