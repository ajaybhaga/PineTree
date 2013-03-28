package com.bhaga.pinetree.ui;

import com.bhaga.pinetree.nn.data.ChartData;
import com.cs.Chart;
import java.text.SimpleDateFormat;

/**
 * Controller Chart
 *
 * @author Ajay Bhaga
 */
public class ControllerChart extends Chart {

    public ControllerChart(ChartData chartData) {
        super();

        String clickJS = null;
        String dataClickJS = null;

        String pattern = "MM/dd/yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        int numPoints = 40;
        String dataJS = "var #id#_s1 = [";
        //for (int i = chartData.getMaxBegIdx(); i < chartData.getInputClose().length; i++) {
        for (int i = chartData.getInputClose().length - numPoints; i < chartData.getInputClose().length; i++) {
            String dateStr = format.format(chartData.getInputDate()[i]);

            dataJS = dataJS + "['" + dateStr + "'," + chartData.getInputOpen()[i] + "," + chartData.getInputHigh()[i] + "," + chartData.getInputLow()[i] + "," + chartData.getInputClose()[i] + "]";

            if (i != chartData.getInputClose().length - 1) {
                dataJS = dataJS + ",";
            }
        }
        dataJS = dataJS + "];";

        String initJS =
                "var #id#_plot;"
                + dataJS
                + "var #id#_plot_options = {"
                + "title: 'OHLC',"
                + "axesDefaults: {"
                + "useSeriesColor:true,"
                + "rendererOptions: {"
                + "}"
                + "},"
                + "axes: {"
                + "xaxis: {"
                + "renderer: $.jqplot.CategoryAxisRenderer,"
                + "rendererOptions:{tickRenderer:$.jqplot.CanvasAxisTickRenderer},"
                + "tickOptions:{ prefix: '', formatString: '%m/%d/%Y %H:%M:%S', angle: 90, fontSize: '8pt' }"
                + "},"
                + "yaxis: {"
                + "tickOptions:{ prefix: '', formatString: '%#.2f' }"
                + "}"
                + "},"
                + "series: [{renderer:$.jqplot.OHLCRenderer, rendererOptions:{candleStick:true,fillUpBody:true,upBodyColor:'#00E000',downBodyColor:'#FF0000'}}],"
                + "cursor:{"
                + "show: false,"
                + "showTooltip: false,"
                + "zoom:false,"
                //+ "constrainZoomTo: 'x'"
                + "},"
                + "highlighter: {"
                + "show: true,"
                + "tooltipContentEditor:"
                + "function(str, seriesIndex, pointIndex, plot) {"
                + ""
                + "return  'Date: ' + plot.data[seriesIndex][pointIndex][0] + '<br\\>' "
                + "+ 'Open: ' + plot.data[seriesIndex][pointIndex][1] + '<br\\>'"
                + "+ 'High: ' + plot.data[seriesIndex][pointIndex][2] + '<br\\>'"
                + "+ 'Low: ' + plot.data[seriesIndex][pointIndex][3] + '<br\\>'"
                + "+ 'Close: ' + plot.data[seriesIndex][pointIndex][4];"
                + "}"
                + "}"
                + "};"
                + "$.jqplot.config.enablePlugins = true;"
                + "try{ #id#_plot.target.empty(); #id#_plot.destroy(); }catch(e){};"
                + "#id#_plot = $.jqplot('#id#', [#id#_s1], #id#_plot_options);";
        
        String refreshJS = initJS;

        super.init(initJS, refreshJS, clickJS, dataClickJS);
    }

    @Override
    public void attach() {
        super.attach();
        this.drawChart();
    }
}
