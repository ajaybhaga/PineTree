/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bhaga.pinetree.ui.util;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ajaybhaga
 */
public class StringUtility {

    public static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    public static String randomColorGenerator() {
        String code = "" + (int) (Math.random() * 256);
        code = code + code + code;
        int i = Integer.parseInt(code);
        return Integer.toHexString(0x1000000 | i).substring(1).toUpperCase();
    }
}
