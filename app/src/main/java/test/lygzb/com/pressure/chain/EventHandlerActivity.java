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
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterEvent;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.loop.ChildLoopActivity;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventHandler;
import test.lygzb.com.pressure.loop.LoopHandler;
import test.lygzb.com.pressure.main.Main3Activity;

public class EventHandlerActivity extends AppCompatActivity {

	public static MyHandler handler;
	private EventHandler eventHandler;
	public static boolean ADD;

	private Toolbar toolbar;
	private Button btnName;
	private Button btnAdd;
	private ListView listViewEvent;
	private AdapterEvent adapterEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_handler);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		findViews();
		setListener();
		if(ADD){
			eventHandler = new EventHandler();
			eventHandler.setName(getDefaultName());
		}else{
			eventHandler = LoopHandler.getIns().getSelectedLoop().getSelectedEventHandler();
			if(null == eventHandler){
				finish();
				return;
			}
		}
		setNameText();
		setListViewEvent();

		handler = new MyHandler(this);
	}

	@Override
	protected void onDestroy() {
		if(ADD){
			LoopHandler.getIns().getSelectedLoop().addEventHandler(eventHandler);
			if(null != ChildLoopActivity.handler){
				Message message = Message.obtain();
				message.arg1 = ChildLoopActivity.REFRESH_EVENT_HANDLER_LIST;
				ChildLoopActivity.handler.sendMessage(message);
			}
		}
		handler = null;
		super.onDestroy();
	}

	private void findViews(){
		btnName = (Button)findViewById(R.id.btn_name);
		btnAdd = (Button)findViewById(R.id.btn_add);
		listViewEvent = (ListView)findViewById(R.id.list_event);
	}

	private void setListener(){
		btnName.setOnClickListener(onClickListener);
		btnAdd.setOnClickListener(onClickListener);
		listViewEvent.setOnItemClickListener(onItemClickListener);
		listViewEvent.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListViewEvent(){
		adapterEvent = new AdapterEvent(this, eventHandler.getListEvent());
		listViewEvent.setAdapter(adapterEvent);
	}

	private String getDefaultName(){
		String name = "条件";
		boolean have;
		for(int i=1; i< 1000; i++){
			have = false;
			name = "条件" + i;
			for(EventHandler eventHandler1 : LoopHandler.getIns().getSelectedLoop().getListEventHandler()){
				if(eventHandler1.getName().equals(name)){
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
		toolbar.setSubtitle(eventHandler.getName());
		btnName.setText("名称:" + eventHandler.getName());
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btn_name:
					showNameDialog();
					break;
				case R.id.btn_add:
					if(ADD){
						LoopHandler.getIns().getSelectedLoop().addEventHandler(eventHandler);
						LoopHandler.getIns().getSelectedLoop().setSelectedEventHandler(eventHandler);
					}
					EventActivity.handler = handler;
					startActivity(new Intent(EventHandlerActivity.this, EventActivity.class));
					break;
			}
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			eventHandler.setSelectedEvent(eventHandler.getListEvent().get(position));
			EventActivity.event = eventHandler.getSelectedEvent();
			EventActivity.handler = handler;
			startActivity(new Intent(EventHandlerActivity.this, EventActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			eventHandler.setSelectedEvent(eventHandler.getListEvent().get(position));
			showElectricalPopUp(view);
			return true;
		}
	};

	/**
	 * 名称对话框
	 */
	private void showNameDialog() {
		final EditText editHour = new EditText(this);
		editHour.setText(String.valueOf(eventHandler.getName()));
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(editHour)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String strName = String.valueOf(editHour.getText());
								eventHandler.setName(strName);
								setNameText();
							}
						})
				.setNegativeButton(
						Main3Activity.strCancel,
						null).create().show();

	}

	public void showElectricalPopUp(View v) {
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
				eventHandler.getListEvent().remove(eventHandler.getSelectedEvent());
				adapterEvent.notifyDataSetChanged();
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<EventHandlerActivity> mActivity;

		MyHandler(EventHandlerActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final EventHandlerActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case ChildChainActivity.REFRESH_EVENT_HANDLER_LIST:
					theActivity.eventHandler.add((Event) msg.obj);
					theActivity.adapterEvent.notifyDataSetChanged();
					break;
			}

		}
	};

}
