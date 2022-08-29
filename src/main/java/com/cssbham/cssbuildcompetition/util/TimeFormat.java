package com.cssbham.cssbuildcompetition.util;

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

        StringBuilder sb = new StringBuilder();
        if (h > 0) {
            sb.append(h).append(" hour").append(h != 1 ? "s " : " ");
        }
        if (h > 0 || m > 0) {
            sb.append(m).append(" minute").append(m != 1 ? "s " : " ");
        }
        if (h == 0) {
            sb.append(s).append(" second").append(s != 1 ? "s" : "");
        }
        return sb.toString();
    }

}
