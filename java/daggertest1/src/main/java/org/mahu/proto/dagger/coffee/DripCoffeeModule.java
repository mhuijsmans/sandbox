package org.mahu.proto.dagger.coffee;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = PumpModule.class)
class DripCoffeeModule {
    @Provides
    @Singleton
    Heater provideHeater() {
        return new ElectricHeater();
    }
}