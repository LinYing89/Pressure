package test.lygzb.com.pressure.main;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import lygzb.zsmarthome.ClimateButler;
import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.User;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.TSeekDeviceOnlineState;
import lygzb.zsmarthome.net.NetHelper;
import lygzb.zsmarthome.net.RecT;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.SectionsPagerAdapter;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.application.HttpRequest;
import test.lygzb.com.pressure.application.SharedHelper;
import test.lygzb.com.pressure.chain.ChainActivity;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.esptouch.EsptouchActivity;
import test.lygzb.com.pressure.homehelper.MyClimateQueryProcess;
import test.lygzb.com.pressure.homehelper.MyFileHelper;
import test.lygzb.com.pressure.loop.CheckLoopThread;
import test.lygzb.com.pressure.loop.SendChainThread;
import test.lygzb.com.pressure.network.CheckNetThread;
import test.lygzb.com.pressure.network.DownloadFileThread;
import test.lygzb.com.pressure.network.FileIo;
import test.lygzb.com.pressure.network.MyMessageAnalysiser;
import test.lygzb.com.pressure.network.UploadFileThread;
import test.lygzb.com.pressure.network.WebClient;
import test.lygzb.com.pressure.network.WebFileBase;
import test.lygzb.com.pressure.service.DeviceOnlineStateChanged;
import test.lygzb.com.pressure.systemset.SearchActivity;
import test.lygzb.com.pressure.systemset.SystemSetActivity;

