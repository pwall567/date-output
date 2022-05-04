/*
 * @(#) DateOutput.java
 *
 * date-output  Date output functions
 * Copyright (c) 2022 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.IntConsumer;

/**
 * A set of static functions to output date/time classes in RFC 3339 format.
 *
 * @author  Peter Wall
 */
public class DateOutput {

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLIS_PER_DAY = SECONDS_PER_DAY * 1000;
    private static final int MILLIS_PER_HOUR = SECONDS_PER_HOUR * 1000;
    private static final int MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * 1000;
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int NANOS_PER_MILLI = 1_000_000;

    /**
     * Append a {@link Date} to an {@link Appendable}.  Because a {@link Date} does not include time zone information,
     * UTC+00:00 is assumed.
     *
     * @param   a               the {@link Appendable}
     * @param   date            the {@link Date}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendDate(Appendable a, Date date) throws IOException {
        long millis = date.getTime();
        long days = Math.floorDiv(millis, MILLIS_PER_DAY);
        appendLocalDate(a, LocalDate.ofEpochDay(days));
        a.append('T');
        int m = (int)(millis - (days * MILLIS_PER_DAY));
        int hours = m / MILLIS_PER_HOUR;
        m -= hours * MILLIS_PER_HOUR;
        int minutes = m / MILLIS_PER_MINUTE;
        m -= minutes * MILLIS_PER_MINUTE;
        int seconds = m / MILLIS_PER_SECOND;
        m -= seconds * MILLIS_PER_SECOND;
        appendTime(a, hours, minutes, seconds, m * NANOS_PER_MILLI);
        a.append('Z');
    }

    /**
     * Append a {@link Calendar} to an {@link Appendable}.
     *
     * @param   a               the {@link Appendable}
     * @param   calendar        the {@link Calendar}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendCalendar(Appendable a, Calendar calendar) throws IOException {
        appendYearValue(a, calendar.get(Calendar.YEAR));
        a.append('-');
        IntOutput.append2Digits(a, calendar.get(Calendar.MONTH) + 1);
        a.append('-');
        IntOutput.append2Digits(a, calendar.get(Calendar.DAY_OF_MONTH));
        a.append('T');
        appendTime(a, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND) * NANOS_PER_MILLI);
        int offsetMillis = calendar.get(Calendar.ZONE_OFFSET) +
                (calendar.getTimeZone().inDaylightTime(calendar.getTime()) ? calendar.get(Calendar.DST_OFFSET) : 0);
        appendOffset(a, offsetMillis / 1000);
    }

    /**
     * Append an {@link Instant} to an {@link Appendable}.
     *
     * @param   a               the {@link Appendable}
     * @param   instant         the {@link Instant}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendInstant(Appendable a, Instant instant) throws IOException {
        long epochSeconds = instant.getEpochSecond();
        long days = Math.floorDiv(epochSeconds, SECONDS_PER_DAY);
        appendLocalDate(a, LocalDate.ofEpochDay(days));
        a.append('T');
        int seconds = (int)(epochSeconds - (days * SECONDS_PER_DAY));
        int hours = seconds / SECONDS_PER_HOUR;
        seconds -= hours * SECONDS_PER_HOUR;
        int minutes = seconds / SECONDS_PER_MINUTE;
        seconds -= minutes * SECONDS_PER_MINUTE;
        appendTime(a, hours, minutes, seconds, instant.getNano());
        a.append('Z');
    }

    /**
     * Append a {@link ZonedDateTime} to an {@link Appendable}.  There is no RFC 3339 representation for the zone name
     * component of {@link ZonedDateTime}, so the object is output as an {@link OffsetDateTime}.
     *
     * @param   a               the {@link Appendable}
     * @param   zonedDateTime   the {@link ZonedDateTime}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendZonedDateTime(Appendable a, ZonedDateTime zonedDateTime) throws IOException {
        appendLocalDateTime(a, zonedDateTime.toLocalDateTime());
        appendOffset(a, zonedDateTime.getOffset().getTotalSeconds());
    }

    /**
     * Append an {@link OffsetDateTime} to an {@link Appendable}.
     *
     * @param   a               the {@link Appendable}
     * @param   offsetDateTime  the {@link OffsetDateTime}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendOffsetDateTime(Appendable a, OffsetDateTime offsetDateTime) throws IOException {
        appendLocalDateTime(a, offsetDateTime.toLocalDateTime());
        appendOffset(a, offsetDateTime.getOffset().getTotalSeconds());
    }

    /**
     * Append an {@link OffsetTime} to an {@link Appendable}.
     *
     * @param   a               the {@link Appendable}
     * @param   offsetTime      the {@link OffsetTime}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendOffsetTime(Appendable a, OffsetTime offsetTime) throws IOException {
        appendLocalTime(a, offsetTime.toLocalTime());
        appendOffset(a, offsetTime.getOffset().getTotalSeconds());
    }

    /**
     * Append a {@link LocalDateTime} to an {@link Appendable}.
     *
     * @param   a               the {@link Appendable}
     * @param   localDateTime   the {@link LocalDateTime}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLocalDateTime(Appendable a, LocalDateTime localDateTime) throws IOException {
        appendLocalDate(a, localDateTime.toLocalDate());
        a.append('T');
        appendLocalTime(a, localDateTime.toLocalTime());
    }

    /**
     * Append a {@link LocalDate} to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   localDate   the {@link LocalDate}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLocalDate(Appendable a, LocalDate localDate) throws IOException {
        appendYearValue(a, localDate.getYear());
        a.append('-');
        IntOutput.append2Digits(a, localDate.getMonthValue());
        a.append('-');
        IntOutput.append2Digits(a, localDate.getDayOfMonth());
    }

    /**
     * Append a {@link LocalTime} to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   localTime   the {@link LocalTime}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLocalTime(Appendable a, LocalTime localTime) throws IOException {
        appendTime(a, localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano());
    }

    /**
     * Append a {@link Year} to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   year        the {@link Year}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendYear(Appendable a, Year year) throws IOException {
        appendYearValue(a, year.getValue());
    }

    /**
     * Append a {@link YearMonth} to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   yearMonth   the {@link YearMonth}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendYearMonth(Appendable a, YearMonth yearMonth) throws IOException {
        appendYearValue(a, yearMonth.getYear());
        a.append('-');
        IntOutput.append2Digits(a, yearMonth.getMonthValue());
    }

    /**
     * Append a {@link MonthDay} to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   monthDay    the {@link MonthDay}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendMonthDay(Appendable a, MonthDay monthDay) throws IOException {
        a.append('-');
        a.append('-');
        IntOutput.append2Digits(a, monthDay.getMonthValue());
        a.append('-');
        IntOutput.append2Digits(a, monthDay.getDayOfMonth());
    }

    private static void appendYearValue(Appendable a, int year) throws IOException {
        int value = Math.min(year > 0 ? year : -year + 1, 9999);
        int century = value / 100;
        IntOutput.append2Digits(a, century);
        IntOutput.append2Digits(a, value - (century * 100));
    }

    private static void appendTime(Appendable a, int hours, int minutes, int seconds, int nanos) throws IOException {
        IntOutput.append2Digits(a, hours);
        a.append(':');
        IntOutput.append2Digits(a, minutes);
        a.append(':');
        IntOutput.append2Digits(a, seconds);
        if (nanos > 0) {
            a.append('.');
            int digits = nanos / 1_000_000;
            IntOutput.append3Digits(a, digits);
            nanos -= digits * 1_000_000;
            if (nanos > 0) {
                digits = nanos / 1_000;
                IntOutput.append3Digits(a, digits);
                nanos -= digits * 1_000;
                if (nanos > 0)
                    IntOutput.append3Digits(a, nanos);
            }
        }
    }

    private static void appendOffset(Appendable a, int seconds) throws IOException {
        if (seconds == 0)
            a.append('Z');
        else {
            a.append(seconds < 0 ? '-' : '+');
            int s = Math.abs(seconds);
            int hours = s / SECONDS_PER_HOUR;
            s -= hours * SECONDS_PER_HOUR;
            int minutes = s / SECONDS_PER_MINUTE;
            IntOutput.append2Digits(a, hours);
            a.append(':');
            IntOutput.append2Digits(a, minutes);
        }
    }

    /**
     * Output a {@link Date} using an {@link IntConsumer}.  Because a {@link Date} does not include time zone
     * information, UTC+00:00 is assumed.
     *
     * @param   date            the {@link Date}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputDate(Date date, IntConsumer consumer) {
        long millis = date.getTime();
        long days = Math.floorDiv(millis, MILLIS_PER_DAY);
        outputLocalDate(LocalDate.ofEpochDay(days), consumer);
        consumer.accept('T');
        int m = (int)(millis - (days * MILLIS_PER_DAY));
        int hours = m / MILLIS_PER_HOUR;
        m -= hours * MILLIS_PER_HOUR;
        int minutes = m / MILLIS_PER_MINUTE;
        m -= minutes * MILLIS_PER_MINUTE;
        int seconds = m / MILLIS_PER_SECOND;
        m -= seconds * MILLIS_PER_SECOND;
        outputTime(hours, minutes, seconds, m * NANOS_PER_MILLI, consumer);
        consumer.accept('Z');
    }

    /**
     * Output a {@link Calendar} using an {@link IntConsumer}.
     *
     * @param   calendar        the {@link Calendar}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputCalendar(Calendar calendar, IntConsumer consumer) {
        outputYearValue(calendar.get(Calendar.YEAR), consumer);
        consumer.accept('-');
        IntOutput.output2Digits(calendar.get(Calendar.MONTH) + 1, consumer);
        consumer.accept('-');
        IntOutput.output2Digits(calendar.get(Calendar.DAY_OF_MONTH), consumer);
        consumer.accept('T');
        outputTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND) * NANOS_PER_MILLI, consumer);
        int offsetMillis = calendar.get(Calendar.ZONE_OFFSET) +
                (calendar.getTimeZone().inDaylightTime(calendar.getTime()) ? calendar.get(Calendar.DST_OFFSET) : 0);
        outputOffset(offsetMillis / 1000, consumer);
    }

    /**
     * Output an {@link Instant} using an {@link IntConsumer}.
     *
     * @param   instant         the {@link Instant}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputInstant(Instant instant, IntConsumer consumer) {
        long epochSeconds = instant.getEpochSecond();
        long days = Math.floorDiv(epochSeconds, SECONDS_PER_DAY);
        outputLocalDate(LocalDate.ofEpochDay(days), consumer);
        consumer.accept('T');
        int seconds = (int)(epochSeconds - (days * SECONDS_PER_DAY));
        int hours = seconds / SECONDS_PER_HOUR;
        seconds -= hours * SECONDS_PER_HOUR;
        int minutes = seconds / SECONDS_PER_MINUTE;
        seconds -= minutes * SECONDS_PER_MINUTE;
        outputTime(hours, minutes, seconds, instant.getNano(), consumer);
        consumer.accept('Z');
    }

    /**
     * Output a {@link ZonedDateTime} using an {@link IntConsumer}.  There is no RFC 3339 representation for the zone
     * name component of {@link ZonedDateTime}, so the object is output as an {@link OffsetDateTime}.
     *
     * @param   zonedDateTime   the {@link ZonedDateTime}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputZonedDateTime(ZonedDateTime zonedDateTime, IntConsumer consumer) {
        outputLocalDateTime(zonedDateTime.toLocalDateTime(), consumer);
        outputOffset(zonedDateTime.getOffset().getTotalSeconds(), consumer);
    }

    /**
     * Output an {@link OffsetDateTime} using an {@link IntConsumer}.
     *
     * @param   offsetDateTime  the {@link OffsetDateTime}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputOffsetDateTime(OffsetDateTime offsetDateTime, IntConsumer consumer) {
        outputLocalDateTime(offsetDateTime.toLocalDateTime(), consumer);
        outputOffset(offsetDateTime.getOffset().getTotalSeconds(), consumer);
    }

    /**
     * Output an {@link OffsetTime} using an {@link IntConsumer}.
     *
     * @param   offsetTime      the {@link OffsetTime}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputOffsetTime(OffsetTime offsetTime, IntConsumer consumer) {
        outputLocalTime(offsetTime.toLocalTime(), consumer);
        outputOffset(offsetTime.getOffset().getTotalSeconds(), consumer);
    }

    /**
     * Output a {@link LocalDateTime} using an {@link IntConsumer}.
     *
     * @param   localDateTime   the {@link LocalDateTime}
     * @param   consumer        the {@link IntConsumer}
     */
    public static void outputLocalDateTime(LocalDateTime localDateTime, IntConsumer consumer) {
        outputLocalDate(localDateTime.toLocalDate(), consumer);
        consumer.accept('T');
        outputLocalTime(localDateTime.toLocalTime(), consumer);
    }

