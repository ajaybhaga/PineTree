package com.vaadin.demo.dashboard;

/*
 * Author:  Ajay Bhaga
 */
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

/**
 * This class is used to update progress indicators from threads
 *
 * @author Ajay Bhaga
 */
public class Progress {

    public final static int MAX = 100;
    private int progress;
    private ProgressIndicator pi;
    private Window window = null;

    public Progress(ProgressIndicator pi) {
        // Store the progress indicator
        this.pi = pi;

        // Set progress indicator type
        this.pi.setIndeterminate(false);

        // Set polling frequency to 0.5 seconds.
        pi.setPollingInterval(500);
    }

    public void disable() {
        this.pi.setEnabled(false);
        this.pi.setValue(-1f);
    }

    public boolean isActive() {
        if ((progress >= 0) && (progress < 100)) {
            return true;
        } else {
            return false;
        }
    }

    public int getProgress() {
        return progress;
    }

    // Progress is a percentage
    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void closeWindow() {

        if (window != null) {
            // Remove progress window
            window.close();
        }
    }

    // Return the float representation of the progress
    public float getFloat() {
        return (float) ((float) this.progress / (float) this.MAX);
    }

    public void updateProgress(String status) {
        pi.getUI().getSession().lock();
        try {
            pi.setCaption(status);

            if (this.progress == this.MAX) {
                pi.setValue(1f);
            } else {

                if (this.progress >= 0) {
                    // Make sure the progress indicator is enabled
                    this.pi.setEnabled(true);
                    float completed = getFloat();
                    pi.setValue(completed);
                } else {
                    // Disable the progress indicator
                    this.pi.setEnabled(false);
                }
            }

        } finally {
            pi.getUI().getSession().unlock();
        }
    }
}