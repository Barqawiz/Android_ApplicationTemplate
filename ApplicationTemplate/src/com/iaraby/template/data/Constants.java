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

/**
 * Static variables encapsulated into one place for easy access any where in the application 
 *
 */
public class Constants {

	/*Constants*/
	public final static String LOG_TAG = "MonkeyLog";
	public final static String SPLIT = ":";
	public final static int EMPTY_INT = -1;
	
	/*Pages*/
	public final static int LIST_PAGE    = 0;
	public final static int DETAILS_PAGE = 1;
	//public final static int COMMON_PAGE  = 2;
	
	
	/*MODES*/
	public static int Content_MODE = 1;
	public static int FAV_MODE     = 2;
	
	/*Links*/
	public final static String MARKET_LINK =  "market://search?q=pub:";
	
	/*Params Tags*/
	public final static String PARAM_CAT_ID_TAG    = "cat_id";
	public final static String PARAM_CAT_POS_TAG   = "cat_position";
	public final static String PARAM_SEL_POS_TAG   = "selected_position";
	public final static String PARAM_SEL_ID_TAG    = "selected_id";
	public final static String PARAM_SEL_PAGE_TAG  = "selected_page";
	public final static String PARAM_IS_FAV     = "is_fav";
	public final static String PARAM_FAV_LIST   = "fav_list";
	public final static String PARAM_FAV_VER    = "version";
}
