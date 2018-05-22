package test.lygzb.com.pressure.systemset;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.main.UserHelper;

public class SystemSetActivity extends AppCompatActivity {

	private EditText editServerIp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_set);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		editServerIp = (EditText)findViewById(R.id.edit_server_ip);
		editServerIp.setText(UserHelper.getServerConfig().SERVER_IP);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*String strServerIp = editServerIp.getText().toString();
		if(!TextUtils.isEmpty(strServerIp)){
			WebClient.URL_ROOT = "http://" + strServerIp + ":8080/hm";
			UserHelper.getSystemConfig().SERVER_IP = strServerIp;
			FileHelper.saveSystemConfigXml(UserHelper.getSystemConfig());
		}*/
	}
}
