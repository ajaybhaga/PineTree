package com.bhaga.pinetree.ui;

import com.bhaga.pinetree.nn.data.ChartData;
import com.bhaga.pinetree.nn.data.ResultData;
import com.bhaga.pinetree.ui.data.DataProvider;
import com.cs.Chart;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Forecast Chart
 *
 * @author Ajay Bhaga
 */
public class ForecastChart extends Chart {

    public ForecastChart(ResultData resultData) {
        super();

        String clickJS = null;
        String dataClickJS = null;

        String pattern = "MM/dd/yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        Set<Integer> forwardStepSet = resultData.getChartDataKeySet();

        List<String> seriesList = new ArrayList<String>();

        int numPoints = 40;
        ChartData chartData = resultData.getChartData(1);
        String dataJS = "";                

        seriesList.add("#id#_close_s1");
        dataJS = dataJS + "var #id#_close_s1 = [";        
        //for (int i = chartData.getMaxBegIdx(); i < max; i++) {
        //for (int i = chartData.getMaxBegIdx(); i < chartData.getInputClose().length; i++) {
        for (int i = chartData.getInputClose().length-numPoints; i < chartData.getInputClose().length; i++) {
            String dateStr = format.format(chartData.getInputDate()[i]);

            dataJS = dataJS + "['" + dateStr + "'," + chartData.getInputClose()[i] + "]";

            if (i != chartData.getInputClose().length - 1) {
                dataJS = dataJS + ",";
            }
        }
        dataJS = dataJS + "];";


        for (int forwardStep : forwardStepSet) {

            seriesList.add("#id#_s" + forwardStep);
            dataJS = dataJS + "var #id#_s" + forwardStep + " = [";
            for (int i = resultData.getChartData(forwardStep).getForecast().length-numPoints; i < resultData.getChartData(forwardStep).getForecast().length; i++) {
                String dateStr = format.format(resultData.getChartData(forwardStep).getInputDate()[resultData.getChartData(forwardStep).getMaxBegIdx() + i]);

                dataJS = dataJS + "['" + dateStr + "'," + resultData.getChartData(forwardStep).getForecast()[i] + "]";

                if (i != resultData.getChartData(forwardStep).getForecast().length - 1) {
                    dataJS = dataJS + ",";
                }
            }
            dataJS = dataJS + "];";
        }

        String seriesNameJS = "";
        String seriesListJS = "";
        for (int i = 0; i < seriesList.size(); i++) {
            String series = seriesList.get(i);
            seriesListJS = seriesListJS + series;
            seriesNameJS = seriesNameJS + "'" + series + "'";
            if (i != seriesList.size() - 1) {
                seriesListJS = seriesListJS + ",";
                seriesNameJS = seriesNameJS + ",";
            }
        }

        String tickIntervalJS = "";

        /*if (resultData.getTimeInterval() == DataProvider.Config.TIME_INTERVAL_HOURLY) {
            tickIntervalJS = "tickInterval:'7200000'";
        } else if (resultData.getTimeInterval() == DataProvider.Config.TIME_INTERVAL_DAILY) {
            tickIntervalJS = "tickInterval:'1 week'";
        }*/

        String initJS =
                "var #id#_plot;"
                + dataJS
                + "var #id#_plot_options = {"
                + "title: 'Forecast',"
                + "axesDefaults: {"
                + "useSeriesColor:true,"
                + "rendererOptions: {"
                + "}"
                + "},"
                + "axes: {"
                + "xaxis: {"
                + "renderer: $.jqplot.CategoryAxisRenderer,"
                + "rendererOptions:{tickRenderer:$.jqplot.CanvasAxisTickRenderer},"
                + "tickOptions:{ prefix: '', formatString: '%m/%d/%Y %H:%M:%S', angle: 90, fontSize: '8pt' },"
                + tickIntervalJS
                + "},"
                + "yaxis: {"
                + "tickOptions:{ prefix: '', formatString: '%#.2f' }"
                + "}"
                + "},"
                + "seriesDefaults: {lineWidth:2, markerOptions: { show: false, size: 1 }},"
                + "legend: {"
                + "show: true,"
                + "location: 'sw',"
                + "showLabels: true,"
                + "labels: [" + seriesNameJS + "]},"
                + "cursor:{"
                + "show: false,"
                + "showTooltip: true,"
                //+ "zoom:true,"
                + "tooltipOffset: 10,"
                + "tooltipLocation: 'nw'"
                //+ "constrainZoomTo: 'x'"
                + "},"
                + "highlighter: {"
                + "show: true,"
                + "sizeAdjust: 7.5,"
                + "tooltipContentEditor:"
                + "function(str, seriesIndex, pointIndex, plot) {"
                + ""
                + "return  'Date: ' + plot.data[seriesIndex][pointIndex][0] + '<br\\>' "
                + "+ 'Value: ' + plot.data[seriesIndex][pointIndex][1];"
                + "}"
                + "}"
                + "};"
                + "$.jqplot.config.enablePlugins = true;"
                + "try{ #id#_plot.target.empty(); #id#_plot.destroy(); }catch(e){};"
                + "#id#_plot = $.jqplot('#id#', [" + seriesListJS + "], #id#_plot_options);";

        String refreshJS = initJS;

        super.init(initJS, refreshJS, clickJS, dataClickJS);
    }

    @Override
    public void attach() {
        super.attach();
        this.drawChart();
    }
}
