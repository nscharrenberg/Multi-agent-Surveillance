package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Collection of objects
public abstract class Area<T> {
    protected List<T> region;

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

    public boolean isEmpty() {
        return region.isEmpty();
    }

    public abstract boolean within(int x1, int y1, int x2, int y2);

    public abstract boolean within(int x, int y);

    public abstract List<T> subset(int x1, int y1, int x2, int y2);

    public abstract Optional<T> getByCoordinates(int x, int y);

    public abstract List<T> getBounds();
}
