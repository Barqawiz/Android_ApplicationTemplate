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

import java.io.IOException;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.company.appname.R;
import com.iaraby.db.helper.Config;
import com.iaraby.monkeycore.MasterNotifier;
import com.iaraby.template.data.Constants;
import com.iaraby.template.data.FavoriteManager;
import com.iaraby.template.data.MyDataAdapter;
import com.iaraby.template.data.Preferences;
import com.iaraby.template.view.fragment.DetailsFrag;
import com.iaraby.template.view.fragment.ListFrag;
import com.iaraby.utility.LogManager;

public class Master_Activity extends FragmentActivity implements MasterNotifier {

	private int currentPage;
	private boolean isDual;
	private boolean isFav;
	// views
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_master_);
		// **Initial
		this.currentPage = Constants.EMPTY_INT;
		initialDatabase();
		int landThreshold = (int) getResources().getDimension(R.dimen.is_land);
		if (landThreshold > 0)
			this.isDual = true;
		
		if (getIntent().getExtras() != null) {
			isFav = getIntent().getExtras().getBoolean(Constants.PARAM_IS_FAV);
		}
		if (savedInstanceState != null) {
			this.currentPage = savedInstanceState.getInt(
					Constants.PARAM_SEL_PAGE_TAG, this.currentPage);
			isFav = savedInstanceState.getBoolean(Constants.PARAM_IS_FAV);
		}
		// **UI
		if (Preferences.getInstance(this).isRTL() && isDual) {
			setContentView(R.layout.activity_master_right);
		} else {
			setContentView(R.layout.activity_master_);
		}

		// **Process
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.list_container,
							ListFrag.createInstance(this.isDual, this.isFav)).commit();
		} 

	}

	private void initialDatabase() {
		if (!MyDataAdapter.getInstance().isOpen()) {
			int version = 1;
			try {
				version = Preferences.getInstance(this).getDatabaseVersion();
			} catch(NotFoundException ex){
				LogManager.getIns().ee(Constants.LOG_TAG, "Error to get database version from config.xml");
			} catch (Exception e) {
				LogManager.getIns().ee(Constants.LOG_TAG, "Error to get is_right_to_left from config.xml");
			}
			Config config = new Config("data.sqlite", version, this);
			try {
				MyDataAdapter.getInstance().openAndMaintainFav(config);
			} catch (IOException e) {
				LogManager.getIns().ee(getString(R.string.app_name),
						"Error openning database: " + e.toString());
			} // try to open the database
		} // check if database opened or not
	} // method: initial the database to be ready for operations

	@Override
	public void onBackPressed() {
		if (!this.isDual && findViewById(R.id.container) != null) {

			if (findViewById(R.id.content_container).getVisibility() == View.VISIBLE) {
				// if one view and current visible is details back to list
				if (isFav)
					validateListItems(-1); //always refresh when back from favorite details
				activeList();
			} else {
				super.onBackPressed();
			} // check which view is visible
		} else {
			// for dual view always processed with on back
			super.onBackPressed();
		} // check if dual views or one view
	} // Method: handle on back pressed

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.PARAM_SEL_PAGE_TAG, this.currentPage);
		outState.putBoolean(Constants.PARAM_IS_FAV, this.isFav);
		super.onSaveInstanceState(outState);
	}

	// ** Notify Methods
	@Override
	public void setDetails(int position, long id) {
		DetailsFrag detailsFrag = getDetailsFrag();
		if (detailsFrag != null)
			detailsFrag.clear();
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_container,
						DetailsFrag.createInstance(position, id, this.isFav))
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
		this.currentPage = Constants.DETAILS_PAGE;
	} // method: set details for selected position in the list

	public void activeList() {
		findViewById(R.id.content_container).setVisibility(View.GONE);
		findViewById(R.id.list_container).setVisibility(View.VISIBLE);
		setTitle(getString(R.string.title_activity_master_));
		this.currentPage = Constants.LIST_PAGE;
		setMneuVisibility(false);
	}

	public void activeDetails() {
		findViewById(R.id.content_container).setVisibility(View.VISIBLE);
		findViewById(R.id.list_container).setVisibility(View.GONE);
		this.currentPage = Constants.DETAILS_PAGE;
		setMneuVisibility(true);
	}

	public void setTitle(String title) {
		getActionBar().setTitle(title);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public DetailsFrag getDetailsFrag() {
		DetailsFrag detailsFrag = (DetailsFrag) getSupportFragmentManager()
				.findFragmentById(R.id.content_container);
		return detailsFrag;
	}

	public void updateFavButton(long selectedId) {
		if (menu != null && selectedId != Constants.EMPTY_INT) {
			MenuItem favItem = menu.findItem(R.id.action_fav);
			if (FavoriteManager.getInstance(this).isFav(selectedId)) {
				favItem.setTitle(getString(R.string.action_fav));
				favItem.setIcon(R.drawable.menu_item_fav);
			} else {
				favItem.setTitle(getString(R.string.action_not_fav));
				favItem.setIcon(R.drawable.menu_item_not_fav);
			} // check if selected item favorite or not
		} // make sure menu item not null
	} // method: update favorite menu item

	public void validateListItems(int numDetailsItems) {
		ListFrag frag = (ListFrag) getSupportFragmentManager()
				.findFragmentById(R.id.list_container);
		if (frag != null) {
			if (frag.countItems() != numDetailsItems)
				frag.refreshList();
		} //
	}
	
	/* Menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.master_, menu);
		//this.menu = menu;
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.menu = menu;
		
		// control menu visibility
		if (isDual || this.currentPage == Constants.DETAILS_PAGE)
			setMneuVisibility(true);
		else if (this.currentPage == Constants.LIST_PAGE)
			setMneuVisibility(false);
		//update action button status
		DetailsFrag frag = getDetailsFrag();
		if (frag != null) {
			long selectedId = frag.getSelectedId();
			updateFavButton(selectedId);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		DetailsFrag frag = getDetailsFrag();
		if (frag != null ) {
			int id = item.getItemId();
			if (id == R.id.action_share) {
				frag.shareCurrent();
			} else if (id == R.id.action_fav) {
				long selectedID = frag.handleFavAction();
				updateFavButton(selectedID);
			} // check selected menu item
		}
		return super.onOptionsItemSelected(item);
	}

	private void setMneuVisibility(boolean isVisible) {
		if (menu != null) {
			menu.findItem(R.id.action_fav).setVisible(isVisible);
			menu.findItem(R.id.action_share).setVisible(isVisible);
		}
	} // method: set menu v
}
