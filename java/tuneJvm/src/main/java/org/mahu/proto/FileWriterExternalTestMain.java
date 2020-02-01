package org.mahu.proto;

final class FileWriterExternalTestMain {
    public static void main(final String[] args) {
        final int count = Integer.parseInt(args[0]);
        final int size = Integer.parseInt(args[1]);
        Object[] b = new Object[count];
        for(int i=0; i<count; i++) {
        	b[i] = new byte[size];
        }
        System.exit(0);
    }
}