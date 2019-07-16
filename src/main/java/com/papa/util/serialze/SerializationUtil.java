package com.papa.util.serialze;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.papa.util.http.IpPortPair;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 序列化工具类（基于 Protostuff 实现）
 *
 * @author eric
 * @since 1.0.0
 */
public class SerializationUtil {

    /**
     * 需要使用包装类进行序列化/反序列化的class集合
     */
    private static final Set<Class<?>> WRAPPER_SET = new HashSet<>();
    /**
     * 序列化/反序列化包装类 Class 对象
     */
    private static final Class<SerializeDeserializeBeanWrapper> WRAPPER_CLASS = SerializeDeserializeBeanWrapper.class;

    /**
     * 序列化/反序列化包装类 Schema 对象
     */
    private static final Schema<SerializeDeserializeBeanWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(WRAPPER_CLASS);

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    /**
     * 如果一个类没有参数为空的构造方法时候，
     * 那么你直接调用newInstance方法试图得到一个实例对象的时候是会抛出异常的。
     * 能不能有 办法绕过构造方法来实例化一个对象呢？!
     * Objenesis 内部根据得到平台提供的jvm版本和供应商来选择不同的实例化策略。
     */
    private static Objenesis objenesis = new ObjenesisStd(true);


    /**
     * 预定义一些Protostuff无法直接序列化/反序列化的对象
     */
    static {
        WRAPPER_SET.add(List.class);
        WRAPPER_SET.add(ArrayList.class);
        WRAPPER_SET.add(CopyOnWriteArrayList.class);
        WRAPPER_SET.add(LinkedList.class);
        WRAPPER_SET.add(Stack.class);
        WRAPPER_SET.add(Vector.class);

        WRAPPER_SET.add(Map.class);
        WRAPPER_SET.add(HashMap.class);
        WRAPPER_SET.add(TreeMap.class);
        WRAPPER_SET.add(Hashtable.class);
        WRAPPER_SET.add(SortedMap.class);
        //add luos 数组 String[] 20180806
        WRAPPER_SET.add(String[].class);
        WRAPPER_SET.add(Integer[].class);
        WRAPPER_SET.add(Long[].class);
        WRAPPER_SET.add(Object[].class);
        //add luos  数组 String[] 20180806
        
        WRAPPER_SET.add(Object.class);
    }

    private SerializationUtil() {
    }

    /**
     * 注册需要使用包装类进行序列化/反序列化的 Class 对象
     *
     * @param clazz 需要包装的类型 Class 对象
     */
    public static void registerWrapperClass(Class clazz) {
        WRAPPER_SET.add(clazz);
    }

    /**
     * 序列化（对象 -> 字节数组） 使用Protostuff来实现java对象的序列化
     *
     * @param obj 需要序列化的对象
     * @param <T> 序列化对象的类型
     * @return 序列化后的二进制数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        if(clazz.isArray()) {
        	return ArraySeriazationUtil.serializeArray(obj);
        }
        
        
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Object serializeObject = obj;
            Schema schema = WRAPPER_SCHEMA;
            if (!WRAPPER_SET.contains(clazz)) {
                schema = getSchema(clazz);
            } else {
                serializeObject = SerializeDeserializeBeanWrapper.builder(obj);
            }
            return ProtostuffIOUtil.toByteArray(serializeObject, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化对象 （字节数组 -> 对象）<br>使用Protostuff来实现java对象的反序列化
     *
     * @param data  需要反序列化的二进制数组
     * @param clazz 反序列化后的对象class
     * @param <T>   反序列化后的对象类型
     * @return 反序列化后的对象集合
     */
    @SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data, Class<T> clazz) {
    	//Class<T> clazz = (Class<T>) obj.getClass();
        if(clazz.isArray() ) {
        	return  ArraySeriazationUtil.deserializeArray(data,clazz);
        }
        
        try {
            if (!WRAPPER_SET.contains(clazz)) {
                T message = objenesis.newInstance(clazz);
                Schema<T> schema = getSchema(clazz);
                ProtostuffIOUtil.mergeFrom(data, message, schema);
                return message;
            } else {
                SerializeDeserializeBeanWrapper<T> wrapper = new SerializeDeserializeBeanWrapper<>();
                ProtostuffIOUtil.mergeFrom(data, wrapper, WRAPPER_SCHEMA);
                return wrapper.getData();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    /**
     * protostuff 内部核心逻辑； 从class 获取相应的类定义schema。
     * 内部缓存起来，以便提升效率；
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }

    
    public static void main(String[] args) {
		IpPortPair ipp = new IpPortPair("localhost", 8080);
		System.err.println(ipp);//out
		byte[] bytes = serialize(ipp);
		IpPortPair ipp2 = deserialize(bytes, IpPortPair.class);
		System.out.println(ipp2);
		
//		IpPortPair ipp3 = (IpPortPair) deserialize(bytes, Object.class);
//		System.out.println(ipp3);
	}
}
