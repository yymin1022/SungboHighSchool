package com.sungbospot.lunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.webkit.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import static com.sungbospot.lunch.Tools.isNetwork;

public class ActivityBus extends AppCompatActivity
{
    String ActivityBus_url;

	WebView infoWebView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        if (isNetwork(getApplicationContext())) {
            Intent intent = getIntent();
            String where = intent.getStringExtra("where");
            switch(where){
                case "Nangok":
                    ActivityBus_url = "http://m.bus.go.kr/mBus/bus.bms?search=21109";
                    break;
                case "Sillim":
                    ActivityBus_url = "http://m.bus.go.kr/mBus/bus.bms?search=21116";
                    break;
            }
            infoWebView = findViewById(R.id.WebViewBus);
            infoWebView.getSettings().setJavaScriptEnabled(true);
            infoWebView.setWebViewClient(new mWebViewClient());
            infoWebView.loadUrl(ActivityBus_url);
        } else {
            Toast.makeText(getApplicationContext(), "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
            finish();
        }
	}

	private class mWebViewClient extends WebViewClient{
		ProgressDialog dialog;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			dialog = new ProgressDialog(ActivityBus.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("로딩중입니다.");
			dialog.setCancelable(false);
			dialog.setProgress(0);
			dialog.setMax(100);
			dialog.show();
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Toast.makeText(ActivityBus.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
			if (dialog.isShowing()) {
				dialog.cancel();
				finish();
			}
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			if (dialog.isShowing()) {
				view.loadUrl("javascript:(function() { " +
									"document.getElementById('header').style.display='none';" +
									"document.getElementById('busArvlInfoByRouteLayer').style.display='none';" +
									"})()");
				dialog.cancel();
			}
		}
	}
}
