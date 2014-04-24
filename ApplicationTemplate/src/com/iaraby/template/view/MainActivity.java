/*The MIT License (MIT)

Copyright (c) 2014 Ahmad Barqawi (github.com/Barqawiz)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/
package com.iaraby.template.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.company.appname.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.iaraby.template.control.FavGarbagCollector;
import com.iaraby.template.data.Constants;
import com.iaraby.template.data.MyDataAdapter;
import com.iaraby.template.util.DialogManager;
import com.iaraby.template.view.fragment.MainFragment;
import com.iaraby.utility.LogManager;
import com.iaraby.utility.Utility;

public class MainActivity extends FragmentActivity {

	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initial
		LogManager.getIns().setDebug(true);
		LogManager.getIns().setError(true);

		// UI
		if (savedInstanceState == null) {
			LogManager.getIns().d(Constants.LOG_TAG, "Set Main Fragment");
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
			
			//call method to handle ad display
			handleInterstitialAd();
		}

	} // method: on create

	private void handleInterstitialAd() {
		String adId = getString(R.string.interstitial_Id);
		if (adId == null || adId.length() == 0)
			return;

		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(adId);
		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();
		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);

		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				if (interstitial.isLoaded()) {
					interstitial.show();
				}
			}
		});
		
	} // method: show ad that cover all the page on start

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			DialogManager.showInfoDialog(MainActivity.this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		//close the database connection before exiting the application
		if (!FavGarbagCollector.IS_RUNNING
				&& MyDataAdapter.getInstance().isOpen())
			MyDataAdapter.getInstance().close();
		
		super.onDestroy();
	}

	
} // class: main
