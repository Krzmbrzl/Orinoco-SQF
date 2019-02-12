package arma.orinocosqf;

import arma.orinocosqf.type.CodeType;
import arma.orinocosqf.type.ExpandedValueType;
import arma.orinocosqf.type.PolymorphicWrapperValueType;
import arma.orinocosqf.type.SingletonArrayExpandedValueType;
import org.junit.Test;

import static arma.orinocosqf.type.ValueType.BaseType;
import static arma.orinocosqf.type.ValueType.typeEquivalent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Kayler
 * @since 11/19/2017
 */
public class ValueTypeEquivalenceTest {
	@Test
	public void typeEqual_code()  {
		assertTrue(typeEquivalent(BaseType.CODE, BaseType.CODE));
		assertTrue(typeEquivalent(BaseType.CODE, new ExpandedValueType(BaseType.CODE)));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.CODE), BaseType.CODE));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.CODE), new ExpandedValueType(BaseType.CODE)));

		assertTrue(typeEquivalent(BaseType.CODE, new CodeType(BaseType.NAMESPACE)));
		assertTrue(typeEquivalent(new CodeType(BaseType.NAMESPACE), BaseType.CODE));
		assertTrue(typeEquivalent(new CodeType(BaseType.NAMESPACE), new CodeType(BaseType.NAMESPACE)));
	}

	@Test
	public void typeEqual_number()  {
		assertTrue(typeEquivalent(BaseType.NUMBER, BaseType.NUMBER));
		assertTrue(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.NUMBER)));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.NUMBER), BaseType.NUMBER));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.NUMBER), new ExpandedValueType(BaseType.NUMBER)));
	}

	@Test
	public void typeEqual_object()  {
		assertTrue(typeEquivalent(BaseType.OBJECT, BaseType.OBJECT));
		assertTrue(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.OBJECT)));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.OBJECT), BaseType.OBJECT));
		assertTrue(typeEquivalent(new ExpandedValueType(BaseType.OBJECT), new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_code() {
		assertFalse(typeEquivalent(new CodeType(BaseType.NUMBER), new CodeType(BaseType.NAMESPACE)));
		assertFalse(typeEquivalent(new CodeType(BaseType.NAMESPACE), new CodeType(BaseType.NUMBER)));
		assertFalse(typeEquivalent(BaseType.NUMBER, new CodeType(BaseType.NUMBER)));
		assertFalse(typeEquivalent(new CodeType(BaseType.NUMBER), BaseType.NUMBER));
		assertFalse(typeEquivalent(BaseType.CODE, BaseType.NUMBER));
		assertFalse(typeEquivalent(BaseType.NUMBER, BaseType.CODE));
	}

	@Test
	public void typeNotEqual_number()  {
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.ARRAY)));
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.CONFIG)));
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.CODE)));
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_object()  {
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.ARRAY)));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.CONFIG)));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.CODE)));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.NUMBER)));
	}


	@Test
	public void typeEqual_array_empty()  {
		assertTrue(typeEquivalent(BaseType.ARRAY, new ExpandedValueType(true)));
		assertTrue(typeEquivalent(new ExpandedValueType(true), BaseType.ARRAY));
	}

	@Test
	public void typeNotEqual_array_empty()  {
		//these are not equal for sanity reasons. It doesn't really make sense to ever have an Expanded type of ARRAY
		//and would become a real pain in the butt to assume that ARRAY is equal to ExpandedValueType(ARRAY)
		assertFalse(typeEquivalent(BaseType.ARRAY, new ExpandedValueType(BaseType.ARRAY)));
		assertFalse(typeEquivalent(new ExpandedValueType(BaseType.ARRAY), BaseType.ARRAY));
	}

	@Test
	public void typeEqual_emptyUnboundedArray()  {
		//these are true because an unbounded array with no elements provided counts as an array of infinitely many things or nothing

		assertTrue(typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true)));
		assertTrue(typeEquivalent(new ExpandedValueType(true, BaseType.NUMBER), new ExpandedValueType(true)));
		assertTrue(typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true, BaseType.NUMBER)));
	}

	@Test
	public void typeNotEqual_emptyUnboundedArray()  {
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(true)));
		assertFalse(typeEquivalent(new ExpandedValueType(true), BaseType.NUMBER));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(true)));
		assertFalse(typeEquivalent(new ExpandedValueType(true), BaseType.OBJECT));
	}

	@Test
	public void typeEquivalence_emptyBoundedExpandedType()  {
		assertFalse(typeEquivalent(new ExpandedValueType(false), BaseType.NUMBER));
		assertFalse(typeEquivalent(new ExpandedValueType(false), BaseType.OBJECT));
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(false)));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(false)));

		assertTrue(typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false)));
		assertTrue(typeEquivalent(BaseType.ARRAY, new ExpandedValueType(false)));
		assertTrue(typeEquivalent(new ExpandedValueType(false), BaseType.ARRAY));
	}

	@Test
	public void typeEqual_notEmpty_unboundedArray()  {
		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT)
		));

		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE),
				new ExpandedValueType(true, BaseType.CODE)
		));

		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT, BaseType.OBJECT)
		));
		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE, BaseType.OBJECT, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.CODE, BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_unboundedArray()  {
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.OBJECT)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT, BaseType.CODE)
		));
		assertFalse(typeEquivalent(BaseType.NUMBER, new ExpandedValueType(true, BaseType.NUMBER)));
		assertFalse(typeEquivalent(new ExpandedValueType(true, BaseType.NUMBER), BaseType.NUMBER));
		assertFalse(typeEquivalent(BaseType.OBJECT, new ExpandedValueType(true)));
		assertFalse(typeEquivalent(new ExpandedValueType(true, BaseType.OBJECT), BaseType.OBJECT));
	}

	@Test
	public void typeEqual_notEmpty_array()  {
		assertTrue(typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)
		));
		assertTrue(typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_array()  {
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT, BaseType.OBJECT)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.OBJECT)
		));

		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE)
		));

		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT)
		));
	}

	@Test
	public void typeEqual_singleton()  {
		assertTrue(typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertTrue(typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(true, BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_singleton()  {
		assertFalse(typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));

		assertFalse(typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				BaseType.CODE
		));
		assertFalse(typeEquivalent(
				BaseType.CODE,
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_largerSecondArray()  {
		//If the length of the second array is >= to the first array, they are considered equal.
		//The reason for this is that the first array specifies the minimum requirements. Anything after the requirements,
		//can safely be ignored.

		assertTrue(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER, BaseType.NUMBER)
		));

		assertTrue(typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_largerFirstArray()  {
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
		));
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_nestedArray()  {
		assertTrue(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertTrue(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertTrue(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));
	}

	@Test
	public void typeNotEqual_nestedArray()  {
		assertFalse(typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE)
		));

		assertFalse(typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(true, BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_expandedAndLookup()  {
		assertTrue(typeEquivalent(BaseType.ANYTHING, BaseType.ANYTHING.getExpanded()));
		assertTrue(typeEquivalent(BaseType.ANYTHING.getExpanded(), BaseType.ANYTHING));
		assertTrue(typeEquivalent(BaseType._VARIABLE, BaseType._VARIABLE.getExpanded()));
		assertTrue(typeEquivalent(BaseType._VARIABLE.getExpanded(), BaseType._VARIABLE));

		assertTrue(typeEquivalent(BaseType.NUMBER.getExpanded(), BaseType.NUMBER));
		assertTrue(typeEquivalent(BaseType.NUMBER, BaseType.NUMBER.getExpanded()));
	}

	@Test
	public void typeEqual_polymorphTypes()  {
		assertTrue(typeEquivalent(BaseType.ANYTHING, new PolymorphicWrapperValueType(BaseType.ANYTHING)));
		assertTrue(typeEquivalent(new PolymorphicWrapperValueType(BaseType.ANYTHING), BaseType.ANYTHING));
		assertTrue(typeEquivalent(new PolymorphicWrapperValueType(BaseType.NUMBER), BaseType.NUMBER));
		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(BaseType.NUMBER);
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CONFIG));

			assertTrue(typeEquivalent(BaseType.ANYTHING, wrap));
			assertTrue(typeEquivalent(wrap, BaseType.ANYTHING));
			assertTrue(typeEquivalent(BaseType.NUMBER, wrap));
			assertTrue(typeEquivalent(wrap, BaseType.NUMBER));
			assertTrue(typeEquivalent(BaseType.CONFIG, wrap));
			assertTrue(typeEquivalent(wrap, BaseType.CONFIG));
		}

		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER));
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CONFIG));

			assertTrue(typeEquivalent(BaseType.ANYTHING, wrap));
			assertTrue(typeEquivalent(wrap, BaseType.ANYTHING));
			assertTrue(typeEquivalent(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER), wrap));
			assertTrue(typeEquivalent(wrap, new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)));
			assertTrue(typeEquivalent(BaseType.CONFIG, wrap));
			assertTrue(typeEquivalent(wrap, BaseType.CONFIG));
		}
	}

	@Test
	public void typeNotEqual_polymorphTypes()  {
		assertFalse(typeEquivalent(BaseType.NUMBER, new PolymorphicWrapperValueType(BaseType.CONTROL)));
		assertFalse(typeEquivalent(new PolymorphicWrapperValueType(BaseType.CONTROL), BaseType.NUMBER));
		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(BaseType.CONTROL);
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.DISPLAY));

			assertFalse(typeEquivalent(BaseType.NUMBER, wrap));
			assertFalse(typeEquivalent(wrap, BaseType.NUMBER));
			assertFalse(typeEquivalent(BaseType.CONFIG, wrap));
			assertFalse(typeEquivalent(wrap, BaseType.CONFIG));
		}

		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(new ExpandedValueType(false, BaseType.DISPLAY, BaseType.DISPLAY));
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CODE));

			assertFalse(typeEquivalent(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER), wrap));
			assertFalse(typeEquivalent(wrap, new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)));
			assertFalse(typeEquivalent(BaseType.CONFIG, wrap));
			assertFalse(typeEquivalent(wrap, BaseType.CONFIG));
		}
	}

}