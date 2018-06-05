package com.example.nvvuong.ggmap2.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceAPI {
    @GET("geocode/json")
    Call<GeocodingRoot> getLocation(@Query("address") String address,
                                    @Query("key") String key);

    @GET("directions/json")
    Call<DirectionRoot> getDirection(@Query("origin") String origin,
                                     @Query("destination") String destination);

    @GET("place/nearbysearch/json")
    Call<GeocodingRoot> getLocationByType(@Query("location") String location,
                                          @Query("radius") String radius,
                                          @Query("type") String type,
                                          @Query("key") String key);

    @GET("place/photo?maxwidth=400")
    Call<GeocodingRoot> getPhoto(@Query("photoreference") String photoreference,
                                 @Query("key") String key);

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<GeocodingRoot> getNearbyPlaces(@Query("type") String type,
                                        @Query("location") String location,
                                        @Query("radius") int radius);
}
