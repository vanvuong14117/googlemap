package com.example.nvvuong.ggmap2.Direction;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.nvvuong.ggmap2.MapsActivity;
import com.example.nvvuong.ggmap2.R;
import com.example.nvvuong.ggmap2.Retrofit.API;
import com.example.nvvuong.ggmap2.Retrofit.GeocodingRoot;
import com.example.nvvuong.ggmap2.Retrofit.Geometry;
import com.example.nvvuong.ggmap2.Retrofit.Loading;
import com.example.nvvuong.ggmap2.Retrofit.Result;
import com.example.nvvuong.ggmap2.Retrofit.ServiceAPI;
import com.example.nvvuong.ggmap2.Retrofit.GPSTracker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Seachplace {
    private final String MAP_TYPE_SEARCH_RESTAURANT = "restaurant";
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;
    private final String MAP_SEARCH_RADIUS = "5000";
    Context context;

    public Seachplace(DirectionFinderListener listener, String origin) {
        this.listener = listener;
        this.origin = origin;

    }
    //download noi dung json ve
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());//goi downloadrawdata và createUrl
    }
    //rap noi dung nguoi dung nhap vao url
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
      //  String urlDestination = URLEncoder.encode(destination, "utf-8");
//        Toast.makeText(context, "aaaaa",Toast.LENGTH_LONG).show();
        return DIRECTION_URL_API +  urlOrigin + "&radius="+MAP_SEARCH_RADIUS+"&type=" + "restaurant"+ "&keyword=cruise&key=" + GOOGLE_API_KEY;
    }
    //thuc hien download data theo url ve
    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        //down ve xong thi xu ly
        @Override
        protected void onPostExecute(String res) {

            //  parseJSon(res);//goi ham xu ly

        }
    }


    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;
        ServiceAPI serviceAPI = API.getData();
        //GPSTracker gpsTracker = new GPSTracker(this);
        List<Router> routes = new ArrayList<Router>();
        JSONObject jsonData = new JSONObject(data); // data la nguyen ket qua
        //chuyen thanh object


        //lay thong tin mang routes (thuong co 1 routes : 1 cách duy nhất chỉ đường)
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

        //sử lý nhiều kết quả trả về (nhiều cách tìm đường)
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Router route = new Router();
//overview_polyline danh sach cac tọa độ chi đường
            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));

            //dinh dang danh sach toa do thanh kieu LatLng roi add vao danh sach
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }
//goi ham onDirectionFinderSuccess  duoc dinh nghia trong main
        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}

