package org.mahu.proto.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This rule defined what expectation to expect
 */
public class ExpectedExceptionRuleTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void throwsIllegalArgumentExceptionIfIconIsNull() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("Negative value not allowed");
    ClassToBeTested t = new ClassToBeTested();
    t.methodToBeTest(-1);
  }
  
  static class ClassToBeTested {
	  public void methodToBeTest(int i) {
		  if (i<0) {
			  throw new IllegalArgumentException("Negative value not allowed");
		  }
	  }
  }
}
