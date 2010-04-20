/* 
 *
 *  Developed By:      Intecs  S.P.A.
 *  File Name:         $RCSfile: TimeUtil.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:25 $
 *
 */
package it.intecs.pisa.toolbox.util;

import java.util.*;

public class TimeUtil {

  private static final String DATE_SEPARATOR = "-";
  private static final String DATE_TIME_SEPARATOR = "T";
  private static final String TIME_SEPARATOR = ":";
  private static final String ZERO = "0";

  public static String getTimeString(long delta) {
    int millis = (int)delta % 1000;
    delta /= 1000;
    int seconds = (int)delta % 60;
    delta /= 60;
    int minutes = (int)delta % 60;
    delta /= 60;
    int hours = (int)delta % 60;
    return getTimeString(hours, minutes, seconds, millis);
  }

  public static String getTimeString(int hours, int minutes, int seconds, int millis) {
    String result = "";
    result += hours > 9 ? String.valueOf(hours) : "0" + hours;
    result += ':';
    result += minutes > 9 ? String.valueOf(minutes) : "0" + minutes;
    result += ':';
    result += seconds > 9 ? String.valueOf(seconds) : "0" + seconds;
    result += '.';
    result += millis > 99 ? String.valueOf(millis) : (millis > 9 ? "0" + millis : "00" + millis);
    return result;
  }

  public static int getYear() {
    return new GregorianCalendar().get(Calendar.YEAR);
  }

  public static int getMonth() {
    return new GregorianCalendar().get(Calendar.MONTH);
  }

  public static int getDay() {
    return new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
  }

  public static int getHour() {
    return new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
  }

  public static int getMinute() {
    return new GregorianCalendar().get(Calendar.MINUTE);
  }

  public static int getSecond() {
    return new GregorianCalendar().get(Calendar.SECOND);
  }

  public static int getMillisecond() {
    return new GregorianCalendar().get(Calendar.MILLISECOND);
  }

  public static String getDate() {
    return getDate(new GregorianCalendar());
  }

  private static String getDate(GregorianCalendar time) {
    return getDate(time, DATE_SEPARATOR);
  }

  private static String getDate(GregorianCalendar time, String dateSeparator) {
    StringBuffer dateString = new StringBuffer();
    dateString.append(time.get(Calendar.YEAR)).append(dateSeparator);
    int month = time.get(Calendar.MONTH) + 1;
    if (month < 10)
      dateString.append(ZERO);
    dateString.append(month).append(dateSeparator);
    int day = time.get(Calendar.DAY_OF_MONTH);
    if (day < 10)
      dateString.append(ZERO);
    dateString.append(day);
    return dateString.toString();
  }

  public static String getTime() {
    return getTime(new GregorianCalendar());
  }

  private static String getTime(GregorianCalendar time) {
    return getTime(time, TIME_SEPARATOR);
  }

  private static String getTime(GregorianCalendar time, String timeSeparator) {
    StringBuffer timeString = new StringBuffer();
    int hour = time.get(Calendar.HOUR_OF_DAY);
    if (hour < 10)
      timeString.append(ZERO);
    timeString.append(hour).append(timeSeparator);
    int minute = time.get(Calendar.MINUTE);
    if (minute < 10)
      timeString.append(ZERO);
    timeString.append(minute).append(timeSeparator);
    int second = time.get(Calendar.SECOND);
    if (second < 10)
      timeString.append(ZERO);
    timeString.append(second);
    return timeString.toString();
  }

  public static String getDateTime(GregorianCalendar time) {
    return getDate(time) + DATE_TIME_SEPARATOR + getTime(time);
  }

  public static String getDateTime() {
    return getDateTime(new GregorianCalendar());
  }

  public static String getDateTime(GregorianCalendar time, String dateSeparator, String dateTimeSeparator, String timeSeparator) {
    return getDate(time, dateSeparator) + dateTimeSeparator + getTime(time, timeSeparator);
  }

  public static String getDateTime(String dateSeparator, String dateTimeSeparator, String timeSeparator) {
    return getDateTime(new GregorianCalendar(), dateSeparator, dateTimeSeparator, timeSeparator);
  }

  public static String getTimeMillis() {
    GregorianCalendar time = new GregorianCalendar();
    return getTime(time) + "." + time.get(Calendar.MILLISECOND);
  }

}
