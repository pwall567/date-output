/*
 * @(#) DateOutputIntConsumerTest.java
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

package io.jstuff.util.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.IntConsumer;

import io.jstuff.util.DateOutput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateOutputIntConsumerTest {

    @Test
    public void shouldConvertDateUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        Instant instant = OffsetDateTime.of(2022, 4, 8, 18, 39, 2, 456_000_000, ZoneOffset.UTC).toInstant();
        DateOutput.outputDate(Date.from(instant), ic);
        assertEquals("2022-04-08T18:39:02.456Z", sb.toString());
    }

    @Test
    public void shouldConvertCalendarUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.DAY_OF_MONTH, 8);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 39);
        calendar.set(Calendar.SECOND, 2);
        calendar.set(Calendar.MILLISECOND, 456);
        calendar.set(Calendar.ZONE_OFFSET, 10 * 60 * 60 * 1000);
        DateOutput.outputCalendar(calendar, ic);
        assertEquals("2022-04-08T18:39:02.456+10:00", sb.toString());
    }

    @Test
    public void shouldConvertInstantUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.12");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(10));
        Instant instant = offsetDateTime.toInstant();
        DateOutput.outputInstant(instant, ic);
        assertEquals("2022-04-07T08:32:47.120Z", sb.toString());
    }

    @Test
    public void shouldConvertZonedDateTimeUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.55");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Australia/Sydney"));
        DateOutput.outputZonedDateTime(zonedDateTime, ic);
        assertEquals("2022-04-07T18:32:47.550+10:00", sb.toString());
    }

    @Test
    public void shouldConvertOffsetDateTimeUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.5446");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(10));
        DateOutput.outputOffsetDateTime(offsetDateTime, ic);
        assertEquals("2022-04-07T18:32:47.544600+10:00", sb.toString());
        sb.setLength(0);
        localDateTime = LocalDateTime.of(1999, 6, 1, 9, 15, 10, 456_000_000);
        offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        DateOutput.outputOffsetDateTime(offsetDateTime, ic);
        assertEquals("1999-06-01T09:15:10.456Z", sb.toString());
    }

    @Test
    public void shouldConvertOffsetTimeUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        OffsetTime offsetTime = OffsetTime.of(8, 27, 55, 544_233_100, ZoneOffset.ofHours(-5));
        DateOutput.outputOffsetTime(offsetTime, ic);
        assertEquals("08:27:55.544233100-05:00", sb.toString());
    }

    @Test
    public void shouldConvertLocalDateTimeUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalDateTime dateTime = LocalDateTime.parse("2022-04-07T18:32:47.544");
        DateOutput.outputLocalDateTime(dateTime, ic);
        assertEquals("2022-04-07T18:32:47.544", sb.toString());
        sb.setLength(0);
        dateTime = LocalDateTime.parse("1999-04-01T08:45");
        DateOutput.outputLocalDateTime(dateTime, ic);
        assertEquals("1999-04-01T08:45:00", sb.toString());
    }

    @Test
    public void shouldConvertLocalDateUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalDate date = LocalDate.of(2022, 4, 7);
        DateOutput.outputLocalDate(date, ic);
        assertEquals("2022-04-07", sb.toString());
        sb.setLength(0);
        date = LocalDate.parse("1999-12-31");
        DateOutput.outputLocalDate(date, ic);
        assertEquals("1999-12-31", sb.toString());
        sb.setLength(0);
        DateOutput.outputLocalDate(date.plusDays(1), ic);
        assertEquals("2000-01-01", sb.toString());
    }

    @Test
    public void shouldConvertLocalTimeUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        LocalTime time = LocalTime.of(14, 3, 0);
        DateOutput.outputLocalTime(time, ic);
        assertEquals("14:03:00", sb.toString());
        sb.setLength(0);
        time = LocalTime.parse("09:31:27");
        DateOutput.outputLocalTime(time, ic);
        assertEquals("09:31:27", sb.toString());
        sb.setLength(0);
        DateOutput.outputLocalTime(time.withNano(230000000), ic);
        assertEquals("09:31:27.230", sb.toString());
        sb.setLength(0);
        DateOutput.outputLocalTime(time.withNano(234500000), ic);
        assertEquals("09:31:27.234500", sb.toString());
        sb.setLength(0);
        DateOutput.outputLocalTime(time.withNano(234567890), ic);
        assertEquals("09:31:27.234567890", sb.toString());
    }

    @Test
    public void shouldConvertYearUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        Year year = Year.of(2022);
        DateOutput.outputYear(year, ic);
        assertEquals("2022", sb.toString());
    }

    @Test
    public void shouldConvertYearMonthUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        YearMonth yearMonth = YearMonth.of(2022, 4);
        DateOutput.outputYearMonth(yearMonth, ic);
        assertEquals("2022-04", sb.toString());
    }

    @Test
    public void shouldConvertMonthDayUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        MonthDay monthDay = MonthDay.of(4, 8);
        DateOutput.outputMonthDay(monthDay, ic);
        assertEquals("--04-08", sb.toString());
    }

}
