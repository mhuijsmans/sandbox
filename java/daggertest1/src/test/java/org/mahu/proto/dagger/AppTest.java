package org.mahu.proto.dagger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    @Test
    public void test() {
        final String TEXT = "text";
        ExampleWithInjectAndBinds.Console consoleMock = Mockito.mock(ExampleWithInjectAndBinds.Console.class);
        ExampleWithInjectAndBinds.ConsolePrinter consolePrinter = new ExampleWithInjectAndBinds.ConsolePrinter(
                consoleMock);

        consolePrinter.printMsg(TEXT);

        Mockito.verify(consoleMock, Mockito.times(1)).println(TEXT);
    }

}
