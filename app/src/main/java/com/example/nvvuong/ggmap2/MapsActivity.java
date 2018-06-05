package com.example.nvvuong.ggmap2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nvvuong.ggmap2.Direction.DirectionFinder;
import com.example.nvvuong.ggmap2.Direction.DirectionFinderListener;
import com.example.nvvuong.ggmap2.Direction.Router;
import com.example.nvvuong.ggmap2.Direction.Seachplace;
import com.example.nvvuong.ggmap2.Retrofit.API;
import com.example.nvvuong.ggmap2.Retrofit.GPSTracker;
import com.example.nvvuong.ggmap2.Retrofit.GeocodingRoot;
import com.example.nvvuong.ggmap2.Retrofit.Geometry;
import com.example.nvvuong.ggmap2.Retrofit.Loading;
import com.example.nvvuong.ggmap2.Retrofit.Result;
import com.example.nvvuong.ggmap2.Retrofit.ServiceAPI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    int  PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static String MAP_SEARCH_RADIUS = "500";
    private GoogleMap mMap;
    private Button btnFindPath, btnSeach;
    private EditText etOrigin;
    private TextView a;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient apiClient;
    private Location currentLocation;
    LocationManager locationManager;
    Criteria criteria;
    Location location;
    String origin = "", origin1 = "";
    //private final String MAP_SEARCH_RADIUS = "500";
    private final String MAP_TYPE_SEARCH_RESTAURANT = "restaurant";
    private final String MAP_TYPE_SEARCH_ATM = "atm";
    private final String MAP_TYPE_SEARCH_HOPITOL = "hospital";


    public static String google_api_key = "AIzaSyAOiWfx_dxUK4ww0S4xcoIq4JwIkOGqiLA";
    private static final int LOCATION_REQUEST = 500;
    String arr[]={
            "nhahang",
            "hospital",
            "atm"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );

        origin ="10.7388249,106.6785877";
        //Lấy đối tượng Spinner ra
        Spinner spin=(Spinner) findViewById(R.id.spinner1);
        //Gán Data source (arr) vào Adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        arr
                );
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        //Thiết lập adapter cho Spinner
            spin.setAdapter(adapter);
        //thiết lập sự kiện chọn phần tử cho Spinner
        spin.setOnItemSelectedListener(new MyProcessEvent());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        btnFindPath = (Button) findViewById( R.id.btnFindPath );
        etOrigin = (EditText) findViewById( R.id.etOrigin );
        etDestination = (EditText) findViewById( R.id.etDestination );
        btnSeach = findViewById( R.id.buttonSeach );
        //a = findViewById( R.id.test );
        // origin ="10.7388249,106.6785877";
        // load the animation
        Animation animFadein;
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        // set animation listener
       // animFadein.setAnimationListener(MapsActivity.this);
btnSeach.startAnimation( animFadein );
        btnFindPath.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaceHere();
                mMap.clear();
                sendRequestSeach_timduong();

            }
        } );
        btnSeach.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaceHere();
                // sendRequest_seachplace();
                origin = location.getLatitude() + "," + location.getLongitude();
                if (btnSeach.getText()=="nhahang") {
                    showListPlaceGoogleMaps( MAP_TYPE_SEARCH_RESTAURANT, R.drawable.restaurant_market );
                }else if (btnSeach.getText()=="atm"){
                    showListPlaceGoogleMaps( MAP_TYPE_SEARCH_ATM, R.drawable.marker01 );

                }else if (btnSeach.getText()=="hospital")
                {
                    showListPlaceGoogleMaps( MAP_TYPE_SEARCH_HOPITOL, R.drawable.shopping_market );
                }
            }
        } );

    }

    public void showListPlaceGoogleMaps(final String typeName, final int resource) {
        mMap.clear();
        final Loading loading = Loading.create( MapsActivity.this );
        loading.show();

        //khoi tao ket noi retrofit toi https://maps.googleapis.com/maps/api/"
        ServiceAPI serviceAPI = API.getData();
        //khoi tao gpstracker lay vi tri hien tai
        GPSTracker gpsTracker = new GPSTracker( this );

        //goi call.
        //goi getlocationByTyper trong serviceAPI , truyen vao 4 tham so

        Call<GeocodingRoot> rootCall = serviceAPI.getLocationByType( origin, MAP_SEARCH_RADIUS, typeName, google_api_key );
      // kieu tra về là 1  geocodingRoot .ben trong geocodingroot chua list result
        rootCall.enqueue( new Callback<GeocodingRoot>() {
            @Override
            public void onResponse(Call<GeocodingRoot> call, Response<GeocodingRoot> response) {
                GeocodingRoot geocodingRoot = response.body();
                List<Result> results = geocodingRoot.getResults();
                if (results == null || results.isEmpty()) {
                    Toast.makeText( getApplicationContext(), "Not Found!", Toast.LENGTH_SHORT ).show();
                } else {
                    //Toast.makeText( getApplicationContext(), "tim duoc r" + results, Toast.LENGTH_SHORT ).show();
                    //a.setText(results.toString());
                    //etDestination.setText(  results.toString());
                    for (Result result : results) {

                        Geometry geometry = result.getGeometry();
                        LatLng latLng = new LatLng( geometry.getLocation().getLat(), geometry.getLocation().getLng() );
                        MarkerOptions markerOptions = new MarkerOptions().position( latLng )
                                .flat( true )
                                .icon( BitmapDescriptorFactory.fromResource( resource ) )
                                .snippet( result.getVicinity() )
                                .title( result.getName() );
                        Marker marker = mMap.addMarker( markerOptions );

                    }
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<GeocodingRoot> call, Throwable t) {
                Toast.makeText( getApplicationContext(), "ERROR", Toast.LENGTH_SHORT ).show();
                Log.d( "DDDDDDDDDDDDD2", t.toString() );
                loading.dismiss();
            }
        } );

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled( true );
                }
        }

        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
