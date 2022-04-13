package com.nscharrenberg.um.multiagentsurveillance.headless.utils;


public class StopWatch {
    private long startTime;
    private long endTime;
    private boolean isRunning = false;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    public void stop() throws Exception {
        if (!isRunning) {
            throw new Exception("Stopwatch is not running");
        }

        this.endTime = System.currentTimeMillis();
        this.isRunning = false;
    }

    public Long getStatEndTime(){
        return endTime - startTime;
    }

    public Long getDurationInMillis() {
        return System.currentTimeMillis() - startTime;
    }
}
