package net.serenitybdd.core.pages;

import net.thucydides.core.pages.Pages;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link net.serenitybdd.core.pages.PagesAnnotatedField}.
 */
public class PagesAnnotedFieldTest {

	private static final class TestDefault {

		public Pages pages;
	}


	private static final class ExtendedPages extends Pages {
		// nothing specific
	}

	private static final class TestExtended {

		public ExtendedPages pages;

	}

	@Test
	public void testFieldIsRightTypeDefault() {
		Field field = ReflectionUtils.findField(TestDefault.class, "pages");
		assertTrue(PagesAnnotatedField.fieldIsRightType(field));
	}

	@Test
	public void testFieldIsRightTypeExtended() {
		Field field = ReflectionUtils.findField(TestExtended.class, "pages");
		assertTrue(PagesAnnotatedField.fieldIsRightType(field));
	}

}
