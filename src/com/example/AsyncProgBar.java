package com.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class AsyncProgBar extends Activity {
  

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private Button mStartBtn;
	private ProgressDialog mProgressDialog;
	private MySQLiteOpenHelper dbHelper=null;
	
	  /* version */
	  int version = 1;
	  
	  /* Table name*/
	  String tables[] = { "mytable" };
	  
	  /* Table field */
	  String fieldNames[][] =
	  {
	    { "f_id", "f_name", "f_file", "f_note" }
	  };
	  
	  /* type*/
	  String fieldTypes[][] =
	  {
	    { "INTEGER PRIMARY KEY AUTOINCREMENT", "text" , "bolb", "text"}
	  };
	  
	  
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		//String url = "http://clh.myds.me/sample.avi";//13.54mb
		//String url = "http://clh.myds.me/video.mp4";//57.35mb
		String url = "http://clh.myds.me/Learning.pdf";//3.62mb
		new DownloadFileAsync().execute(url);


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
	          
			boolean bFirst = false;
			SQLiteDatabase db = null;
		    //download file
			try {
				//for db
				dbHelper = new MySQLiteOpenHelper(AsyncProgBar.this, null/*savepath+"mydb.db"*/, null, version, tables, fieldNames, fieldTypes);//open in Ram
				//dbHelper = new MySQLiteOpenHelper(AsyncProgBar.this, savepath+"mydb.db", null, version, tables, fieldNames, fieldTypes);//open in sd
				 String f2[] = {  "f_file"};
				 String f1[] = {"f_id"};
				 Log.i("db","open db");
				//File vSDCard = null;
				//check sd card is available
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

					
					//vSDCard = Environment.getExternalStorageDirectory();

					//save file
					//File vFile = new File(savepath+ "downloadTest.avi");
					//File vFile = new File(savepath+ "downloadTest.mp4");
					File vFile = new File(savepath+ "downloadTest.pdf");

					URL url = new URL(aurl[0]);
					URLConnection conexion = url.openConnection();
					conexion.connect();

					int lenghtOfFile = conexion.getContentLength();
					Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(vFile);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					byte data[] = new byte[1024];

					long total = 0;

					db = dbHelper.getWritableDatabase();
					db.beginTransaction();
					while ((count = input.read(data)) != -1) {
						total += count;
						publishProgress("" + (int) ((total * 100) / lenghtOfFile));
						output.write(data, 0, count);
						baos.write(data,0,count);
						//select cursor
						 //getBlob
//						 Cursor c = dbHelper.select(tables[0], f1, "f_id=1", null, null, null, null);
//						if(c.getCount()!=0){
//							c.moveToFirst();
//						}
						
						long rowid = 0;
						if(!bFirst){
							rowid  = dbHelper.insert(tables[0], f2, baos.toByteArray());
							Log.i("db","insert data row=>"+rowid);
							bFirst = true;
						}else{
							rowid = dbHelper.update(tables[0], f2, baos.toByteArray(), "f_id=1",null);
							Log.i("db","replace data row=>"+rowid);
						}
						
						
						
						
						
					}
					db.setTransactionSuccessful();
				    
				    
					output.flush();
					output.close();
					input.close();
				}

			} catch (Exception e) {

				Log.d("ImageManager", "Error: " + e);

			}finally{
					db.endTransaction();
		          if(dbHelper!=null && dbHelper.getReadableDatabase().isOpen())
		          {
		            dbHelper.close();
		            Log.i("db","close db");
		          }		
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