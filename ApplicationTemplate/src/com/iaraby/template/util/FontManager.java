package com.iaraby.template.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.company.appname.R;
import com.iaraby.utility.LogManager;

public class FontManager {
		
	//public static final int APP_TITLE = 0;
	public static final int TITLE = 1;
	public static final int CONTENT = 2;
	
	private static FontManager instance;
	private Context context;
	
	public static FontManager getInstance(Context context) {
		if (instance == null)
			instance = new FontManager(context);
		
		return instance;
	} //manage class instances
	
	public FontManager(Context context) {
		this.context = context;
	} //constructor
	
	public void setTextFont(TextView text, int key) {
		try {
		Typeface typeface = null;
		switch (key) {
		
		case TITLE:
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_title));
			break;
		case CONTENT:
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_content));
			break;
		default:
			break;
		} //check font type to set 
		
		if (typeface != null && text != null)
			text.setTypeface(typeface);
		} catch (Exception e) {
			LogManager.getIns().e("BQ", "Error Loading Font" + e.toString());
		}
	} //method: set text font
}
