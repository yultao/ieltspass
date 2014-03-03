package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Logger;
import tt.lab.android.ieltspass.data.Utilities;
import tt.lab.android.ieltspass.model.lyrics.Lyrics;
import tt.lab.android.ieltspass.model.lyrics.Sentence;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
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
	//private Map<String, Map<String, Object>> lyricsTextViewMap = new HashMap<String, Map<String, Object>>();
	//private List<Map<String, Object>> lyricsTextViewList = new ArrayList<Map<String, Object>>();
	private Handler handler = new Handler();
	private TextView lastTextView = null;
	private MediaPlayer player;
	private SeekBar seekBar;
	private String lyricsFileName;
	private Lyrics lyrics;

	public void setPlayer(MediaPlayer player, SeekBar seekBar, String lyricsFileName) {
		this.player = player;
		this.seekBar = seekBar;
		this.lyricsFileName = lyricsFileName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_listening_dummy, container, false);

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView1);
		LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
		try {

			// List<Map<String, String>> lyricsList = Database.parseLyrics(lyricsFileName);
			// for (int i = 0; i < lyricsList.size(); i++) {
			// Map<String, String> sentence = lyricsList.get(i);
			// String time = sentence.get("time");
			// String word = sentence.get("word");
			//
			// TextView textView = createTextView(time+": "+ word);
			// linearLayout1.addView(textView);
			//
			// Map<String, Object> m = new HashMap<String, Object>();
			// m.put("time", time);
			// m.put("text", textView);
			// lyricsTextViewList.add(m);
			//
			// Map<String, Object> mm = new HashMap<String, Object>();
			// mm.put("index", i);
			// mm.put("text", textView);
			// lyricsTextViewMap.put(time, mm);
			// }

			lyrics = Utilities.parseLyrics(lyricsFileName);
			for (int index = 0; index < lyrics.getSentenceCount(); index++) {
				Sentence sentence = lyrics.getSentence(index);
				TextView textView = createTextView(sentence.getRaw());

				
				sentence.setTextView(textView);//This is enough
				linearLayout.addView(textView);

//				Map<String, Object> m = new HashMap<String, Object>();
//				m.put("time", sentence.getTime());
//				m.put("text", textView);
//				lyricsTextViewList.add(m);
//
//				Map<String, Object> mm = new HashMap<String, Object>();
//				mm.put("index", index);
//				mm.put("text", textView);
//				lyricsTextViewMap.put(sentence.getTime(), mm);
			}
			Logger.i(TAG, "lyrics: \n"+ lyrics);
			handler.post(updateLyricsThread);
		} catch (Exception e) {
			Logger.i(TAG, "addView e: " + e.getMessage());
		}
		return rootView;
	}

	private Runnable updateLyricsThread = new Runnable() {
		public void run() {
			try {

				// Algorithm 1
				// int cp = player.getCurrentPosition();
				algorithm2();

				// Algorithm 2
				/*
				 * if (currentIndex < lyricsTextViewList.size()) { Map<String, Object> map =
				 * lyricsTextViewList.get(currentIndex); String time = (String)map.get("time");
				 * 
				 * int timeInt = formatTimeInt(time); int cp = player.getCurrentPosition(); if(cp>=timeInt){
				 * 
				 * TextView textView = (TextView)map.get("text"); if (lastTextView != null) {
				 * lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color)); } lastTextView =
				 * textView;
				 * 
				 * int real = currentIndex + 5; if (real >= lyricsTextViewList.size()) { real =
				 * lyricsTextViewList.size() - 1; } TextView toScroll =
				 * (TextView)lyricsTextViewList.get(real).get("text");
				 * textView.setTextColor(getResources().getColor(R.color.red));
				 * 
				 * 
				 * scrollView1.requestChildFocus(textView,textView); currentIndex++; } }
				 */
				handler.postDelayed(updateLyricsThread, 10);
			} catch (Exception e) {
				Logger.i(TAG, "updateLyricsThread e: " + e.getMessage());
			}

		}

//		private void algorithm1() {
//			int cp = seekBar.getProgress();
//			Map<String, Object> map = findSentence(cp);
//
//			if (map != null) {
//				TextView textView = (TextView) map.get("text");
//				// Logger.i(TAG, "equals: " + (textView.equals(lastTextView)));
//				if (lastTextView == null || !lastTextView.equals(textView)) {
//					Logger.i(TAG, "in: ");
//					if (lastTextView != null) {
//						lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
//					}
//					textView.setTextColor(getResources().getColor(R.color.red));
//					lastTextView = textView;
//
//					int index = (Integer) map.get("index");
//					Logger.i(TAG, "index: " + index);
//					int real = index - 5;
//					if (real <= 0) {
//						real = 0;
//					}
//					TextView toScroll = (TextView) lyricsTextViewList.get(real).get("text");
//					scrollView.requestChildFocus(toScroll, toScroll);
//
//					real = index + 5;
//					if (real >= lyricsTextViewList.size()) {
//						real = lyricsTextViewList.size() - 1;
//					}
//					toScroll = (TextView) lyricsTextViewList.get(real).get("text");
//					scrollView.requestChildFocus(toScroll, toScroll);
//				}
//			}
//		}

//		private Map<String, Object> findSentence(int cp) {
//			String formatedTime = Utilities.formatTime(cp);
//			Map<String, Object> map = lyricsTextViewMap.get(formatedTime);
//			return map;
//		}
		private void algorithm2() {
			int cp = seekBar.getProgress();
			Sentence sentence = findSentenceBinary(lyrics.getSentences(),cp);

			if (sentence != null) {
				TextView textView = (TextView) sentence.getTextView();
				// Logger.i(TAG, "equals: " + (textView.equals(lastTextView)));
				if (lastTextView == null || !lastTextView.equals(textView)) {
					if (lastTextView != null) {
						lastTextView.setTextColor(getResources().getColor(R.color.sub_text_color));
					}
					textView.setTextColor(getResources().getColor(R.color.red));
					lastTextView = textView;
					
					TextView toScroll = (TextView) lyrics.getScrollTopSentence(sentence).getTextView();
					scrollView.requestChildFocus(toScroll, toScroll);

					toScroll = (TextView) lyrics.getScrollBottomSentence(sentence).getTextView();
					scrollView.requestChildFocus(toScroll, toScroll);
				}
			}
		}
		
		/**
		 * Search a point.
		 * This is not accurate, as it can only find sentences once a second. 
		 * @param cp
		 * @return
		 */
		private Sentence findSentenceHash(int cp) {
			String formatedTime = Utilities.formatTime(cp);
			return lyrics.getSentence(formatedTime);
		}
		
		/**
		 * Search a range.
		 * @param cp
		 * @return
		 */
		private Sentence findSentenceBinary(List<Sentence> sentences, int cp) {
			Sentence s = null;
			int middle = sentences.size()/2;
			Sentence middleSentence = sentences.get(middle);
			if(cp<middleSentence.getStart()){
				s = findSentenceBinary(sentences.subList(0, middle),cp);
			} else if(cp==middleSentence.getStart()){
				s =sentences.get(middle);
			} else if(cp>middleSentence.getStart() && cp<middleSentence.getEnd()){
				s =sentences.get(middle);
			} else if(middle==0 && cp==middleSentence.getEnd()){
				s =sentences.get(middle);
			} else {
				s = findSentenceBinary(sentences.subList(middle, sentences.size()),cp);
			}
//			for (int i = 0; i < ; i++) {
//				Sentence sentence = lyrics.getSentence(i);
//				if (sentence.getStart() <= cp && cp < sentence.getEnd()){
//					s= sentence;
//					break;
//				}
//			}
			return s;
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private TextView createTextView(String text) {
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView textView = new TextView(this.getActivity());
		textView.setLayoutParams(lp);
		textView.setTextSize(18);
		textView.setText(text);

		return textView;
	}
}