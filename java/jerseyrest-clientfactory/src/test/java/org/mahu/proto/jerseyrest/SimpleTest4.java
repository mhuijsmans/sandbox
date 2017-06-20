package org.mahu.proto.jerseyrest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class SimpleTest4 {
    private final String OUTPUT = fixedLengthString("a", 4096);

    /**
     * The objective of this test is to explore if a server response is subject
     * to a memory leak. conclusion is no.
     */
    @Test
    public void test1() {
        final List<Response> list = new LinkedList<>();
        final int MAX = 1000 * 10;
        for (int i = 0; i < MAX * 10; i++) {
            for (int j = 0; j < MAX; j++) {
                list.add(Response.status(200).entity(OUTPUT).build());
            }
            list.clear();
        }
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

}