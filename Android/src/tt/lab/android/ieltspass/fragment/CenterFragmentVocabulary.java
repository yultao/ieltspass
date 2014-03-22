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
import tt.lab.android.ieltspass.DownloadImageAsyncTask;
import tt.lab.android.ieltspass.Logger;
import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.Utilities;
import tt.lab.android.ieltspass.activity.VocabularyActivity;
import tt.lab.android.ieltspass.data.Settings;
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
	int familarity[] = new int[] { R.drawable.category_0, R.drawable.category_1, R.drawable.category_2,
			R.drawable.category_3, R.drawable.category_4, R.drawable.category_5 };

	private SimpleAdapter simpleAdapter;

	private SearchView searchView;
	private Spinner sortSpinner;
	private boolean sortSpinnerInited;
	private Spinner filterSpinner;
	private boolean filterSpinnerInited;

	private boolean toend = false;
	private boolean loading;
	private ProgressBar footerView;

	private String queryStr = "";
	private int familarityClass;
	private String orderby = "random()", order = "";
	private int totalCount, currentPage = 0, pageSize = 30, maxPage;
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

		query();
		return view;
	}

	private void initListFootView() {
		footerView = new ProgressBar(context);
	}

	private void initListView() {
		listView = (ListView) view.findViewById(R.id.listView1);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount > 1) {//skip footview.
					toend = (firstVisibleItem + visibleItemCount == totalItemCount);

					if (toend && !loading && currentPage < maxPage - 1) {
						currentPage++;
						// loadingText.setText("Loading "+((currentPage)*pageSize+1)+"-"+((currentPage+1)*pageSize)+"...");

						// Toast.makeText(context, "第 " + currentPage +" 页  "+totalItemCount,Toast.LENGTH_LONG).show();
						Logger.i(TAG, "Loading " + ((currentPage) * pageSize + 1) + "-"
								+ ((currentPage + 1) * pageSize) + ", toend: " + firstVisibleItem + ", "
								+ visibleItemCount + ", " + totalItemCount + "...");
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
				setQueryString();
				query();
				return false;
			}

		});
		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				//postInitData();
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

				setSortCriteria();
				if (sortSpinnerInited) {
					query();
				} else {
					sortSpinnerInited = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	private void initFilterSpinner() {
		final String[] filters = { "   全部", "1.很生", "2.较生", "3.一般", "4.较熟", "5.很熟" };
		filterSpinner = (Spinner) view.findViewById(R.id.spinner2);
		ArrayAdapter<String> filtersArrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, filters);
		filtersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(filtersArrayAdapter);
		filterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setFamilarityClass();
				if (filterSpinnerInited) {
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

	private void queryAsync() {
		addFootView();
		new InitDataAsyncTask().execute();
	}
	private void query() {
		addFootView();
		new InitDataAsyncTask().execute();
	}
	private void querySync() {
		addFootView();
		initData();
		postInitData();
	}

	
	private void initData() {
		Logger.i(TAG, "initData I");
		try {

			listData = new ArrayList<Map<String, Object>>();
			currentPage = 0;
			totalCount = wordsDao.getWordListCount(queryStr, familarityClass);
			maxPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
			
			listWords(listData);
		} catch (Exception e) {
			Logger.e(TAG, "initData E:" + e);
		}
		Logger.i(TAG, "initData O");
	}


	private List<Map<String, Object>> updateData() {
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		listWords(listData);
		return listData;
	}

	private void postInitData() {
		simpleAdapter = new MySimpleAdapter(context, listData, R.layout.center_fragment_vocabulary_vlist, new String[] {
				"title", "phon", "info", "img", "fami" }, new int[] { R.id.title, R.id.phon, R.id.info, R.id.img,
				R.id.fami });
		listView.setAdapter(simpleAdapter);
		postRefreshData();
	}

	private void postUpdateData() {
		simpleAdapter.notifyDataSetChanged();
		postRefreshData();
	}

	private void postRefreshData() {
		searchView.setQueryHint(listData.size() + "/" + totalCount);
		removeFootView();
	}

	private void setQueryString() {
		queryStr = searchView.getQuery().toString();
	}

	private void setFamilarityClass() {
		this.familarityClass = filterSpinner.getSelectedItemPosition();
	}

	private void setSortCriteria() {
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

			// simpleAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("sort", "e: " + e.getMessage());
		}
	}

	private void addFootView() {
		if (listView.getFooterViewsCount() == 0) {
			listView.addFooterView(footerView);
		}
	}

	private void removeFootView() {
		if (listData.size() == totalCount) {
			listView.removeFooterView(footerView);
		}
	}

	private void listWords(List<Map<String, Object>> listData) {
		Logger.i(TAG, "listWords I: "+listData.size());
		try {

			List<Word> wordList = wordsDao.getWordList(queryStr, familarityClass, orderby, order, pageSize, currentPage
					* pageSize);

			for (Word word : wordList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", word.getWord_vocabulary());
				map.put("phon", word.getBE_phonetic_symbol());
				map.put("info", word.getExplanation());
				map.put("fami", String.valueOf(familarity[word.getFamiliarity()]));

				if (word.getTinyPic() == null || word.getTinyPic().trim().equals("")) {
					map.put("img", String.valueOf(R.drawable.no_pic));
				} else {
					map.put("img", Utilities.getTinyPic(Settings.getVocabularyImagesLogoPath(), word.getTinyPic()));
				}

				listData.add(map);
			}
		} catch (Exception e) {
			Logger.e(TAG, "listWords E: "+e);
		}
		Logger.i(TAG, "listWords O: "+listData.size());
	}

	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		public void setViewImage(final ImageView v, String value) {
			try {

				// Logger.i(TAG, "setViewImage: "+v.getId()+", "+value);
				// 从网络下载
				if (value != null && value.toLowerCase().startsWith("http")) {
					if (!Settings.isNetwordForbidden() && (Utilities.isWifiConnected()
							|| (Utilities.isMobileConnected() && Settings.isWifiAndMobile()))) {
						DownloadImageAsyncTask downloadImageTask = new DownloadImageAsyncTask(value,Settings.getVocabularyImagesLogoPath()) {

							@Override
							public void onPostExecute(Bitmap bitmap) {
								v.setImageBitmap(bitmap);								
							}							
						};
						downloadImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						super.setViewImage(v, String.valueOf(R.drawable.no_net));
					}
				} else if (value.startsWith("/")) {
					Bitmap bitmap = BitmapFactory.decodeFile(value);
					v.setImageBitmap(bitmap);
				} else {
					super.setViewImage(v, value);// 会变小
				}
			} catch (Exception e) {
				Logger.e(TAG, "setViewImage E: " + e + ", ImageView: " + v + ", value: " + value);
			}
		}

	}

	private class InitDataAsyncTask1 extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			Logger.i(TAG, "InitDataAsyncTask doInBackground I");
			initData();
			Logger.i(TAG, "InitDataAsyncTask doInBackground O");
			return "DONE.";
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.i(TAG, "InitDataAsyncTask onPostExecute I");
			try {
				postInitData();
			} catch (Exception e) {
				Logger.e(TAG, "InitDataAsyncTask onPostExecute E: " + e);
			}
			Logger.i(TAG, "InitDataAsyncTask onPostExecute O");
		}

	}
	private class InitDataAsyncTask extends AsyncTask<Integer, Integer, String> {
		private List<Map<String, Object>> updateData;
		
		@Override
		protected String doInBackground(Integer... arg0) {
			//Logger.i(TAG, "InitDataAsyncTask doInBackground I");
			listData = new ArrayList<Map<String, Object>>();//重来
			currentPage = 0;
			totalCount = wordsDao.getWordListCount(queryStr, familarityClass);
			maxPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
			
			updateData = updateData();//先将查询到的数据缓存下来，等到执行完再对listData更新，否则偶尔会闪退
			//Logger.i(TAG, "InitDataAsyncTask doInBackground O");
			return "DONE.";
		}

		@Override
		protected void onPostExecute(String result) {
			//Logger.i(TAG, "InitDataAsyncTask onPostExecute I");
			try {
				listData.addAll(updateData);
				postInitData();
			} catch (Exception e) {
				Logger.e(TAG, "InitDataAsyncTask onPostExecute E: " + e);
			}
			//Logger.i(TAG, "InitDataAsyncTask onPostExecute O");
		}

	}
	private class UpdateDataAsyncTask extends AsyncTask<Integer, Integer, String> {
		private List<Map<String, Object>> updateData;

		@Override
		protected String doInBackground(Integer... arg0) {
			//Logger.i(TAG, "UpdateDataAsyncTask doInBackground I");
			updateData = updateData();//先将查询到的数据缓存下来，等到执行完再对listData更新，否则偶尔会闪退
			//Logger.i(TAG, "UpdateDataAsyncTask doInBackground O");
			return "DONE.";
		}

		@Override
		protected void onPostExecute(String result) {
			//Logger.i(TAG, "UpdateDataAsyncTask onPostExecute I: " + result);
			try {
				loading = false;
				listData.addAll(updateData);
				postUpdateData();

			} catch (Exception e) {
				Logger.e(TAG, "UpdateDataAsyncTask onPostExecute E: " + e);
			}
			//Logger.i(TAG, "UpdateDataAsyncTask onPostExecute O");
		}
	}

}
