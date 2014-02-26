
package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.activity.LoginActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class LeftFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		TextView name = (TextView)view.findViewById(R.id.name_textview);
		name.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				intent.putExtra("title", "");
				startActivity(intent);
			}
			
		});
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
