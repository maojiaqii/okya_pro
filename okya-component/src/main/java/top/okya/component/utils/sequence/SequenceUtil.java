package top.okya.component.utils.sequence;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.okya.component.constants.RedisConstants;
import top.okya.component.domain.Sequence;
import top.okya.component.enums.SequenceExceptionType;
import top.okya.component.exception.SequenceException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.redis.JedisUtil;
import top.okya.component.utils.redis.RedisLockUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 15:24
 * @describe： 序列生成器（redis）
 */

@Component
@ConfigurationProperties(prefix = "sequence")
@Slf4j
public class SequenceUtil {

    private static final int TIME_OUT = 30000;

    public void setList(List<Sequence> list) {
        this.list = list;
    }

    private List<Sequence> list;

    @Resource
    private JedisUtil jedisUtil;

    private static JedisUtil redisUtil;

    private static List<Sequence> sequenceList;

    @PostConstruct
    private void init() {
        log.info("初始化 SequenceUtil；规则列表：{}", list);
        redisUtil = jedisUtil;
        sequenceList = list;
    }

    /**
     * 从redis缓存中获取序列，获取超时时间默认30s
     *
     * @param sequenceKey redis缓存中的序列的key
     * @return 最终序列结果
     * @throws SequenceException 序列器自定义异常
     */
    public static String redisSequence(String sequenceKey) throws SequenceException {

        Sequence sequence = sequenceList.stream().filter(i -> sequenceKey.equals(i.getKey())).findFirst().orElse(null);
        if (sequence == null) {
            throw new SequenceException(SequenceExceptionType.UNKNOWN_KEY, sequenceKey);
        }
        if (sequence.getLength() == 0L) {
            throw new SequenceException(SequenceExceptionType.MISSING_LENGTH);
        }
        if (sequence.getMaxValue() <= sequence.getMinValue()) {
            throw new SequenceException(SequenceExceptionType.ILLEGAL_RANGE, sequence.getMaxValue(), sequence.getMinValue());
        }
        if (Math.abs(sequence.getMaxValue() - sequence.getMinValue()) < Math.abs(sequence.getStep())) {
            throw new SequenceException(SequenceExceptionType.ILLEGAL_STEP);
        }

        //开始时间
        long startTime = System.currentTimeMillis();

        //30秒内继续获取锁
        while ((startTime + TIME_OUT) >= System.currentTimeMillis()) {
            if (RedisLockUtils.tryLock(RedisConstants.LOCK_KEY_PREFIX + sequenceKey)) {
                try {
                    // 检查是否放入缓存
                    if (sequence.getInitialValue() != null && !redisUtil.hasKey(RedisConstants.SEQUENCE_KEY_PREFIX + sequenceKey)) {
                        redisUtil.set(RedisConstants.SEQUENCE_KEY_PREFIX + sequenceKey, sequence.getInitialValue(), StringUtils.isNotBlank(sequence.getUpdateInterval()) ? DateFormatUtil.getRemainMilliSeconds(sequence.getUpdateInterval()) : -1);
                    }
                    Long next = redisUtil.incrBy(RedisConstants.SEQUENCE_KEY_PREFIX + sequenceKey, sequence.getStep());
                    // 检查上下限
                    if (next > sequence.getMaxValue() || next < sequence.getMinValue()) {
                        if (!sequence.isCircle()) {
                            throw new SequenceException(SequenceExceptionType.OUT_OF_RANGE, sequence.getMaxValue(), sequence.getMinValue());
                        } else {
                            redisUtil.set(RedisConstants.SEQUENCE_KEY_PREFIX + sequenceKey, sequence.getInitialValue(), StringUtils.isNotBlank(sequence.getUpdateInterval()) ? DateFormatUtil.getRemainMilliSeconds(sequence.getUpdateInterval()) : -1);
                            next = redisUtil.incrBy(RedisConstants.SEQUENCE_KEY_PREFIX + sequenceKey, sequence.getStep());
                        }
                    }
                    // 检查长度
                    int nextLength = String.valueOf(next).length();
                    if (nextLength > sequence.getLength()) {
                        throw new SequenceException(SequenceExceptionType.ILLEGAL_LENGTH);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(sequence.getPrefix())
                            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern(sequence.getPattern())))
                            .append(String.format("%0" + sequence.getLength() + "d", next));
                    return sb.toString();
                } catch (Exception e) {
                    throw new SequenceException(SequenceExceptionType.SERVER_EXCEPTION, e.toString());
                } finally {
                    RedisLockUtils.unlock(RedisConstants.LOCK_KEY_PREFIX + sequenceKey);
                }
            }
        }
        throw new SequenceException(SequenceExceptionType.TIME_OUT);
    }

}
