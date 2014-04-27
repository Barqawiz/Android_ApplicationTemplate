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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.appname.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.iaraby.monkeycore.MasterNotifier;
import com.iaraby.template.control.DetailsPagerAdapter;
import com.iaraby.template.control.DetailsTask;
import com.iaraby.template.data.Constants;
import com.iaraby.template.data.FavoriteManager;
import com.iaraby.template.data.Preferences;
import com.iaraby.template.util.FontManager;
import com.iaraby.utility.LogManager;
import com.iaraby.utility.Utility;

public class DetailsFrag extends Fragment {

	private DetailsPagerAdapter adapter;
	// received
	private MasterNotifier notifier;
	private int catPosition;
	private long catId;
	private boolean isFav;

	// selection
	private int selectedPosition;

	// Views
	private TextView titleView;
	private ViewPager pager;
	private AdView adView;

	public static DetailsFrag createInstance(int position, long id,
			boolean isFav) {

		DetailsFrag instance = new DetailsFrag();
		Bundle args = new Bundle();
		args.putInt(Constants.PARAM_CAT_POS_TAG, position);
		args.putLong(Constants.PARAM_CAT_ID_TAG, id);
		args.putBoolean(Constants.PARAM_IS_FAV, isFav);
		instance.setArguments(args);
		return instance;
	} // method: manage instance

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_details, container,
				false);

		// Initial
		this.selectedPosition = 0;
		catPosition = Constants.EMPTY_INT;
		if (getArguments() != null) {
			this.catPosition = getArguments().getInt(
					Constants.PARAM_CAT_POS_TAG, Constants.EMPTY_INT);
			this.catId = getArguments().getLong(Constants.PARAM_CAT_ID_TAG);
			this.isFav = getArguments().getBoolean(Constants.PARAM_IS_FAV);
			if (isFav)
				this.selectedPosition = this.catPosition;
		}

		if (savedInstanceState != null) {
			this.selectedPosition = savedInstanceState.getInt(
					Constants.PARAM_SEL_POS_TAG, this.selectedPosition);
			this.catPosition = savedInstanceState.getInt(
					Constants.PARAM_CAT_POS_TAG, catPosition);
			this.catId = savedInstanceState.getLong(Constants.PARAM_CAT_ID_TAG,
					catId);
			this.isFav = savedInstanceState.getBoolean(Constants.PARAM_IS_FAV,
					this.isFav);

		}
		// UI
		pager = (ViewPager) rootView.findViewById(R.id.content_pager);
		titleView = (TextView) rootView.findViewById(R.id.content_header);
		adView = (AdView) rootView.findViewById(R.id.ad_container);
		titleView.setText("");
		FontManager.getInstance(getActivity()).setTextFont(titleView,
				FontManager.CONTENT);

		// Process
		if (catPosition != Constants.EMPTY_INT) {
			DetailsTask process = new DetailsTask(getActivity(), catId,
					catPosition, this.isFav, new DetailsTask.HandleResult() {

						@Override
						public void setResult(String title, String desc) {
						}

						@Override
						public void setResult(String title, Cursor contCur) {
							fillData(title, contCur);
						}

						@Override
						public void handleError(String error) {
							LogManager.getIns().e(Constants.LOG_TAG, error);
						}

					});
			process.execute();
			handleAds(adView);
		} // make sure the data is well received
		return rootView;
	} // method: on create

	private void fillData(String title, Cursor contCur) {
		if (titleView != null)
			titleView.setText(title);

		if (pager != null && contCur != null) {
			adapter = new DetailsPagerAdapter(contCur, getActivity());
			pager.setAdapter(adapter);
			this.selectedPosition = adapter.getItemPosition(selectedPosition);
			pager.setCurrentItem(selectedPosition);
			pager.setOnPageChangeListener(new PageChangeListener());
			notifier.updateFavButton(getSelectedId());
			if (isFav) {
				titleView.setText(adapter.getTitle(selectedPosition));
				notifier.validateListItems(adapter.getCount());
			}
			if (contCur.getCount() == 1 && titleView != null) {
				titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
		} // make sure the pager not null

	} // method: fill the data

	private void handleAds(AdView adView) {
		//if ad info not available do not try to get ads
		if (Preferences.getInstance(getActivity()).getAdmobBannerId().length() == 0)
			return;
		
		adView.setVisibility(View.VISIBLE);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	/* Override */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.PARAM_SEL_POS_TAG, this.selectedPosition);
		outState.putInt(Constants.PARAM_CAT_POS_TAG, this.catPosition);
		outState.putLong(Constants.PARAM_CAT_ID_TAG, this.catId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity != null)
			notifier = (MasterNotifier) activity;

		super.onAttach(activity);
	} // method: on Fragment attached to activity

	/* Pager Listener */
	private class PageChangeListener extends SimpleOnPageChangeListener {
		public void onPageSelected(int position) {
			selectedPosition = position;
			if (adapter != null) {
				notifier.updateFavButton(getSelectedId());
				if (isFav)
					titleView.setText(adapter.getTitle(position));
			}
		}
	}

	public void shareCurrent() {
		// share current text
		if (adapter != null) {
			String data = adapter.getContent(selectedPosition);
			if (data != null)
				Utility.share("", data, "", getActivity());
		}

	}

	public long getSelectedId() {
		if (adapter != null)
			return adapter.getId(selectedPosition);
		else
			return Constants.EMPTY_INT;
	}

	public long handleFavAction() {
		long selectedId = getSelectedId();
		FavoriteManager favManager = FavoriteManager.getInstance(getActivity());
		if (favManager.isFav(selectedId)) {
			// remove from favorite
			favManager.removeFav(selectedId);
			Utility.showToastMessage(getActivity(),
					getString(R.string.action_fav));
			// if remove the last item finish the activity
			if (isFav
					&& FavoriteManager.getInstance(getActivity()).countFav() == 0) {
				getActivity().finish();
			}

		} else if (selectedId != Constants.EMPTY_INT) {
			// add to favorite
			if (favManager.isItemInDatabase(selectedId)) {
				favManager.addToFav(selectedId);
				Utility.showToastMessage(getActivity(),
						getString(R.string.action_not_fav));
			} else {
				Utility.showToastMessage(getActivity(),
						getString(R.string.cannot_set_fav));
			} // check if the item in the database

		} // check if the item is favorite or not
		return selectedId;
	}

	public void clear() {
		if (titleView != null) {
			titleView.setText("");
			titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		if (adView != null) {
			adView.setVisibility(View.INVISIBLE);
			adView.destroy();
		}
		pager.removeAllViews();

	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

} // class
