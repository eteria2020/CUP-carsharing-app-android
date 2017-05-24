package it.sharengo.development;

import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import it.sharengo.development.data.datasources.api.ApiModule;
import it.sharengo.development.injection.components.ApplicationComponent;
import it.sharengo.development.injection.components.DaggerApplicationComponent;
import it.sharengo.development.injection.modules.ApplicationModule;

public class App extends MultiDexApplication {

    private static App instance;

    protected ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }

        buildComponent();

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static App getInstance() {
        return instance;
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }

    protected void buildComponent() {
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new ApiModule())
                .build();
    }

    public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler defaultUEH;

        public CustomExceptionHandler() {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        public void uncaughtException(Thread t, Throwable e) {
            String mString = "";
            mString = mString.concat("VERSION.RELEASE {" + Build.VERSION.RELEASE + "}")
                    .concat("\nVERSION.INCREMENTAL {" + Build.VERSION.INCREMENTAL + "}")
                    .concat("\nVERSION.SDK {" + Build.VERSION.SDK_INT + "}")
                    .concat("\nBOARD {" + Build.BOARD + "}")
                    .concat("\nBRAND {" + Build.BRAND + "}")
                    .concat("\nDEVICE {" + Build.DEVICE + "}")
                    .concat("\nFINGERPRINT {" + Build.FINGERPRINT + "}")
                    .concat("\nHOST {" + Build.HOST + "}")
                    .concat("\nID {" + Build.ID + "}");
            
            //TODO: Enable this for rich log on Fabric
//            Crashlytics.log(mString);
            defaultUEH.uncaughtException(t, e);
        }
    }

}