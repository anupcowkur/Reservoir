package com.anupcowkur.reservoir;

public class TestUtils {

    static String getLargeString() {
        //create a string of more than 2048 bytes since that's the size of the cache in the
        // sample app.
        final int stringSize = 2049;
        StringBuilder sb = new StringBuilder(stringSize);
        for (int i = 0; i < stringSize; i++) {
            sb.append('a');
        }
        return sb.toString();
    }
}
