package com.sungbospot.lunch;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.sungbospot.lunch.Tools.isNetwork;

public class ActivityMain extends AppCompatActivity
{
    TextView tv_temp, tv_weather;
	
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar((androidx.appcompat.widget.Toolbar)findViewById(R.id.main_toolbar));
        tv_temp = findViewById(R.id.temp);
        tv_weather = findViewById(R.id.weather);

        //Show splash
        startActivity(new Intent(this, ActivitySplash.class));

		//Check LOCATION permission
		int permissionCheck= ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.ACCESS_FINE_LOCATION);
		if(permissionCheck != PackageManager.PERMISSION_GRANTED)
		{
			startActivity(new Intent(ActivityMain.this, ActivityPermission.class));
		}

		//Get weather info if network connected
        if(isNetwork(getApplicationContext())){
            new getWeather().execute();
        }
	}

	public void bus(View v){
		AlertDialog.Builder busDialogBuilder = new AlertDialog.Builder(this);
		busDialogBuilder.setMessage("방향을 선택해주세요")
                .setCancelable(true)
                .setPositiveButton("난곡방향",	 new DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ActivityMain.this, ActivityBus.class);
                        intent.putExtra("where", "Nangok");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("신림역방향", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ActivityMain.this, ActivityBus.class);
                        intent.putExtra("where", "Sillim");
                        startActivity(intent);
                    }
                });
		AlertDialog busDialog = busDialogBuilder.create();
		busDialog.show();
	}

	public void devinfo(View v){
		startActivity(new Intent(this, ActivityDevinfo.class));
	}

	public void dial(View V){
		AlertDialog.Builder dialDialogBuilder = new AlertDialog.Builder(ActivityMain.this);
		dialDialogBuilder.setMessage("장난전화 하지 마세요!")
                .setCancelable(	true)
                .setPositiveButton("대표전화", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
				    	try{
			    			Intent intent= new Intent(Intent.ACTION_DIAL, Uri.parse("tel:028628409"));
			    			startActivity(intent);
                        }catch(ActivityNotFoundException e){
                            Toast.makeText(getApplicationContext(), "오류가 발생하여 전화를 걸 수 없습니다.", Toast.LENGTH_LONG).show();
                        }
			    	}
			    })
                .setNegativeButton("행정실", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:028633734"));
                            startActivity(intent);
                        }catch(ActivityNotFoundException e){
                            Toast.makeText(getApplicationContext(), "오류가 발생하여 전화를 걸 수 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
		    	});
		AlertDialog dialDialog = dialDialogBuilder.create();
		dialDialog.show();
	}

	public void meal(View v){
		startActivity(new Intent(this, ActivityMeal.class));
	}

	public void schoolinfo(View v){
		startActivity(new Intent(this, ActivitySchoolinfo.class));
	}

	public void web(View v){
		AlertDialog.Builder webDialogBuilder = new AlertDialog.Builder(this);
		webDialogBuilder.setMessage("성보고등학교 홈페이지로 이동합니다.")
                .setCancelable(true)
                .setPositiveButton("아니오", new DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int id) {
					    dialog.cancel();
				    }
			    })
                .setNegativeButton("예", new DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int id) {
				    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "http://www.sungbo.hs.kr"  ));
				    	startActivity(intent);
				    }
			    });
		AlertDialog webDialog = webDialogBuilder.create();
		webDialog.show();
	}
	
	public void notice(View v){
		AlertDialog.Builder noticeDialogBuilder = new AlertDialog.Builder(this);
		noticeDialogBuilder.setMessage("게시판을 선택해주세요")
                .setCancelable(	true)
                .setPositiveButton("공지사항", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
					    Intent intent = new Intent(ActivityMain.this, ActivityNotice.class);
					    intent.putExtra("what", "notice");
					    startActivity(intent);
				    }
			    })
                .setNegativeButton("가정통신문", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
					    Intent intent = new Intent(ActivityMain.this, ActivityNotice.class);
					    intent.putExtra("what", "home");
					    startActivity(intent);
				    }
			    });
		AlertDialog noticeDialog = noticeDialogBuilder.create();
		noticeDialog.show();
	}
	
	public void map(View v){
		startActivity(new Intent(this, ActivityMap.class));
	}

	//Back button Listener
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
		switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(this);
                exitDialogBuilder.setMessage("애플리케이션을 종료하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                finish();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
                AlertDialog exitDialog = exitDialogBuilder.create();
                exitDialog.show();
		}
    	return super.onKeyDown(keyCode, event);
    }

    private class getWeather extends AsyncTask<String, Void, Document>
    {
        String page = "http://api.wunderground.com/api/b6671f0269c4e83e/conditions/forecast/alert/q/37.4798,126.919631.xml";
        String temp = "";
        String weather = "";

        Document doc;
        URL url;

        @Override
        protected Document doInBackground(String... urls) {
            try {
                url = new URL(page);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            NodeList nodeList = doc.getElementsByTagName("current_observation");
            for(int i = 0; i< nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList node_temp = fstElmnt.getElementsByTagName("temp_c");
                temp += "오늘의 날씨\n" + node_temp.item(0).getChildNodes().item(0).getNodeValue() + "°C";
                temp = temp.replaceAll("null", "");

                NodeList node_weather = fstElmnt.getElementsByTagName("weather");
                weather += node_weather.item(0).getChildNodes().item(0).getNodeValue();
                weather = weather.replaceAll("null", "");
            }
            if(weather.equals("Clear")) {
                weather = "맑음";
            }
            if(weather.equals("Drizzle")) {
                weather = "이슬비";
            }
            if(weather.equals("Light Drizzle")) {
                weather = "이슬비";
            }
            if(weather.equals("Heavy Drizzle")) {
                weather = "이슬비";
            }
            if(weather.equals("Rain")) {
                weather = "비";
            }
            if(weather.equals("Light Rain")) {
                weather = "비";
            }
            if(weather.equals("Heavy Rain")) {
                weather = "비";
            }
            if(weather.equals("Snow")) {
                weather = "눈";
            }
            if(weather.equals("Light Snow")) {
                weather = "눈";
            }
            if(weather.equals("Heavy Snow")) {
                weather = "눈";
            }
            if(weather.equals("Snow Grains")) {
                weather = "쌀알눈";
            }
            if(weather.equals("Light Snow Grains")) {
                weather = "쌀알눈";
            }
            if(weather.equals("Heavy Snow Grains")) {
                weather = "쌀알눈";
            }
            if(weather.equals("Ice Crystals")) {
                weather = "빙정";
            }
            if(weather.equals("Light Ice Crystals")) {
                weather = "빙정";
            }
            if(weather.equals("Heavy Ice Crystals")) {
                weather = "빙정";
            }
            if(weather.equals("Ice Pellets")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Light Ice Pellets")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Heavy Ice Pellets")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Hail")) {
                weather = "우박";
            }
            if(weather.equals("Light Hail")) {
                weather = "우박";
            }
            if(weather.equals("Heavy Hail")) {
                weather = "우박";
            }
            if(weather.equals("Mist")) {
                weather = "엷은 안개";
            }
            if(weather.equals("Light Mist")) {
                weather = "엷은 안개";
            }
            if(weather.equals("Heavy Mist")) {
                weather = "엷은 안개";
            }
            if(weather.equals("Fog")) {
                weather = "안개";
            }
            if(weather.equals("Light Fog")) {
                weather = "안개";
            }
            if(weather.equals("Heavy Fog")) {
                weather = "안개";
            }
            if(weather.equals("Fog Patches")) {
                weather = "안개";
            }
            if(weather.equals("Light Fog Patches")) {
                weather = "안개";
            }
            if(weather.equals("Heavy Fog Patches")) {
                weather = "안개";
            }
            if(weather.equals("Smoke")) {
                weather = "연기";
            }
            if(weather.equals("Light Smoke")) {
                weather = "연기";
            }
            if(weather.equals("Heavy Smoke")) {
                weather = "연기";
            }
            if(weather.equals("Volcanic Ash")) {
                weather = "화산재";
            }
            if(weather.equals("Light Volcanic Ash")) {
                weather = "화산재";
            }
            if(weather.equals("Heavy Volcanic Ash")) {
                weather = "화산재";
            }
            if(weather.equals("Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Light Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Heavy Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Sand")) {
                weather = "모래";
            }
            if(weather.equals("Light Sand")) {
                weather = "모래";
            }
            if(weather.equals("Heavy Sand")) {
                weather = "모래";
            }
            if(weather.equals("Haze")) {
                weather = "실안개";
            }
            if(weather.equals("Light Haze")) {
                weather = "실안개";
            }
            if(weather.equals("Heavy Haze")) {
                weather = "실안개";
            }
            if(weather.equals("Spray")) {
                weather = "비말";
            }
            if(weather.equals("Light Spray")) {
                weather = "비말";
            }
            if(weather.equals("Heavy Spray")) {
                weather = "비말";
            }
            if(weather.equals("Dust Whirls")) {
                weather = "회오리 바람";
            }
            if(weather.equals("Light Dust Whirls")) {
                weather = "회오리 바람";
            }
            if(weather.equals("Heavy Dust Whirls")) {
                weather = "회오리 바람";
            }
            if(weather.equals("Sandstorm")) {
                weather = "모래 바람";
            }
            if(weather.equals("Light Sandstorm")) {
                weather = "모래 바람";
            }
            if(weather.equals("Heavy Sandstorm")) {
                weather = "모래 바람";
            }
            if(weather.equals("Low Drifting Snow")) {
                weather = "눈";
            }
            if(weather.equals("Light Low Drifting Snow")) {
                weather = "눈";
            }
            if(weather.equals("Heavy Low Drifting Snow")) {
                weather = "눈";
            }
            if(weather.equals("Low Drifting Sand")) {
                weather = "모래";
            }
            if(weather.equals("Light Low Drifting Sand")) {
                weather = "모래";
            }
            if(weather.equals("Heavy Low Drifting Sand")) {
                weather = "모래";
            }
            if(weather.equals("Blowing Snow")) {
                weather = "눈";
            }
            if(weather.equals("Light Blowing Snow")) {
                weather = "눈";
            }
            if(weather.equals("Heavy Blowing Snow")) {
                weather = "눈";
            }
            if(weather.equals("Blowing Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Light Blowing Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Heavy Blowing Widespread Dust")) {
                weather = "흙 먼지";
            }
            if(weather.equals("Blowing Sand")) {
                weather = "모래";
            }
            if(weather.equals("Light Blowing Sand")) {
                weather = "모래";
            }
            if(weather.equals("Heavy Blowing Sand")) {
                weather = "모래";
            }
            if(weather.equals("Rain Mist")) {
                weather = "안개를 동반한 비";
            }
            if(weather.equals("Light Rain Mist")) {
                weather = "안개를 동반한 비";
            }
            if(weather.equals("Heavy Rain Mist")) {
                weather = "안개를 동반한 비";
            }
            if(weather.equals("Rain Showers")) {
                weather = "소나기";
            }
            if(weather.equals("Light Rain Showers")) {
                weather = "소나기";
            }
            if(weather.equals("Heavy Rain Showers")) {
                weather = "소나기";
            }
            if(weather.equals("Snow Showers")) {
                weather = "눈";
            }
            if(weather.equals("Light Snow Showers")) {
                weather = "눈";
            }
            if(weather.equals("Heavy Snow Showers")) {
                weather = "눈";
            }
            if(weather.equals("Snow Blowing Snow Mist")) {
                weather = "눈";
            }
            if(weather.equals("Light Snow Blowing Snow Mist")) {
                weather = "눈";
            }
            if(weather.equals("Heavy Snow Blowing Snow Mist")) {
                weather = "눈";
            }
            if(weather.equals("Ice Pellet Showers")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Light Ice Pellet Showers")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Heavy Ice Pellet Showers")) {
                weather = "싸라기눈";
            }
            if(weather.equals("Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Light Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Heavy Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Small Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Light Small Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Heavy Small Hail Showers")) {
                weather = "우박";
            }
            if(weather.equals("Thunderstorm")) {
                weather = "뇌우";
            }
            if(weather.equals("Light Thunderstorm")) {
                weather = "뇌우";
            }
            if(weather.equals("Heavy Thunderstorm")) {
                weather = "뇌우";
            }
            if(weather.equals("Thunderstorms and Rain")) {
                weather = "뇌우";
            }
            if(weather.equals("Light Thunderstorms and Rain")) {
                weather = "뇌우";
            }
            if(weather.equals("Heavy Thunderstorms and Rain")) {
                weather = "뇌우";
            }
            if(weather.equals("Thunderstorms and Snow")) {
                weather = "뇌우를 동반한 눈";
            }
            if(weather.equals("Light Thunderstorms and Snow")) {
                weather = "뇌우를 동반한 눈";
            }
            if(weather.equals("Heavy Thunderstorms and Snow")) {
                weather = "뇌우를 동반한 눈";
            }
            if(weather.equals("Thunderstorms and Ice Pellets")) {
                weather = "뇌우를 동반한 싸라기눈";
            }
            if(weather.equals("Thunderstorms with Small Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Light Thunderstorms with Small Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Heavy Thunderstorms with Small Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Thunderstorms with Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Light Thunderstorms with Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Heavy Thunderstorms with Hail")) {
                weather = "뇌우를 동반한 우박";
            }
            if(weather.equals("Freezing Drizzle")) {
                weather = "얼어 붙은 이슬비";
            }
            if(weather.equals("Light Freezing Drizzle")) {
                weather = "얼어 붙은 이슬비";
            }
            if(weather.equals("Heavy Freezing Drizzle")) {
                weather = "얼어 붙은 이슬비";
            }
            if(weather.equals("Freezing Rain")) {
                weather = "얼어 붙은 비";
            }
            if(weather.equals("Light Freezing Rain")) {
                weather = "얼어 붙은 비";
            }
            if(weather.equals("Heavy Freezing Rain")) {
                weather = "얼어 붙은 비";
            }
            if(weather.equals("Freezing Fog")) {
                weather = "얼어 붙은 안개";
            }
            if(weather.equals("Light Freezing Fog")) {
                weather = "얼어 붙은 안개";
            }
            if(weather.equals("Heavy Freezing Fog")) {
                weather = "얼어 붙은 안개";
            }
            if(weather.equals("Patches of Fog")) {
                weather = "안개";
            }
            if(weather.equals("Shallow Fog")) {
                weather = "안개";
            }
            if(weather.equals("Partial Fog")) {
                weather = "일부 안개";
            }
            if(weather.equals("Overcast")) {
                weather = "흐림";
            }
            if(weather.equals("Partly Cloudy")) {
                weather = "구름 조금";
            }
            if(weather.equals("Mostly Cloudy")) {
                weather = "구름 많음";
            }
            if(weather.equals("Scattered Clouds")) {
                weather = "흩어진 구름";
            }
            if(weather.equals("Small Hail")) {
                weather = "약한 우박";
            }
            if(weather.equals("Squalls")) {
                weather = "스콜";
            }
            if(weather.equals("Funnel Cloud")) {
                weather = "깔때기 구름";
            }
            if(weather.equals("Unknown Precipitation")) {
                weather = "알수없는 강수";
            }
            if(weather.equals("Unknown")) {
                weather = "알수없음";
            }
            tv_temp.setText(temp);
            tv_weather.setText(weather);
            super.onPostExecute(doc);
        }
    }
}