    /**
     * Output a {@link LocalDate} using an {@link IntConsumer}.
     *
     * @param   localDate   the {@link LocalDate}
     * @param   consumer    the {@link IntConsumer}
     */
    public static void outputLocalDate(LocalDate localDate, IntConsumer consumer) {
        outputYearValue(localDate.getYear(), consumer);
        consumer.accept('-');
        IntOutput.output2Digits(localDate.getMonthValue(), consumer);
        consumer.accept('-');
        IntOutput.output2Digits(localDate.getDayOfMonth(), consumer);
    }

    /**
     * Output a {@link LocalTime} using an {@link IntConsumer}.
     *
     * @param   localTime   the {@link LocalTime}
     * @param   consumer    the {@link IntConsumer}
     */
    public static void outputLocalTime(LocalTime localTime, IntConsumer consumer) {
        outputTime(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano(), consumer);
    }

    /**
     * Output a {@link Year} using an {@link IntConsumer}.
     *
     * @param   year        the {@link Year}
     * @param   consumer    the {@link IntConsumer}
     */
    public static void outputYear(Year year, IntConsumer consumer) {
        outputYearValue(year.getValue(), consumer);
    }

    /**
     * Output a {@link YearMonth} using an {@link IntConsumer}.
     *
     * @param   yearMonth   the {@link YearMonth}
     * @param   consumer    the {@link IntConsumer}
     */
    public static void outputYearMonth(YearMonth yearMonth, IntConsumer consumer) {
        outputYearValue(yearMonth.getYear(), consumer);
        consumer.accept('-');
        IntOutput.output2Digits(yearMonth.getMonthValue(), consumer);
    }

