package org.inanme.springbatch;


public class CustomPojo {

    public final int id;

    public CustomPojo(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CustomPojo{id=" + id + '}';
    }
}
