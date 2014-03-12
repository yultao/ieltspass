package tt.lab.android.ieltspass.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.Constants;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.activity.VocabularyActivity;
import tt.lab.android.ieltspass.data.WordsDao;
import tt.lab.android.ieltspass.model.Word;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class CenterFragmentVocabulary extends Fragment {
	private static final String TAG = CenterFragmentVocabulary.class.getName();
	private List<Map<String, Object>> currentData;
	private List<Map<String, Object>> listData0 = new ArrayList<Map<String, Object>>(),
			listData1 = new ArrayList<Map<String, Object>>(), listData2 = new ArrayList<Map<String, Object>>(),
			listData3 = new ArrayList<Map<String, Object>>(), listData4 = new ArrayList<Map<String, Object>>(),
			listData5 = new ArrayList<Map<String, Object>>();

	private View view;
	private Context context;
	private ListView listView;
	private SimpleAdapter simpleAdapter;

	private Spinner sortSpinner;
	private SearchView searchView;

	private boolean filterSpinnerInited;
	
	private String orderby="random()", order = "";
	private int currentPage = 0, pageSize = 30, maxPage;
	private WordsDao wordsDao;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Logger.i(TAG, "onCreateView");
		context = this.getActivity();
		view = inflater.inflate(R.layout.center_fragment_vocabulary, null);
		wordsDao = new WordsDao(context);
		/*
		 * simpleAdapter.registerDataSetObserver(new DataSetObserver() { public void onChanged() {
		 * Log.i("registerDataSetObserver", "onChanged");
		 * 
		 * }
		 * 
		 * public void onInvalidated() { Log.i("registerDataSetObserver", "onInvalidated"); } });
		 */
		// adapter.notifyDataSetChanged();

		// List View
		initListView();
		initListFootView();
		// Search View
		initSearchView();

		// Sort Spinner
		initSortSpinner();

		// Filter Spinner
		initFilterSpinner();

		initData();
		resetData();
		return view;
	}

	private void initListFootView() {
		footerView = new ProgressBar(context);

		Logger.i(TAG, "initListFootView "+footerView);
	}

	private boolean toend = false;
	private boolean loading;
	private int totalCount;
	private ProgressBar footerView;
	private void initListView() {
		listView = (ListView) view.findViewById(R.id.listView1);
		
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount != 0) {
					toend = (firstVisibleItem + visibleItemCount == totalItemCount);
					if (toend && !loading && currentPage < maxPage-1) {
						currentPage++;
						//loadingText.setText("Loading "+((currentPage)*pageSize+1)+"-"+((currentPage+1)*pageSize)+"...");


						//Toast.makeText(context, "第 " + currentPage +" 页  "+totalItemCount,Toast.LENGTH_LONG).show();
						Logger.i(TAG, "Loading "+((currentPage)*pageSize+1)+"-"+((currentPage+1)*pageSize)+"...");
						loading = true;
						new UpdateDataAsyncTask().execute();
					}
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("onItemClick", "position: " + position + ", id: " + id);
				Map<String, Object> item = (Map<String, Object>) simpleAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), VocabularyActivity.class);
				intent.putExtra("title", item.get("title").toString());
				startActivity(intent);

			}
		});
	}

	private void initSearchView() {
		searchView = (SearchView) view.findViewById(R.id.searchView1);

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				query();
				return false;
			}

		});
		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				resetData();
				return false;
			}

		});
		searchView.setIconifiedByDefault(false);
		searchView.setFocusable(false);
	}

	private void initSortSpinner() {
		final String[] sortCriteria = { "随机", "A-Z", "Z-A", /*"远-近", "近-远", */"生-熟", "熟-生" };
		sortSpinner = (Spinner) view.findViewById(R.id.spinner);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
				sortCriteria);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(arrayAdapter);

		sortSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.i("onItemSelected", "Sort by：" + sortCriteria[arg2]);
				arg0.setVisibility(View.VISIBLE);
				sort();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	private void initFilterSpinner() {
		final String[] filters = { "0.全部", "1.很生", "2.较生", "3.一般", "4.较熟", "5.很熟" };
		Spinner filterSpinner = (Spinner) view.findViewById(R.id.spinner2);
		ArrayAdapter<String> filtersArrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, filters);
		filtersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(filtersArrayAdapter);
		filterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				arg0.setVisibility(View.VISIBLE);

				switch (arg2) {
				case 0://
					currentData = listData0;
					break;
				case 1:
					currentData = listData1;
					break;
				case 2:
					currentData = listData2;
					break;
				case 3:
					currentData = listData3;
					break;
				case 4:
					currentData = listData4;
					break;
				case 5:
					currentData = listData5;
					break;

				}

				// 第一次加载无需重新排序、过滤
				if (filterSpinnerInited) {
					resetData();
					sort();
					query();
				} else {
					filterSpinnerInited = true;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	/**
	 * Reset the adapter, otherwise, after filtering, the data of the adapter will point to another heap memory.
	 * 
	 * @param data
	 */
	private void resetData() {
		simpleAdapter = new MySimpleAdapter(context, currentData, R.layout.center_fragment_vocabulary_vlist, new String[] {
				"title", "phon", "info", "img" }, new int[] { R.id.title, R.id.phon, R.id.info, R.id.img });
		listView.setAdapter(simpleAdapter);

		searchView.setQueryHint(currentData.size() + "/"+totalCount);

	}

	private void filter() {
		if (searchView.getQuery().length() == 0) {
			resetData();
		} else {
			simpleAdapter.getFilter().filter(searchView.getQuery());
			simpleAdapter.notifyDataSetChanged();
		}
	}
	private void query() {
		initData();
		resetData();
		updateFootView();
	}
	private void sort() {
		long t1 = System.currentTimeMillis();
		try {

			switch (sortSpinner.getSelectedItemPosition()) {
			case 0:// "随机"
				orderby = "random()";
				order ="";
				query();
//				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
//					@Override
//					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
//						int i = (int) (Math.random() * 10) - 5;
//						return i;
//					}
//				});
				break;
			case 1:// a-z
				orderby = "b.word_vocabulary";
				order ="asc";
				query();
//				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
//					@Override
//					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
//						String title1 = (String) lhs.get("title");
//						String title2 = (String) rhs.get("title");
//						return title1.compareTo(title2);
//					}
//				});
				break;
			case 2:// z-a
				orderby = "b.word_vocabulary";
				order ="desc";
				query();
//				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
//
//					@Override
//					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
//						String title1 = (String) lhs.get("title");
//						String title2 = (String) rhs.get("title");
//						return title2.compareTo(title1);
//					}
//
//				});

				break;
			/*
			case 3:// 近-远
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("date");
						String title2 = (String) rhs.get("date");
						return title1.compareTo(title2);
					}
				});
				break;
			case 4:// 远-近
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("date");
						String title2 = (String) rhs.get("date");
						return title2.compareTo(title1);
					}
				});
				break;
			*/
			case 3:// "生-熟"
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("category");
						String title2 = (String) rhs.get("category");
						return title1.compareTo(title2);
					}
				});
				break;
			case 4:// 熟-生"
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("category");
						String title2 = (String) rhs.get("category");
						return title2.compareTo(title1);
					}
				});
				break;

			}
			simpleAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("sort", "e: " + e.getMessage());
		}
		long t2 = System.currentTimeMillis();
		Logger.i(TAG, "sort: " + (t2 - t1) + " ms.");
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initData() {
		listData0.clear();
		listData1.clear();
		listData2.clear();
		listData3.clear();
		listData4.clear();
		listData5.clear();
		
		Logger.i(TAG, "initData: "+listView.getFooterViewsCount());
		if(listView.getFooterViewsCount()==0){
			listView.addFooterView(footerView);
		}
		currentPage = 0;
		totalCount = wordsDao.getWordListCount(searchView.getQuery().toString());
		maxPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
		
		List<Word> wordList = wordsDao.getWordList(searchView.getQuery().toString(), orderby, order, pageSize, currentPage * pageSize);
		
		for (Word word : wordList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", word.getWord_vocabulary());
			map.put("phon", word.getBE_phonetic_symbol());
			map.put("info", word.getExplanation());
			map.put("category", word.getCategory());

			if ("1.很生".equals(word.getCategory())) {
				map.put("img", String.valueOf(R.drawable.category_1));
				listData1.add(map);
			} else if ("2.较生".equals(word.getCategory())) {
				map.put("img", String.valueOf(R.drawable.category_2));
				listData2.add(map);
			} else if ("3.一般".equals(word.getCategory())) {
				map.put("img", String.valueOf(R.drawable.category_3));
				listData3.add(map);
			} else if ("4.较熟".equals(word.getCategory())) {
				map.put("img", String.valueOf(R.drawable.category_4));
				listData4.add(map);
			} else if ("5.很熟".equals(word.getCategory())) {
				map.put("img", String.valueOf(R.drawable.category_5));
				listData5.add(map);
			} else {
				if (word.getTinyPic() == null || word.getTinyPic().trim().equals("")) {
					map.put("img", String.valueOf(R.drawable.no_pic));
				} else {

					map.put("img", Utilities.getTinyPic(word.getTinyPic()));
				}
			}
			listData0.add(map);
		}
		currentData = listData0;
		Logger.i(TAG, "currentData.size: "+currentData.size());
	}

	private List<Map<String, Object>> updateData() {
		Logger.i(TAG, "updateData I");
		ArrayList<Map<String, Object>> cd = new ArrayList<Map<String, Object>>();
		WordsDao wordsDao = new WordsDao(context);
		List<Word> wordList = wordsDao.getWordList(searchView.getQuery().toString(), orderby, order, pageSize, currentPage * pageSize);
		Logger.i(TAG, "updateData wordList " + wordList.size());
		for (Word word : wordList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", word.getWord_vocabulary());
			map.put("phon", word.getBE_phonetic_symbol());
			map.put("info", word.getExplanation());
			map.put("category", word.getCategory());

			if (word.getTinyPic() == null || word.getTinyPic().trim().equals("")) {
				map.put("img", String.valueOf(R.drawable.no_pic));
			} else {

				map.put("img", Utilities.getTinyPic(word.getTinyPic()));
			}
			cd.add(map);
		}
		Logger.i(TAG, "updateData O " + currentData.size());

		return cd;
	}
	private void updateUI() {
		simpleAdapter.notifyDataSetChanged();
		searchView.setQueryHint(currentData.size() + "/"+totalCount);
		updateFootView();
	}

	private void updateFootView() {
		boolean b = currentData.size()==totalCount;
		Logger.i(TAG, "b: "+b+", updateFootView: "+listView.getFooterViewsCount());
		if(b)
			listView.removeFooterView(footerView);
	}
	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		public void setViewImage(ImageView v, String value) {
			// Logger.i(TAG, "setViewImage: "+v.getId()+", "+value);
			// 从网络下载
			if (value != null && value.toLowerCase().startsWith("http")) {
				if (Utilities.isWifiConnected() || (Utilities.isMobileConnected() && !Constants.Preference.onlyUseWifi)) {
					new DownloadImageAsyncTask(v, value).execute();
				} else {
					super.setViewImage(v, String.valueOf(R.drawable.no_net));
				}
			} else if (value.startsWith("/")) {
				// super.setViewImage(v, value);
				// new DisplayImageAsyncTask(v, value).execute();
				Bitmap bitmap = BitmapFactory.decodeFile(value);
				v.setImageBitmap(bitmap);
			} else {
				super.setViewImage(v, value);// 会变小
			}
		}

	}

	private class UpdateDataAsyncTask extends AsyncTask<Integer, Integer, String> {
		private List<Map<String, Object>> updateData;

		@Override
		protected String doInBackground(Integer... arg0) {
			updateData = updateData();
			return "DONE.";
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				currentData.addAll(updateData);

				loading = false;
				updateUI();

			} catch (Exception e) {
				Logger.i(TAG, "onPostExecute E: " + e);
			}

		}

		

		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	private class DownloadImageAsyncTask extends AsyncTask<Integer, Integer, String> {
		private String strurl;
		private ImageView imageView;
		private Bitmap bitmap;

		public DownloadImageAsyncTask(ImageView imageView, String url) {
			this.strurl = url;
			this.imageView = imageView;
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

				String filename = Constants.VOCABULARY_IMAGE_PATH + "/" + name;
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
				// bitmap = BitmapFactory.decodeStream(openConnection.getInputStream());

			} catch (Exception e) {
				Logger.i(TAG, "doInBackground E: " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						Logger.i(TAG, "doInBackground is close " + e);
						e.printStackTrace();
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						Logger.i(TAG, "doInBackground os close " + e);
						e.printStackTrace();
					}
				}
			}
			return "DONE.";
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(String result) {
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

}
