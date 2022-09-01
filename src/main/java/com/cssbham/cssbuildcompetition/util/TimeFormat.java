package com.cssbham.cssbuildcompetition.util;

import java.util.ArrayList;
import java.util.List;

public class TimeFormat {

    /**
     * Converts to: (([0-9] hours )?[0-9] minutes )?[0-9] seconds
     *
     * @param milliseconds the time in milliseconds
     * @return the formatted time
     */
    public static String convertToHumanReadableTime(long milliseconds) {
        int s = (int) (milliseconds / 1000) % 60;
        int m = (int) ((milliseconds / (1000*60)) % 60);
        int h = (int) ((milliseconds / (1000*60*60)) % 24);

        List<String> time = new ArrayList<>();
        if (h > 0) {
            time.add(h + " hour" + (h != 1 ? "s" : ""));
        }
        if (h > 0 || m > 0) {
            time.add(m + " minute" + (m != 1 ? "s" : ""));
        }
        if (h == 0) {
            time.add(s + " second" + (s != 1 ? "s" : ""));
        }
        return String.join(" ", time);
    }

    /**
     * Converts to: ([0-9] hours )?([0-9] minutes )?([0-9] seconds)?
     *
     * @param milliseconds the time in milliseconds
     * @return the formatted time
     */
                         // makes no sense and I don't care
    public static String convertToOptionalHumanReadableTime(long milliseconds) {
        int s = (int) (milliseconds / 1000) % 60;
        int m = (int) ((milliseconds / (1000*60)) % 60);
        int h = (int) ((milliseconds / (1000*60*60)) % 24);

        List<String> time = new ArrayList<>();
        if (h > 0) {
            time.add(h + " hour" + (h != 1 ? "s" : ""));
        }
        if (m > 0) {
            time.add(m + " minute" + (m != 1 ? "s" : ""));
        }
        if (s > 0) {
            time.add(s + " second" + (s != 1 ? "s" : ""));
        }
        return String.join(" ", time);
    }

}
