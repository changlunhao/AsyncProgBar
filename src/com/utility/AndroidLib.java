package com.utility;

import java.io.File;

import android.content.Context;

public class AndroidLib {

	public static String getExternalStoreageName(Context ctx, String folder) {
		String f = null;
		File a;
		if(folder == null)
			f ="downloadfile";
		else
			f= folder;
		
		a= new File(ctx.getExternalFilesDir(null), f);
			
		String name = a.getPath() + "/";

		new File(name).mkdir();
		File b = new File(name);
		if (!b.exists() || !b.isDirectory()) {
			name = ctx.getFilesDir().toString() + "/"+f+"/";
			new File(name).mkdir();
		}
		
		return name;
	}
	
}
