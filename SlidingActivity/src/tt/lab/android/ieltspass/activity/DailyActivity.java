package tt.lab.android.ieltspass.activity;

import java.util.Map;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Word;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class DailyActivity extends Activity {
	private Handler handler = new Handler();
	private WebView webView;
	private Uri photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily);
		initTitle();
		initWebView();

	}

	@SuppressLint("JavascriptInterface")
	private void initWebView() {
		webView = (WebView) this.findViewById(R.id.webview);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webView.loadUrl("javascript:callFromAndroid_onPageFinished(" + 1 + ")");
			}

		});

		// boolean netWorkStatus = netWorkStatus();

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		webView.setHapticFeedbackEnabled(false);
		webView.addJavascriptInterface(new Object() {
			public void callFromJS_getAvailMemory() {
				handler.post(new Runnable() {
					public void run() {
						// webView.loadUrl("javascript:callFromAndroid_getAvailMemory(" + getAvailMemoryInfo() +
						// ")");
					}
				});
			}

			public void callFromJS_showMessage(final String title, final String message) {
				handler.post(new Runnable() {
					public void run() {

					}
				});
			}

			public void callFromJS_exit() {
				handler.post(new Runnable() {
					public void run() {
						System.exit(0);
					}
				});
			}

			public void callFromJS_showCamera() {
				handler.post(new Runnable() {
					public void run() {

					}
				});
			}

		}, "android");

		webView.loadUrl("http://m.dict.cn/daily.php");
		// webView.loadUrl("file:///android_asset/index.html");
	}

	private void initTitle() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		final String string = bundle.getString("title");
		TextView title = (TextView) findViewById(R.id.textView1);
		title.setText(string);
		
		
		Button back = (Button) findViewById(R.id.button2);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				navigateUp();
			}
		});
		Button share = (Button) findViewById(R.id.button3);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_SEND);  
				intent.setType("image/*");//intent.setType("text/plain");  
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");  
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享："+ string +" 详见：http://www.ieltspass.com");  
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				startActivity(Intent.createChooser(intent, "分享"));  
			}
		});
		
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, SlidingActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.daily, menu);
		return true;
	}

}
