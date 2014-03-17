package tt.lab.android.ieltspass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class Logger {
	private static File logfile;
	private static String logsPath;// LogsPath must be reset before all.

	public static void initLog(String path) {
		logsPath = path;
		Utilities.ensurePath(logsPath);
		String logfilePath = logsPath + "/" + getFormatedDate().substring(0, 10) + ".txt";
		logfile = new File(logfilePath);
	}

	public static String getFormatedDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static void i(String tag, String msg) {

		Log.i(tag, msg);
		write(getFormatedDate() + ": " + tag + "\t" + msg);
	}

	private static void write(String s) {
		try {
			if (logfile != null) {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logfile, true)));
				writer.println(s);
				writer.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
