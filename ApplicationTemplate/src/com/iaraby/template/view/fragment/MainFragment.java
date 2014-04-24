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
package com.iaraby.template.view.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.company.appname.R;
import com.iaraby.template.data.FavoriteManager;
import com.iaraby.template.data.Preferences;
import com.iaraby.template.data.Constants;
import com.iaraby.template.util.FontManager;
import com.iaraby.template.view.Master_Activity;
import com.iaraby.utility.Utility;

public class MainFragment extends Fragment implements OnClickListener {

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

		// UI
		TextView appTitle = (TextView) rootView.findViewById(R.id.title_view);
		TextView listButton = (TextView) rootView
				.findViewById(R.id.list_button);
		TextView favButton = (TextView) rootView.findViewById(R.id.fav_button);
		TextView moreButton = (TextView) rootView
				.findViewById(R.id.more_button);

		// Click
		listButton.setOnClickListener(this);
		favButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);

		// Process
		FontManager.getInstance(getActivity()).setTextFont(appTitle,
				FontManager.TITLE);
		FontManager.getInstance(getActivity()).setTextFont(listButton,
				FontManager.TITLE);
		FontManager.getInstance(getActivity()).setTextFont(favButton,
				FontManager.TITLE);
		FontManager.getInstance(getActivity()).setTextFont(moreButton,
				FontManager.TITLE);

		return rootView;
	}

	private void openDeveloperPage() {
		// NOTE: you can edit this to open site or ads instead of developer page
		try {
			Uri marketUri = Uri.parse(Preferences.getInstance(getActivity())
					.getMarketLink());
			Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Utility.showToastMessage(getActivity(),
					"Install marketplace for more apps");
		} // try-catch
	} // method: open developer page in the market for more apps

	/* Handle Clicks */
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.list_button:
			intent = new Intent(getActivity(), Master_Activity.class);
			getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			startActivity(intent);
			break;

		case R.id.fav_button:
			if (FavoriteManager.getInstance(getActivity()).countFav() == 0) {
				Utility.showToastMessage(getActivity(),
						getString(R.string.no_fav_message));
			} else {
				intent = new Intent(getActivity(), Master_Activity.class);
				intent.putExtra(Constants.PARAM_IS_FAV, true);
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				startActivity(intent);
			}
			break;

		case R.id.more_button:
			openDeveloperPage();
			break;
		} // check the clicked button
	}

}