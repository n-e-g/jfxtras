/**
 * Copyright (c) 2011-2020, JFXtras
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *    Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *    Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jfxtras.icalendarfx.property.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import jfxtras.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.Count;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.FrequencyType;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.Interval;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.RecurrenceRuleValue;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.Until;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.WeekStart;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.byxxx.ByDay;
import jfxtras.icalendarfx.properties.component.recurrence.rrule.byxxx.ByMonth;
import jfxtras.icalendarfx.utilities.ICalendarUtilities;
import jfxtras.icalendarfx.utilities.Pair;

public class RecurrenceRuleTest
{    
    @Test
    public void canParseRRuleProperty()
    {
        String contentLine = "RRULE:FREQ=DAILY;UNTIL=20160417T235959Z;INTERVAL=2";
        List<Pair<String, String>> valueList = ICalendarUtilities.parseInlineElementsToListPair(contentLine);
        List<Pair<String, String>> expectedList = new ArrayList<>();
        expectedList.add(new Pair<>(ICalendarUtilities.PROPERTY_VALUE_KEY, "FREQ=DAILY;UNTIL=20160417T235959Z;INTERVAL=2"));
        assertEquals(expectedList, valueList);
    }
    
    @Test
    public void canParseRRuleProperty2()
    {
        String contentLine = "FREQ=DAILY;UNTIL=20160417T235959Z;INTERVAL=2";        
        List<Pair<String, String>> valueList = ICalendarUtilities.parseInlineElementsToListPair(contentLine);
        List<Pair<String, String>> expectedList = new ArrayList<>();
        expectedList.add(new Pair<>("FREQ", "DAILY"));
        expectedList.add(new Pair<>("UNTIL", "20160417T235959Z"));
        expectedList.add(new Pair<>("INTERVAL", "2"));
        assertEquals(expectedList, valueList);
    }
    
    @Test
    public void canParseRecurrenceRule()
    {
        String content = "RRULE:FREQ=YEARLY;UNTIL=19730429T070000Z;BYMONTH=4;BYDAY=-1SU";
        RecurrenceRule madeProperty = RecurrenceRule.parse(content);
        assertEquals(content, madeProperty.toString());
        RecurrenceRule expectedProperty = new RecurrenceRule(
                new RecurrenceRuleValue()
                    .withFrequency(FrequencyType.YEARLY)
                    .withUntil("19730429T070000Z")
                    .withByRules(new ByMonth(Month.APRIL),
                                new ByDay(new ByDay.ByDayPair(DayOfWeek.SUNDAY, -1))));
        assertEquals(content, madeProperty.toString());
        assertEquals(expectedProperty, madeProperty);
    }
    
    @Test // different ordering of rule parts
    public void canParseRecurrenceRule2()
    {
        String content = "RRULE:BYDAY=-1SU;UNTIL=19730429T070000Z;BYMONTH=4;FREQ=YEARLY";
        RecurrenceRule madeProperty = RecurrenceRule.parse(content);
        RecurrenceRule expectedProperty = new RecurrenceRule(
                new RecurrenceRuleValue()
                    .withByRules(new ByDay(new ByDay.ByDayPair(DayOfWeek.SUNDAY, -1)))
                    .withUntil("19730429T070000Z")
                    .withByRules(new ByMonth(Month.APRIL))
                    .withFrequency(FrequencyType.YEARLY));
        assertEquals(content, madeProperty.toString());
        assertEquals(expectedProperty, madeProperty);
    }
    
    @Test
    public void canCopyRecurrenceRule()
    {
        String content = "RRULE:UNTIL=19730429T070000Z;FREQ=YEARLY;BYMONTH=4;BYDAY=-1SU;BYMINUTE=1";
        RecurrenceRule r1 = RecurrenceRule.parse(content);
        RecurrenceRule r2 = new RecurrenceRule(r1);
        assertEquals(r1, r2);
        assertTrue(r1 != r2);
        assertTrue(r1.equals(r2));
        assertEquals(content, r2.toString());
        assertTrue(r1.getValue() != r2.getValue());
    }
    
    /*
     * TEST RECURRENCE RULE ELEMENTS
     */
    
    @Test
    public void canParseUntil()
    {
        String content = "19730429T070000Z";
        Until element = Until.parse(content);
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(1973, 4, 29, 7, 0), ZoneId.of("Z"));
        assertEquals(t, element.getValue());
        assertEquals("UNTIL=19730429T070000Z", element.toString());
    }
    
    @Test (expected = DateTimeException.class)
    public void canCatchWrongUntil()
    {
        Thread.currentThread().setUncaughtExceptionHandler((t1, e) ->
        {
            throw (RuntimeException) e;
        });
        String content = "19730429T070000";
        Until.parse(content);
    }
    
    @Test
    public void canParseCount()
    {
        Count element = Count.parse("2");
        assertEquals((Integer) 2, element.getValue());
        assertEquals("COUNT=2", element.toString());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void canCatchNonPositiveCount()
    {
        Count element = new Count(5);
        assertEquals((Integer) 5, element.getValue());
        element.setValue(0);
        assertEquals("COUNT=5", element.toString());
    }
    
    @Test
    public void canParseInterval()
    {
        Interval element = Interval.parse("2");
        assertEquals((Integer) 2, element.getValue());
        assertEquals("INTERVAL=2", element.toString());
    }

    @Test
    public void canParseWeekStart()
    {
        WeekStart element = new WeekStart().withValue("SU");
        assertEquals(DayOfWeek.SUNDAY, element.getValue());
        assertEquals("WKST=SU", element.toString());
    }
    
    @Test
    public void canDetectErrors1()
    {
        RecurrenceRule expectedProperty = new RecurrenceRule(
                new RecurrenceRuleValue()
                    .withUntil("19730429T070000Z")
                    .withFrequency(FrequencyType.YEARLY)
                    .withByRules(new ByDay()));
        assertEquals(1, expectedProperty.errors().size());
        String expected = "BYDAY: value is null.  The RRULE part MUST have a value.";
        assertEquals(expected, expectedProperty.errors().get(0));
    }
    
    @Test
    public void canDetectErrors2()
    {
        RecurrenceRule madeProperty = new RecurrenceRule(
                new RecurrenceRuleValue()
                .withUntil("19730429T070000Z")
                .withByRules(new ByMonth(Month.APRIL),
                            new ByDay(new ByDay.ByDayPair(DayOfWeek.SUNDAY, -1))));
        assertEquals(1, madeProperty.errors().size());
    }
    
    @Test
    public void canRemoveParameter()
    {
        String content = "FREQ=DAILY;UNTIL=20160417T235959Z;INTERVAL=2";
        RecurrenceRule property1 = RecurrenceRule.parse(content);
        property1.getValue().setUntil((Until) null);
        RecurrenceRule expectedProperty = RecurrenceRule.parse("FREQ=DAILY;INTERVAL=2");
        assertEquals(expectedProperty, property1);
    }
    
    @Test
    public void canRemoveParameter2()
    {
        Until until = new Until(ZonedDateTime.of(LocalDateTime.of(2015, 11, 19, 23, 30), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z")));
        RecurrenceRule property1 = new RecurrenceRule(new RecurrenceRuleValue()
            .withFrequency(FrequencyType.DAILY)
            .withUntil(until)
            .withInterval(2));
        Temporal until2 = ZonedDateTime.of(LocalDateTime.of(2015, 11, 18, 23, 30), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
        property1.getValue().setUntil(until2); // use Temporal setter to test ability to remove reset property
        assertEquals(3, property1.getValue().childrenUnmodifiable().size());
        property1.getValue().setUntil((Until) null);
        assertEquals(2, property1.getValue().childrenUnmodifiable().size());
        RecurrenceRule expectedProperty = RecurrenceRule.parse("FREQ=DAILY;INTERVAL=2");
        assertEquals(expectedProperty, property1);
    }
    
    @Test
    public void canBuildEmptyRecurrenceRule()
    {
        RecurrenceRule r = new RecurrenceRule(new RecurrenceRuleValue()); // value can't be null
        List<String> errors = r.errors();
        List<String> expected = Arrays.asList("RRULE:FREQ is not present.  FREQ is REQUIRED and MUST NOT occur more than once");
        assertEquals(expected, errors);
    }
    
    @Test
    public void canBuildEmptyRecurrenceRuleValue()
    {
        RecurrenceRuleValue r = new RecurrenceRuleValue(); // value can't be null
        assertEquals(0, r.childrenUnmodifiable().size());
        List<String> errors = r.errors();
        List<String> expected = Arrays.asList("RRULE:FREQ is not present.  FREQ is REQUIRED and MUST NOT occur more than once");
        assertEquals(expected, errors);
    }
    
    @Test
    public void canCheckEquals()
    {
    	RecurrenceRule r1 = RecurrenceRule.parse("FREQ=DAILY");
    	RecurrenceRule r2 = new RecurrenceRule(r1);
    	assertEquals(r1, r2);
    }
}
