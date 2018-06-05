package com.example.nvvuong.ggmap2.Direction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Router> route);
}
