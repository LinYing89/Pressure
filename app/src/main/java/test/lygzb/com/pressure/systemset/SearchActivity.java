package test.lygzb.com.pressure.systemset;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.ZEncoding;
import lygzb.zsmarthome.device.Controller;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.net.MessageAnalysiser;
import lygzb.zsmarthome.net.SearchDeviceThread;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.SearchDeviceAdapter;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.electrical.ChildElectricalActivity;
import test.lygzb.com.pressure.electrical.DeviceModelHelper;
import test.lygzb.com.pressure.homehelper.MyFileHelper;
import test.lygzb.com.pressure.main.ClimateFragment;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.network.MySearchDeviceResult;
import test.lygzb.com.pressure.network.WebClient;

public class SearchActivity extends AppCompatActivity {

	public static final int REFRESH_ELE_LIST = 3;
	public final static int SHOW_ERR_LIST = 4;
	/**
	 * 设置设备模式时，服务器的响应
	 */
	public static final int SET_MODEL_RESPONSE = 5;
	public static final int SHOW_ALERT_DIALOG= 6;

	public static DeviceModelHelper deviceModelHelper;
	private Device selectedDevice;

	/** update UI handler */
	public static MyHandler handler;

	private ListView listviewDevice;
	private SearchDeviceAdapter adapterEleHolder;
	private ProgressDialog progressDialog;
	private SearchDeviceThread searchDeviceThread;

