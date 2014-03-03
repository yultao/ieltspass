package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class ListeningFragmentQuestions extends Fragment {
	private static final String TAG = ListeningFragmentQuestions.class.getName();
	private String questions;
	private WebView webView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_listening_questions, container, false);
		webView = (WebView) rootView.findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.loadUrl("file:///android_asset/cambridge/"+questions);
		return rootView;
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	

}