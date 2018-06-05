package com.example.nvvuong.ggmap2.Retrofit;

import java.util.List;

public class Result { private Geometry geometry;
    private String icon;
    private String name;
    private String vicinity;
    private String reference;
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public Result setPhotos(List<Photo> photos) {
        this.photos = photos;
        return this;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Result setGeometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public Result setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getName() {
        return name;
    }

    public Result setName(String name) {
        this.name = name;
        return this;
    }


    public String getVicinity() {
        return vicinity;
    }

    public Result setVicinity(String vicinity) {
        this.vicinity = vicinity;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Result setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "geometry=" + geometry +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", reference='" + reference + '\'' +
                ", photos=" + photos +
                '}';
    }
}