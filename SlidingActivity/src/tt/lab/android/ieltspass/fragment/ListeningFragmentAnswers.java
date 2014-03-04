package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Logger;
import tt.lab.android.ieltspass.data.Utilities;
import tt.lab.android.ieltspass.model.lyrics.Lyrics;
import tt.lab.android.ieltspass.model.lyrics.Sentence;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ListeningFragmentAnswers extends Fragment {
	private static final String TAG = ListeningFragmentAnswers.class.getName();
	private String answers;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_listening_answers, container, false);
		webView = (WebView) rootView.findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/cambridge/listening/"+answers);
		return rootView;
	}


	public String getAnswers() {
		return answers;
	}


	public void setAnswers(String answers) {
		this.answers = answers;
	}



}