package it.handroix.baseuiwidgets.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by andrealucibello on 19/05/14.
 *
 */
public class HdxListView extends ListView {

    public static interface Callbacks {
        public void onScrollChanged(int scrollY);
    }

    private float xDistance, yDistance, lastX, lastY;
    private Callbacks mCallbacks;
    private int mCellHeight;

    public HdxListView(Context context) {
        super(context);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public HdxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public HdxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(makeScrollListener());
        mCellHeight = -1;
    }

    public void setCallbacks(Callbacks listener) {
        mCallbacks = listener;
    }

    public void setCellHeight(int height){
        mCellHeight = height;
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


    /**
     * Controlla se il movimento è orizzontale o verticale, se orizzontale lo inoltra a eventuali altri componenti
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if(xDistance > yDistance)
                    return false;
        }

        return super.onInterceptTouchEvent(ev);
    }
}