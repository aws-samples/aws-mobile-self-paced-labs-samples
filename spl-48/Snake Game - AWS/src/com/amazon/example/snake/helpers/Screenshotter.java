package com.amazon.example.snake.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshotter {

	
	public static String getScreen(View v1){
		
	    String mPath = v1.getContext().getExternalFilesDir(null) + "/screenshots/";
	    View v = v1.getRootView();
	    v.setDrawingCacheEnabled(true);
	    Bitmap b = v.getDrawingCache();
	    
	    File myPath = new File(mPath);
	    myPath.mkdirs();
	    
	    myPath = new File(mPath +"screenshot1.png");
	    FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(myPath);
	        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	        fos.flush();
	        fos.close();
	    }catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return myPath.getPath();
	    
	}
}
