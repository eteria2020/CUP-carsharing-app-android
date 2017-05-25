package it.sharengo.development.injection.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import it.sharengo.development.App;
import it.sharengo.development.data.datasources.DataSourceModule;
import it.sharengo.development.data.datasources.JsonPlaceholderRetrofitDataSource;
import it.sharengo.development.data.datasources.api.ApiModule;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.injection.ApplicationContext;
import it.sharengo.development.injection.modules.ApplicationModule;
import it.sharengo.development.ui.base.fragments.BaseFragment;
import it.sharengo.development.ui.base.presenters.PresenterManager;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                DataSourceModule.class,
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
    PostRepository postRepository();
    //SampleRetrofitDataSource sampleRetrofitDataSource();
    //JsonPlaceholderRetrofitDataSource jsonPlaceholderRetrofitDataSource();

}