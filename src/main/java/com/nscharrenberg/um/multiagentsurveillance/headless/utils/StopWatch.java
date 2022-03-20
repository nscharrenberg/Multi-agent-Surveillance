package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class StopWatch {
    private static long SPLIT_FREQUENCY = 10;
    private Instant startTime = null;
    private Instant endTime = null;
    private boolean isRunning = false;

    private HashMap<Float, Long> splits = new HashMap<>();
    private float lastPercentage = 0;

    public void start() {
        this.startTime = Instant.now();
        this.isRunning = true;
    }

    public void stop() throws Exception {
        if (!isRunning) {
            throw new Exception("Stopwatch is not running");
        }

        this.endTime = Instant.now();
        this.isRunning = false;
    }

    public void split(float percentage) throws Exception {
        if (!isRunning) {
            throw new Exception("Stopwatch is not running");
        }

        splits.put(percentage, Duration.between(startTime, Instant.now()).toMillis());
    }

    public void saveOrIgnoreSplit(float percentage) throws Exception {
        if (percentage > lastPercentage + SPLIT_FREQUENCY) {
            lastPercentage = percentage;
            split(percentage);
        }
    }

    public Long getTimeSplit(float percentage) {
        return splits.get(percentage);
    }

    public Duration getDuration() throws Exception {
        if (startTime == null) {
            throw new Exception("Stopwatch is not running");
        }

        // Get the current duration
        if (endTime == null && isRunning) {
            return Duration.between(startTime, Instant.now());
        }

        if (endTime == null) {
            throw new Exception("Stopwatch has finished unexpected");
        }

        return Duration.between(startTime, endTime);
    }

    public Long getDurationInMillis() throws Exception {
        return getDuration().toMillis();
    }

    public Long getDurationInSeconds() throws Exception {
        return getDuration().toSeconds();
    }

    public Long getDurationInMinutes() throws Exception {
        return getDuration().toMinutes();
    }

    public Long getDurationInHours() throws Exception {
        return getDuration().toHours();
    }

    public void minusMillis(Long time){
        this.startTime = this.startTime.minusMillis(time);
    }

    public void minusSeconds(Long time){
        this.startTime = this.startTime.minusSeconds(time);
    }
}
