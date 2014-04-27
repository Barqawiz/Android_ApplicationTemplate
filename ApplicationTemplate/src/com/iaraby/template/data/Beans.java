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
 * Collection of objects to describe database tables and fields
 */
public class Beans {

	/**
	 * Class to describe category table
	 *
	 */
	public class Category {
		public final static String TABLE_NAME = "Category";
		public final static String COL_ID = "_id"; //Integer
		public final static String COL_NAME = "name";
		
		
	} //class: describe table category

	/**
	 * Class to describe content table
	 *
	 */
	public class Content {
		public final static String TABLE_NAME = "Content";
		public final static String COL_ID = "_id"; //Integer
		public final static String COL_TITLE = "title";
		public final static String COL_CONTENT = "content";
		public final static String COL_IMAGE = "image";
		public final static String COL_FLAG = "flag";
		public final static String COL_CAT_ID = "category_id"; //Integer
		
	} //class: describe table content
	
	/**
	 * class to describe favorite table
	 * 
	 *NOTE: THIS CLASS NOT SUPPORTED IN THIS VERION AS THE FAVORITES ARE STORED IN
	 *SHARED PREFERENCES AND MANAGED THROUGH "FavoriteManager"
	 */
	public class Favorite {
		public final static String TABLE_NAME = "Favorite";
		public final static String COL_ID   = "_id"; //Integer
		public final static String COL_TITLE = "title";
		public final static String COL_CONTENT = "content";
		public final static String COL_IMAGE = "image";
		public final static String COL_CONTENT_ID = "content_id";
	} //class: describe table favorite

} // class: collection of objects thats describe the database
