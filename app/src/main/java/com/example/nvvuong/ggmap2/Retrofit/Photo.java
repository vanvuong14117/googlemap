package com.example.nvvuong.ggmap2.Retrofit;

public class Photo {
    String photo_reference;

    public String getPhoto_reference() {
        return photo_reference;
    }

    public Photo setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
        return this;
    }

    @Override
    public String toString() {
        return "Photos{" +
                "photo_reference='" + photo_reference + '\'' +
                '}';
    }
}
