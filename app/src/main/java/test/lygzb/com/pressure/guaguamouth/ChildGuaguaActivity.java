package test.lygzb.com.pressure.guaguamouth;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterAffectedDevice;
import test.lygzb.com.pressure.adapter.AdapterEvent;
import test.lygzb.com.pressure.adapter.AdapterGuaguaAffrectdDevice;
import test.lygzb.com.pressure.adapter.AdapterGuaguaEvent;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.ChainFragment;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.chain.ChildChainActivity;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.chain.EventActivity;
import test.lygzb.com.pressure.chain.GuaguaFragment;
import test.lygzb.com.pressure.chain.LoopFragment;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.main.Main3Activity;

public class ChildGuaguaActivity extends AppCompatActivity {

	public static ChildGuaguaActivity.MyHandler handler;
	public static final int REFRESH_DEVICE_LIST = 1;
	public static final int REFRESH_EVENT_HANDLER_LIST = 2;

	private GuaGua guaGua;
	public static boolean ADD;

	private Button btnName;
	private ImageButton btnAddEvent;
	private ImageButton btnAddDevice;
	private ListView listViewEvent;
	private ListView listViewDevice;

	private AdapterGuaguaEvent adapterEvent;
	private AdapterGuaguaAffrectdDevice adapterAffectedDevice;
	private SceneActHelper sceneActHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_guagua);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		sceneActHelper = new SceneActHelper();
		findViews();
		setListener();
		if(ADD){
			guaGua = new GuaGua();
			guaGua.setName(getDefaultName());
		}else{
			guaGua = GuaguaHandler.getIns().getSelectedGuagua();
			if(guaGua == null){
				finish();
				return;
			}
		}
		init();
		setListViewEvent();
		setListViewDevice();
		handler = new ChildGuaguaActivity.MyHandler(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish(); // back button
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ADD){
			GuaguaHandler.getIns().add(guaGua);
			if(null != GuaguaFragment.handler){
				Message message = Message.obtain();
				message.arg1 = LoopFragment.REFRESH_LIST;
				GuaguaFragment.handler.sendMessage(message);
			}
		}
	}

	private void findViews(){
		btnName = (Button)findViewById(R.id.btn_name);
		btnAddEvent = (ImageButton)findViewById(R.id.btn_add_event);
		btnAddDevice = (ImageButton)findViewById(R.id.btn_add_device);
		listViewEvent = (ListView)findViewById(R.id.list_event);
		listViewDevice = (ListView)findViewById(R.id.list_device);
	}

	private void setListener(){
		btnName.setOnClickListener(onClickListener);
		btnAddEvent.setOnClickListener(onClickListener);
		btnAddDevice.setOnClickListener(onClickListener);

		listViewEvent.setOnItemClickListener(eventOnItemClickListener);
		listViewEvent.setOnItemLongClickListener(eventOnItemLongClickListener);
		listViewDevice.setOnItemLongClickListener(deviceOnItemLongClickListener);
	}

	private void init(){
		setNameText();
	}

	private String getDefaultName(){
		String name = "呱呱";
		boolean have;
		for(int i=1; i< 1000; i++){
			have = false;
			name = "呱呱" + i;
			for(GuaGua guaGua : GuaguaHandler.getIns().getListGuagua()){
				if(guaGua.getName().equals(name)){
					have = true;
					break;
				}
			}
			if(!have){
				return name;
			}
		}
		return name;
	}

	private void setNameText(){
		btnName.setText("名称:" + guaGua.getName());
	}

	private void setListViewEvent(){
		adapterEvent = new AdapterGuaguaEvent(this, guaGua.getListEvent());
		listViewEvent.setAdapter(adapterEvent);
	}

	private void setListViewDevice(){
		adapterAffectedDevice = new AdapterGuaguaAffrectdDevice(this, guaGua.getTrigger().getListLinkedDevice());
		listViewDevice.setAdapter(adapterAffectedDevice);
	}

	//按钮点击事件
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btn_name:
					showNameDialog();
					break;
				case R.id.btn_add_event:
					if(ADD){
						GuaguaHandler.getIns().add(guaGua);
						GuaguaHandler.getIns().setSelectedGuagua(guaGua);
					}
					AddGuaguaEventActivity.ADD = true;
					startActivity(new Intent(ChildGuaguaActivity.this, AddGuaguaEventActivity.class));
					break;
				case R.id.btn_add_device:
					AddGuguaMouthActivity.ADD = true;
					startActivity(new Intent(ChildGuaguaActivity.this, AddGuguaMouthActivity.class));
					break;
			}
		}
	};

	//条件列表点击事件
	private AdapterView.OnItemClickListener eventOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			guaGua.setSelectedEvent(guaGua.getListEvent().get(position));
			AddGuaguaEventActivity.ADD = false;
			startActivity(new Intent(ChildGuaguaActivity.this, AddGuaguaEventActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			guaGua.setSelectedEvent(guaGua.getListEvent().get(position));
			showElectricalPopUp(view, 0);
			return true;
		}
	};

	private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			guaGua.setSelectedLinkedDevice(guaGua.getTrigger().getListLinkedDevice().get(position));
			showElectricalPopUp(view, 1);
			return true;
		}
	};

	/**
	 * 名称对话框
	 */
	private void showNameDialog() {
		final EditText editHour = new EditText(this);
		editHour.setText(String.valueOf(guaGua.getName()));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(editHour)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String strName = String.valueOf(editHour.getText());
								guaGua.setName(strName);
								setNameText();
							}
						})
				.setNegativeButton(
						Main3Activity.strCancel,
						null).create().show();

	}

	public void showElectricalPopUp(View v, final int which) {
		Button layoutDelete = new Button(this);
		layoutDelete.setText("删除");
		final PopupWindow popupWindow = new PopupWindow(layoutDelete, Constant.displayWidth,
				Constant.getEleHeight());

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
//		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				if(which == 0){
					guaGua.getListEvent().remove(guaGua.getSelectedEvent());
					adapterEvent.notifyDataSetChanged();
				}else if(which == 1){
					guaGua.getTrigger().removeLinkedDevice(guaGua.getSelectedLinkedDevice());
					adapterAffectedDevice.notifyDataSetChanged();
				}
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<ChildGuaguaActivity> mActivity;

		MyHandler(ChildGuaguaActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChildGuaguaActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_EVENT_HANDLER_LIST:
					theActivity.adapterEvent.notifyDataSetChanged();
					break;
				case REFRESH_DEVICE_LIST :
					theActivity.adapterAffectedDevice.notifyDataSetChanged();
					break;
			}
		}
	};
}