    /**
     * Output a {@link MonthDay} using an {@link IntConsumer}.
     *
     * @param   monthDay    the {@link MonthDay}
     * @param   consumer    the {@link IntConsumer}
     */
    public static void outputMonthDay(MonthDay monthDay, IntConsumer consumer) {
        consumer.accept('-');
        consumer.accept('-');
        IntOutput.output2Digits(monthDay.getMonthValue(), consumer);
        consumer.accept('-');
        IntOutput.output2Digits(monthDay.getDayOfMonth(), consumer);
    }

    private static void outputYearValue(int year, IntConsumer consumer) {
        int value = Math.min(year > 0 ? year : -year + 1, 9999);
        int century = value / 100;
        IntOutput.output2Digits(century, consumer);
        IntOutput.output2Digits(value - (century * 100), consumer);
    }

    private static void outputTime(int hours, int minutes, int seconds, int nanos, IntConsumer consumer) {
        IntOutput.output2Digits(hours, consumer);
        consumer.accept(':');
        IntOutput.output2Digits(minutes, consumer);
        consumer.accept(':');
        IntOutput.output2Digits(seconds, consumer);
        if (nanos > 0) {
            consumer.accept('.');
            int digits = nanos / 1_000_000;
            IntOutput.output3Digits(digits, consumer);
            nanos -= digits * 1_000_000;
            if (nanos > 0) {
                digits = nanos / 1_000;
                IntOutput.output3Digits(digits, consumer);
                nanos -= digits * 1_000;
                if (nanos > 0)
                    IntOutput.output3Digits(nanos, consumer);
            }
        }
    }

    private static void outputOffset(int seconds, IntConsumer consumer) {
        if (seconds == 0)
            consumer.accept('Z');
        else {
            consumer.accept(seconds < 0 ? '-' : '+');
            int s = Math.abs(seconds);
            int hours = s / SECONDS_PER_HOUR;
            s -= hours * SECONDS_PER_HOUR;
            int minutes = s / SECONDS_PER_MINUTE;
            IntOutput.output2Digits(hours, consumer);
            consumer.accept(':');
            IntOutput.output2Digits(minutes, consumer);
        }
    }

}
