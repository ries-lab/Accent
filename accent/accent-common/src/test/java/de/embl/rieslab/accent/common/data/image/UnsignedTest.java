package de.embl.rieslab.accent.common.data.image;

import org.junit.Test;

public class UnsignedTest {

	@Test
	public void testShortToUnsignedInt() {
		// negative short are mapped to the upper int values
		for(int i =0;i<16;i++) {
			short s = (short) (-32768+4300*i);
			System.out.println(s+" to "+Short.toUnsignedInt(s));
		}
	}
	
	@Test
	public void testUnsignedIntToShort() {
		// negative short are mapped to the upper int values
		for(int i =0;i<16;i++) {
			Integer s = new Integer(4300*i);
			System.out.println(s+" to "+s.shortValue());
		}
	}
}
