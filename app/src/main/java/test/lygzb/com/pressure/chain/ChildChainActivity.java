package test.lygzb.com.pressure.chain;

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
import test.lygzb.com.pressure.adapter.AdapterEvent;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.main.Main3Activity;

public class ChildChainActivity extends AppCompatActivity {

	public static MyHandler handler;
	public static final int REFRESH_DEVICE_LIST = 1;
	public static final int REFRESH_EVENT_HANDLER_LIST = 2;

	private Chain chain;
	public static boolean ADD;

	private Toolbar toolbar;
	private Button btnName;
	private ImageButton btnAddEvent;
	private ImageButton btnAddDevice;
	private ListView listViewEvent;
	private ListView listViewDevice;

	private AdapterEvent adapterEvent;
	private AdapterAffectedDevice adapterAffectedDevice;
	private SceneActHelper sceneActHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_chain);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		sceneActHelper = new SceneActHelper();
		findViews();
		setListener();
		if(ADD){
			chain = new Chain();
			chain.setName(getDefaultName());
		}else{
			chain = ChainHandler.getIns().getSelectedChain();
			if(chain == null){
				finish();
				return;
			}
		}
		init();
		setListViewEvent();
		setListViewDevice();
		handler = new MyHandler(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ADD){
			ChainHandler.getIns().add(chain);
			if(null != ChainFragment.handler){
				Message message = Message.obtain();
				message.arg1 = LoopFragment.REFRESH_LIST;
				ChainFragment.handler.sendMessage(message);
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
		String name = "连锁";
		boolean have;
		for(int i=1; i< 1000; i++){
			have = false;
			name = "连锁" + i;
			for(Chain chain : ChainHandler.getIns().getListChain()){
				if(chain.getName().equals(name)){
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
		toolbar.setSubtitle(chain.getName());
		btnName.setText("名称:" + chain.getName());
	}

	private void setListViewEvent(){
		adapterEvent = new AdapterEvent(this, chain.getListEvent());
		listViewEvent.setAdapter(adapterEvent);
	}

	private void setListViewDevice(){
		adapterAffectedDevice = new AdapterAffectedDevice(this, chain.getTrigger().getListLinkedDevice(), true);
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
						ChainHandler.getIns().add(chain);
						ChainHandler.getIns().setSelectedChain(chain);
					}
					EventActivity.handler = handler;
					startActivity(new Intent(ChildChainActivity.this, EventActivity.class));
					break;
				case R.id.btn_add_device:
					sceneActHelper.showElectricalDialog(ChildChainActivity.this,
							chain.getTrigger(), handler);
					break;
			}
		}
	};

	//条件列表点击事件
	private AdapterView.OnItemClickListener eventOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			chain.setSelectedEvent(chain.getListEvent().get(position));
			EventActivity.event = chain.getSelectedEvent();
			EventActivity.handler = handler;

			startActivity(new Intent(ChildChainActivity.this, EventActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			chain.setSelectedEvent(chain.getListEvent().get(position));
			showElectricalPopUp(view, 0);
			return true;
		}
	};

	private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			chain.setSelectedLinkDevice(chain.getTrigger().getListLinkedDevice().get(position));
			showElectricalPopUp(view, 1);
			return true;
		}
	};

	/**
	 * 名称对话框
	 */
	private void showNameDialog() {
		final EditText editHour = new EditText(this);
		editHour.setText(String.valueOf(chain.getName()));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(editHour)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String strName = String.valueOf(editHour.getText());
								chain.setName(strName);
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

		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				if(which == 0){
					chain.getListEvent().remove(chain.getSelectedEvent());
					adapterEvent.notifyDataSetChanged();
				}else if(which == 1){
					chain.getTrigger().removeLinkedDevice(chain.getSelectedLinkDevice());
					adapterAffectedDevice.notifyDataSetChanged();
				}
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<ChildChainActivity> mActivity;

		MyHandler(ChildChainActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChildChainActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_EVENT_HANDLER_LIST:
					ChainHandler.getIns().getSelectedChain().addEvent((Event) msg.obj);
					theActivity.adapterEvent.notifyDataSetChanged();
					break;
				case REFRESH_DEVICE_LIST :
					theActivity.adapterAffectedDevice.notifyDataSetChanged();
					break;
			}

		}
	}

}
