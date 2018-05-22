package test.lygzb.com.pressure.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;

import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.SystemConfig;
import lygzb.zsmarthome.configration.LinkageMasterXmlWriter;
import lygzb.zsmarthome.net.NetHelper;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.application.SharedHelper;
import test.lygzb.com.pressure.homehelper.MyDefaultConfigIniter;
import test.lygzb.com.pressure.homehelper.MyFileHelper;
import test.lygzb.com.pressure.network.WebClient;

public class WelcomeActivity extends AppCompatActivity {

	public static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Constant.displayWidth = displayMetrics.widthPixels;
		Constant.displayHeight = displayMetrics.heightPixels;
		Constant.titleHeight = (int)(displayMetrics.heightPixels * 0.07f);

		FileHelper.ROOT_PATH = MyFileHelper.getRootFilePath(this);

		MyDefaultConfigIniter md = new MyDefaultConfigIniter();
		md.initMapAddress();
		md.initMapDevice();
		md.initMapElectrical();
		md.initMapScene();
		md.initMapAction();
		md.initMapCurtainAction();

		File file = new File(FileHelper.getSystemConfigXmlPath());
		if(!file.exists()){
			FileHelper.saveSystemConfigXml(UserHelper.getSystemConfig());
		}

		SystemConfig sc = FileHelper.getSystemConfig();

		String scenePath =FileHelper.getSceneXmlPath();
		File fScene =new File(scenePath);
		if(!fScene.exists()){
			LinkageMasterXmlWriter.writeDefaultLinkageMaster(scenePath);
		}

		String securityPath = FileHelper.getSecurityXmlPath();
		File fSecurity =new File(securityPath);
		if(!fSecurity.exists()){
			UserHelper.getSecurityConfig().initDefaultSecurityConfig();
			FileHelper.saveSecurityXml(UserHelper.getSecurityConfig());
		}

		context = this;

		SharedHelper sharedHelper = new SharedHelper();
		sharedHelper.getUser();

		String zhiboPatn = MyFileHelper.getZhiBoFile();
		Log.e("WelcomeAct", "zhiboPath: " + zhiboPatn);
		if(null != zhiboPatn){
			File sdFile=new File(zhiboPatn);
			if(!sdFile.exists()){
				boolean b = sdFile.mkdirs();
				Log.e("WelcomeAct", "b: " + b);
			}
		}
		//configLog();
//		UserHelper.getServerConfig().SERVER_IP="192.168.2.101";
//		UserHelper.getServerConfig().SERVER_IP="bairock.com";
//		UserHelper.getServerConfig().SERVER_IP="123.206.104.15";
//		WebClient.URL_ROOT = "http://" + UserHelper.getServerConfig().SERVER_IP + ":8080/sd";
        UserHelper.getServerConfig().SERVER_IP="051801.cn";
		WebClient.URL_ROOT = "http://" + UserHelper.getServerConfig().SERVER_IP + "/sd";

		//startMain();
		ToMainTask toMainTask = new ToMainTask();
		toMainTask.execute();
	}

	class ToMainTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
				return true;
			}catch (Exception e){
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				if(TextUtils.isEmpty(UserHelper.getUser().getName()) || TextUtils.isEmpty(UserHelper.getUser().getPsd())) {
					WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
				}else{
					Main3Activity.IS_ADMIN = UserHelper.getUser().getName().equals("admin");
					WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, Main3Activity.class));
				}
				finish();
			}
		}

		@Override
		protected void onCancelled() {

		}
	}
}
