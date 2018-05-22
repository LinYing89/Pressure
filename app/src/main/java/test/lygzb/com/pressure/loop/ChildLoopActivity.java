package test.lygzb.com.pressure.loop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterAffectedDevice;
import test.lygzb.com.pressure.adapter.AdapterEventHandler;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.chain.ChildChainActivity;
import test.lygzb.com.pressure.chain.EventHandlerActivity;
import test.lygzb.com.pressure.chain.LoopFragment;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.main.Main3Activity;

public class ChildLoopActivity extends AppCompatActivity {

	public static MyHandler handler;
	public static final int REFRESH_EVENT_HANDLER_LIST = 2;

	private Loop loop;
	public static boolean ADD;

	private Toolbar toolbar;
	private Button btnName;
	private Button btnLoopCount;
	private ImageButton btnAddEvent;
	private ImageButton btnAddDevice;
	private ListView listViewEventHandler;
	private ListView listViewDevice;

	private AdapterEventHandler adapterEventHandler;
	private SceneActHelper sceneActHelper;
	private AdapterAffectedDevice adapterAffectedDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_loop);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		sceneActHelper = new SceneActHelper();
		findViews();
		setListener();
		if(ADD){
			loop = new Loop();
			loop.setName(getDefaultName());
			LoopHandler.getIns().add(loop);
			LoopHandler.getIns().setSelectedLoop(loop);
		}else{
			loop = LoopHandler.getIns().getSelectedLoop();
			if(loop == null){
				finish();
				return;
			}
		}
		init();
		setListViewEventHandler();
		setListViewDevice();
		handler = new MyHandler(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ADD){
			//LoopHandler.getIns().add(loop);
			if(null != LoopFragment.handler){
				Message message = Message.obtain();
				message.arg1 = LoopFragment.REFRESH_LIST;
				LoopFragment.handler.sendMessage(message);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_child_loop, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_duration_time) {
			startActivity(new Intent(ChildLoopActivity.this, DurationListActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

	private void findViews(){
		btnName = (Button)findViewById(R.id.btn_name);
		btnLoopCount = (Button)findViewById(R.id.btn_loop_count);
		btnAddEvent = (ImageButton)findViewById(R.id.btn_add_event);
		btnAddDevice = (ImageButton)findViewById(R.id.btn_add_device);
		listViewEventHandler = (ListView)findViewById(R.id.list_event_handler);
		listViewDevice = (ListView)findViewById(R.id.list_device);
	}

	private void setListener(){
		btnName.setOnClickListener(onClickListener);
		btnLoopCount.setOnClickListener(onClickListener);
		btnAddEvent.setOnClickListener(onClickListener);
		btnAddDevice.setOnClickListener(onClickListener);

		listViewEventHandler.setOnItemClickListener(eventOnItemClickListener);
		listViewEventHandler.setOnItemLongClickListener(eventOnItemLongClickListener);
		listViewDevice.setOnItemLongClickListener(deviceOnItemLongClickListener);
	}

	private void init(){
		setNameText();
		setCountText();
	}

	private String getDefaultName(){
		String name = "循环";
		boolean have;
		for(int i=1; i< 1000; i++){
			have = false;
			name = "循环" + i;
			for(Loop loop1 : LoopHandler.getIns().getListLoop()){
				if(loop1.getName().equals(name)){
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
		toolbar.setSubtitle(loop.getName());
		btnName.setText("名称:" + loop.getName());
	}

	private void setCountText(){
		if(loop.getLoopCount() == -1){
			btnLoopCount.setText("次数:" + "无限");
		}else{
			btnLoopCount.setText("次数:" + loop.getLoopCount());
		}
	}

	private void setListViewEventHandler(){
		adapterEventHandler = new AdapterEventHandler(this, loop.getListEventHandler());
		listViewEventHandler.setAdapter(adapterEventHandler);
	}

	private void setListViewDevice(){
		adapterAffectedDevice = new AdapterAffectedDevice(this, loop.getTrigger().getListLinkedDevice(), false);
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
				case R.id.btn_loop_count:
					showLoopCountDialog();
					break;
				case R.id.btn_add_event:
					if(ADD){
						LoopHandler.getIns().add(loop);
						LoopHandler.getIns().setSelectedLoop(loop);
					}
					EventHandlerActivity.ADD = true;
					startActivity(new Intent(ChildLoopActivity.this, EventHandlerActivity.class));
					break;
				case R.id.btn_add_device:
					sceneActHelper.showElectricalDialog(ChildLoopActivity.this,
							loop.getTrigger(), handler);
					//showElectricalDialog();
					break;
			}
		}
	};

	//条件列表点击事件
	private AdapterView.OnItemClickListener eventOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			EventHandlerActivity.ADD = false;
			loop.setSelectedEventHandler(loop.getListEventHandler().get(position));
			startActivity(new Intent(ChildLoopActivity.this, EventHandlerActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			loop.setSelectedEventHandler(loop.getListEventHandler().get(position));
			showElectricalPopUp(view, 0);
			return true;
		}
	};

	private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			loop.setSelectedDevice(loop.getTrigger().getListLinkedDevice().get(position));
			showElectricalPopUp(view, 1);
			return true;
		}
	};

	/**
	 * 名称对话框
	 */
	private void showNameDialog() {
		final EditText editHour = new EditText(this);
		editHour.setText(String.valueOf(loop.getName()));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(editHour)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String strName = String.valueOf(editHour.getText());
								loop.setName(strName);
								setNameText();
							}
						})
				.setNegativeButton(
						Main3Activity.strCancel,
						null).create().show();

	}

	/**
	 * 次数对话框
	 */
	private void showLoopCountDialog() {
		View convertView = this.getLayoutInflater().inflate(
				R.layout.dialog_loop_count, null);
		final EditText editLoopCount = (EditText) convertView
				.findViewById(R.id.edit_loop_count);
		final CheckBox checkBoxLoopInfinite = (CheckBox)convertView
				.findViewById(R.id.check_loop_infinite);
		if(loop.getLoopCount() == -1){
			editLoopCount.setEnabled(false);
			checkBoxLoopInfinite.setChecked(true);
		}else{
			editLoopCount.setText(String.valueOf(loop.getLoopCount()));
			checkBoxLoopInfinite.setChecked(false);
		}
		checkBoxLoopInfinite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					editLoopCount.setEnabled(false);
					loop.setLoopCount(-1);
				}else{
					editLoopCount.setEnabled(true);
				}
			}
		});

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(convertView)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								if(!checkBoxLoopInfinite.isChecked()){
									String strHour = String.valueOf(editLoopCount.getText());
									try{
										loop.setLoopCount(Integer.parseInt(strHour));
									}catch (Exception e){
										e.printStackTrace();
										Snackbar.make(editLoopCount, "格式错误", Snackbar.LENGTH_SHORT).show();
									}
								}
								setCountText();
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
					loop.getListEventHandler().remove(loop.getSelectedEventHandler());
					adapterEventHandler.notifyDataSetChanged();
				}else if(which == 1){
					loop.getTrigger().removeLinkedDevice(loop.getSelectedDevice());
					adapterAffectedDevice.notifyDataSetChanged();
				}
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<ChildLoopActivity> mActivity;

		MyHandler(ChildLoopActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			final ChildLoopActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_EVENT_HANDLER_LIST:
					theActivity.adapterEventHandler.notifyDataSetChanged();
					break;
				case ChildChainActivity.REFRESH_DEVICE_LIST :
					theActivity.setListViewDevice();
					break;
			}

		}
	};

}
