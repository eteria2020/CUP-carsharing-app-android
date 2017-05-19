package it.sharengo.eteria.injection.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import it.sharengo.eteria.App;
import it.sharengo.eteria.data.datasources.SampleRetrofitDataSource;
import it.sharengo.eteria.data.datasources.api.ApiModule;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.injection.modules.ApplicationModule;
import it.sharengo.eteria.ui.base.fragments.BaseFragment;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                ApiModule.class
        }
)
public interface ApplicationComponent {

    void inject(App app);
    void inject(BaseFragment baseFragment);

    @ApplicationContext
    Context context();
    App application();
    PresenterManager presenterManager();
    SchedulerProvider schedulerProvider();

    AppRepository appRepository();
    
    SampleRetrofitDataSource sampleRetrofitDataSource();

}