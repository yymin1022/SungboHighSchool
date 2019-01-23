package com.sungbospot.lunch;

import android.os.*;
import android.widget.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;

import static com.sungbospot.lunch.Tools.isNetwork;

public class ActivityMap extends AppCompatActivity
{
    private GoogleMap mMap;
    private LatLng schoolLocation = new LatLng(37.4798049,126.9196267);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
        if(isNetwork(getApplicationContext())){
            SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_view);
            mapFragment.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.addMarker(new MarkerOptions().position(schoolLocation).title("성보고등학교").snippet("서울특별시 관악구 남부순환로 156길 39 (미성동, 성보고등학교)"));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.setMinZoomPreference(15);
                    mMap.setMaxZoomPreference(19);
                    mMap.setBuildingsEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(schoolLocation));
                }
            });
        }else{
            Toast toast = Toast.makeText(this, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT);toast.show();
            finish();
        }
	}
}