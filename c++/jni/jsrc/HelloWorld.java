class HelloWorld {
    public native void displayHelloWorld();

    static {
        System.loadLibrary("HelloWorldImp");
    }

    public static void main(String[] args) {
        new HelloWorld().displayHelloWorld();
    }
}
