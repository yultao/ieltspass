package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.model.Lyrics;
import tt.lab.android.ieltspass.model.Sentence;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ListeningFragmentLyrics extends Fragment {
	private static final String TAG = ListeningFragmentLyrics.class.getName();
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private ScrollView scrollView;
	private Handler handler = new Handler();
	private TextView lastTextView = null;
	private MediaPlayer player;
	private SeekBar seekBar;
	private String lyricsFileName;
	private Lyrics lyrics;
	private boolean userClicked;
	public void setPlayer(MediaPlayer player, SeekBar seekBar, String lyricsFileName) {
		this.player = player;
		this.seekBar = seekBar;
		this.lyricsFileName = lyricsFileName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_listening_lyrics, container, false);

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView1);
		scrollView.setSmoothScrollingEnabled(true);
		
		LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
		try {


			lyrics = Utilities.parseLyrics(lyricsFileName);
			
			for (int index = 0; index < lyrics.getSentenceCount(); index++) {
				Sentence sentence = lyrics.getSentence(index);
				LyricsTextView textView = createTextView(sentence.getText(), sentence);
				
				textView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						LyricsTextView textView = (LyricsTextView)v;
						Sentence sentence = textView.getSentence();
						if(seekBar.isEnabled()){//player is OK
							seekBar.setProgress(sentence.getStart());
							player.seekTo(sentence.getStart());
							userClicked = true;
						}
					}
				});
				sentence.setTextView(textView);
				linearLayout.addView(textView);
			}
			//Logger.i(TAG, "lyrics: \n"+ lyrics);
			handler.post(updateLyricsThread);
		} catch (Exception e) {
			Logger.e(TAG, "addView e: " + e.getMessage());
		}
		return rootView;
	}

	private Runnable updateLyricsThread = new Runnable() {
		public void run() {
			try {
				algorithm2();
				handler.postDelayed(updateLyricsThread, 10);
			} catch (Exception e) {
				Logger.e(TAG, "updateLyricsThread e: " + e.getMessage());
			}

		}

		private void algorithm2() {
			int cp = seekBar.getProgress();
			//Sentence sentence = Utilities.findSentenceHash(lyrics,cp);
			//Utilities.c=0;
			//Sentence sentence = Utilities.findSentenceSimple(lyrics.getSentences(),cp);
			Sentence sentence = Utilities.findSentenceBinary(lyrics.getSentences(),cp);
			//Logger.i(TAG,"c: "+Utilities.c);
			if (sentence != null) {
				TextView textView = (TextView) sentence.getTextView();
				// Logger.i(TAG, "equals: " + (textView.equals(lastTextView)));
				if (lastTextView == null || !lastTextView.equals(textView)) {//第一条，或有新的一条
					if (lastTextView != null) {
						lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
					}
					textView.setTextColor(getResources().getColor(R.color.red));
					lastTextView = textView;
					
					if(!userClicked){//如果这条是用户自己点的，就不滚动。
						TextView toScroll = (TextView) lyrics.getScrollTopSentence(sentence).getTextView();
						scrollView.requestChildFocus(toScroll, toScroll);
	
						toScroll = (TextView) lyrics.getScrollBottomSentence(sentence).getTextView();
						scrollView.requestChildFocus(toScroll, toScroll);
					} else{
						userClicked = false;
					}
				}
			}
		}
		
		

	};

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
	private class LyricsTextView extends TextView{
		private Sentence sentence;
		public LyricsTextView(Context context, Sentence sentence) {
			super(context);
			this.setSentence(sentence);
		}
		public Sentence getSentence() {
			return sentence;
		}
		public void setSentence(Sentence sentence) {
			this.sentence = sentence;
		}
	}
	private LyricsTextView createTextView(String text, Sentence sentence) {
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		LyricsTextView textView = new LyricsTextView(this.getActivity(), sentence);
		textView.setLayoutParams(lp);
		textView.setTextSize(18);
		textView.setText(text);
		textView.setPadding(10, 4, 10, 4);
		textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lyrics_background));

		return textView;
	}
}