package org.mahu.proto.restapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mahu.proto.restapp.engine.impl.AnnotationProcessorTest;

@RunWith(Suite.class)
@SuiteClasses({ ProcessBuilderTest.class, ProcessPathBuilderTest.class,
		ProcessDefinitionRepoTest.class, WorkflowEngine1Test.class,  WorkflowEngine2Test.class,
		AnnotationProcessorTest.class, XmlAdapterTest.class })
public class AllTests {

}
