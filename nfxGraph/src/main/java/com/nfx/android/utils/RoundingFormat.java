package com.nfx.android.utils;

import android.annotation.SuppressLint;

/**
 * NFX Development
 * Created by nick on 8/01/17.
 */
public class RoundingFormat {
    private static final long KILO = 1000;
    private static final long MEGA = 1000000;
    private static final long GIGA = 1000000000;

    @SuppressLint("DefaultLocale")
    public static String frequencyToString(long frequency) {
        if(frequency < KILO) {
            return String.format("%d Hz", frequency);
        } else if(frequency < MEGA && frequency % KILO == 0) { // KHz
            return String.format("%.00f kHz", (float) frequency / KILO);
        } else if(frequency < MEGA) {
            return String.format("%.2f kHz", (float) frequency / KILO);
        } else if(frequency < GIGA && frequency % MEGA == 0) { // MHz
            return String.format("%.00f MHz", (float) frequency / MEGA);
        } else if(frequency < GIGA) {
            return String.format("%.2f MHz", (float) frequency / MEGA);
        }

        return "N/A";
    }
}
