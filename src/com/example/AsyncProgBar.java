package com.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


import com.utility.AndroidLib;
import com.utility.MySQLiteOpenHelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AsyncProgBar extends Activity {
  

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private Button mStartBtn;
	private ProgressDialog mProgressDialog;
	private MySQLiteOpenHelper dbHelper=null;
	
	  /* version必須大於等於1 */
	  int version = 1;
	  
	  /* Table資料表 */
	  String tables[] = { "mytable" };
	  
	  /* 欄位名稱 */
	  String fieldNames[][] =
	  {
	    { "f_id", "f_name", "f_file", "f_note" }
	  };
	  
	  /* 欄位型態 */
	  String fieldTypes[][] =
	  {
	    { "INTEGER PRIMARY KEY AUTOINCREMENT", "text" , "text", "text"}
	  };
	  
	  
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mStartBtn = (Button) findViewById(R.id.startBtn);
		mStartBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startDownload();
			}
		});
	}
	  
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		if(dbHelper!=null && dbHelper.getReadableDatabase().isOpen())   {
//		      dbHelper.close();
//		}
	}
	private void startDownload() {
//		String url = "http://clh.myds.me/sample.avi";
//		new DownloadFileAsync().execute(url);

		final String savepath = AndroidLib.getExternalStoreageName(AsyncProgBar.this, null);
		
		//dbHelper = new MySQLiteOpenHelper(AsyncProgBar.this, null, null, version, tables, fieldNames, fieldTypes);
		dbHelper = new MySQLiteOpenHelper(AsyncProgBar.this, savepath+"mydb.db", null, version, tables, fieldNames, fieldTypes);
		 String f1[] = { "f_id", "f_name" };
		 String aa ="3333";
         String[] selectionArgs = { aa.toString() };
	    /* SELECT f[] FROM tables[0] */
		
//		/* 呼叫select方法搜尋資料表 */
//          Cursor c = dbHelper.select(tables[0], f1, "f_name=?", selectionArgs, null, null, null);
//          String strRes = "";
//          while (c.moveToNext())
//          {
//            strRes += c.getString(0) + "\n";
//          }
//          
//          if(strRes == "")
//          {
        	  long ltimebase = SystemClock.uptimeMillis();
        	  String s_id = String.valueOf(ltimebase);
            /* 資料庫未找到餐廳名稱，新增它 */
            String f2[] = {"f_id", "f_name", "f_file", "f_note"};
            String v[] = { s_id.toString(),aa.toString(),aa.toString(),aa.toString() };
            long rowid = dbHelper.insert(tables[0], f2, v);
            
//            strRes += rowid + "\n";
//          }
//          else
//          {
//            /* 餐廳名稱已存在資料庫 */
//            
//          }
          
          /* 前往編輯功能 */
          if(dbHelper!=null && dbHelper.getReadableDatabase().isOpen())
          {
            dbHelper.close();
          }		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;
			//create SQLite
			//get external store path
			final String savepath = AndroidLib.getExternalStoreageName(AsyncProgBar.this, null);
//			
//			dbHelper = new MySQLiteOpenHelper(AsyncProgBar.this, savepath+"demo.sqlite", null, version, tables, fieldNames, fieldTypes);
//			 String f[] = { "f_id", "f_name" };
//	         String[] selectionArgs = { "111" };
//		    /* SELECT f[] FROM tables[0] */
//			
//			/* 呼叫select方法搜尋資料表 */
//	          Cursor c = dbHelper.select(tables[0], f, "f_name=?", selectionArgs, null, null, null);
//	          String strRes = "";
//	          while (c.moveToNext())
//	          {
//	            strRes += c.getString(0) + "\n";
//	          }
//	          
//	          if(strRes == "")
//	          {
//	            /* 資料庫未找到餐廳名稱，新增它 */
//	            String f2[] = { "f_name", "f_address", "f_cal"};
//	            String v[] = { "aa", "bb", "cc" };
//	            long rowid = dbHelper.insert(tables[0], f2, v);
//	            strRes += rowid + "\n";
//	          }
//	          else
//	          {
//	            /* 餐廳名稱已存在資料庫 */
//	            
//	          }
//	          
//	          /* 前往編輯功能 */
//	          if(dbHelper!=null && dbHelper.getReadableDatabase().isOpen())
//	          {
//	            dbHelper.close();
//	          }
	          
		    
		    //download file
			try {

				//File vSDCard = null;
				//check sd card is available
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

					
					//vSDCard = Environment.getExternalStorageDirectory();

					//save file
					File vFile = new File(savepath
							+ "downloadTest.avi");

					URL url = new URL(aurl[0]);
					URLConnection conexion = url.openConnection();
					conexion.connect();

					int lenghtOfFile = conexion.getContentLength();
					Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(vFile);

					byte data[] = new byte[1024];

					long total = 0;

					while ((count = input.read(data)) != -1) {
						total += count;
						publishProgress("" + (int) ((total * 100) / lenghtOfFile));
						output.write(data, 0, count);
						
						
					}

					output.flush();
					output.close();
					input.close();
				}

			} catch (Exception e) {

				Log.d("ImageManager", "Error: " + e);

			}
			return null;

		}

		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
	}
	
	
}