package com.example.nvvuong.ggmap2.Retrofit;

import com.example.nvvuong.ggmap2.Direction.Router;

import java.util.List;

public class DirectionRoot
{
    private List<Router> routes;

    public List<Router> getRoutes() {
        return routes;
    }

    public DirectionRoot setRoutes(List<Router> routes) {
        this.routes = routes;
        return this;
    }
}
