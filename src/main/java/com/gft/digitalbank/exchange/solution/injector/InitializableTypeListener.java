package com.gft.digitalbank.exchange.solution.injector;

import com.gft.digitalbank.exchange.solution.interfaces.Initializable;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

@SuppressWarnings("unchecked")
public class InitializableTypeListener implements TypeListener {
    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        InjectionListener injectionListener = i -> ((Initializable) i).initialize();
        typeEncounter.register(injectionListener);
    }
}
