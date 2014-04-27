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
package com.iaraby.template.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.iaraby.template.data.Beans;
import com.iaraby.template.data.Constants;
import com.iaraby.template.data.FavoriteManager;
import com.iaraby.template.data.MyDataAdapter;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * This service insure the consistency of items in the database and favorite share preferences
 */
public class FavGarbagCollector extends IntentService {


	private static String VERSION_CLR_TAG = "version_clear:";
	public static boolean IS_RUNNING;

	public FavGarbagCollector() {
		super("FavGarbagCollector");
	}
	
	protected void onHandleIntent(Intent intent) {

		IS_RUNNING = true;

		if (intent != null && MyDataAdapter.getInstance().isOpen()) {

			int version = intent.getIntExtra(Constants.PARAM_FAV_VER, Constants.EMPTY_INT);
			
			if (version != Constants.EMPTY_INT) {
				
				HashMap<String, ?> favHash = (HashMap<String, ?>) FavoriteManager.getInstance(this).getFavSet();
				HashMap<String, String> deleteHash = new  HashMap<String, String>();
				
				String selection = null;
				ArrayList<String> whereList = new ArrayList<String>();
				

				for (Map.Entry<String,?> entry : favHash.entrySet()) {
					if (entry.getValue() == null)
						continue;
					if (!entry.getKey().startsWith(FavoriteManager.FAV_ITEM_TAG))
						continue;
					
					String idItem = entry.getValue().toString();
					
					if (selection == null) {
						selection = Beans.Content.COL_ID + " = ? ";
					} else {
						selection += " OR " + Beans.Content.COL_ID + " = ? ";
					}
					whereList.add(idItem);
					deleteHash.put(idItem, idItem);
				} // loop on all favorite ids
				
				//clear the favorite hash because it is reference from shared preferences and the
				//guide line from android don't edit hash from preferences
				favHash = null;
				
				String[] where = new String[whereList.size()];
				where = whereList.toArray(where);
				
				Cursor cursor = MyDataAdapter.getInstance().fetchDataWhere(
						Beans.Content.TABLE_NAME, selection, where, null,
						Beans.Content.COL_ID);

				if (cursor != null && cursor.moveToFirst()) {
					do {
						String itemId = cursor.getString(cursor
								.getColumnIndex(Beans.Content.COL_ID));
						deleteHash.remove(itemId);
					} while (cursor.moveToNext());

				} // make sure the cursor not null
				
				// now the favorite hash contains only the ids to delete
				if (deleteHash.size() > 0) {
					for (Map.Entry<String,String> entry : deleteHash.entrySet()) { 
						try {
							long id = Long.parseLong(entry.getValue());
							FavoriteManager.getInstance(this).removeFav(id);
						}catch(NumberFormatException ex) {}
					}
				} //check if there is items to delete
				
				clearVersion(this, version);
			} // make sure favorite list is well received

		}
	}
	
	@Override
	public void onDestroy() {
		IS_RUNNING = false;
		super.onDestroy();
	}

	public static void clearVersion(Context context, int version) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
			.putInt(VERSION_CLR_TAG + version, version).commit();
		
	}
	
	public static boolean isVersionClear(Context context, int version) {
		return PreferenceManager.getDefaultSharedPreferences(context).
			getInt(VERSION_CLR_TAG + version, Constants.EMPTY_INT) != Constants.EMPTY_INT;
		
	}
} // class: Service
