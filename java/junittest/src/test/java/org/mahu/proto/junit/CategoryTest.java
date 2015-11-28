package org.mahu.proto.junit;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mahu.proto.junit.category.Category1;

// Category is experimental
@Category(Category1.class)
public class CategoryTest {

	@Test
	public void testInfiniteLoop() {
		// empty
	}
}