package it.sharengo.development.ui.map.CircleLayout;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.example.x.circlelayout.CircleLayoutAdapter;
import com.example.x.circlelayout.CircularLayoutItem;

import java.util.LinkedList;

/**
 * Created by mindvalley on 12/17/14.
 */


public class MyCircleLayoutAdapter extends CircleLayoutAdapter {


    private LinkedList<Integer> adapter=new LinkedList<>();
    private int startingIdIndex=0;
    private boolean isStartingIdSetten=false;

    public boolean carAlpha = true;
    public boolean centerAlpha = false;
    public boolean animationRefresh = false;

    private OnItemActionListener mListener;

    public interface OnItemActionListener {
        void onItemClick(int i);
    }

    private MyCircleLayoutItem.OnItemActionListener mActionListener = new MyCircleLayoutItem.OnItemActionListener(){
        @Override
        public void onItemClick(int i) {
            mListener.onItemClick(i);
        }
    };

    public MyCircleLayoutAdapter(OnItemActionListener listener){
        super();
        mListener = listener;
    }


    public boolean setStartingIndex(int startingIndex) {
        if(!isStartingIdSetten)
        {
            isStartingIdSetten=true;
            startingIdIndex = startingIndex;

            return true;
        }
        return false;
    }

    @Override
    public void add(Object object)
    {
        Integer model=(Integer)object;
        adapter.add(model);


    }


    @Override
    public Integer getRoundedIndex(Integer index)
    {
        try {
            int new_index=((-1*index)%adapter.size()+adapter.size())%adapter.size();
            return new_index;
        }
        catch (Exception e) {
            return -1;
        }
    }

    public Object getCurrentObject()
    {
        return adapter.get( getRoundedIndex(getRoundedIndex(parent.getCurrent_step())-startingIdIndex));
    }


    @Override
    public CircularLayoutItem get(int index)
    {
        Integer drawable=adapter.get(getRoundedIndex((getRoundedIndex(index)-startingIdIndex)));
        MyCircleLayoutItem civ = new MyCircleLayoutItem(context, mActionListener);
        civ.setBackground(context.getResources().getDrawable(drawable));
        civ.setIndex(index);
        //if(index == 1)
        //civ.setAlpha(0.5f);
        if(index < 0 || index > 3) civ.setAlpha(0.0f);

        //Cars
        if(index == 3 && carAlpha) civ.setAlpha(0.4f);

        //Center
        if(index == 1 && centerAlpha) civ.setAlpha(0.4f);


        if(index == 2 && animationRefresh) civ.startRotateAnimation();
        else civ.stopRotateAnimation();

        return civ;
    }


    public int getSize()
    {
        return adapter.size();
    }

}