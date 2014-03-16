package tt.lab.android.ieltspass.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.Context;
import tt.lab.android.ieltspass.Constants;

public class Settings {
	public static String INTERNET = Constants.SD_PATH;
	public static String STORAGE = Constants.SETTINGS_VALUE_INTERNET_WIFI;
	Activity activity;
	public static boolean onlyUseWifi = true;
	
	public Settings(Activity activity){
		this.activity = activity;
		read();
	}
	public void read() {
		
		try {
//			InputStream is = Constants.assetManager.open();
			FileInputStream is = this.activity.openFileInput(Constants.SETTINGS_NAME);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {
				s = s.trim();
				String[] ss = s.split("\t");
				String key = ss[0];
				String value = ss[1];
				if(Constants.SETTINGS_KEY_INTERNET.equals(key)){
					INTERNET = value;
				} else if(Constants.SETTINGS_KEY_STORAGE.equals(key)){
					STORAGE = value;
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public  void write(String key, String value){
		try {
			//FileOutputStream fileOutputStream = new FileOutputStream("");
			FileOutputStream fileOutputStream =  this.activity.openFileOutput(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream));
			writer.println(key+"\t"+value);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}