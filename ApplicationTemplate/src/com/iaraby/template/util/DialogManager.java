package com.iaraby.template.util;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.company.appname.R;

public class DialogManager {
	
	public static void showInfoDialog(Context context) {
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.about);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	} 
}
