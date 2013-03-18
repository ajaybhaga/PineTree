package com.nnstockpredict.Utility;

import com.nnstockpredict.data.candlestick.CandlestickPattern;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Candlestick Utility
 *
 * @author ajaybhaga
 */
public class CandlestickUtility {

    // Pattern IDs
    public static final int HAMMER = 1; // Bullish reversal (60%)
    public static final int BULLISH_SEPARATING_LINES = 2; // Bullish continuation (72%)
    public static final int SHOOTING_STAR = 3; // Bullish continuation (61%)
    public static final int BEARISH_ENGULFING = 4; // Bearish reversal (79%)
    public static final int BULLISH_ENGULFING = 5; // Bullish reversal (63%)
    //public static final int BEARISH_DOJI_STAR = 6; // Bullish continuation (69%)
    //public static final int BULLISH_DOJI_STAR = 7; // Bearish continuation (64%)
    // Pattern Names
    public static final String PATTERN_NAME_HAMMER = "hammer";
    public static final String PATTERN_NAME_BULLISH_SEPARATING_LINES = "bullishSeparatingLines";
    public static final String PATTERN_NAME_SHOOTING_STAR = "shootingStar";
    public static final String PATTERN_NAME_BEARISH_ENGULFING = "bearishEngulfing";
    public static final String PATTERN_NAME_BULLISH_ENGULFING = "bullishEngulfing";
    // Rules
    // working
    public static final String RULE_HAMMER = "if trend is down and c0LowerShadow is above middle and c0UpperShadow is below short and c0BodyColour is white then pattern is hammer";
    public static final String RULE_BULLISH_SEPARATING_LINES = "if trend is up and c0LowerShadow is below middle and c0UpperShadow is below middle and c0OpenStyle is high and c0CloseStyle is above high and c0Body is above middle and c0BodyColour is white and c1LowerShadow is below middle and c1UpperShadow is below middle and c1OpenStyle is high and c1CloseStyle is below high and c1Body is above middle and c1BodyColour is black then pattern is bullishSeparatingLines";
    //public static final String RULE_BULLISH_SEPARATING_LINES = "if trend is up and c0Body is above middle and c0BodyColour is white and c1Body is above middle and c1BodyColour is black then pattern is bullishSeparatingLines";
    public static final String RULE_SHOOTING_STAR = "if trend is up and c0LowerShadow is short and c0UpperShadow is above middle and c0OpenStyle is above high and c0CloseStyle is above high and c0Body is below middle and c1Body is above middle and c1BodyColour is white then pattern is shootingStar";
    public static final String RULE_BEARISH_ENGULFING = "if trend is up and c0OpenStyle is high and c0CloseStyle is low and c0Body is above middle and c0BodyColour is black and c1Body is short and c1BodyColour is white then pattern is bearishEngulfing";
    public static final String RULE_BULLISH_ENGULFING = "if trend is down and c0OpenStyle is low and c0CloseStyle is high and c0Body is above middle and c0BodyColour is white and c1Body is short and c1BodyColour is black then pattern is bullishEngulfing";
    private static HashMap<Integer, CandlestickPattern> candlestickPatternHashMap = new HashMap<Integer, CandlestickPattern>();

    // Shooting star falsely called
    public static void loadPatterns() {
        //candlestickPatternHashMap.put(HAMMER, new CandlestickPattern(HAMMER, PATTERN_NAME_HAMMER, RULE_HAMMER, 0.60D));
       // candlestickPatternHashMap.put(BULLISH_SEPARATING_LINES, new CandlestickPattern(BULLISH_SEPARATING_LINES, PATTERN_NAME_BULLISH_SEPARATING_LINES, RULE_BULLISH_SEPARATING_LINES, 0.72D));
        candlestickPatternHashMap.put(SHOOTING_STAR, new CandlestickPattern(SHOOTING_STAR, PATTERN_NAME_SHOOTING_STAR, RULE_SHOOTING_STAR, 0.61D));
        candlestickPatternHashMap.put(BEARISH_ENGULFING, new CandlestickPattern(BEARISH_ENGULFING, PATTERN_NAME_BEARISH_ENGULFING, RULE_BEARISH_ENGULFING, -0.79D));
        candlestickPatternHashMap.put(BULLISH_ENGULFING, new CandlestickPattern(BULLISH_ENGULFING, PATTERN_NAME_BULLISH_ENGULFING, RULE_BULLISH_ENGULFING, 0.63D));
    }

    public static String getName(int patternId) {
        CandlestickPattern candlestickPattern = candlestickPatternHashMap.get(patternId);
        return candlestickPattern.getName();
    }

    public static String getAllRules() {
        String rules = "";
        Iterator it = candlestickPatternHashMap.keySet().iterator();
        while (it.hasNext()) {
            int id = (Integer) it.next();
            CandlestickPattern candlestickPattern = (CandlestickPattern) candlestickPatternHashMap.get(id);
            rules = rules + candlestickPattern.getRule() + "\n";
        }
        
        //System.out.println("fuzzy rules: " + rules);

        return rules;
    }

    public static HashMap<Integer, CandlestickPattern> getMap() {
        return candlestickPatternHashMap;
    }
}
