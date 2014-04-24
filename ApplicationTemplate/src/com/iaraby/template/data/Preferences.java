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

import com.company.appname.R;
import com.iaraby.utility.LogManager;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

public class Preferences {

	private static Preferences instance;

	private Context context;
	
	public static Preferences getInstance(Context context) {
		if (instance == null)
			instance = new Preferences(context);
		return instance;
	} //Method: manage instance
	
	private Preferences(Context context) {
		this.context = context;
	} //Constructor
	
	public boolean isRTL() {
		boolean isRigthToLeft = false;
		try {
			isRigthToLeft = context.getResources().getBoolean(R.bool.is_right_to_left);
		} catch(NotFoundException ex){
			LogManager.getIns().ee(Constants.LOG_TAG, "Error to get is_right_to_left from config.xml");
		} catch (Exception e) {
			LogManager.getIns().ee(Constants.LOG_TAG, "Error to get is_right_to_left from config.xml");
		}
		return isRigthToLeft;
	} //method: is right to left
	
	public String getMarketLink() {
		return Constants.MARKET_LINK + context.getString(R.string.google_play_developer_name);
	}
	
} //Class
