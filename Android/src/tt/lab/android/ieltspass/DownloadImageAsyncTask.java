package tt.lab.android.ieltspass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import tt.lab.android.ieltspass.data.Settings;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public abstract class DownloadImageAsyncTask extends
		AsyncTask<Integer, Integer, String> {

	private static final String TAG = DownloadImageAsyncTask.class.getName();
	private String strurl;
	private String localPath;
	private Bitmap bitmap;

	public DownloadImageAsyncTask(String url, String localPath) {
		this.strurl = url;
		this.localPath = localPath;
	}

	/**
	 * 先缓存到本地
	 */
	@Override
	protected String doInBackground(Integer... arg0) {
		InputStream is = null;
		OutputStream os = null;
		try {
			URL url = new URL(strurl);
			URLConnection openConnection = url.openConnection();
			is = openConnection.getInputStream();

			String name = strurl.substring(strurl.lastIndexOf("/") + 1);
			Utilities.ensurePath(localPath);
			String filename = localPath + "/" + name;
			String tmpfilename = filename + ".d";
			File tmp = new File(tmpfilename);
			os = new FileOutputStream(tmp);
			byte[] buffer = new byte[1024];
			int len;
			int read = 0;
			while ((len = is.read(buffer)) != -1) {
				read += len;
				os.write(buffer, 0, len);
			}
			if (read > 1024) {// >1k
				File file = new File(filename);
				boolean renameTo = tmp.renameTo(file);
				bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
				// Logger.i(TAG, "doInBackground renameTo " + renameTo);
			} else {
				boolean delete = tmp.delete();
				// Logger.i(TAG, "doInBackground delete " + delete);
			}
			// bitmap =
			// BitmapFactory.decodeStream(openConnection.getInputStream());

		} catch (Exception e) {
			Logger.e(TAG, "doInBackground E: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.e(TAG, "doInBackground is close " + e);
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					Logger.e(TAG, "doInBackground os close " + e);
					e.printStackTrace();
				}
			}
		}
		return "DONE.";
	}

	@Override
	protected void onPostExecute(String result) {
		if (bitmap != null)
			onPostExecute(bitmap);
	}

	public abstract void onPostExecute(Bitmap bitmap);

}