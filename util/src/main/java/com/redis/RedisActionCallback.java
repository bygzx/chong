package com.redis;

import redis.clients.jedis.Jedis;

/**
 * @author bygzx
 * @date 2019/5/14 14:22
 **/
public interface RedisActionCallback <T> {

    T call(Jedis jedis);

}
