package it.handroix.core.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by andrealucibello on 11/12/14.
 */
public class HdxUiUtility {

    /**
     *
     * @param pContext
     * @return screen width in pixels
     */
    public static int getScreenWidth(Context pContext){
        WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     *
     * @param pContext
     * @return screen heigth in pixels
     */
    public static int getScreenHeigth(Context pContext){
        WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     *
     * @param pContext
     * @param  pDp
     * @return The pixels conversion of the Dp in input
     */
    public static int fromDptoPx(Context pContext, int pDp) {
        DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
        return (int) ((pDp * displayMetrics.density) + 0.5);
    }

    public static int getColor(Context context, int resourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(resourceId, null);
        } else {
            return context.getResources().getColor(resourceId);
        }

    }

    public static Drawable getDrawable(Context context, int resourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getDrawable(resourceId, null);
        } else {
            return context.getResources().getDrawable(resourceId);
        }

    }
}
