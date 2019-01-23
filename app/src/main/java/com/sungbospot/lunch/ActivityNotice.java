package com.sungbospot.lunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.webkit.*;
import android.widget.*;

import static com.sungbospot.lunch.Tools.isNetwork;


public class ActivityNotice extends Activity {
	String url;
	ProgressDialog dialog;
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		if(isNetwork(getApplicationContext())){
			Intent intent = getIntent();
			String strWhat = intent.getStringExtra("what");
			switch(strWhat){
				case "notice":
					url="http://www.sungbo.hs.kr/70447/subMenu.do";
					break;
				case "home":
					url="http://www.sungbo.hs.kr/70448/subMenu.do";
					break;
			}
			webView = (WebView)findViewById(R.id.NotiWebView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebViewClient(new mWebViewClient());
			webView.loadUrl(url);
		}else{
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
			dialog = new ProgressDialog(ActivityNotice.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("로딩중입니다.");
			dialog.setCancelable(false);
			dialog.show();
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Toast.makeText(ActivityNotice.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
			if (dialog.isShowing()) {
				dialog.cancel();
				finish();
			}
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			if (dialog.isShowing()) {
				view.loadUrl("javascript:(function() { " +
					"document.getElementById('usrCmntDivDivTop').style.display='none';" +
					"document.getElementById('top_menu_031_260171').style.display='none';" +
					"document.getElementById('section_1').style.display='none';" +
					"document.getElementById('section_2').style.display='none';" +
					"document.getElementById('section_3').style.display='none';" +
					"document.getElementById('section_4').style.display='none';" +
					"document.getElementById('section_5').style.display='none';" +
					"document.getElementById('cntTitle').style.display='none';" +
					"document.getElementById('boardMngr_layer').style.display='none';" +
					"document.getElementById('Footer').style.display='none';" +
					"})()");
				dialog.cancel();
			}
		}
	}
}
