package com.example.shuber;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.shuber.databinding.ActivityMainMapsCustomerBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

public class MainMapsCustomerActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button requestBtn;
    private MarkerOptions origin, destination;
    private Polyline currentPolyline;
    private TextView originView, destinationView;
    private String originStr, destinationStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps_customer);

//        binding = ActivityMainMapsCustomerBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestBtn = (Button) findViewById(R.id.btn_request);
        originView = (TextView) findViewById(R.id.origin);
        destinationView = (TextView) findViewById(R.id.destination);
        originStr = "Sheffield Hallam University";
        destinationStr = "Meadowhall Shopping Centre";
        originView.setText(originStr);
        destinationView.setText(destinationStr);
        origin = new MarkerOptions().position(new LatLng(53.37858515848802, -1.4662131352110608)).title(originStr);
        destination = new MarkerOptions().position(new LatLng(53.41448193146003, -1.4112114584024953)).title(destinationStr);


        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getUrl(origin.getPosition(), destination.getPosition(), "driving");
                System.out.println(url);
                request(url);
            }
        });
    }

    private void request(String url){
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url).get();
        Request request = builder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("#Error");
            };
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if(body != null){
                    try {
                        JSONObject jObject = new JSONObject(body.string());
                        JSONArray routes =  jObject.getJSONArray("routes");
                        if(currentPolyline != null){
                            currentPolyline.remove();
                        }
                        List<LatLng> decodedPoints = PolyUtil.decode(routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points"));
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {

                                        mMap.addMarker(origin);
                                        mMap.addMarker(destination);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(),12),1300,null);
                                        PolylineOptions options = new PolylineOptions();
                                        options.width(8);
                                        options.color(Color.RED);
                                        options.addAll(decodedPoints);
                                        currentPolyline = mMap.addPolyline(options);
                                    }
                                });
                            }
                        }).start();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
        });
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
        mMap = googleMap;
    }

    private String getUrl(LatLng origin, LatLng destination, String directionMode) {
        String originStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationStr = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=" + directionMode;
        String parameter = originStr + "&" + destinationStr + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameter + "&key=AIzaSyBXUl5rsuZjF4LJSiTGdb19AV3UnApGqhE";
        return url;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.origin:
                System.out.println("origin");
                break;
            case R.id.destination:
                System.out.println("destination");
                break;
        }
    }
}


