/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 **/

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ceab.movelab.tigabib.R;

/**
 * Displays the License screen.
 * 
 * @author John R.B. Palmer
 * 
 */
public class License extends Activity {

	Context context = this;

	private static final String LICENSE_URL_EN = UtilLocal.URL_TIGASERVER + "license/android/en";
	private static final String LICENSE_URL_CA = UtilLocal.URL_TIGASERVER + "license/android/ca";
	private static final String LICENSE_URL_ES = UtilLocal.URL_TIGASERVER + "license/android/es";

	private static final String LICENSE_URL_OFFLINE_EN = "file:///android_asset/html/license_en.html";
	private static final String LICENSE_URL_OFFLINE_CA = "file:///android_asset/html/license_ca.html";
	private static final String LICENSE_URL_OFFLINE_ES = "file:///android_asset/html/license_es.html";

	private WebView myWebView;

	String lang;
	Resources res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {

		res = getResources();
		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		// if (Util.isOnline(context)) {
		setContentView(R.layout.webview);

		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				if (lang.equals("ca"))
					myWebView.loadUrl(LICENSE_URL_OFFLINE_CA);

				else if (lang.equals("es"))
					myWebView.loadUrl(LICENSE_URL_OFFLINE_ES);

				else
					myWebView.loadUrl(LICENSE_URL_OFFLINE_EN);

			}

		});

		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		if (Build.VERSION.SDK_INT >= 7) {
			WebViewApi7.api7settings(myWebView, context);
		}

		if (!Util.isOnline(context)) { // loading offline only if not online
			myWebView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}

		if (lang.equals("ca"))
			myWebView.loadUrl(LICENSE_URL_CA);
		else if (lang.equals("es"))
			myWebView.loadUrl(LICENSE_URL_ES);
		else
			myWebView.loadUrl(LICENSE_URL_EN);

		super.onResume();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Util.isOnline(context)) {
			// Check if the key event was the Back button and if there's history
			if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
				myWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.license_menu, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.language) {

			Intent i = new Intent(License.this, LanguageSelector.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.gpl) {

			Intent i = new Intent(License.this, GPLView.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}
