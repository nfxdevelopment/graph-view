package com.nfx.android.utils;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * NFX Development
 * Created by nick on 8/01/17.
 */
public class RoundingFormat {
    private static final long KILO = 1000;
    private static final long MEGA = 1000000;
    private static final long GIGA = 1000000000;
    private static final double SECONDS = 1;
    private static final double MILLI = 0.001;
    private static final double MICRO = 0.000001;
    private static final double NANO = 0.000000001;
    private static NumberFormat numberFormat = new DecimalFormat("###.##");

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

    @SuppressLint("DefaultLocale")
    public static String secondsToString(double seconds) {
        if(seconds < NANO) {
            return numberFormat.format(seconds / NANO) + " ns";
        } else if(seconds == NANO) {
            return numberFormat.format(seconds / NANO) + " ns";
        } else if(seconds < MICRO) {
            return numberFormat.format(seconds / NANO) + " ns";
        } else if(seconds == MICRO) {
            return numberFormat.format(seconds / MICRO) + " us";
        } else if(seconds < MILLI) {
            return numberFormat.format(seconds / MICRO) + " us";
        } else if(seconds == MILLI) {
            return numberFormat.format(seconds / MILLI) + " ms";
        } else if(seconds < SECONDS) {
            return numberFormat.format(seconds / MILLI) + " ms";
        } else {
            return numberFormat.format(seconds) + " s";
        }
    }

    @SuppressLint("DefaultLocale")
    public static String voltsToString(float volts) {
        if(volts < NANO) {
            return numberFormat.format(volts / NANO) + " nV";
        } else if(volts == NANO) {
            return numberFormat.format(volts / NANO) + " nV";
        } else if(volts < MICRO) {
            return numberFormat.format(volts / NANO) + " nV";
        } else if(volts == MICRO) {
            return numberFormat.format(volts / MICRO) + " uV";
        } else if(volts < MILLI) {
            return numberFormat.format(volts / MICRO) + " uV";
        } else if(volts == MILLI) {
            return numberFormat.format(volts / MILLI) + " mV";
        } else if(volts < SECONDS) {
            return numberFormat.format(volts / MILLI) + " mV";
        } else {
            return numberFormat.format(volts) + " V";
        }
    }
}
