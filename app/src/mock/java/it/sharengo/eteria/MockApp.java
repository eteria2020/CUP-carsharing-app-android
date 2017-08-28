package it.sharengo.eteria;

import it.sharengo.eteria.data.datasources.api.MockApiModule;
import it.sharengo.eteria.injection.components.DaggerApplicationComponent;
import it.sharengo.eteria.injection.modules.ApplicationModule;

public class MockApp extends App {
    
    @Override
    protected void buildComponent() {
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new MockApiModule())
                .build();
    }
}