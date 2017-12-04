package it.handroix.core.filesutils;

import android.util.Log;

/**
 * Classe Astratta per ottenere il progress (0--100) di lunghe operazione su files
 * Created by Andrea Richiardi on 23/06/14.
 */
public abstract class HdxProgressPublisher {

    public static final String TAG = "HdxProgressPublisher";

    private Long mTotal = 0L;
    private Long mCumulative = 0L;
    private float mLastProgress = 0.0f;

    /**
     *
     * @param total size in bytes
     */
    public HdxProgressPublisher(Long total) {
        mTotal = total;
    }

    /**
     * Used Internally
     * @param oldSize
     * @param newSize
     */
    protected void updateTotal(Long oldSize, Long newSize) {
        mTotal = mTotal - oldSize + newSize;
    }

    /**
     *
     * @param step
     */
    protected void publish(Long step) {
        float currentProgress = Math.min(Math.round((100 * (mCumulative + step)) / (float) mTotal), 100);
        mCumulative += step;

        if(currentProgress - mLastProgress > 0.5) {
            mLastProgress = currentProgress;
            Log.v(TAG, "Publishing progress: " + currentProgress);
            publishProgress(currentProgress);
        }
    }

    protected abstract void publishProgress(float progress);
}
