package org.inanme.spring;

import java.util.function.Supplier;

public class SomeService {

    private final Supplier<Integer> intSupplier;

    public SomeService(Supplier<Integer> intSupplier) {
        this.intSupplier = intSupplier;
    }

    public int callService() {
        return intSupplier.get();
    }
}
