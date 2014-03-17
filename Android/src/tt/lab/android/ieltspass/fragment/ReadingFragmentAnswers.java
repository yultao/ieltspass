package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class ReadingFragmentAnswers extends Fragment {
	private static final String TAG = ReadingFragmentAnswers.class.getName();
	private String answers;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reading_answers, container, false);
		webView = (WebView) rootView.findViewById(R.id.webview);
		webView.loadUrl("file://"+Constants.LISTENING_ANSWER_PATH+"/"+answers);
		return rootView;
	}


	public String getAnswers() {
		return answers;
	}


	public void setAnswers(String answers) {
		this.answers = answers;
	}



}