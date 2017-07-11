package com.iainrauch.apibenchmark.common;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static byte[] getBlob(int len) {
        byte[] blob = new byte[len];
        ThreadLocalRandom.current().nextBytes(blob);
        return blob;
    }

}
