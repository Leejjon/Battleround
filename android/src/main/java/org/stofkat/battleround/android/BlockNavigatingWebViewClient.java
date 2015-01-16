package org.stofkat.battleround.android;

import java.net.HttpCookie;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BlockNavigatingWebViewClient extends WebViewClient {
	private Activity mainActivity;
	private String serverHostName = "http://192.168.2.4:8888/";
	
	public BlockNavigatingWebViewClient(Activity mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// This will block the user from going to other pages that our own HTML pages!
		if (url.startsWith("file:///android_asset/")) {
			return false;
		} else if (url.startsWith(serverHostName + "Login")) {
			return false;
		} else if (url.startsWith(serverHostName + "assets/pages/levels.html")) {
			return false;
		} else if (url.startsWith(serverHostName + "index.html")) {
			CookieManager cookieManager = CookieManager.getInstance();
			String cookies = cookieManager.getCookie(url);
			
			List<HttpCookie> cookiesList = HttpCookie.parse(cookies);
			
			String JSESSIONID = null;
			for (HttpCookie httpCookie : cookiesList) {
				if (httpCookie.getName().equals("JSESSIONID")) {
					JSESSIONID = httpCookie.getValue();
				}
			}
			
			Intent intent = new Intent(mainActivity, MainActivity.class);
			intent.putExtra("level", "TestLevel");
			intent.putExtra("sessionid", JSESSIONID);
			mainActivity.startActivity(intent);
			return true;
		} else {
			// Attempt to stop the service if a user browses to an url that is not allowed.
//			mainActivity.stopService(new Intent(mainActivity, AuthenticationActivity.class));
			return true;
		}
    }
}
