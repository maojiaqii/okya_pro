package top.okya.component.utils.common;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/7/13 14:40
 * @describe： 日期工具类
 */
public class DateFormatUtil {

    private static final String DAY = "DAY";

    private static final String WEEK = "WEEK";

    private static final String MONTH = "MONTH";

    private static final String YEAR = "YEAR";

    /**
     * 获取当前系统时间
     *
     * @return 当前系统时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前系统时间
     *
     * @return 当前系统时间
     */
    public static Date nowDate() {
        return new Date();
    }

    /**
     * 毫秒转化为DATE
     *
     * @param millis 毫秒
     * @return 时间
     */
    public static Date millisToDate(Long millis) {
        Date date = new Date();
        date.setTime(millis);
        return date;
    }

    /**
     * 获取当前系统时间（毫秒）
     *
     * @return 当前系统时间（毫秒）
     */
    public static Long currentMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 格式化当前系统时间
     *
     * @param pattern 返回日期格式
     * @return 当前系统时间格式化字符串
     */
    public static String formatNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化毫秒时间
     *
     * @param millis 毫秒
     * @param pattern 返回日期格式
     * @return 时间格式化字符串
     */
    public static String formatMillis(Long millis, String pattern) {
        return new Date(millis).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @param count   当前系统时间的偏移量
     * @param unit    偏移单位
     * @param pattern 返回日期格式
     * @return 日期格式字符串
     */
    public static String formatDateTimeFromNow(Long count, TemporalUnit unit, String pattern) {
        return LocalDateTime.now().plus(count, unit).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取现在到指定下一个时间节点0点的毫秒数
     *
     * @param symbol 节点：DAY、WEEK、MONTH、YEAR
     * @return: 相差的毫秒数
     */
    public static long getRemainMilliSeconds(String symbol) {
        LocalDateTime midnight = LocalDateTime.now();
        if (Objects.equals(DAY, symbol.toUpperCase())) {
            midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0)
                    .withSecond(0).withNano(0);
        } else if (Objects.equals(WEEK, symbol.toUpperCase())) {
            midnight = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(0)
                    .withMinute(0).withSecond(0).withNano(0);
        } else if (Objects.equals(MONTH, symbol.toUpperCase())) {
            midnight = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0)
                    .withMinute(0).withSecond(0).withNano(0);
        } else if (Objects.equals(YEAR, symbol.toUpperCase())) {
            midnight = LocalDateTime.now().plusYears(1).withDayOfYear(1).withHour(0)
                    .withMinute(0).withSecond(0).withNano(0);
        }
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight) * 1000;
    }

}