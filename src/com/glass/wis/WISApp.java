package com.glass.wis;

import android.app.Application;
import android.content.Context;

public class WISApp extends Application  {
	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        WISApp.context = getApplicationContext();
	    }

	    public static Context getAppContext() {
	        return WISApp.context;
	    }
}
