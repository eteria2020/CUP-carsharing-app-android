package it.sharengo.eteria.utils.schedulers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Provides different types of schedulers.
 */
public class AndroidSchedulerProvider implements SchedulerProvider {

    @Nullable
    private static AndroidSchedulerProvider INSTANCE;

    // Prevent direct instantiation.
    private AndroidSchedulerProvider() {
    }

    public static synchronized AndroidSchedulerProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AndroidSchedulerProvider();
        }
        return INSTANCE;
    }

    @Override
    @NonNull
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    @NonNull
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    @NonNull
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
