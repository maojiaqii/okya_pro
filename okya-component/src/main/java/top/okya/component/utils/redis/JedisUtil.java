package top.okya.component.utils.redis;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * @author: maojiaqi
 * @Date: 2022/9/13 15:47
 * @describe: Redis工具类
 */

@Component
@Slf4j
public class JedisUtil<T> {

    @Autowired
    JedisPool jedisPool;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(毫秒)
     * @return
     */
    public boolean expire(String key, long time) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            if (time > 0) {
                jedis = jedisPool.getResource();
                jedis.pexpire(key, time);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("set expire error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(毫秒) 返回0代表为永久有效
     */
    public Long getExpire(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get expire error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            flag = jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("check key exists error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public boolean del(String... key) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("delete key error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return result;
    }

    /**
     * 普通缓存获取，返回转换为指定对象
     *
     * @param key 键
     * @return 值
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        T result = null;
        try {
            jedis = jedisPool.getResource();
            //redis中存的是String，取出redis的value后，需要反序列化为bean
            result = stringToBean(jedis.get(key), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return result;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, T value) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            //java中是bean对象，需要序列化为String，再存入redis
            String str = beanToString(value);
            if (str != null && str.length() > 0) {
                jedis.set(key, str);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("set value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(毫秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, T value, long time) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            //java中是bean对象，需要序列化为String，再存入redis
            String str = beanToString(value);
            if (str != null && str.length() > 0) {
                jedis.psetex(key, time, str);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("set value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long decrBy(String key, long delta) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.decrBy(key, delta);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("decr value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return result;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public Long incrBy(String key, long delta) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.incrBy(key, delta);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("incr value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return result;
    }

    /**
     * 为指定的 key 追加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean append(String key, String value) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = jedisPool.getResource();
            jedis.append(key, value);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("append value error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return flag;
    }

    /**
     * 根据指定的 key 截取值
     *
     * @param key   键
     * @param start 开始
     * @return
     * @end end 结束
     */
    public String substr(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.substr(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("substr value error: " + e.getMessage());
            return null;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 模糊查询获取key值
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        Set<String> result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get keys error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return result;
    }

    /**
     * 查询set集合数据
     *
     * @param key
     * @return
     */
    public Set<String> getSet(String key) {
        Jedis jedis = null;
        Set<String> set = null;
        try {
            jedis = jedisPool.getResource();
            set = jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get set error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return set;
    }

    /**
     * 往set中添加数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long addSet(String key, String... values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("add set error: " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return 0L;
    }

    /**
     * 删除数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long delSet(String key, String... values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("del set error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return 0L;
    }

    /**
     * 求第一个key与其他key不同的部分
     *
     * @param keys
     * @return
     */
    public Set<String> getDiffSet(String... keys) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sdiff(keys);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get diff set error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    /**
     * 求key的合集
     *
     * @param keys
     * @return
     */
    public Set<String> getUnionSet(String... keys) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sunion(keys);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get union set error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    /**
     * 求key的交集
     *
     * @param keys
     * @return
     */
    public Set<String> getInterSet(String... keys) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sinter(keys);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get inter set error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    /**
     * 获取key的长度
     *
     * @param key
     * @return
     */
    public Long getSetCount(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("get set count error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return 0L;
    }

    /**
     * 判断值是否存在
     *
     * @param key
     * @param value
     * @return
     */
    public boolean checkValueIsInSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("check member is in set error : " + e.getMessage());
        } finally {
            returnToPool(jedis);
        }
        return false;
    }

    private String beanToString(T t) {
        if (t == null) {
            return null;
        }
        Class clazz = t.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return t + "";
        } else if (clazz == long.class || clazz == Long.class) {
            return t + "";
        } else if (clazz == String.class) {
            return (String) t;
        } else {
            return JSON.toJSONString(t);
        }
    }

    private <T> T stringToBean(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(value);
        } else if (clazz == String.class) {
            return (T) value;
        } else {
            return JSON.parseObject(value, clazz);
        }
    }

    /**
     * 关闭JedisPool
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        jedis.close();
    }
}
