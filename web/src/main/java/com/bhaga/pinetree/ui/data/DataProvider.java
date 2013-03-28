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

import com.bhaga.pinetree.nn.data.ResultData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private HashMap<String, List<ResultData>> resultDataHashMap;

    /**
     * Initialize the data for this application.
     */
    public DataProvider() {
        // Instantiate hash map
        resultDataHashMap = new HashMap<String, List<ResultData>>();
    }

    public void addResultData(String symbol, ResultData resultData) {

        List resultDataList = resultDataHashMap.get(symbol);
        if (resultDataList != null) {
            // Ensure we add no duplicates
            if (!resultDataList.contains(resultData)) {
                resultDataList.add(resultData);
            }
        } else {
            resultDataList = new ArrayList();
            resultDataList.add(resultData);
            resultDataHashMap.put(symbol, resultDataList);
        }
    }

    public List<ResultData> getAllResultDataBySymbol(String symbol) {
        return resultDataHashMap.get(symbol);
    }
    
    public Set<String> getAllResultDataKeySet() {
        return resultDataHashMap.keySet();
    }
}
