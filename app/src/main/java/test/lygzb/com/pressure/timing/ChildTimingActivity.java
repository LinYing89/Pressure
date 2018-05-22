package test.lygzb.com.pressure.timing;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import test.lygzb.com.pressure.adapter.AdapterTimer;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.chain.ChainFragment;
import test.lygzb.com.pressure.chain.ChildChainActivity;
import test.lygzb.com.pressure.chain.TimingFragment;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.main.Main3Activity;

public class ChildTimingActivity extends AppCompatActivity {

	public static MyHandler handler;

	private MyTiming timing;
	public static boolean ADD;

	private Toolbar toolbar;
	private Button btnName;
	private ImageButton btnAddTimer;
	private ImageButton btnAddDevice;
	private ListView listViewTimer;
	private ListView listViewDevice;

	private AdapterTimer adapterTimer;
	private AdapterAffectedDevice adapterAffectedDevice;
	private SceneActHelper sceneActHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_timing);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		sceneActHelper = new SceneActHelper();
		findViews();
		setListener();
		if(ADD){
			timing = new MyTiming();
			timing.setName(getDefaultName());
		}else{
			timing = TimingHandler.getIns().getSelectedTiming();
			if(timing == null){
				finish();
				return;
			}
		}
		init();
		setListViewTimer();
		setListViewDevice();
		handler = new MyHandler(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ADD){
			TimingHandler.getIns().add(timing);
			if(null != ChainFragment.handler){
				Message message = Message.obtain();
				message.arg1 = TimingFragment.REFRESH_LIST;
				TimingFragment.handler.sendMessage(message);
			}
		}
	}

	private void findViews(){
		btnName = (Button)findViewById(R.id.btn_name);
		btnAddTimer = (ImageButton)findViewById(R.id.btn_add_timer);
		btnAddDevice = (ImageButton)findViewById(R.id.btn_add_device);
		listViewTimer = (ListView)findViewById(R.id.list_timer);
		listViewDevice = (ListView)findViewById(R.id.list_device);
	}

	private void setListener(){
		btnName.setOnClickListener(onClickListener);
		btnAddTimer.setOnClickListener(onClickListener);
		btnAddDevice.setOnClickListener(onClickListener);

		listViewTimer.setOnItemClickListener(eventOnItemClickListener);
		listViewTimer.setOnItemLongClickListener(eventOnItemLongClickListener);
		listViewDevice.setOnItemLongClickListener(deviceOnItemLongClickListener);
	}
	private void init(){
		setNameText();
	}

	private String getDefaultName(){
		String name = "定时";
		boolean have;
		for(int i=1; i< 1000; i++){
			have = false;
			name = "定时" + i;
			for(MyTiming timing : TimingHandler.getIns().getListMyTiming()){
				if(timing.getName().equals(name)){
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
		toolbar.setSubtitle(timing.getName());
		btnName.setText("名称:" + timing.getName());
	}

	private void setListViewTimer(){
		adapterTimer = new AdapterTimer(this, timing.getListTimer());
		listViewTimer.setAdapter(adapterTimer);
	}

	private void setListViewDevice(){
		adapterAffectedDevice = new AdapterAffectedDevice(this, timing.getTrigger().getListLinkedDevice(), false);
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
				case R.id.btn_add_timer:
					if(ADD){
						TimingHandler.getIns().add(timing);
						TimingHandler.getIns().setSelectedTiming(timing);
					}
					startActivity(new Intent(ChildTimingActivity.this, TimerActivity.class));
					break;
				case R.id.btn_add_device:
					sceneActHelper.showElectricalDialog(ChildTimingActivity.this,
							timing.getTrigger(), handler);
					break;
			}
		}
	};

	//条件列表点击事件
	private AdapterView.OnItemClickListener eventOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			timing.setSelectedTimer(timing.getListTimer().get(position));
			TimerActivity.timer = timing.getSelectedTimer();
			startActivity(new Intent(ChildTimingActivity.this, TimerActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			timing.setSelectedTimer(timing.getListTimer().get(position));
			showElectricalPopUp(view, 0);
			return true;
		}
	};

	private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			timing.setSelectedLinkDevice(timing.getTrigger().getListLinkedDevice().get(position));
			showElectricalPopUp(view, 1);
			return true;
		}
	};

	/**
	 * 名称对话框
	 */
	private void showNameDialog() {
		final EditText editHour = new EditText(this);
		editHour.setText(String.valueOf(timing.getName()));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(editHour)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String strName = String.valueOf(editHour.getText());
								timing.setName(strName);
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
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				if(which == 0){
					timing.getListTimer().remove(timing.getSelectedTimer());
					adapterTimer.notifyDataSetChanged();
				}else if(which == 1){
					timing.getTrigger().removeLinkedDevice(timing.getSelectedLinkDevice());
					adapterAffectedDevice.notifyDataSetChanged();
				}
			}
		});
	}
	public static class MyHandler extends Handler {
		WeakReference<ChildTimingActivity> mActivity;

		MyHandler(ChildTimingActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChildTimingActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case ChildChainActivity.REFRESH_EVENT_HANDLER_LIST:
					theActivity.adapterTimer.notifyDataSetChanged();
					break;
				case ChildChainActivity.REFRESH_DEVICE_LIST :
					theActivity.adapterAffectedDevice.notifyDataSetChanged();
					break;
			}

		}
	};
}
