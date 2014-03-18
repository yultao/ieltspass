package tt.lab.android.ieltspass.activity;

import tt.lab.android.ieltspass.HttpUtilities;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.MailSender;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.R.layout;
import tt.lab.android.ieltspass.R.menu;
import tt.lab.android.ieltspass.data.Settings;
import tt.lab.android.ieltspass.Utilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
	private static final String TAG = FeedbackActivity.class.getName();
	private Handler handler = new Handler();
	private TextView textView1, textView2;
	private Settings settings;
	private ProgressDialog progressDialog; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_feedback);
		settings = Settings.getInstance(this);
		initTitle();

		textView1 = (TextView) findViewById(R.id.editText1);
		textView2 = (TextView) findViewById(R.id.editText2);

		Button send = (Button) this.findViewById(R.id.button1);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean canSend = false;
				if(textView1.getText().toString().trim().length()==0){
					Toast.makeText(FeedbackActivity.this, "请输入意见和建议", Toast.LENGTH_SHORT).show();
					textView1.requestFocus();
					canSend = false;
				} else if (textView2.getText().toString().trim().length()==0){
					Toast.makeText(FeedbackActivity.this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
					textView2.requestFocus();
					canSend = false;
				} else {
					canSend = true;
				}
				
				if(canSend){
					if (Utilities.isNetworkConnected()) {
						if (settings.isNetwordForbidden()) {
							Toast.makeText(FeedbackActivity.this, "有网络，但已设为禁止", Toast.LENGTH_SHORT).show();
							canSend = false;
						} else {
							if (Utilities.isWifiConnected()) {
								canSend = true;
							} else if (Utilities.isMobileConnected()) {
								if (settings.isWifiAndMobile()) {
									canSend = true;
								} else {
									Toast.makeText(FeedbackActivity.this, "仅有手机网络，但已设为禁止", Toast.LENGTH_SHORT).show();
									canSend = false;
								}
							}
						}
					} else {
						Toast.makeText(FeedbackActivity.this, "无网络", Toast.LENGTH_SHORT).show();
						canSend = false;
					}
				}
				if (canSend) {
					new Thread(post).start();
					progressDialog = ProgressDialog.show(FeedbackActivity.this, "意见反馈", "正在发送，请稍等...", true, false);
				}
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String s = msg.what == 0x0 ? "发送成功" : "发送失败";
				
				handler.removeCallbacks(post);
				if (msg.what == 0x0) {
					progressDialog.dismiss();
				} else {
					progressDialog.dismiss();
				}
				Toast.makeText(FeedbackActivity.this, s, Toast.LENGTH_SHORT).show();

			}
		};
	}

	private Runnable post = new Runnable() {
		@Override
		public void run() {
			// HttpUtilities.doGet("http://www.baidu.com");
			try {

				MailSender ms = new MailSender();
				boolean sent = ms.prepareAndSend(textView1.getText().toString() + "\n\n联系方式："
						+ textView2.getText().toString());
				Message msg = new Message();
				msg.what = sent ? 0x0 : 0x1;
				handler.sendMessage(msg);
			} catch (Throwable e) {
				Logger.e(TAG, "post E: " + e);
				e.printStackTrace();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feedback, menu);
		return true;
	}

	private void initTitle() {
		Button back = (Button) findViewById(R.id.menuButton);
		// back.setBackground(Resources.getSystem().getDrawable(R.drawable.back));
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.backbutton));
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				navigateUp();
			}
		});
		Button share = (Button) findViewById(R.id.shareButton);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");// intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");
				intent.putExtra(Intent.EXTRA_TEXT, "雅思通(IELTS PASS)分享：");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));
			}
		});
		TextView titleText = (TextView) findViewById(R.id.titleText);
		titleText.setText("意见反馈".toUpperCase());
	}

	private void navigateUp() {
		NavUtils.navigateUpTo(this, new Intent(this, LauncherActivity.class));
	}
}
