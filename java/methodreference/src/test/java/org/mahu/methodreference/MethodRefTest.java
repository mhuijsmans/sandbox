package org.mahu.methodreference;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

// source: https://stackoverflow.com/questions/35914775/java-8-difference-between-method-reference-bound-receiver-and-unbound-receiver
public class MethodRefTest {

    // Class used for testing
    static class TestClass {

        public static final String HI = "hi";
        public static final String STATIC_HI = "static hi";

        public TestClass() {
        }

        static public String echo(int v) {
            return Integer.toString(v);
        }

        static public void wasteBasket(String str) {
        }

        static public String getStaticString() {
            return STATIC_HI;
        }

        public String getString() {
            return HI;
        }
    }

    // Get a reference to an instance reference and use that
    @Test
    public void unboundInstanceMethodTest() {
        TestClass t = new TestClass();

        Function<TestClass, String> f = TestClass::getString;

        assertEquals(TestClass.HI, f.apply(t));
    }

    // Get a reference to an instance reference and use that
    @Test
    public void boundInstanceMethodTest() {
        TestClass t = new TestClass();

        Supplier<String> f = t::getString;

        assertEquals(TestClass.HI, f.get());
    }

    // Get a reference to an instance reference and use that
    @Test
    public void staticMethod1Test() {
        Supplier<String> f = TestClass::getStaticString;

        assertEquals(TestClass.STATIC_HI, f.get());
    }

    @Test
    public void staticMethod2Test() {
        final String TEXT = "TEXT";
        Consumer<String> f = TestClass::wasteBasket;

        f.accept(TEXT);
    }

    @Test
    public void staticMethod3Test() {
        Function<Integer, String> f = TestClass::echo;

        assertEquals("52", f.apply(53));
    }
}
