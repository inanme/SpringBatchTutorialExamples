package org.inanme.spring;

import java.util.function.LongSupplier;

public class ConstantLongSupplier implements LongSupplier {

    private final long value;

    public ConstantLongSupplier(long value) {
        this.value = value;
    }

    @Override
    public long getAsLong() {
        return value;
    }
}
