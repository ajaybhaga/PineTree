/**
 * DISCLAIMER
 *
 * The quality of the code is such that you should not copy any of it as best
 * practice how to build Vaadin applications.
 *
 * @author ajay.bhaga@gmail.com
 *
 */
package com.bhaga.pinetree.ui;

import com.bhaga.pinetree.nn.data.ResultData;
import com.bhaga.pinetree.ui.data.DataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.awt.Color;
import java.util.List;
import java.util.Set;

public class ResultsView extends VerticalLayout implements View {

    @Override
    public void enter(ViewChangeEvent event) {
        setSizeFull();
        addStyleName("results");

        TabSheet results = new TabSheet();
        results.setSizeFull();
        results.addStyleName("borderless");
        addComponent(results);

        Set<String> keySet = ((DashboardUI) getUI()).dataProvider.getAllResultDataKeySet();

        for (String symbol : keySet) {
            List<ResultData> resultDataList = ((DashboardUI) getUI()).dataProvider.getAllResultDataBySymbol(symbol);

            int k = 1;
            for (ResultData resultData : resultDataList) {

                VerticalLayout runVL = new VerticalLayout();
                runVL.setMargin(true);
                runVL.setSpacing(true);

                String runDataType = "";
                if (resultData.getTimeInterval() == DataProvider.Config.TIME_INTERVAL_HOURLY) {
                    runDataType = "(" + "hourly" + ")";
                } else if (resultData.getTimeInterval() == DataProvider.Config.TIME_INTERVAL_DAILY) {
                    runDataType = "(" + "daily" + ")";
                }
                runVL.setCaption(symbol + " #" + k + " " + runDataType);

                // Add title
                Label title = new Label("Analysis Results");
                title.addStyleName("h1");
                title.setSizeUndefined();
                runVL.addComponent(title);


                final Label tickerSymbolLabel = new Label();
                tickerSymbolLabel.setValue("Ticker Symbol: " + resultData.getTickerSymbol());
                runVL.addComponent(tickerSymbolLabel);

                final Label numInputsLabel = new Label();
                numInputsLabel.setValue("Number of inputs: " + resultData.getNumInputs());
                runVL.addComponent(numInputsLabel);

                for (int i = 0; i < resultData.getDirPredictAcc().length; i++) {
                    final Label titleLabel = new Label();
                    titleLabel.setContentMode(ContentMode.HTML);
                    titleLabel.setValue("<br />Network " + (i + 1) + "<br />");
                    runVL.addComponent(titleLabel);
                                        
                    final Label networkErrorLabel = new Label();
                    networkErrorLabel.setValue("Network Error: " + (resultData.getNetworkError()[i] * 100.0D) + "%");
                    runVL.addComponent(networkErrorLabel);
                    
                    final Label dirPredictAccLabel = new Label();
                    dirPredictAccLabel.setValue("Directional Prediction Accuracy: " + (resultData.getDirPredictAcc()[i] * 100.0D) + "%");
                    runVL.addComponent(dirPredictAccLabel);
                    
                    final Label smapeLabel = new Label();
                    smapeLabel.setValue("SMAPE: " + (resultData.getSMAPE()[i] * 100.0D) + "%");
                    runVL.addComponent(smapeLabel);
                    
                    final Label predictivePercLabel = new Label();
                    predictivePercLabel.setValue("Predictive Percentage (Overall): " + (resultData.getPredictivePerc()[i] * 100.D) + "%");
                    runVL.addComponent(predictivePercLabel);                                       
                }

                ControllerChart controllerChart = new ControllerChart(resultData.getChartData(1));
                controllerChart.setSizeFull();
                runVL.addComponent(controllerChart);
                controllerChart.drawChart();

                ForecastChart forecastChart = new ForecastChart(resultData);
                forecastChart.setSizeFull();
                runVL.addComponent(forecastChart);
                forecastChart.drawChart();

                MACDChart macdChart = new MACDChart(resultData.getChartData(1));
                macdChart.setSizeFull();
                runVL.addComponent(macdChart);
                macdChart.drawChart();

                results.addTab(runVL).setClosable(false);
                k++;
            }
        }


        results.setSelectedTab(results.getComponentCount() - 1);
        /*
         results.setCloseHandler(new CloseHandler() {
         public void onTabClose(TabSheet tabsheet, Component tabContent) {

         }
         });*/
    }
}
