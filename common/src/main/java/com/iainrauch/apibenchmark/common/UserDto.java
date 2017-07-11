package com.iainrauch.apibenchmark.common;

public class UserDto {

    private String id;
    private byte[] data;
    private long counter;

    public UserDto() {
    }

    public UserDto(String id, byte[] data, long counter) {
        this.id = id;
        this.data = data;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public long getCounter() {
        return counter;
    }
}
