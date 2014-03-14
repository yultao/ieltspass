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
	private List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

	private View view;
	private Context context;
	private ListView listView;
	int familarity[] = new int[]{R.drawable.category_0,R.drawable.category_1,R.drawable.category_2,R.drawable.category_3,R.drawable.category_4,R.drawable.category_5};

	private SimpleAdapter simpleAdapter;

	private SearchView searchView;
	private Spinner sortSpinner;
	private Spinner filterSpinner;
	private boolean filterSpinnerInited;

	private boolean toend = false;
	private boolean loading;
	private int totalCount;
	private ProgressBar footerView;

	private String orderby = "random()", order = "";
	private int currentPage = 0, pageSize = 30, maxPage;
	private WordsDao wordsDao;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Logger.i(TAG, "onCreateView");
		context = this.getActivity();
		view = inflater.inflate(R.layout.center_fragment_vocabulary, null);
		wordsDao = new WordsDao(context);

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

		Logger.i(TAG, "initListFootView " + footerView);
	}

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
					if (toend && !loading && currentPage < maxPage - 1) {
						currentPage++;
						// loadingText.setText("Loading "+((currentPage)*pageSize+1)+"-"+((currentPage+1)*pageSize)+"...");

						// Toast.makeText(context, "第 " + currentPage +" 页  "+totalItemCount,Toast.LENGTH_LONG).show();
						Logger.i(TAG, "Loading " + ((currentPage) * pageSize + 1) + "-"
								+ ((currentPage + 1) * pageSize) + "...");
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
		final String[] sortCriteria = { "随机", "A-Z", "Z-A", /* "远-近", "近-远", */"生-熟", "熟-生" };
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
				setSortCriteria();
				query();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	private void initFilterSpinner() {
		final String[] filters = { "0.全部", "1.很生", "2.较生", "3.一般", "4.较熟", "5.很熟" };
		filterSpinner = (Spinner) view.findViewById(R.id.spinner2);
		ArrayAdapter<String> filtersArrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, filters);
		filtersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(filtersArrayAdapter);
		filterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 第一次加载无需重新排序、过滤
				if (filterSpinnerInited) {
//					resetData();
//					sort();
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
		simpleAdapter = new MySimpleAdapter(context, listData, R.layout.center_fragment_vocabulary_vlist,
				new String[] { "title", "phon", "info", "img" , "fami" },
				new int[] { R.id.title, R.id.phon, R.id.info, R.id.img, R.id.fami });
		listView.setAdapter(simpleAdapter);

		searchView.setQueryHint(listData.size() + "/" + totalCount);

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
		Logger.i(TAG, "\n==================\nquery I");
		initData();
		resetData();
		updateFootView();
		Logger.i(TAG, "query O\n==================\n");
	}

	private void setSortCriteria() {
		long t1 = System.currentTimeMillis();
		try {

			switch (sortSpinner.getSelectedItemPosition()) {
			case 0:// "随机"
				orderby = "random()";
				order = "";
				break;
			case 1:// a-z
				orderby = "w.word_vocabulary";
				order = "asc";
				break;
			case 2:// z-a
				orderby = "w.word_vocabulary";
				order = "desc";

				break;
			case 3:// "生-熟"
				orderby = "f.familiarity_class";
				order = "asc";
				break;
			case 4:// 熟-生"
				orderby = "f.familiarity_class";
				order = "desc";
				break;
			}

			
			//simpleAdapter.notifyDataSetChanged();
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
		listData.clear();

		//Logger.i(TAG, "initData: " + listView.getFooterViewsCount());
		if (listView.getFooterViewsCount() == 0) {
			listView.addFooterView(footerView);
		}
		currentPage = 0;
		totalCount = wordsDao.getWordListCount(searchView.getQuery().toString(),
				filterSpinner.getSelectedItemPosition());
		maxPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

		listWords(listData);
		Logger.i(TAG, "listData.size: " + listData.size());
	}

	private void listWords(List<Map<String, Object>> listData) {
		List<Word> wordList = wordsDao.getWordList(searchView.getQuery().toString(),
				filterSpinner.getSelectedItemPosition(), orderby, order, pageSize, currentPage * pageSize);
		for (Word word : wordList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", word.getWord_vocabulary());
			map.put("phon", word.getBE_phonetic_symbol());
			map.put("info", word.getExplanation());
			map.put("fami", String.valueOf(familarity[word.getFamiliarity()]));

			if (word.getTinyPic() == null || word.getTinyPic().trim().equals("")) {
				map.put("img", String.valueOf(R.drawable.no_pic));
			} else {

				map.put("img", Utilities.getTinyPic(word.getTinyPic()));
			}
			listData.add(map);
		}
	}

	private List<Map<String, Object>> updateData() {
		Logger.i(TAG, "updateData I");
		ArrayList<Map<String, Object>> cd = new ArrayList<Map<String, Object>>();
		listWords(cd);
		Logger.i(TAG, "updateData O " + listData.size());
		return cd;
	}

	private void updateUI() {
		simpleAdapter.notifyDataSetChanged();
		searchView.setQueryHint(listData.size() + "/" + totalCount);
		updateFootView();
	}

	private void updateFootView() {
		boolean b = listData.size() == totalCount;
		Logger.i(TAG, "b: " + b + ", updateFootView: " + listView.getFooterViewsCount());
		if (b)
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
				listData.addAll(updateData);
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
