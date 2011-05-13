/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.datetime;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TimeInterval {

    /**
     * Returns the interval as milliseconds
     * @param interval
     * @return
     */
    public static long getIntervalAsLong(String interval) {
        char granularity;
        long multiplier = 0;
        String value;

        granularity = interval.charAt(interval.length() - 1);

        switch (granularity) {
            case 's':
                multiplier = 1;
                break;

            case 'm':
                multiplier = 60;
                break;

            case 'h':
                multiplier = 3600;
                break;

            case 'D':
                multiplier = 86400;
                break;

            case 'W':
                multiplier = 604800;
                break;
                   
        }
//['weeks','W'], ['days','D'], ['months','M'], ['years', 'Y']
        value = interval.substring(0, interval.length() - 1);
        return Long.parseLong(value) * multiplier*1000;
    }
}
