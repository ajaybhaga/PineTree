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
import com.bhaga.pinetree.ui.data.DataProvider;
import com.bhaga.pinetree.nn.data.ResultData;
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

    public class Worker extends Thread {

        @Override
        public void run() {

            ResultData resultData = new ResultData();
            try {
                MarketBuildTraining.generate(symbol.getValue(), exchange.getValue(), null, selectedTimeInterval, resultData, progress);

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
                    dirPredictAccLabel.setValue("Directional Prediction Accuracy: " + resultData.getDirPredictAcc()[i] + "%");
                    vl.addComponent(dirPredictAccLabel);
                    final Label mseLabel = new Label();
                    mseLabel.setValue("MSE: " + resultData.getMse()[i]);
                    vl.addComponent(mseLabel);
                    final Label dirMseLabel = new Label();
                    dirMseLabel.setValue("Directional MSE: " + resultData.getDirMse()[i]);
                    vl.addComponent(dirMseLabel);
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
