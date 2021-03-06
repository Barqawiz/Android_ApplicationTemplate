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
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.company.appname.R;
import com.iaraby.db.helper.lib.SimpleCursorLoader;
import com.iaraby.monkeycore.MasterNotifier;
import com.iaraby.template.data.Beans;
import com.iaraby.template.data.Beans.Category;
import com.iaraby.template.data.Constants;
import com.iaraby.template.data.FavoriteManager;
import com.iaraby.template.data.MyDataAdapter;
import com.iaraby.template.data.Preferences;
import com.iaraby.template.util.FontManager;

public class ListFrag extends ListFragment implements LoaderCallbacks<Cursor> {

	private MasterNotifier notifier;
	private boolean isFav;
	private boolean isDual;
	private boolean isSelectDefault;
	private int selectedPos;
	private long selectedId;

	/* Database variables */
	private SimpleCursorAdapter adapter;

	public static ListFrag createInstance(boolean isDual, boolean isFav) {

		ListFrag instance = new ListFrag();
		Bundle args = new Bundle();
		args.putBoolean(Constants.PARAM_IS_FAV, isFav);
		instance.setArguments(args);
		
		return instance;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// initial
		this.selectedId = Constants.EMPTY_INT;
		this.isSelectDefault = false;
		this.selectedPos = 0;
		if (getArguments() != null)
			this.isFav = getArguments().getBoolean(Constants.PARAM_IS_FAV);

		if (savedInstanceState != null) {
			this.selectedPos = savedInstanceState.getInt(
					Constants.PARAM_CAT_POS_TAG, this.selectedPos);
			this.selectedId = savedInstanceState.getLong(
					Constants.PARAM_CAT_ID_TAG, this.selectedId);
			this.isFav = savedInstanceState.getBoolean(Constants.PARAM_IS_FAV,
					this.isFav);
		}

		int landThreshold = (int) getResources().getDimension(R.dimen.is_land);
		if (landThreshold > 0)
			isDual = true;
		customList();
		// Process
		populateList();
		handleMasterStatus(savedInstanceState);

	} // method: on create

	private void handleMasterStatus(Bundle savedInstanceState) {
		if (savedInstanceState != null
				&& this.selectedId != Constants.EMPTY_INT) {
			// come from sleep or rotate
			if ((notifier.getCurrentPage() == Constants.LIST_PAGE || notifier
					.getCurrentPage() == Constants.EMPTY_INT) && !isDual) {
				notifier.activeList();
			} else if (notifier.getCurrentPage() == Constants.DETAILS_PAGE
					&& !isDual) {
				setSelection(selectedPos);
				notifier.activeDetails();
			} else {
				// dual page status
				setSelection(selectedPos);
				if (notifier.getDetailsFrag() == null)
					showDetails(selectedPos, selectedId, true);
			} // back to application latest status
		} else {
			// open first time
			if (isDual) {
				isSelectDefault = true;
			} else {
				notifier.activeList();
			} // check if dual page or single page mode
		} // check instance status
	}

	private void populateList() {

		String[] from = null;
		if (isFav) {
			from = new String[] { Beans.Favorite.COL_TITLE };
		} else {
			from = new String[] { Beans.Category.COL_NAME };
		} //check if fav or list
		int[] to = { R.id.list_item_text };
		int layoutId = R.layout.list_item;
		if (Preferences.getInstance(getActivity()).isRTL())
			layoutId = R.layout.list_item_right;
		
		getLoaderManager().initLoader(1, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), layoutId, null, from,
				to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new Binder());
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		this.selectedPos = position;
		this.selectedId = id;
		showDetails(position, id, false);
	}

	void showDetails(int position, long id, boolean isSelect) {
		if (isSelect) {
			selectionListItem(position);
		}
		notifier.setDetails(position, id);
		if (!isDual)
			notifier.activeDetails();
	}

	private void selectionListItem(int position) {
		getListView().setSelection(position);
		getListView().setItemChecked(position, true);
	}

	public int countItems() {
		return getListView().getCount();
	}
	
	public void refreshList() {
		if (getLoaderManager().getLoader(1) != null) {
			if (selectedPos > 0 && selectedPos+1 == countItems())
				--selectedPos;
			selectedId = Constants.EMPTY_INT;
			getLoaderManager().getLoader(1).onContentChanged();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.PARAM_IS_FAV, this.isFav);
		outState.putInt(Constants.PARAM_CAT_POS_TAG, this.selectedPos);
		outState.putLong(Constants.PARAM_CAT_ID_TAG, this.selectedId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity != null)
			notifier = (MasterNotifier) activity;

		super.onAttach(activity);
	}

	/* Cursor loader */
	@Override
	public Loader<Cursor> onCreateLoader(int args0, Bundle bundel) {
		SimpleCursorLoader loader = new SimpleCursorLoader(getActivity()) {
			@Override
			public Cursor loadInBackground() {

				Cursor cursor = null;
				if (isFav) {
					try {
					cursor = FavoriteManager.getInstance(getActivity())
							.getFavCursor();
					} catch (IllegalAccessError ex) {
						Log.e(Constants.LOG_TAG, ex.toString());
					}	
					
				} else {
					cursor = MyDataAdapter.getInstance().getCtegories();
				} // check if list or favotite mode

				if (selectedId == Constants.EMPTY_INT && cursor != null &&
					cursor.moveToPosition(selectedPos))
					selectedId = cursor.getLong(cursor
							.getColumnIndex(Category.COL_ID));
				if (isSelectDefault) {
					showDetails(selectedPos, selectedId, true);
					isSelectDefault = false;
				}
				return cursor;
			}
		};

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (adapter != null && cursor != null)
			adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (adapter != null)
			adapter.swapCursor(null);
	}

	/* Bind */
	class Binder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int index) {
			if (view.getId() == R.id.list_item_text) {
				TextView text = (TextView) view;
				FontManager.getInstance(getActivity()).setTextFont(text,
						FontManager.CONTENT);
			}
			return false;
		}

	} // class: handle list view bind

	/* Custom */
	private void customList() {
		getListView().setCacheColorHint(0);
		getListView().setSelector(R.drawable.listitem_selector);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setDivider(null);
		getListView().setDividerHeight(
				(int) getResources().getDimension(R.dimen.devider_size));
		if (isDual)
			getListView().setBackgroundColor(
					getResources().getColor(R.color.side_color));
		
	}
} // class: list fragment
