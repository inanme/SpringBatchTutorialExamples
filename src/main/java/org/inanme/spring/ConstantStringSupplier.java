package org.inanme.spring;

import java.util.function.Supplier;

public class ConstantStringSupplier implements Supplier<String> {

    private final String value;

    public ConstantStringSupplier(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }
}
