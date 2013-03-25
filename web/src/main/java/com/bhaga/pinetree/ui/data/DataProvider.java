/**
 * DISCLAIMER
 *
 * The quality of the code is such that you should not copy any of it as best
 * practice how to build Vaadin applications.
 *
 * @author Ajay Bhaga (ajay.bhaga@gmail.com)
 *
 */
package com.bhaga.pinetree.ui.data;

import java.util.Random;

public class DataProvider {

    public static Random rand = new Random();

    public static class Config {

        public static final String USER = "bhaga";
        public static final String PASSWORD = "intech";
        public static final String USER_FIRST_NAME = "Ajay";
        public static final String USER_LAST_NAME = "Bhaga";
        public static final int TIME_INTERVAL_HOURLY = 0;
        public static final int TIME_INTERVAL_DAILY = 1;
        public static final String[] TIME_INTERVAL = {"Hourly", "Daily"};
    }

    /**
     * Initialize the data for this application.
     */
    public DataProvider() {
    }
}
