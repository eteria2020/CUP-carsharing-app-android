package it.sharengo.eteria.injection.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import it.sharengo.eteria.App;
import it.sharengo.eteria.data.datasources.DataSourceModule;
import it.sharengo.eteria.data.datasources.api.ApiModule;
import it.sharengo.eteria.data.repositories.AddressRepository;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.CarRepository;
import it.sharengo.eteria.data.repositories.CityRepository;
import it.sharengo.eteria.data.repositories.KmlRepository;
import it.sharengo.eteria.data.repositories.PostRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.injection.modules.ApplicationModule;
import it.sharengo.eteria.ui.base.fragments.BaseFragment;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

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
    CarRepository carRepository();
    AddressRepository addressRepository();
    PreferencesRepository preferencesRepository();
    UserRepository userRepository();
    CityRepository cityRepository();
    KmlRepository kmlRepository();
    //SampleRetrofitDataSource sampleRetrofitDataSource();
    //JsonPlaceholderRetrofitDataSource jsonPlaceholderRetrofitDataSource();

}