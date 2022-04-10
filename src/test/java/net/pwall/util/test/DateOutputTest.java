/*
 * @(#) DateOutputTest.java
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

package net.pwall.util.test;

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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import net.pwall.util.DateOutput;

public class DateOutputTest {

    @Test
    public void shouldConvertDate() throws IOException {
        StringBuilder sb = new StringBuilder();
        Instant instant = OffsetDateTime.of(2022, 4, 8, 18, 39, 2, 456_000_000, ZoneOffset.UTC).toInstant();
        DateOutput.appendDate(sb, Date.from(instant));
        assertEquals("2022-04-08T18:39:02.456Z", sb.toString());
    }

    @Test
    public void shouldConvertCalendar() throws IOException {
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.DAY_OF_MONTH, 8);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 39);
        calendar.set(Calendar.SECOND, 2);
        calendar.set(Calendar.MILLISECOND, 456);
        calendar.set(Calendar.ZONE_OFFSET, 10 * 60 * 60 * 1000);
        DateOutput.appendCalendar(sb, calendar);
        assertEquals("2022-04-08T18:39:02.456+10:00", sb.toString());
    }

    @Test
    public void shouldConvertInstant() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.12");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(10));
        Instant instant = offsetDateTime.toInstant();
        DateOutput.appendInstant(sb, instant);
        assertEquals("2022-04-07T08:32:47.120Z", sb.toString());
    }

    @Test
    public void shouldConvertZonedDateTime() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.55");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Australia/Sydney"));
        DateOutput.appendZonedDateTime(sb, zonedDateTime);
        assertEquals("2022-04-07T18:32:47.550+10:00", sb.toString());
    }

    @Test
    public void shouldConvertOffsetDateTime() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.parse("2022-04-07T18:32:47.5446");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(10));
        DateOutput.appendOffsetDateTime(sb, offsetDateTime);
        assertEquals("2022-04-07T18:32:47.544600+10:00", sb.toString());
        sb.setLength(0);
        localDateTime = LocalDateTime.of(1999, 6, 1, 9, 15, 10, 456_000_000);
        offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        DateOutput.appendOffsetDateTime(sb, offsetDateTime);
        assertEquals("1999-06-01T09:15:10.456Z", sb.toString());
    }

    @Test
    public void shouldConvertOffsetTime() throws IOException {
        StringBuilder sb = new StringBuilder();
        OffsetTime offsetTime = OffsetTime.of(8, 27, 55, 544_233_100, ZoneOffset.ofHours(-5));
        DateOutput.appendOffsetTime(sb, offsetTime);
        assertEquals("08:27:55.544233100-05:00", sb.toString());
    }

    @Test
    public void shouldConvertLocalDateTime() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalDateTime dateTime = LocalDateTime.parse("2022-04-07T18:32:47.544");
        DateOutput.appendLocalDateTime(sb, dateTime);
        assertEquals("2022-04-07T18:32:47.544", sb.toString());
        sb.setLength(0);
        dateTime = LocalDateTime.parse("1999-04-01T08:45");
        DateOutput.appendLocalDateTime(sb, dateTime);
        assertEquals("1999-04-01T08:45:00", sb.toString());
    }

    @Test
    public void shouldConvertLocalDate() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalDate date = LocalDate.of(2022, 4, 7);
        DateOutput.appendLocalDate(sb, date);
        assertEquals("2022-04-07", sb.toString());
        sb.setLength(0);
        date = LocalDate.parse("1999-12-31");
        DateOutput.appendLocalDate(sb, date);
        assertEquals("1999-12-31", sb.toString());
        sb.setLength(0);
        DateOutput.appendLocalDate(sb, date.plusDays(1));
        assertEquals("2000-01-01", sb.toString());
    }

    @Test
    public void shouldConvertLocalTime() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime time = LocalTime.of(14, 3, 0);
        DateOutput.appendLocalTime(sb, time);
        assertEquals("14:03:00", sb.toString());
        sb.setLength(0);
        time = LocalTime.parse("09:31:27");
        DateOutput.appendLocalTime(sb, time);
        assertEquals("09:31:27", sb.toString());
        sb.setLength(0);
        DateOutput.appendLocalTime(sb, time.withNano(230000000));
        assertEquals("09:31:27.230", sb.toString());
        sb.setLength(0);
        DateOutput.appendLocalTime(sb, time.withNano(234500000));
        assertEquals("09:31:27.234500", sb.toString());
        sb.setLength(0);
        DateOutput.appendLocalTime(sb, time.withNano(234567890));
        assertEquals("09:31:27.234567890", sb.toString());
    }

    @Test
    public void shouldConvertYear() throws IOException {
        StringBuilder sb = new StringBuilder();
        Year year = Year.of(2022);
        DateOutput.appendYear(sb, year);
        assertEquals("2022", sb.toString());
    }

    @Test
    public void shouldConvertYearMonth() throws IOException {
        StringBuilder sb = new StringBuilder();
        YearMonth yearMonth = YearMonth.of(2022, 4);
        DateOutput.appendYearMonth(sb, yearMonth);
        assertEquals("2022-04", sb.toString());
    }

    @Test
    public void shouldConvertMonthDay() throws IOException {
        StringBuilder sb = new StringBuilder();
        MonthDay monthDay = MonthDay.of(4, 8);
        DateOutput.appendMonthDay(sb, monthDay);
        assertEquals("--04-08", sb.toString());
    }

}
