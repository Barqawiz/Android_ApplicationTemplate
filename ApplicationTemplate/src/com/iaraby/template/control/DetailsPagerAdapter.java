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

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.company.appname.R;
import com.iaraby.template.data.Beans.Content;
import com.iaraby.template.data.Preferences;
import com.iaraby.template.data.Constants;
import com.iaraby.template.util.FontManager;

public class DetailsPagerAdapter extends PagerAdapter{

	private Context context;
	private Cursor contentCur;
	private int numViews;
	
	public DetailsPagerAdapter(Cursor contentCur, Context context) {
		this. context = context;
		this.contentCur = contentCur;	
		
		if (contentCur != null) 
			this.numViews = contentCur.getCount();
	} //constructor 
	

	public Object instantiateItem(View container, int position) { 
		
		View rootView = LayoutInflater.from(context).inflate(R.layout.details_item, null);
		
		int itemPosition = getItemPosition(position);
		
		//custom the text
		TextView text = (TextView) rootView.findViewById(R.id.details_text_item);
		FontManager.getInstance(context).setTextFont(text, FontManager.CONTENT);
		if (Preferences.getInstance(context).isRTL()) text.setGravity(Gravity.RIGHT);
		
		//set the text
		if (contentCur != null && contentCur.moveToPosition(itemPosition)) {
			String data = "";
			data = contentCur.getString(contentCur.getColumnIndex(Content.COL_CONTENT));
			text.setText(data);
		} //check if current position available in the cursor
		
		((ViewPager) container).addView(rootView, 0);
		return rootView;
	} //method: create each item in the view pager
	
	public int getItemPosition(int position) {
		if (position >= numViews)
			return 0;
		
		if (Preferences.getInstance(context).isRTL()) {
			position = (numViews - position - 1 >= 0) ? numViews - position - 1: 0;
		}
		return position;
	}
	
	@Override
	public int getCount() {
		return numViews;
	} //method: count the items

	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View)view);
	} // method: remove item from pager adapter
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	public String getContent(int position) {
		if (position < 0 || position >= numViews)
			return null;
		
		contentCur.moveToPosition(position);
		String data = contentCur.getString(contentCur.getColumnIndex(Content.COL_CONTENT));
        
		return data;
	} //method: return data for the selected page
	
	public String getTitle(int position) {
		if (position < 0 || position >= numViews)
			return null;
		
		contentCur.moveToPosition(position);
		String data = contentCur.getString(contentCur.getColumnIndex(Content.COL_TITLE));
        
		return data;
	} //method: return data for the selected page

	
	public Long getId(int position) {
		if (position < 0 || position >= numViews)
			return null;
		
		contentCur.moveToPosition(position);
		long data = contentCur.getLong(contentCur.getColumnIndex(Content.COL_ID));
        
		return data;
	} //method: return data for the selected page

}
