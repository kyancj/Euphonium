package net.cancheta.ai.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class PrivateFieldUtils {
	private PrivateFieldUtils() {
	}

	/**
	 * Get the value of a private field if we only know it's exact type and the
	 * class that declared it.
	 * 
	 * @param o
	 *            The Object we want the filed value for.
	 * @param baseClass
	 *            The class that declared the field.
	 * @param fieldType
	 *            The exact type of the field declaration.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object o, Class<?> baseClass,
			Class<T> fieldType) {
		try {
			return (T)getField(o, baseClass, fieldType).get(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> Field getField(Object o, Class<?> baseClass,
			Class<T> fieldType) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!baseClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("Got a "
					+ o.getClass().getName() + " but expected a "
					+ baseClass.getName());
		}
		for (Field f : baseClass.getDeclaredFields()) {
			if (typeEquals(f.getType(), fieldType)
					&& !Modifier.isStatic(f.getModifiers())) {
			//try {
				f.setAccessible(true);
				return f;
			}// catch(Exception e) {
			//	continue;
			//}
		}

		throw new IllegalArgumentException("No field of type " + fieldType
				+ " in " + baseClass);
	}

	public static <T> void setFieldValue(Object o, Class<?> baseClass,
			Class<T> fieldType, T value) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!baseClass.isAssignableFrom(o.getClass())) {
			throw new IllegalArgumentException("Got a "
					+ o.getClass().getName() + " but expected a "
					+ baseClass.getName());
		}
		for (Field f : baseClass.getDeclaredFields()) {
			if (typeEquals(f.getType(), fieldType)
					&& !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				try {
					f.set(o, value);
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}
		}

		throw new IllegalArgumentException("No field of type " + fieldType
				+ " in " + baseClass);
	}

	public static boolean typeEquals(Class<?> a, Class<?> b) {
		return a.equals(b);
	}
}
