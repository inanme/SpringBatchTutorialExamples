package org.inanme.spring;

import java.util.function.IntSupplier;

public class SomeService {

    private final IntSupplier intSupplier;

    public SomeService(IntSupplier intSupplier) {
        this.intSupplier = intSupplier;
    }

    public int callService() {
        return intSupplier.getAsInt();
    }
}
