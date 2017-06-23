package org.mahu.proto.webguice.request;

public class PostRequestData {
    private final String text;

    public PostRequestData(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
