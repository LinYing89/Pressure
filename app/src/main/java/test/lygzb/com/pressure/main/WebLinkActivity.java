package test.lygzb.com.pressure.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import test.lygzb.com.pressure.R;

public class WebLinkActivity extends AppCompatActivity {

	private WebView webLink;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_link);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		webLink = (WebView)findViewById(R.id.web_link);
		webLink.loadUrl("http://051801.cn/sd/");

		//启用支持javascript
		WebSettings settings = webLink.getSettings();
		settings.setJavaScriptEnabled(true);

		webLink.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}

}
