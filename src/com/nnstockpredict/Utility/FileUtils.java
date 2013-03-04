/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nnstockpredict.Utility;

import com.nnstockpredict.data.ChartData;
import com.nnstockpredict.data.indicator.ClosePrice;
import com.nnstockpredict.data.indicator.HighPrice;
import com.nnstockpredict.data.indicator.LowPrice;
import com.nnstockpredict.data.indicator.OpenPrice;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ajaybhaga
 */
public class FileUtils {

    static public final String dataFilename = "data.js";

    /**
     * Fetch the entire contents of a text file, and return it in a String. This
     * style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */
    static public String getContents(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
        /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

    /**
     * Change the contents of text file in its entirety, overwriting any
     * existing text.
     *
     * This style of implementation throws all exceptions to the caller.
     *
     * @param aFile is an existing file which can be written to.
     * @throws IllegalArgumentException if param does not comply.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if problem encountered during write.
     */
    static public void setContents(File aFile, String aContents)
            throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }

        //use buffering
        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write(aContents);
        } finally {
            output.close();
        }
    }

    /**
     * Change the contents of text file in its entirety, overwriting any
     * existing text.
     *
     * This style of implementation throws all exceptions to the caller.
     *
     * @param aFile is an existing file which can be written to.
     * @throws IllegalArgumentException if param does not comply.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if problem encountered during write.
     */
    static public void writeFile(File aFile, String aContents)
            throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }

        //use buffering
        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write(aContents);
        } finally {
            output.close();
        }
    }

    /**
     * Simple test harness.
     */
    public static void test(String... aArguments) throws IOException {
        File testFile = new File("C:\\Temp\\blah.txt");
        System.out.println("Original file contents: " + getContents(testFile));
        setContents(testFile, "The content of this file has been overwritten...");
        System.out.println("New file contents: " + getContents(testFile));
    }

    static public void writeChartData(ChartData chartData) {

        File dataFile = new File(dataFilename);

        // if file doesnt exists, then create it
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        // Use buffering
        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(dataFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        try {

            output.write("name = '" + chartData.getName() + "';\n");

            output.write("ohlc = [");
            for (int i = chartData.getMaxBegIdx(); i < chartData.getInputClose().length; i++) {
                String dateStr = format.format(chartData.getInputDate()[i]);

                output.write("['" + dateStr + "'," + chartData.getInputOpen()[i] + "," + chartData.getInputHigh()[i] + "," + chartData.getInputLow()[i] + "," + chartData.getInputClose()[i] + "]");

                if (i != chartData.getInputClose().length - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("close = [");
            for (int i = chartData.getMaxBegIdx(); i < chartData.getInputClose().length; i++) {
                String dateStr = format.format(chartData.getInputDate()[i]);

                output.write("['" + dateStr + "'," + chartData.getInputClose()[i] + "]");

                if (i != chartData.getInputClose().length - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("forecast = [");
            for (int i = 0; i < chartData.getForecast().length; i++) {
                String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

                output.write("['" + dateStr + "'," + chartData.getForecast()[i] + "]");

                if (i != chartData.getForecast().length - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("forecastError = [");
            for (int i = 0; i < chartData.getForecastError().length; i++) {
                String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

                output.write("['" + dateStr + "'," + chartData.getForecastError()[i] + "]");

                if (i != chartData.getForecastError().length - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("macd = [");
            for (int i = 0; i < chartData.getMACD().getLength(); i++) {
                String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

                output.write("['" + dateStr + "'," + chartData.getMACD().getValues()[i] + "]");

                if (i != chartData.getMACD().getLength() - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("macdZeroScore = [");
            for (int i = 0; i < chartData.getMACDZeroScore().getLength(); i++) {
                String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

                output.write("['" + dateStr + "'," + chartData.getMACDZeroScore().getValues()[i] + "]");

                if (i != chartData.getMACDZeroScore().getLength() - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

            output.write("macdPeakScore = [");
            for (int i = 0; i < chartData.getMACDPeakScore().getLength(); i++) {
                String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

                output.write("['" + dateStr + "'," + chartData.getMACDPeakScore().getValues()[i] + "]");

                if (i != chartData.getMACDPeakScore().getLength() - 1) {
                    output.write(",");
                }
            }
            output.write("];\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}