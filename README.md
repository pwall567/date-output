# date-output

[![Build Status](https://travis-ci.com/pwall567/date-output.svg?branch=main)](https://app.travis-ci.com/github/pwall567/date-output)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.util/date-output?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.util%22%20AND%20a:%22date-output%22)

Static functions for optimised date/time output

## Background

Dates and times form a large part of the data of most commercial IT systems, and the most popular representation of date
and time data is [RFC 3339](https://tools.ietf.org/html/rfc3339), which is based on
[ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html).
That means that a great deal of processor time is spent converting internal dates and times into their external form,
and the standard library functions to achieve this are not well optimised.
In particular, they create a number of intermediate objects (mostly string representations of individual components of
the date or time) and object allocation is a relatively time-consuming operation.

This library provides a set of output functions to convert standard Java date and time values (both the original `Date`
class and the later `java.time.` classes), optimised for the least possible object allocation.

## Usage

There are versions of the functions to append to an `Appendable` or to output using an `IntConsumer` lambda.
The `Appendable` can be `Writer` to write directly to an output stream, or a `StringBuilder` to build an output message
in memory, or any other implementation of `Appendable`.
The `IntConsumer` can be any function that accepts characters (as `int`) one at a time.

The functions using an `Appendable` are shown here; each of them has an equivalent which takes an `IntConsumer` as the
second parameter, with the value to be converted in the first parameter position.

To output a `Date` (the earlier Java data class - this class does not include time zone information so UTC+00:00 will be
assumed):
```java
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        DateOutput.appendDate(sb, date);
```

To output a `Calendar` (the earlier Java data class):
```java
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DateOutput.appendCalendar(sb, calendar);
```

To output an `Instant` (this class does not include time zone information so UTC+00:00 will be assumed):
```java
        StringBuilder sb = new StringBuilder();
        Instant instant = Instant.now();
        DateOutput.appendInstant(sb, instant);
```

To output a `ZonedDateTime` (there is no RFC 3339 representation for the zone name component of `ZonedDateTime`, so this
will not be output):
```java
        StringBuilder sb = new StringBuilder();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateOutput.appendZonedDateTime(sb, zonedDateTime);
```

To output an `OffsetDateTime`:
```java
        StringBuilder sb = new StringBuilder();
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        DateOutput.appendOffsetDateTime(sb, offsetDateTime);
```

To output an `OffsetTime`:
```java
        StringBuilder sb = new StringBuilder();
        OffsetTime offsetTime = OffsetTime.now();
        DateOutput.appendOffsetTime(sb, offsetTime);
```

To output a `LocalDateTime`:
```java
        StringBuilder sb = new StringBuilder();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateOutput.appendLocalDateTime(sb, localDateTime);
```

To output a `LocalDate`:
```java
        StringBuilder sb = new StringBuilder();
        LocalDate localDate = LocalDate.now();
        DateOutput.appendLocalTime(sb, localDate);
```

To output a `LocalTime`:
```java
        StringBuilder sb = new StringBuilder();
        LocalTime localTime = LocalTime.now();
        DateOutput.appendLocalTime(sb, localTime);
```

To output a `Year`:
```java
        StringBuilder sb = new StringBuilder();
        Year year = Year.now();
        DateOutput.appendYear(sb, year);
```

To output a `YearMonth`:
```java
        StringBuilder sb = new StringBuilder();
        YearMonth yearMonth = YearMonth.now();
        DateOutput.appendYearMonth(sb, yearMonth);
```

To output a `MonthDay`:
```java
        StringBuilder sb = new StringBuilder();
        MonthDay monthDay = MonthDay.now();
        DateOutput.appendMonthDay(sb, monthDay);
```

## Additional Notes

### Minutes

The standard `toPrint()` functions for `LocalTime`, `OffsetDateTime` _etc._ will omit the minutes field (and its
preceding colon) if the value is zero.
This does not conform to the RFC 3339 specification, so these function do not do that.

### Fractional Seconds

While the `java.time.` classes allow nanosecond precision, in practice most uses will have no more than 3 decimal places
(milliseconds).
When outputting fractional seconds, the functions will output 3, 6 or 9 decimal places as necessary (but not any other
number of places), following the example of the `toString()` functions of the standard classes.

Also following the lead of the standard classes, the fractional part (and the decimal point) will be omitted if it is
zero.

### Safe Output

The output will contain only the characters specified in the RFC 3339 specification, that is, the digits 0 to 9, slash,
colon, the plus and minus signs and the characters "T" and "Z".
That means that the output may safely be used to create JSON or HTML strings with no need for escaping.

### `ZonedDateTime`

The `ZonedDateTime` class includes a time zone name, but there is no provision for this name in the RFC 3339 standard.
The library includes an `appendZonedDateTime()` function, but the output is the same as for `OffsetDateTime` &ndash; the
zone name will not be output.

### Range of Years

The functions are expected to be used mainly on dates in the modern era, that is, dates in the 20th and 21st centuries.
Any date from the introduction of the Gregorian calendar up to the year 9999CE will be output correctly, but dates in
the BCE range will be output without a sign, and absolute year values of more than 4 digits will be output as 9999.

This information is likely to be of interest only to testers who like to feed outrageous values into systems under test.

## Dependency Specification

The latest version of the library is 1.2, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.util</groupId>
      <artifactId>date-output</artifactId>
      <version>1.2</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.util:date-output:1.2'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.util:date-output:1.2")
```

Peter Wall

2023-11-09
