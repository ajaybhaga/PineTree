package com.bhaga.pinetree.ui;

import com.bhaga.pinetree.nn.data.ChartData;
import com.cs.Chart;
import java.text.SimpleDateFormat;

/**
 * MACD Chart
 *
 * @author Ajay Bhaga
 */
public class MACDChart extends Chart {

    public MACDChart(ChartData chartData) {
        super();

        String clickJS = null;
        String dataClickJS = null;

        String pattern = "MM/dd/yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        int numPoints = 40;
        String dataJS = "var #id#_s1 = [";
        //for (int i = chartData.getMaxBegIdx(); i < Math.min(chartData.getMACD().getLength(), chartData.getMaxLength()); i++) {
        for (int i = Math.min(chartData.getMACD().getLength(), chartData.getMaxLength()) - numPoints; i < Math.min(chartData.getMACD().getLength(), chartData.getMaxLength()); i++) {
            String dateStr = format.format(chartData.getInputDate()[chartData.getMaxBegIdx() + i]);

            dataJS = dataJS + "['" + dateStr + "'," + chartData.getInputOpen()[i] + "," + chartData.getInputHigh()[i] + "," + chartData.getInputLow()[i] + "," + chartData.getInputClose()[i] + "]";

            if (i != Math.min(chartData.getMACD().getLength(), chartData.getMaxLength()) - 1) {
                dataJS = dataJS + ",";
            }
        }
        dataJS = dataJS + "];";

        String initJS =
                "var #id#_plot;"
                + dataJS
                + "var #id#_plot_options = {"
                + "title: 'MACD',"
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
                + "series: [{lineWidth:2, color: '#711D9F', markerOptions: {show: false}}],"
                + "cursor:{"
                + "show: false,"
                + "showTooltip: true,"
                + "zoom:false,"
                + "tooltipOffset: 10,"
                + "tooltipLocation: 'nw',"
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