public class Main3Activity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	public static boolean LOG_REC = false;
	public static boolean LOG_SED = false;

	public static boolean IS_ADMIN;
	public static boolean FIRST_LOGIN;
	/**
	 * 本地连接是否可用
	 */
	public static boolean LOCAL_CONNECTED;

	public static final int PREPARE_SEARCH = 1;
	public static final int EXIT_SEARCH_ACTIVITY = 2;
	public static final int UPLOAD = 10;
	public static final int DOWNLOAD = 11;
	public static final int REFRESH_TITLE = 8;
	public static MyHandler handler = null;

	public static String strEnsure;
	public static String strCancel;

	public static CheckNetThread checkNetThread;
	public static CheckLoopThread checkLoopThread;
	public static SendChainThread sendChainThread;
	public static TSeekDeviceOnlineState tSeekDeviceOnlineState;

	private static RecT recT = null;

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private Toolbar toolbar;
	private ViewPager mViewPager;
	private ProgressDialog progressFileDialog;
	private VersionTask versionTask;
	private PackageInfo packageInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setLogo(R.mipmap.ic_logo_white);
		packageInfo = getAppVersionCode(this);
		toolbar.setTitle(UserHelper.getUser().getName() + UserHelper.getUser().getPetName());
		String strSubTitle = "大发科技:智能物联网控制器";
		if(null != packageInfo){
			strSubTitle += " v" + packageInfo.versionName;
		}
		toolbar.setSubtitle(strSubTitle);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		View headerView = navigationView.getHeaderView(0);
		TextView textUser = (TextView)headerView.findViewById(R.id.text_user);
		textUser.setText(UserHelper.getUser().getName());
		strEnsure = getString(R.string.ensure);
		strCancel = getString(R.string.cancel);
		//开启udp信息接受线程
		if(null == recT || !recT.isAlive()) {
			recT = new RecT(new MyMessageAnalysiser(UserHelper.getUser()));
			recT.start();
		}

		/*if(FIRST_LOGIN){
			FIRST_LOGIN = false;
			downloadFiles();
		}else{
			initDevices();
		}*/
		initDevices();
		versionTask = new VersionTask();
		versionTask.execute((Void)null);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

		handler = new MyHandler(Main3Activity.this);

		if(!IS_ADMIN) {
			startCheckNetThread();
		}

		NetHelper.getIns().setNetPushHelper(netPushHelper);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_reset){
			DeviceChainHelper.getIns().init();
		}else if(id == R.id.action_refresh){
			//SendMsgHelper.refreshState();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if(id == R.id.nav_net){
			Main3Activity.this.startActivity(new Intent(Main3Activity.this, EsptouchActivity.class));
		}else if (id == R.id.nav_search) {
			Main3Activity.this.startActivity(new Intent(Main3Activity.this, SearchActivity.class));
		} else if (id == R.id.nav_sort) {
			Main3Activity.this.startActivity(new Intent(Main3Activity.this, SortActivity.class));
		}else if(id == R.id.nav_set_chain) {
			startActivity(new Intent(Main3Activity.this, ChainActivity.class));
		}else if(id == R.id.nav_system_set) {
			startActivity(new Intent(Main3Activity.this, SystemSetActivity.class));
		} else if (id == R.id.nav_upload) {
			if(!IS_ADMIN) {
				FileIo.ROOT_PATH = FileHelper.ROOT_PATH;
				UploadFileThread uploadFileThread = new UploadFileThread();
				showProgressDialog("上传", uploadFileThread);
				uploadFileThread.start();
			}else{
				Snackbar.make(null, "本地登录不可上传", Snackbar.LENGTH_SHORT).show();
			}
		} else if (id == R.id.nav_download) {
			downloadFiles();
		}else if (id == R.id.nav_exit) {
			new AlertDialog.Builder(Main3Activity.this)
					.setMessage("确定退出账号吗")
					.setNegativeButton(Constant.getString(Main3Activity.this, R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {

								}
							})
					.setPositiveButton(Constant.getString(Main3Activity.this, R.string.ensure),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int whichButton) {
									UserHelper.getUser().setName("");
									UserHelper.getUser().setPsd("");
									new SharedHelper().setUser();
									myfinish();
								}
							}).show();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private NetHelper.INetPushHelper netPushHelper = new NetHelper.INetPushHelper() {
		@Override
		public void create() {

		}

		@Override
		public void send(String s) {
			if(LOG_SED){
				String og = s.replace('$', 'o');
				og = og.replace('#', '?');
				WebClient.getInstance().sendMsg("$ogs:" + og + "#00");
			}
		}

		@Override
		public void closed() {

		}

		@Override
		public void error(String s) {

		}
	};

	public static void stopCheckNetThread(){
		if(null != checkNetThread) {
			checkNetThread.setChecking(false);
			checkNetThread.interrupt();
			checkNetThread = null;
		}
	}

	public static void startCheckNetThread(){
		stopCheckNetThread();
		checkNetThread = new CheckNetThread();
		checkNetThread.start();
	}

	public void stopCheckLoopThread(){
		if(null != checkLoopThread){
			checkLoopThread.close();
			checkLoopThread = null;
		}
	}

	public void startCheckLoopThread(){
		if(checkLoopThread != null){
			stopCheckLoopThread();
		}
		checkLoopThread = new CheckLoopThread();
		checkLoopThread.start();
	}

	public void stopSendChainThread(){
		if(sendChainThread != null){
			sendChainThread.close();
			sendChainThread = null;
		}
	}

	public void startSendChainThread(){
		if(null != sendChainThread){
			stopSendChainThread();
		}
		sendChainThread = new SendChainThread();
		sendChainThread.start();
	}

	public void stopSeekDeviceOnline(){
		if(tSeekDeviceOnlineState != null){
			tSeekDeviceOnlineState.stopSeek();
			sendChainThread = null;
		}
	}

	public void startSeekDeviceOnline(){
		if(null != tSeekDeviceOnlineState){
			stopSeekDeviceOnline();
		}
		tSeekDeviceOnlineState = new TSeekDeviceOnlineState(UserHelper.getHomeMaster().getHouseKeeper().getListDevice(), new DeviceOnlineStateChanged());
		tSeekDeviceOnlineState.startSeek();
	}

	private void initDevices(){
		Device.MAX_SEND_COUNT = 5;
		//UserHelper.getSystemConfig().CLIMATE_QUERY_INTERVAL = 1000;
		//ClimateButler.queryIntervalTime = UserHelper.getSystemConfig().CLIMATE_QUERY_INTERVAL;
		MyFileHelper.myInitConfig(UserHelper.getUser());

		startSendChainThread();
		startCheckLoopThread();
		startSeekDeviceOnline();

		if(null != ElectricalCtrlFragment.handler){
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.REFRESH_SORT;
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
		if(null != ClimateFragment.handler){
			Message msg = Message.obtain();
			msg.arg1 = ClimateFragment.REFRESH_SORT;
			ClimateFragment.handler.sendMessage(msg);
		}
	}

	/**
	 * 下载数据
	 */
	private void downloadFiles(){
		if(!IS_ADMIN) {
			FileIo.ROOT_PATH = FileHelper.ROOT_PATH;
			DownloadFileThread downloadFileThread = new DownloadFileThread();
			showProgressDialog("下载", downloadFileThread);
			downloadFileThread.start();
		}else{
			Snackbar.make(toolbar, "本地登录不可下载", Snackbar.LENGTH_SHORT).show();
		}
	}

	public static class MyHandler extends Handler {
		WeakReference<Main3Activity> mActivity;

		MyHandler(Main3Activity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			Main3Activity theActivity = mActivity.get();
			switch (msg.arg1) {
				case UPLOAD:
					if(null != theActivity.progressFileDialog && theActivity.progressFileDialog.isShowing()) {
						theActivity.progressFileDialog.dismiss();
					}
					if(msg.arg2 == 1) {
						Snackbar.make(theActivity.toolbar, "上传成功", Snackbar.LENGTH_SHORT).show();
					}else{
						Snackbar.make(theActivity.toolbar, "上传失败", Snackbar.LENGTH_SHORT).show();
					}
					break;
				case DOWNLOAD:
					if(null != theActivity.progressFileDialog && theActivity.progressFileDialog.isShowing()) {
						theActivity.progressFileDialog.dismiss();
					}
					if(msg.arg2 == 1) {
						Snackbar.make(theActivity.toolbar, "下载成功", Snackbar.LENGTH_SHORT).show();
						theActivity.initDevices();
					}else{
						Snackbar.make(theActivity.toolbar, "下载失败", Snackbar.LENGTH_SHORT).show();
					}
					break;
				case PREPARE_SEARCH:
					stopCheckNetThread();
					//UserHelper.getHomeMaster().getClimateButler().stopQueryThread();
					theActivity.stopCheckLoopThread();
					theActivity.stopSendChainThread();
					break;
				case EXIT_SEARCH_ACTIVITY:
					startCheckNetThread();
					//UserHelper.getHomeMaster().getClimateButler().startQueryThread(new MyClimateQueryProcess());
					theActivity.startCheckLoopThread();
					theActivity.startSendChainThread();
					theActivity.startSeekDeviceOnline();
					break;
			}
		}
	}

	private void showProgressDialog(String title, final WebFileBase webFileBase){
		//创建ProgressDialog对象
		progressFileDialog = new ProgressDialog(
				Main3Activity.this);
		//设置进度条风格，风格为圆形，旋转的
		progressFileDialog.setProgressStyle(
				ProgressDialog.STYLE_SPINNER);
		//设置ProgressDialog 标题
		progressFileDialog.setTitle(title);
		//设置ProgressDialog 提示信息
		progressFileDialog.setMessage("请稍等");
		//设置ProgressDialog 标题图标
		progressFileDialog.setIcon(android.R.drawable.btn_star);
		//设置ProgressDialog 的进度条是否不明确
		progressFileDialog.setIndeterminate(false);
		//设置ProgressDialog 是否可以按退回按键取消
		progressFileDialog.setCancelable(false);
		//设置取消按钮
		progressFileDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressFileDialog.dismiss();
						webFileBase.close();
					}
				});
		// 让ProgressDialog显示
		progressFileDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(Main3Activity.this)
					.setMessage("退出程序")
					.setNegativeButton(Constant.getString(Main3Activity.this, R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {

								}
							})
					.setPositiveButton(Constant.getString(Main3Activity.this, R.string.ensure),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int whichButton) {
									myfinish();
								}
							}).show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public class VersionTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if(packageInfo != null){
					int version = packageInfo.versionCode;
					String s = HttpRequest.sendGet(WebClient.getVersionUrl(),
							"version=" + version);
					Log.e("MainActivity: ", "get:" + s);
					return s.contains("YES");
				}
			}catch (Exception e){
				e.printStackTrace();
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			versionTask = null;
			if (success) {
				new AlertDialog.Builder(Main3Activity.this)
						.setMessage("有新版本，是否下载更新")
						.setNegativeButton(Constant.getString(Main3Activity.this, R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {

									}
								})
						.setPositiveButton(Constant.getString(Main3Activity.this, R.string.ensure),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int whichButton) {
										//下载
										intoDownloadManager();
										/*Intent i = new Intent(Intent.ACTION_VIEW , Uri.parse("http://192.168.1.104:8080/ZSHWeb/download/smarthome.apk"));
										startActivity(i);*/
									}
								}).show();
			}
		}

		@Override
		protected void onCancelled() {
			versionTask = null;
		}
	}

	/**
	 * 返回当前程序版本名
	 */
	private PackageInfo getAppVersionCode(Context context) {
		PackageInfo pi = null;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return pi;
	}

	private void intoDownloadManager(){
		try {
			DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(WebClient.getDownloadUrl());
			DownloadManager.Request request = new DownloadManager.Request(uri);
			// 设置下载路径和文件名
//			request.setDestinationInExternalFilesDir(this,
//					Environment.DIRECTORY_DOWNLOADS, "hama.apk");
			//request.setDestinationInExternalPublicDir(MyFileHelper.getZhiBoFile(), "hama.apk");
			request.setDescription("智能物联网控制器新版本下载");
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setMimeType("application/vnd.android.package-archive");
			// 设置为可被媒体扫描器找到
			request.allowScanningByMediaScanner();
			// 设置为可见和可管理
			request.setVisibleInDownloadsUi(true);
			long refernece = dManager.enqueue(request);
			// 把当前下载的ID保存起来
			SharedPreferences sPreferences = getSharedPreferences(SharedHelper.SHARED_NUMBER, Context.MODE_PRIVATE);
			sPreferences.edit().putLong(SharedHelper.DOWNLOAD_ID, refernece).apply();
		}catch (IllegalArgumentException ex){
			Snackbar.make(toolbar, "下载器没有启用", Snackbar.LENGTH_LONG).show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void myfinish(){
		//UserHelper.getHomeMaster().getClimateButler().stopQueryThread();
		stopCheckNetThread();
		WebClient.getInstance().release();

		Device.shutdownExecutor();
		DeviceChainHelper.getIns().setListDeviceChain(null);

		handler = null;

		if(null != checkLoopThread) {
			checkLoopThread.close();
		}
		if(null != sendChainThread){
			sendChainThread.close();
		}
		finish();
		if(null != recT) {
			recT.closeThread();
			recT = null;
		}
		FileHelper.saveDeviceXml(UserHelper.getHomeMaster());
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
}
