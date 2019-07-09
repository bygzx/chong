package com.papa.util.serialze;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;


public class ArraySeriazationUtil {

	private static final int MAX_ARRAY_LENGTH = 1000;

//	public static boolean contailsNull(Object obj) {
//		Object[] objArr = (Object[]) obj;
//		for(int i=0;i<objArr.length;i++) {
//			if(objArr[i] == null) {
//				return true;
//			}
//		}
//		return false;
//	}


	public static byte[] serializeArray(Object obj) {
		Object[] objArr = (Object[]) obj;
		byte[] buffer = null;
		if(obj == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		ObjectOutputStream objOS = null;
		try {
			out = new ByteArrayOutputStream();
		    objOS = new ObjectOutputStream(out);
		    for(int i=0;i<objArr.length;i++) {
		    	 objOS.writeObject(objArr[i]);
		    }
		    buffer = out.toByteArray();
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			 try {
				objOS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer;
	}

	public static <T> T  deserializeArray(byte[] data, Class<T> clazz) {
		System.out.println(clazz);
		Class X = clazz.getComponentType();
//		System.out.println(clazz.getComponentType());
		if(data == null || data.length == 0) {
			return null;
		}
		Object[] ret = new Object[MAX_ARRAY_LENGTH];
		ObjectInputStream ois = null;
		 
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
			int i = 0;
			while(i < MAX_ARRAY_LENGTH) {
				try {
					Object o = ois.readObject();
					ret[i] = o;
					i ++;
				}catch(EOFException ex) {
					System.out.println("warn,EOFException,clazz.getComponentType():" + clazz.getComponentType());
					ret = Arrays.copyOfRange(ret, 0, i);
					break;
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			try {
				if(ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		List<Object> list = Arrays.asList(ret);
//		String[] array = new String[list.size()];
//		return (X[]) list.toArray(array);
		System.out.println("ret.length =" + ret.length);
		Object array = Array.newInstance(X, ret.length);
		for(int i=0;i<ret.length;i++) {
			Array.set(array, i, ret[i]);
		}
		return (T) array;
		// 赋值
//		Array.set(array, 0, 1);
//		Array.set(array, 1, 2);

	}

}
