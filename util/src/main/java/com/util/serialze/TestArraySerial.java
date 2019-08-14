package com.util.serialze;



import com.util.http.IpPortPair;

import java.util.Arrays;

public class TestArraySerial {

	public static void main(String[] args) {

		System.out.println("注意  序列化工具传值 null 会有问题");
		TestArraySerial test = new TestArraySerial();
		test.test0();

		System.out.println("-----------------------1");
		test.test1();
		
		System.out.println("-----------------------2");
		test.test2();
		
		System.out.println("-----------------------3");
		test.test3();
		System.out.println("-----------------------4");
		test.test4();
		System.out.println("-----------------------5");
		test.test5();
	}

	private void test0() {
		IpPortPair ipPortPair = new IpPortPair("localhost", 8080);
		byte[] datas = SerializationUtil.serialize(ipPortPair);
		System.out.println(datas);
		IpPortPair xx2 = SerializationUtil.deserialize(datas, IpPortPair.class);
		System.out.println(xx2);
		

	}

	private void test1() {

		String[] sa = new String[] { "abc", "def", "xyz" };
		byte[] bytes = SerializationUtil.serialize(sa);
		System.out.println(bytes);

		String[] sb = SerializationUtil.deserialize(bytes, sa.getClass());
		System.out.println(Arrays.toString(sb));
	}

	private void test2() {

		String[] sa = new String[] { null, "abc", "def", null, "xyz" };
		byte[] bytes = SerializationUtil.serialize(sa);
		System.out.println(bytes);

		String[] sb = SerializationUtil.deserialize(bytes, sa.getClass());
		System.out.println(Arrays.toString(sb));
	}

	private void test3() {

		Integer[] sa = new Integer[] { 1, 2, 3 };
		byte[] bytes = SerializationUtil.serialize(sa);
		System.out.println(bytes);

		Integer[] sb = SerializationUtil.deserialize(bytes, sa.getClass());
		System.out.println(Arrays.toString(sb));
	}

	private void test4() {

		Integer[] sa = new Integer[] { null, 1, 2, null, 3 };
		byte[] bytes = SerializationUtil.serialize(sa);
		System.out.println(bytes);
		System.err.println("sa.getClass().isArray():" + sa.getClass().isArray());

		Integer[] sb = SerializationUtil.deserialize(bytes, sa.getClass());
		System.out.println(Arrays.toString(sb));
	}

	private void test5() {

		Object[] sa = new Object[] { 12.00F, "abc",34L,null,"xyz", 56L };
		byte[] bytes = SerializationUtil.serialize(sa);
		System.out.println(bytes);
		System.err.println("sa.getClass().isArray():" + sa.getClass().isArray());

		Object[] sb = SerializationUtil.deserialize(bytes, sa.getClass());
		System.out.println(Arrays.toString(sb));
	}
}
