package com.anosi.asset.test;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestReflact {
	public static void main(String[] args) throws Exception {
		Class<?> clazz = Single.class;
		Single single = (Single) clazz.newInstance();
		List<String> list = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("hello");
				add("world");
				add("you");
			}
		};

		// 获取method两种方式， 在method中 数组的的空间大小是可以随便写的不一定使用0
		/* 1 */
		Method method = clazz.getDeclaredMethod("method", Array.newInstance(Object.class, 0).getClass());
		/* 2 */
		method = clazz.getDeclaredMethod("method", (new Object[0]).getClass());

		// 初始化参数
		/* 1 */
		Object objs = Array.newInstance(Object.class, 2);
		Array.set(objs, 0, list);
		Array.set(objs, 1, "23");
		method.invoke(single, objs);

		/* 2 */
		Object[] objects = { 1, "and", list };
		method.invoke(single, new Object[] { objects });
	}
	
	@Test
	public void test(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		System.out.println(list+"=========="+list!=null+"-----------"+(list.size()>0));
	}
	
}

class Single {
	public void method(Object... objs) {
		System.out.println(Arrays.deepToString(objs));
	}
}
