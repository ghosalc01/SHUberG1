package com.example.shuber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainMapsDriverActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions origin, destination;
    private Polyline currentPolyline;
    private Button acceptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps_driver);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        origin = new MarkerOptions().position(new LatLng(53.37858515848802, -1.4662131352110608)).title("Sheffield Hallam University");
        destination = new MarkerOptions().position(new LatLng(53.41448193146003, -1.4112114584024953)).title("Meadowhall Shopping Centre");

        acceptBtn = (Button) findViewById(R.id.btn_accept);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMapsDriverActivity.this);
                builder.setMessage("You did accept the customer's request")
                        .setTitle("Notification");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        request(getUrl(origin.getPosition(),destination.getPosition(),"driving"));
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
                                        if(currentPolyline != null){
                                            currentPolyline.remove();
                                        }
                                        mMap.addMarker(origin);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(),12),1300,null);
                                        mMap.addMarker(destination);
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
}
