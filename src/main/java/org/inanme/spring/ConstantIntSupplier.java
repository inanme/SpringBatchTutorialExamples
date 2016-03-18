package org.inanme.spring;

import java.util.function.IntSupplier;

public class ConstantIntSupplier implements IntSupplier {

    private final Integer value;

    public ConstantIntSupplier(Integer value) {
        this.value = value;
    }

    @Override
    public int getAsInt() {
        return value;
    }
}
