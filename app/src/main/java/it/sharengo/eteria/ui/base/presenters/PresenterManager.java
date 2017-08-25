package it.sharengo.development.ui.base.presenters;

import android.os.Bundle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PresenterManager {
    private static final String SIS_KEY_PRESENTER_ID = "presenter_id";

    private final AtomicLong currentId;

    private final Cache<Long, BasePresenter<?>> presenters;

    @Inject
    public PresenterManager() {
        currentId = new AtomicLong();
        presenters = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();
    }

    public <P extends BasePresenter<?>> P restorePresenter(Bundle savedInstanceState) {
        Long presenterId = savedInstanceState.getLong( SIS_KEY_PRESENTER_ID);
        P presenter;
        try {
            presenter = (P) presenters.getIfPresent(presenterId);
        } catch (ClassCastException e) {
            return null;
        }
        presenters.invalidate(presenterId);
        return presenter;
    }

    public long savePresenter(BasePresenter<?> presenter, Bundle outState) {
        long presenterId = currentId.incrementAndGet();
        presenters.put(presenterId, presenter);
        outState.putLong(SIS_KEY_PRESENTER_ID, presenterId);
        return presenterId;
    }
}