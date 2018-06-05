package com.example.nvvuong.ggmap2.Retrofit;

import com.example.nvvuong.ggmap2.Location;

public class Geometry {

    public Location location;

    public Location getLocation() {
        return location;
    }

    public Geometry setLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}


