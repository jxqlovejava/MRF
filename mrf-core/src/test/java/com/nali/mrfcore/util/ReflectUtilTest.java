package com.nali.mrfcore.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ReflectUtilTest {
	
	private Class cls;
	private Object obj1;
	private Object obj2;
	
	@Before
	public void setUp() {
		cls = ReflectObj.class;
		obj1 = new ReflectObj();
		obj2 = new ReflectObj(2, "Will");
	}

	@Test
	public void testGetField() {
		assertNull("inexistFileName field shouldn't exist!",
				ReflectUtil.getField(cls, "inexistFieldName"));
		
		assertNotNull("name field should exist!", 
				ReflectUtil.getField(cls, "name"));
	}
	
	@Test 
	public void testGetFieldValue() {
		assertEquals(null, ReflectUtil.getFieldValue("id", obj1, null));
		assertEquals(null, ReflectUtil.getFieldValue("name", obj1, null));
		
		assertEquals(2, ReflectUtil.getFieldValue("i", obj2, null));
		assertEquals("Will", ReflectUtil.getFieldValue("name", obj2, null));
	}

}
