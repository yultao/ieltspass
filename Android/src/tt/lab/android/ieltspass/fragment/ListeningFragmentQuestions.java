package tt.lab.android.ieltspass.fragment;

import tt.lab.android.ieltspass.R;
import tt.lab.android.ieltspass.data.Constants;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;

public class ListeningFragmentQuestions extends Fragment {
	private static final String TAG = ListeningFragmentQuestions.class.getName();
	private String questions;
	private WebView webView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_listening_questions, container, false);
		
		webView = (WebView) rootView.findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
//		webSettings.setBuiltInZoomControls(true);
//		int screenDensity = getResources().getDisplayMetrics().densityDpi ;   
//		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM ;   
//		switch (screenDensity){   
//		case DisplayMetrics.DENSITY_LOW :  
//		    zoomDensity = WebSettings.ZoomDensity.CLOSE;  
//		    break;  
//		case DisplayMetrics.DENSITY_MEDIUM:  
//		    zoomDensity = WebSettings.ZoomDensity.MEDIUM;  
//		    break;  
//		case DisplayMetrics.DENSITY_HIGH:  
//		    zoomDensity = WebSettings.ZoomDensity.FAR;  
//		    break ;  
//		}  
//		webSettings.setDefaultZoom(zoomDensity); 
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL); 		webView.loadUrl("file:///android_asset/cambridge/listening/"+questions );//
//		ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView1);
//		
//		String myJpgPath = Constants.SD_PATH+"/"+Constants.AUDIO_PATH+"/C6T1S2.Q.png";//"file:///android_asset/cambridge/listening/C6T1S2.Q.png";
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		//options.inSampleSize = 2;
//		Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
//		imageView.setImageBitmap(bm);
		return rootView;
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	

}