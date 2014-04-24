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
package com.iaraby.template.data;

import java.io.IOException;

import android.database.Cursor;

import com.google.android.gms.internal.dv;
import com.iaraby.db.helper.Config;
import com.iaraby.db.helper.DatabaseAdapter;
import com.iaraby.db.helper.DatabaseHelper.DBListener;
import com.iaraby.template.control.FavGarbagCollector;
import com.iaraby.template.data.Beans.Category;
import com.iaraby.template.data.Beans.Content;

public class MyDataAdapter extends DatabaseAdapter {
	private static MyDataAdapter instance;

	public static MyDataAdapter getInstance() {
		if (instance == null)
			instance = new MyDataAdapter();
		return instance;
	}

	/**
	 * Open the database and update the favorite list if the database version
	 * changed
	 * 
	 * @param Config
	 *            database configuration e.g.version
	 * @throws IOException
	 */
	public void openAndMaintainFav(final Config config) throws IOException {

		open(config, new DBListener() {
			public void onFinishCoping() {

			}

			public void onDataExist() {

			}

			public void onDatabaseOpened() {
				if (!FavGarbagCollector.IS_RUNNING
						&& !FavGarbagCollector.isVersionClear(
								config.getContext(), config.getVersion())) {
					FavoriteManager.getInstance(config.getContext())
							.maintainFavChange(config.getVersion());
				} // check if the version cleared and the service not
					// interrupted before
			}
		});

	}

	public Cursor getCtegories() {
		return fetchData(Category.TABLE_NAME, Category.COL_ID,
				Category.COL_NAME);
	}

	public Cursor getCategoryName(long id) {
		String selection = Category.COL_ID + "= ?";
		String[] where = { id + "" };

		return fetchDataWhere(Category.TABLE_NAME, selection, where, null,
				Category.COL_NAME);
	}

	public Cursor getContentList(long cat_id) {
		String selection = Content.COL_CAT_ID + "= ?";
		String[] where = { cat_id + "" };

		return fetchDataWhere(Content.TABLE_NAME, selection, where, null,
				Content.COL_ID, Content.COL_TITLE, Content.COL_CONTENT,
				Content.COL_IMAGE, Content.COL_FLAG);
	}

	public Cursor getContent() {
		return fetchData(Content.TABLE_NAME, Content.COL_ID, Content.COL_TITLE,
				Content.COL_CONTENT, Content.COL_IMAGE, Content.COL_FLAG);
	}

	public Cursor getContentItem(long id) {
		String selection = Content.COL_ID + "= ?";
		String[] where = { id + "" };

		return fetchDataWhere(Content.TABLE_NAME, selection, where, null,
				Content.COL_ID, Content.COL_TITLE, Content.COL_CONTENT,
				Content.COL_IMAGE, Content.COL_FLAG);
	}

	public boolean isContentExist(long id) {
		String selection = Content.COL_ID + "= ?";
		String[] where = { id + "" };

		Cursor cursor = fetchDataWhere(Content.TABLE_NAME, selection, where,
				null, Content.COL_ID);
		if (cursor != null && cursor.moveToFirst())
			return true;

		return false;
	}

} // class: Database Adapter
