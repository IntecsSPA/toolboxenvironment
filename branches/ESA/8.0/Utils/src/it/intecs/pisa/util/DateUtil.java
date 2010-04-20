/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author massi
 */
public class DateUtil {
    public static String getDateAsString(String format,Date date)
    {
        SimpleDateFormat formatter;

        formatter=new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getCurrentDateAsString(String format)
    {
        return getDateAsString(format,new Date());
    }

    public static String getCurrentDateAsUniqueId()
    {
        return getCurrentDateAsString("yyyyMMddHHmmssSSS");
    }
}
