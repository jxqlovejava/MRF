package com.nali.mrfcore.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;

/**
 * Reflect Utility
 * @author will
 * 
 */
public class ReflectUtil {

	/**
	 * Get field according to field name
	 * 
	 * @param c
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class c, String fieldName) {
		if (c == null || StringUtils.isEmpty(fieldName)) {
			return null;
		}

		Field field = null;
		Field[] fs = c.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			if (fieldName.equals(fs[i].getName())) {
				field = fs[i];
				break;
			}
		}

		return field;
	}

	/**
	 * Get specified instance's field value
	 * @param fieldName
	 * @param instance
	 * @param fieldOwnerClass field's owner class, if set null, indicate that field's owner class is deduced from instance
	 * @return field value
	 */
	public static Object getFieldValue(String fieldName, Object instance, Class fieldOwnerClass)  {
		Object fieldVal = null;
		if (instance != null && !StringUtils.isEmpty(fieldName)) {
			Field field = getField(fieldOwnerClass == null ? instance.getClass() : fieldOwnerClass, fieldName);
			
			if(field != null) {
				Method getMethod = null;
				String getMethodName = "";
				
				// Get the getter name, boolean/Boolean type field should be treated separately
				if(field.getType().getName().toLowerCase().contains("boolean")) {
					getMethodName = fieldName;
				}
				else {
					getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				}
				
				try {
					getMethod = getGetMethod(getMethodName, instance);
					if(getMethod != null && Modifier.isPublic(getMethod.getModifiers())
							&& getMethod.getParameterTypes().length == 0) {   // contain the field's getter method
						fieldVal = getMethod.invoke(instance, new Object[] {});
					}
				} catch (Exception e) {
					// do nothing
				} finally {
					if(fieldVal == null) {   // Getter method counldn't return value, use field.invoke(Object) instead
						if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
							field.setAccessible(true);
						}
  						
						try {
							fieldVal = field.get(instance);
						} catch (Exception e) {
							// do nothing
						}
						
/*						if(fieldVal == null) {   // after all these, fieldVal is still null
							// set default value according to field's type
							// but may be confused with normal case. 
							// add an indicater flag parameter to indicate that if to use default value if null?
						}*/
					}
				}
			}   // if(field != null) close
		}
		
		return fieldVal;
	}
	
	
	/**
	 * Acquire the setter method(POJO write method)
	 * @param setMethodName
	 * @param instance
	 * @return
	 * @throws IntrospectionException
	 */
	public static Method getSetMethod(String setMethodName, Object instance) throws IntrospectionException {
		if(instance != null) {
			BeanInfo bi = Introspector.getBeanInfo(instance.getClass());
			PropertyDescriptor[] propDescs = bi.getPropertyDescriptors();
			
			return getSetMethod(setMethodName, propDescs);
		}
		
		return null;
	}

	/**
	 * Acquire the setter method(POJO write method)
	 * @param setMethodName
	 * @param props
	 * @return
	 */
	public static Method getSetMethod(String setMethodName, PropertyDescriptor[] props) {
		for (int i = 0; i < props.length; i++) {
			Method wMethod = props[i].getWriteMethod();
			if (wMethod != null && wMethod.getName().equals(setMethodName)) {
				return wMethod;
			}
		}
		
		return null;
	}
	
	/**
	 * Acquire the getter method(POJO read method)
	 * @param getMethodName
	 * @param instance
	 * @return
	 * @throws IntrospectionException
	 */
	public static Method getGetMethod(String getMethodName, Object instance) throws IntrospectionException {
		if(instance != null) {
			BeanInfo bi = Introspector.getBeanInfo(instance.getClass());
			PropertyDescriptor[] propDescs = bi.getPropertyDescriptors();
			
			return getGetMethod(getMethodName, propDescs);
		}
		
		return null;
	}
	
	/**
	 * Acquire the getter method(POJO read method)
	 * @param getMethodName
	 * @param props
	 * @return
	 */
	public static Method getGetMethod(String getMethodName, PropertyDescriptor[] props) {
		for(int i = 0; i < props.length; i++) {
			Method rMethod = props[i].getReadMethod();
			if(rMethod != null && rMethod.getName().equals(getMethodName)) {
				return rMethod;
			}
		}
		
		return null;
	}

}
