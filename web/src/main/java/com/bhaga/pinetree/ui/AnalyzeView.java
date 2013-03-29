/**
 * DISCLAIMER
 *
 * The quality of the code is such that you should not copy any of it as best
 * practice how to build Vaadin applications.
 *
 * @author Ajay Bhaga
 *
 */
package com.bhaga.pinetree.ui;

import com.bhaga.pinetree.nn.MarketBuildTraining;
import com.bhaga.pinetree.nn.data.NNConfigData;
import com.bhaga.pinetree.nn.data.ResultData;
import com.bhaga.pinetree.ui.data.DataProvider;
import com.bhaga.pinetree.ui.util.StringUtility;
import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AnalyzeView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;
    Object editableId = null;
    private Worker worker = null;
    private Progress progress;
    TextField symbol = new TextField("Symbol:");
    TextField exchange = new TextField("Exchange:");
    private Button analyze = new Button("Analyze");
    private ComboBox intervalSelect = new ComboBox("Time Interval:");
    private int selectedTimeInterval = -1;
    long startTime = System.nanoTime();
    // NN Config
    NNConfigData nnConfigData = new NNConfigData();

    public class Worker extends Thread {

        @Override
        public void run() {

            ResultData resultData = new ResultData();

            try {
                MarketBuildTraining.generate(symbol.getValue(), exchange.getValue(), null, selectedTimeInterval, resultData, nnConfigData, progress);

                progress.setProgress(100);
                progress.updateProgress("Analysis complete.");
                //progress.disable();
                progress.closeWindow();

                final Window w = new Window("Analysis Results");
                w.setWidth("800px");
                progress.setWindow(w);
                w.setModal(true);
                w.setClosable(false);
                w.setResizable(false);
                //w.addStyleName("edit-dashboard");

                VerticalLayout vl = new VerticalLayout();
                vl.setMargin(true);
                vl.setSpacing(true);

                final Label tickerSymbolLabel = new Label();
                tickerSymbolLabel.setValue("Ticker Symbol: " + resultData.getTickerSymbol());
                vl.addComponent(tickerSymbolLabel);

                for (int i = 0; i < resultData.getDirPredictAcc().length; i++) {
                    final Label titleLabel = new Label();
                    titleLabel.setContentMode(ContentMode.HTML);
                    titleLabel.setValue("<br />Iteration " + (i + 1) + "<br />");
                    vl.addComponent(titleLabel);

                    final Label dirPredictAccLabel = new Label();
                    dirPredictAccLabel.setValue("Directional Prediction Accuracy: " + (resultData.getDirPredictAcc()[i] * 100.0D) + "%");
                    vl.addComponent(dirPredictAccLabel);

                    final Label smapeLabel = new Label();
                    smapeLabel.setValue("SMAPE: " + (resultData.getSMAPE()[i] * 100.0D) + "%");
                    vl.addComponent(smapeLabel);

                    final Label predictivePercLabel = new Label();
                    predictivePercLabel.setValue("Predictive Percentage (Overall): " + (resultData.getPredictivePerc()[i] * 100.D) + "%");
                    vl.addComponent(predictivePercLabel);
                }

                vl.addComponent(new HorizontalLayout() {
                    {
                        setMargin(true);
                        setSpacing(true);
                        addStyleName("footer");
                        setWidth("100%");

                        Button close = new Button("Close");
                        close.addClickListener(new ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                w.close();
                            }
                        });
                        close.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);
                        addComponent(close);
                        setExpandRatio(close, 1);
                        setComponentAlignment(close,
                                Alignment.TOP_RIGHT);

                    }
                });


                // Store result data
                ((DashboardUI) getUI()).dataProvider.addResultData(symbol.getValue(), resultData);

                getUI().getSession().lock();
                try {
                    getUI().addWindow(w);
                } finally {
                    getUI().getSession().unlock();
                }

                w.setContent(vl);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                progress.setProgress(100);
                progress.updateProgress("Analysis failed.");
                //progress.disable();
                progress.closeWindow();

                final Window w = new Window("Error Reported");
                w.setWidth("800px");
                progress.setWindow(w);
                w.setModal(true);
                w.setClosable(false);
                w.setResizable(false);
                //w.addStyleName("edit-dashboard");

                VerticalLayout vl = new VerticalLayout();
                vl.setMargin(true);
                vl.setSpacing(true);

                final TextArea errorTextArea = new TextArea();
                errorTextArea.setSizeFull();
                errorTextArea.setValue(e.getMessage());
                e.printStackTrace();
                vl.addComponent(errorTextArea);

                vl.addComponent(new HorizontalLayout() {
                    {
                        setMargin(true);
                        setSpacing(true);
                        addStyleName("footer");
                        setWidth("100%");

                        Button close = new Button("Close");
                        close.addClickListener(new ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                w.close();
                            }
                        });
                        close.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);
                        addComponent(close);
                        setExpandRatio(close, 1);
                        setComponentAlignment(close,
                                Alignment.TOP_RIGHT);

                    }
                });

                getUI().getSession().lock();
                try {
                    getUI().addWindow(w);
                } finally {
                    getUI().getSession().unlock();
                }

                w.setContent(vl);

            }

            // Enable button
            analyze.getUI().getSession().lock();
            try {
                analyze.setEnabled(true);
            } finally {
                analyze.getUI().getSession().unlock();
            }

        }
    }

    @Override
    public void enter(ViewChangeEvent event) {

        //setSizeFull();
        setSpacing(true);
        setMargin(true);
        addStyleName("analyze");

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName("toolbar");
        addComponent(toolbar);

        Label title = new Label("Analyze");
        title.addStyleName("h1");
        title.setSizeUndefined();
        toolbar.addComponent(title);
        toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        symbol = new TextField("Symbol:");
        symbol.setInputPrompt("Enter commodity symbol");
        symbol.setWidth("300px");
        addComponent(symbol);

        exchange = new TextField("Exchange:");
        exchange.setInputPrompt("Enter exchange");
        exchange.setWidth("300px");
        addComponent(exchange);


        intervalSelect.setInputPrompt("Choose time interval");
        for (int i = 0; i < DataProvider.Config.TIME_INTERVAL.length; i++) {
            intervalSelect.addItem(DataProvider.Config.TIME_INTERVAL[i]);
        }
        intervalSelect.setWidth("300px");
        addComponent(intervalSelect);

        intervalSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String valueString = String.valueOf(event.getProperty().getValue());
                if (DataProvider.Config.TIME_INTERVAL[DataProvider.Config.TIME_INTERVAL_DAILY].equals(intervalSelect.getValue())) {
                    selectedTimeInterval = DataProvider.Config.TIME_INTERVAL_DAILY;
                } else if (DataProvider.Config.TIME_INTERVAL[DataProvider.Config.TIME_INTERVAL_HOURLY].equals(intervalSelect.getValue())) {
                    selectedTimeInterval = DataProvider.Config.TIME_INTERVAL_HOURLY;
                } else {
                    selectedTimeInterval = -1;
                }
            }
        });

        Label settingTitle = new Label("Neural Network Input Configuration");
        settingTitle.addStyleName("h2");
        addComponent(settingTitle);

        CheckBox williamsRCheckBox = new CheckBox("Williams R", nnConfigData.getWilliamsR());
        williamsRCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setWilliamsR((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(williamsRCheckBox);

        CheckBox williamsRScoreCheckBox = new CheckBox("Williams R Score", nnConfigData.getWilliamsRScore());
        williamsRScoreCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setWilliamsRScore((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(williamsRScoreCheckBox);

        CheckBox cciCheckBox = new CheckBox("CCI", nnConfigData.getCCI());
        cciCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setCCI((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(cciCheckBox);

        CheckBox mfiCheckBox = new CheckBox("MFI", nnConfigData.getMFI());
        mfiCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMFI((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(mfiCheckBox);

        CheckBox macdCheckBox = new CheckBox("MACD", nnConfigData.getMACD());
        macdCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMACD((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(macdCheckBox);

        CheckBox macdPeakCheckBox = new CheckBox("MACD Peak", nnConfigData.getMacdPeak());
        macdPeakCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMacdPeak((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(macdPeakCheckBox);

        CheckBox macdPeakScoreCheckBox = new CheckBox("MACD Peak Score", nnConfigData.getMacdPeakScore());
        macdPeakScoreCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMacdPeakScore((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(macdPeakScoreCheckBox);

        CheckBox macdZeroScoreCheckBox = new CheckBox("MACD Zero Score", nnConfigData.getMacdZeroScore());
        macdZeroScoreCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMacdZeroScore((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(macdZeroScoreCheckBox);

        CheckBox mfiCciZeroScoreCheckBox = new CheckBox("MFI CCI Zero Score", nnConfigData.getMfiCciZeroScore());
        mfiCciZeroScoreCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setMfiCciZeroScore((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(mfiCciZeroScoreCheckBox);

        CheckBox candlesticksCheckBox = new CheckBox("Candlesticks", nnConfigData.getCandlesticks());
        candlesticksCheckBox.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(final ValueChangeEvent event) {
                nnConfigData.setCandlesticks((Boolean) event.getProperty().getValue());
            }
        });
        addComponent(candlesticksCheckBox);

        analyze.addStyleName("default");
        addComponent(analyze);

        analyze.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                if (symbol.getValue() == null) {
                    Notification.show("No symbol entered", "Please enter a commodity symbol.", Type.ERROR_MESSAGE);
                    return;
                }

                if (exchange.getValue() == null) {
                    Notification.show("No exchange entered", "Please enter an exchange.", Type.ERROR_MESSAGE);
                    return;
                }

                if (selectedTimeInterval == -1) {
                    Notification.show("No time interval selected", "Please select a time interval.", Type.ERROR_MESSAGE);
                    return;
                }

                startTime = System.nanoTime();

                final ProgressIndicator pi = new ProgressIndicator();
                pi.setCaption("Analysis Progress:");
                pi.setWidth("100%");
                pi.setEnabled(true);
                progress = new Progress(pi);

                final Window w = new Window("Analyzing");
                w.setWidth("800px");
                progress.setWindow(w);
                w.setModal(true);
                w.setClosable(false);
                w.setResizable(false);
                //w.addStyleName("edit-dashboard");

                final Label timeLabel = new Label();
                timeLabel.setValue("Elapsed Time: " + StringUtility.formatInterval(0));
                timeLabel.setImmediate(true);
                getUI().addWindow(w);

                // the Refresher polls automatically
                final Refresher refresher = new Refresher();
                refresher.setRefreshInterval(1000);
                refresher.addListener(new RefreshListener() {
                    @Override
                    public void refresh(Refresher source) {

                        // Convert to milliseconds
                        long estimatedTime = (System.nanoTime() - startTime) / 1000000;

                        timeLabel.getUI().getSession().lock();
                        try {
                            timeLabel.setValue("Elapsed Time: " + StringUtility.formatInterval(estimatedTime));
                        } finally {
                            timeLabel.getUI().getSession().unlock();
                        }

                        if (!progress.isActive()) {
                            // stop polling
                            removeExtension(source);
                        }
                    }
                });

                w.setContent(new VerticalLayout() {
                    {
                        addExtension(refresher);
                        setMargin(true);
                        setSpacing(true);
                        addComponent(pi);
                        addComponent(new HorizontalLayout() {
                            {
                                setMargin(true);
                                setSpacing(true);
                                addStyleName("footer");
                                setWidth("100%");
                                addComponent(timeLabel);
                            }
                        });
                    }
                });

                progress.setProgress(0);
                progress.updateProgress("Beginning analysis...");
                worker = new Worker();
                worker.start();
                analyze.setEnabled(false);

            }
        });

    }
}