	/**
	 * 发送设置模式命令线程
	 */
	private ThreadSendModel tSendModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findViews();
		setListener();
		handler = new MyHandler(this);
		if(null != Main3Activity.handler){
			Message msg = Message.obtain();
			msg.arg1 = Main3Activity.PREPARE_SEARCH;
			Main3Activity.handler.sendMessage(msg);
		}
		setDeviceList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_search_device, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id){
			case android.R.id.home:
				myFinish();
				break;
			case R.id.action_search_device:
				showProgressDialog(getString(R.string.title_activity_search));
				searchDeviceThread = new SearchDeviceThread(UserHelper.getUser(), new MySearchDeviceResult());
				searchDeviceThread.setDaemon(true);
				searchDeviceThread.start();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void findViews() {
		listviewDevice = (ListView) findViewById(R.id.list_device);
	}

	private void setListener() {
		listviewDevice.setOnItemClickListener(deviceOnItemClickListener);
		listviewDevice
				.setOnItemLongClickListener(deviceOnItemLongClickListener);
	}

	/** set device list adapter */
	public void setDeviceList() {
		if(UserHelper.getHomeMaster().getHouseKeeper().getListDevice().size() > 0) {
			adapterEleHolder = new SearchDeviceAdapter(this, UserHelper.getHomeMaster().getHouseKeeper().getListDevice());
			listviewDevice.setAdapter(adapterEleHolder);
		}
	}



	private void showProgressDialog(String title){
		//创建ProgressDialog对象
		progressDialog = new ProgressDialog(
				SearchActivity.this);
		//设置进度条风格，风格为圆形，旋转的
		progressDialog.setProgressStyle(
				ProgressDialog.STYLE_SPINNER);
		//设置ProgressDialog 标题
		progressDialog.setTitle(title);
		//设置ProgressDialog 提示信息
		progressDialog.setMessage("请稍等");
		//设置ProgressDialog 标题图标
		progressDialog.setIcon(android.R.drawable.btn_star);
		//设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		//设置取消按钮
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(null != searchDeviceThread){
							searchDeviceThread.interrupt();
						}
						MessageAnalysiser.isAsk = false;
						progressDialog.dismiss();
						//UserHelper.getHomeMaster().getClimateButler().startQueryThread();
					}
				});
		//设置ProgressDialog 是否可以按退回按键取消
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void showRenameDialog(final ZEncoding ecoding) {
		final EditText edit_newName = new EditText(SearchActivity.this);
		edit_newName.setText(ecoding.getName());
		new AlertDialog.Builder(SearchActivity.this)
				.setTitle(
						SearchActivity.this.getString(R.string.rename))
				.setView(edit_newName)
				.setPositiveButton(Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String value = edit_newName.getText()
										.toString();
								updateZdoc(ecoding, value);
							}
						}).setNegativeButton(Main3Activity.strCancel, null).create().show();
	}

	private void showAliasDialog(final Device device) {
		final EditText edit_newName = new EditText(SearchActivity.this);
		edit_newName.setText(device.getAlias());
		new AlertDialog.Builder(SearchActivity.this)
				.setTitle(
						SearchActivity.this.getString(R.string.rename))
				.setView(edit_newName)
				.setPositiveButton(Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String value = edit_newName.getText()
										.toString();
								device.setAlias(value);
							}
						}).setNegativeButton(Main3Activity.strCancel, null).create().show();
	}

	private void updateZdoc(ZEncoding ecoding, String value) {
		if (ecoding instanceof Device) {
			if (!UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().getListDeviceNames()
					.contains(value)) {
				ecoding.setName(value);
				adapterEleHolder.notifyDataSetChanged();
			} else {
				duplicateNameDialog(Constant.getString(SearchActivity.this, R.string.name_repeat));
			}
		}
	}

	private void duplicateNameDialog(String title) {
		new AlertDialog.Builder(SearchActivity.this)
				.setTitle(title)
				.setNegativeButton(
						Constant.getString(SearchActivity.this, R.string.cancel), null).create().show();
	}

	/** list click event listener */
	private AdapterView.OnItemClickListener deviceOnItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			Device device = UserHelper.getHomeMaster().getHouseKeeper().getListDevice().get(arg2);
			UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().setSelectedDevice(device);
			UserHelper.getHomeMaster().getHouseKeeper().setSelectedDevice(device);
			if (device instanceof Controller) {
				ChildElectricalActivity.controller = (Controller)device;
				SearchActivity.this.startActivity(new Intent(SearchActivity.this, ChildElectricalActivity.class));
			}
		}

	};

	private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
			selectedDevice = UserHelper.getHomeMaster().getHouseKeeper().getListDevice().get(arg2);
			UserHelper.getHomeMaster().getHouseKeeper().setSelectedDevice(selectedDevice);
			showDevicePopUp(arg1);
			return true;
		}

	};

	private void closeProgressDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	public static class MyHandler extends Handler {
		WeakReference<SearchActivity> mActivity;

		MyHandler(SearchActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SearchActivity theActivity = mActivity.get();
			switch (msg.arg1){
				case MySearchDeviceResult.NO_MESSAGE:
					theActivity.closeProgressDialog();
					Toast.makeText(theActivity, Constant.getString(theActivity, R.string.no_feedback),
							Toast.LENGTH_LONG).show();
					break;
				case MySearchDeviceResult.REGRESH_OK:
					try {
						theActivity.closeProgressDialog();
						Toast.makeText(theActivity, Constant.getString(theActivity, R.string.update_success),
								Toast.LENGTH_LONG).show();
						theActivity.setDeviceList();
					}catch (Exception e){
					}
					break;
				case SHOW_ERR_LIST:
					StringBuilder sb = new StringBuilder();
					sb.append(MessageAnalysiser.listErrMsg.size());
					sb.append(Constant.getString(theActivity, R.string.illegal_message));
					sb.append("\n");
					for(String eleErr : MessageAnalysiser.listErrMsg){
						sb.append(eleErr);
						sb.append("\n");
					}
					Constant.showErrDialog(theActivity, sb.toString());
					break;
				case REFRESH_ELE_LIST:
					if(null != theActivity.adapterEleHolder) {
						theActivity.adapterEleHolder.notifyDataSetChanged();
					}
					break;
				case SET_MODEL_RESPONSE:
					switch (msg.arg2) {
						case 0:
							Log.e("ElectricalCtrlFrag", "收到改变进度");
							//进度0，设置为远程时，收到服务器回应，转为pad向设备发设置命令
							if (null != theActivity.tSendModel) {
								theActivity.tSendModel.setModelProgressValue = 1;
							}
							break;
						case 1:
							//配置完成
							if(null != theActivity.tSendModel){
								theActivity.tSendModel.interrupt();
								theActivity.tSendModel.keep = false;
								theActivity.tSendModel = null;
							}
							if(null != theActivity.progressDialog){
								theActivity.progressDialog.dismiss();
								theActivity.progressDialog = null;
							}
							if(null != deviceModelHelper){
								deviceModelHelper.getDevToSet().setDeviceModel(deviceModelHelper.getToDeviceModel());
								deviceModelHelper = null;

							}
							theActivity.showAlertDialog("配置成功");
							break;
					}
					break;
				case SHOW_ALERT_DIALOG:
					theActivity.showAlertDialog(msg.obj.toString());
					break;
			}
		}
	}

	public void showDevicePopUp(View v) {
		View layout = this.getLayoutInflater()
				.inflate(R.layout.pop_device_long_click, null);
		Button layoutRename = (Button) layout
				.findViewById(R.id.text_rename);
		Button btnAlias = (Button) layout
				.findViewById(R.id.text_alias);
		Button layoutDelete = (Button) layout
				.findViewById(R.id.text_delete);
		final Button btnSetDeviceModel = (Button) layout
				.findViewById(R.id.btn_set_device_model);
		//btnSetDeviceModel.setVisibility(View.INVISIBLE);

		final Device dev = selectedDevice;
		deviceModelHelper = new DeviceModelHelper();
		deviceModelHelper.setDevToSet(dev);
		if(dev.getDeviceModel() == EDeviceModel.LOCAL){
			deviceModelHelper.setToDeviceModel(EDeviceModel.REMOTE);
			btnSetDeviceModel.setText("设为远程模式");
		}else{
			deviceModelHelper.setToDeviceModel(EDeviceModel.LOCAL);
			btnSetDeviceModel.setText("设为本地模式");
		}

		final PopupWindow popupWindow = new PopupWindow(layout, Constant.displayWidth,
				Constant.getEleHeight());

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
//		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
		layoutRename.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showRenameDialog(dev);
			}
		});

		btnAlias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showAliasDialog(dev);
			}
		});
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				UserHelper.getHomeMaster().getHouseKeeper().removeDevice(dev);
				adapterEleHolder.notifyDataSetChanged();
				//setDeviceList();
			}
		});
		//设置设备控制模式（本地/远程）
		btnSetDeviceModel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showSetModelWaitDialog(deviceModelHelper.getToDeviceModel());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		setDeviceList();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			myFinish();
			return super.onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler = null;
	}

	//显示等待进度对话框
	private void showSetModelWaitDialog(EDeviceModel model){
		//创建ProgressDialog对象
		progressDialog = new ProgressDialog(SearchActivity.this);
		//设置进度条风格，风格为圆形，旋转的
		progressDialog.setProgressStyle(
				ProgressDialog.STYLE_SPINNER);
		//设置ProgressDialog 标题
		progressDialog.setTitle("正在配置设备...");
		//设置ProgressDialog 提示信息
		progressDialog.setMessage("请稍等");
		//设置ProgressDialog 标题图标
		progressDialog.setIcon(android.R.drawable.btn_star);
		//设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		//设置ProgressDialog 是否可以按退回按键取消
		progressDialog.setCancelable(false);
		progressDialog.show();
		String order = null;
		Device dev = UserHelper.getHomeMaster().getHouseKeeper().getSelectedDevice();
		if(model == EDeviceModel.LOCAL) {
			order = dev.turnToRemoteModel();
		}else{
			order = dev.turnToLocalModel();
		}

		if(null!= order){
			order = dev.createFinalOrder(order);
			deviceModelHelper.setOrder(order);
			if(null != tSendModel && tSendModel.isAlive()){
				tSendModel.interrupt();
			}
			tSendModel = new ThreadSendModel();
			Device.getExec().execute(tSendModel);
		}
	}

	/**
	 * 发送设置模式命令的线程
	 */
	private class ThreadSendModel extends Thread{
		/**
		 * 设置模式进度
		 * 0:向服务器发送
		 * 1:向设备发送
		 */
		int setModelProgressValue = 0;
		private int count;
		boolean keep = true;
		@Override
		public void run() {
			while (keep){
				try {
					if(setModelProgressValue == 0) {
						//第一步 向服务器发送
//                        Log.e("ElectricalCtrlFrag", "向服务器发送" + order);
						WebClient.getInstance().sendMsg(deviceModelHelper.getOrder());
					}else{
						//第二步
						//如果时设为远程模式，向本地发送报文，
						// 如果设为本地模式，不需要向本地发，只需向服务器发，收到服务器响应后等待设备本地心跳
						if(deviceModelHelper.getToDeviceModel() == EDeviceModel.REMOTE){
							SendMsgHelper.sendMessage(deviceModelHelper.getOrder());
						}
					}
					//计数加1
					count++;
					if(count > 10){
						//设置失败
						if(null != progressDialog && progressDialog.isShowing()){
							progressDialog.dismiss();
							progressDialog = null;
						}
						deviceModelHelper = null;
						if(setModelProgressValue == 0) {
							Message message = Message.obtain();
							message.arg1 = SHOW_ALERT_DIALOG;
							message.obj = "服务器无响应";
							handler.sendMessage(message);
						}else{
							Message message = Message.obtain();
							message.arg1 = SHOW_ALERT_DIALOG;
							message.obj = "可能设备无响应";
							handler.sendMessage(message);
						}
						return;
					}
					Thread.sleep(5000);
				}catch (Exception ex){
					Log.e("ElectricalCtrlFragment", ex.getMessage());
					return;
				}
			}
		}
	}

	private void showAlertDialog(String text){
		new android.support.v7.app.AlertDialog.Builder(SearchActivity.this).setTitle("提示")
				.setMessage(text)
				.setPositiveButton("确定", null)
				.show();
	}

	private void myFinish(){
		FileHelper.saveDeviceXml(UserHelper.getHomeMaster());
		FileHelper.saveLinkageMasterXml(UserHelper.getLinkageMaster());
		FileHelper.saveSecurityXml(UserHelper.getSecurityConfig());
		DeviceChainHelper.getIns().getListDeviceChain().clear();
		MyFileHelper.myInitConfig(UserHelper.getUser());
		if (ElectricalCtrlFragment.handler != null) {
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE;
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
		if(null != ClimateFragment.handler) {
			Message msg = Message.obtain();
			msg.arg1 = ClimateFragment.REFRESH_DEVICE;
			ClimateFragment.handler.sendMessage(msg);
		}
		if(null != Main3Activity.handler){
			Message msg = Message.obtain();
			msg.arg1 = Main3Activity.EXIT_SEARCH_ACTIVITY;
			Main3Activity.handler.sendMessage(msg);
		}
		finish();
	}
}
