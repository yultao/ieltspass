package tt.lab.android.ieltspass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//import android.util.Log;

public class HttpUtilities {
	private final static String TAG = HttpUtilities.class.getName();

	public static void main(String[] args) {
		HttpUtilities.doGet("http://www.baidu.com");
	}

	public static void doGet(String url) {
		try {
			// 得到HttpClient对象
			HttpClient getClient = new DefaultHttpClient();
			// 得到HttpGet对象
			HttpGet request = new HttpGet(url);
			// 客户端使用GET方式执行请教，获得服务器端的回应response
			HttpResponse response = getClient.execute(request);

			// 判断请求是否成功
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Logger.i(TAG, "请求服务器端成功");
				// 获得输入流
				BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer string = new StringBuffer("");
				String lineStr = null;
				while ((lineStr = in.readLine()) != null) {
					string.append(lineStr + "\n");
				}
				in.close();

				String resultStr = string.toString();
				Logger.i(TAG, resultStr);
			} else {
				Logger.i(TAG, "请求服务器端失败 " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Logger.i(TAG, "doGet E: " + e);
			e.printStackTrace();
		}
	}

	public static void doPost(String url, Map<String, String> body) {

		try {
			HttpClient client = new DefaultHttpClient();

			// REQUEST
			HttpPost request = new HttpPost(url);
			// 使用NameValuePair来保存要传递的Post参数
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			// 添加要传递的参数
			for (String key : body.keySet()) {
				postParameters.add(new BasicNameValuePair(key, body.get(key)));
			}
			// 实例化UrlEncodedFormEntity对象
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);

			// 使用HttpPost对象来设置UrlEncodedFormEntity的Entity
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			
			// RESPONSE
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer string = new StringBuffer("");
				String lineStr = null;
				while ((lineStr = in.readLine()) != null) {
					string.append(lineStr + "\n");
				}
				in.close();

				String resultStr = string.toString();
				System.out.println(resultStr);
			} else {
				Logger.i(TAG, "请求服务器端失败 " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			Logger.i(TAG, "doPost E: " + e);
		}
	}
}