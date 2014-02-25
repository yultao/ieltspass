package tt.lab.android.ieltspass.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.VocabularyDetailActivity;
import tt.lab.android.ieltspass.data.Database;
import tt.lab.android.ieltspass.data.Word;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class PageFragmentVocabulary extends Fragment {

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

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		view = inflater.inflate(R.layout.page_vocabulary, null);

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

	private void initListView() {
		listView = (ListView) view.findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("onItemClick", "position: " + position + ", id: " + id);
				Map<String, Object> item = (Map<String, Object>) simpleAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), VocabularyDetailActivity.class);
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
				filter();
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
	}

	private void initSortSpinner() {
		final String[] sortCriteria = { "随机", "A-Z", "Z-A", "远-近", "近-远", "生-熟", "熟-生" };
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
				case 1:// a-z
					currentData = listData1;
					break;
				case 2:// z-a
					currentData = listData2;
					break;
				case 3:// 近-远
					currentData = listData3;
					break;
				case 4:// 近-远
					currentData = listData4;
					break;
				case 5:// 近-远
					currentData = listData5;
					break;

				}
				
				//第一次加载无需重新排序、过滤
				if (filterSpinnerInited) {
					resetData();
					sort();
					filter();
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
		simpleAdapter = new SimpleAdapter(context, currentData, R.layout.page_vocabulary_vlist, new String[] { "title",
				"phon", "info", "img" }, new int[] { R.id.title, R.id.phon, R.id.info, R.id.img });
		listView.setAdapter(simpleAdapter);
	}

	private void filter() {
		if (searchView.getQuery().length() == 0) {
			resetData();
		} else {
			simpleAdapter.getFilter().filter(searchView.getQuery());
			simpleAdapter.notifyDataSetChanged();
		}
	}

	private void sort() {
		try {

			switch (sortSpinner.getSelectedItemPosition()) {
			case 0:// "随机"
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						int i = (int) (Math.random() * 10) - 5;
						return i;
					}
				});
				break;
			case 1:// a-z
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("title");
						String title2 = (String) rhs.get("title");
						return title1.compareTo(title2);
					}
				});
				break;
			case 2:// z-a
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {

					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("title");
						String title2 = (String) rhs.get("title");
						return title2.compareTo(title1);
					}

				});

				break;
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
			case 5:// 熟-生"
				Collections.sort(currentData, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
						String title1 = (String) lhs.get("category");
						String title2 = (String) rhs.get("category");
						return title1.compareTo(title2);
					}
				});
				break;
			case 6:// "生-熟",
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

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initData() {

		Map<String, Word> wordMap = Database.getWords();
		if (wordMap.size() == 0) {
			for (int i = 0; i < 100; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", "ABC title " + i);
				map.put("info", "ABC info " + i);
				map.put("phon", "ABC phon " + i);
				if (i % 2 == 0) {
					map.put("img", String.valueOf(R.drawable.wei));
				} else if (i % 3 == 0) {
					map.put("img", String.valueOf(R.drawable.shu));
				} else {
					map.put("img", String.valueOf(R.drawable.wu));
				}
				listData0.add(map);
			}
		} else {
			for (String key : wordMap.keySet()) {
				Word word = wordMap.get(key);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", word.getTitle());
				map.put("phon", word.getPhoneticSymbol());
				map.put("info", word.getExplanation());
				map.put("category", word.getCategory());
				map.put("date", word.getDate());
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
				}
				listData0.add(map);
			}
		}

		currentData = listData0;

	}

}
