package it.sharengo.development;

import it.sharengo.development.data.datasources.api.MockApiModule;
import it.sharengo.development.injection.components.DaggerApplicationComponent;
import it.sharengo.development.injection.modules.ApplicationModule;

public class MockApp extends App {
    
    @Override
    protected void buildComponent() {
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new MockApiModule())
                .build();
    }
}