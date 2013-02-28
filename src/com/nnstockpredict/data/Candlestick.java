package com.nnstockpredict.data;

/**
 * Candlestick definitions
 *
 * @author ajaybhaga
 */
public class Candlestick {

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
    public static final String RULE_SHOOTING_STAR = "if trend is up and c0LowerShadow is below short and c0UpperShadow is above middle and c0OpenStyle is above high and c0CloseStyle is above high and c0Body is below middle and c1OpenStyle is above low and c1CloseStyle is high and c1Body is above middle and c1BodyColour is white then pattern is shootingStar";
    public static final String RULE_BEARISH_ENGULFING = "if trend is up and c0OpenStyle is high and c0CloseStyle is low and c0Body is above middle and c0BodyColour is black and c1Body is short and c1BodyColour is white then pattern is bearishEngulfing";
    public static final String RULE_BULLISH_ENGULFING = "if trend is down and c0OpenStyle is low and c0CloseStyle is high and c0Body is above middle and c0BodyColour is white and c1Body is short and c1BodyColour is black then pattern is bullishEngulfing";

    public static String getName(int patternId) {
        String patternName = "unknown";
        switch (patternId) {
            case Candlestick.HAMMER:
                patternName = Candlestick.PATTERN_NAME_HAMMER;
                break;
            case Candlestick.BULLISH_SEPARATING_LINES:
                patternName = Candlestick.PATTERN_NAME_BULLISH_SEPARATING_LINES;
                break;
            case Candlestick.SHOOTING_STAR:
                patternName = Candlestick.PATTERN_NAME_SHOOTING_STAR;
                break;
            case Candlestick.BEARISH_ENGULFING:
                patternName = Candlestick.PATTERN_NAME_BEARISH_ENGULFING;
                break;
            case Candlestick.BULLISH_ENGULFING:
                patternName = Candlestick.PATTERN_NAME_BULLISH_ENGULFING;
                break;
        }

        return patternName;
    }
}
