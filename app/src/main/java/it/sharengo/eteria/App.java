package it.sharengo.eteria;

import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import it.sharengo.eteria.data.datasources.api.ApiModule;
import it.sharengo.eteria.injection.components.ApplicationComponent;
import it.sharengo.eteria.injection.components.DaggerApplicationComponent;
import it.sharengo.eteria.injection.modules.ApplicationModule;

public class App extends MultiDexApplication {

    private static App instance;

    protected ApplicationComponent mComponent;

    private static ArrayList<Class> mStackActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Crashlytics());
        mStackActivity = new ArrayList<Class>();

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
                //.dataSourceModule(new DataSourceModule())
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

    public static void addActivityToStack(Class classe){
        if(!mStackActivity.contains(classe)) {
            mStackActivity.add(0,classe);
        }
    }

    public static void removeActivityToStack(Class classe){
        if(mStackActivity.contains(classe)) {
            mStackActivity.remove(classe);
        }
    }

    public static ArrayList<Class> getmStackActivity() {
        return mStackActivity;
    }

    public static Class getCurrentActivity(){
        if(mStackActivity!=null && mStackActivity.size()>0){
            return mStackActivity.get(0);
        }
        return null;
    }
}