//        getPlaceHere();
        //atLng diadiem1 = new LatLng( 10.762984, 106.682329 );

        mMap.clear();

        //tao marker, title là ten cua cái marker ,tao marker don gian
        //  mMap.addMarker(new MarkerOptions().position(diadiem1).title("day la dh khoa hoc tu nhien"));
        //custom marker
      /*  mMap.addMarker( new MarkerOptions()
                .position( diadiem1 )
                .title( "tu nhien univercity" )
                .icon( BitmapDescriptorFactory.fromResource( R.drawable.marker01 ) ) );


*/

        /*-----------------------------------------------------------
        //ve duong di toi toa do
        LatLng diadiem2 = new LatLng(10.759677, 106.682387);
        mMap.addPolyline(new PolylineOptions().add(
                diadiem1,
                new LatLng(10.761184, 106.683331),
                new LatLng(10.761184, 106.683331),
                diadiem2
                )
                        .width(10)
                        .color(Color.RED)
        );
        -------------------------------------------------------------*/
        //di chuyen camera
       // mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( diadiem1, 20 ) );

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //set vi tri hien tai.
        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        criteria = new Criteria();
        location = locationManager.getLastKnownLocation( locationManager.getBestProvider( criteria, false ) );
        origin ="";
        origin = "" + location.getLatitude() + "," + location.getLongitude() + "";

        mMap.setMyLocationEnabled( true );
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),
                                                                        location.getLongitude() ), 20 ) );
    }

    //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( origin, 20 ) );

    private void sendRequest_seachplace() {

        //kiem tra rong
        String origin1 = etOrigin.getText().toString();
        origin1 = origin;
        String destination1 = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText( this, "Please enter origin address!", Toast.LENGTH_SHORT ).show();
            return;
        }
        try {

            //goi destinationFinder.execute
            new Seachplace( this, origin1 ).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestSeach_timduong() {
        //tim kiem theo destination
        //kiem tra rong
        String origin1 = etOrigin.getText().toString();
        origin1 = origin.toString();
        String destination1 = etDestination.getText().toString();
        if (origin1.isEmpty()) {
            Toast.makeText( this, "Please enter origin address!", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (destination1.isEmpty()) {
            Toast.makeText( this, "Please enter destination address!", Toast.LENGTH_SHORT ).show();
            return;
        }

        try {

            //goi destinationFinder.execute
            new DirectionFinder( this, origin1, destination1 ).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show( this, "Please wait.",
                "Finding direction..!", true );

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Router> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Router route : routes) {
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( route.startLocation, 16 ) );
            ((TextView) findViewById( R.id.tvDuration )).setText( route.duration.text );
            ((TextView) findViewById( R.id.tvDistance )).setText( route.distance.text );

            originMarkers.add( mMap.addMarker( new MarkerOptions()
                    .icon( BitmapDescriptorFactory.fromResource( R.drawable.start_blue ) )
                    .title( route.startAddress )
                    .position( route.startLocation ) ) );
            destinationMarkers.add( mMap.addMarker( new MarkerOptions()
                    .icon( BitmapDescriptorFactory.fromResource( R.drawable.end_green ) )
                    .title( route.endAddress )
                    .position( route.endLocation ) ) );
//tao polyline,add danh sach cac toa do vao polyline
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic( true ).
                    color( Color.BLUE ).
                    width( 10 );

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add( route.points.get( i ) );
//luu lai polyline là 1 mang len co thể xóa
            polylinePaths.add( mMap.addPolyline( polylineOptions ) );
        }
    }

    private void getPlaceHere() {
        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation( locationManager.getBestProvider( criteria, false ) );
        origin ="";
        origin = "" + location.getLatitude() + "," + location.getLongitude() + "";
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(), location.getLongitude() ), 20 ) );
    }

    private class MyProcessEvent implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0,
                                   View arg1,
                                   int arg2,
                                   long arg3) {
            //arg2 là phần tử được chọn trong data source
            btnSeach.setText(arr[arg2]);
        }
        //Nếu không chọn gì cả
        public void onNothingSelected(AdapterView<?> arg0) {
            btnSeach.setText("");
        }
    }
    }
