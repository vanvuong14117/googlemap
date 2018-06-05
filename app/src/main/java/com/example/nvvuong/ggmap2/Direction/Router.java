package com.example.nvvuong.ggmap2.Direction;

import com.example.nvvuong.ggmap2.Retrofit.Leg;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;


public class Router {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;

    public OverviewPolyline overview_polyline;
    public List<Leg> legs;

    public OverviewPolyline getOverview_polyline() {
        return overview_polyline;
    }

    public Router setOverview_polyline(OverviewPolyline overview_polyline) {
        this.overview_polyline = overview_polyline;
        return this;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public Router setLegs(List<Leg> legs) {
        this.legs = legs;
        return this;
    }
}
