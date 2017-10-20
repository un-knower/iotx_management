package com.anosi.asset.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class BeanRefUtil {

	/***
	 * set值
	 * 
	 * @param bean
	 * @param valMap
	 * @throws Exception
	 */
	public static void setValue(Object bean, Map<String, Object> valMap) throws Exception {
		Class<?> cls = bean.getClass();
		// 取出bean里的所有方法
		Method[] methods = cls.getDeclaredMethods();
		// 取出bean里的所有字段，包括父类
		List<Field> fields = new ArrayList<>();
		Class<?> tempClass = cls;
		while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
			fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
		}
		for (Field field : fields) {
			String fieldName = field.getName();
			if (valMap.containsKey(fieldName)) {
				String typeName = field.getType().getSimpleName();
				// 判断是否存在这个属性的set方法
				String fieldSetName = parSetName(fieldName);
				if (!checkSetMet(methods, fieldSetName)) {
					continue;
				}
				Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());

				Object value = null;
				boolean fieldHasAnno = field.isAnnotationPresent(ExtraName.class);
				if (fieldHasAnno) {
					value = valMap.get(field.getAnnotation(ExtraName.class).name());
				} else {
					value = valMap.get(fieldName);
				}

				if (Objects.equals("Date", typeName)) {
					value = new Date((long) value);
				}
				fieldSetMet.invoke(bean, value);
			}
		}
	}

	/**
	 * 判断是否存在某属性的 set方法
	 * 
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	private static boolean checkSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拼接在某属性的 set方法
	 * 
	 * @param fieldName
	 * @return String
	 */
	private static String parSetName(String fieldName) {
		if (StringUtils.isNoneBlank(fieldName)) {
			int startIndex = 0;
			if (fieldName.charAt(0) == '_')
				startIndex = 1;
			return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
					+ fieldName.substring(startIndex + 1);
		}
		return null;
	}

	/****
	 * 别名
	 * 
	 * @author jinyao
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	@Inherited
	public @interface ExtraName {

		String name() default "";

	}

}
