package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.ArrayList;
import java.util.List;

// Collection of objects
public class Area<T> {
    private List<T> region;

    public Area() {
        this.region = new ArrayList<>();
    }

    public Area(List<T> region) {
        this.region = region;
    }

    public List<T> getRegion() {
        return region;
    }

    public void setRegion(List<T> region) {
        this.region = region;
    }
}
