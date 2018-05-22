package test.lygzb.com.pressure.network;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.net.NetPot;
import lygzb.zsmarthome.net.NetPotHelper;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.NetPointAdapter;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.esptouch.EsptouchActivity;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;

public class NetActivity extends AppCompatActivity {

	private ListView listViewNet;

	public static MyHandler handler;

	private NetPointAdapter netAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		findViews();
		setListener();
		setNetList();
		handler = new MyHandler(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_net, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_net_add_input){
			showEditNetDialog(true);
		}else if(id == R.id.action_net_add_auto){
			NetActivity.this.startActivity(new Intent(NetActivity.this, EsptouchActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

	private void findViews(){
		listViewNet = (ListView)findViewById(R.id.list_net);
	}

	private void setListener(){
		listViewNet.setOnItemLongClickListener(netOnItemLongClickListener);
	}

	private AdapterView.OnItemLongClickListener netOnItemLongClickListener = new AdapterView.OnItemLongClickListener(){
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			showPopUp(view);
			return false;
		}
	};
	public void setNetList() {
		netAdapter = new NetPointAdapter(this);
		listViewNet.setAdapter(netAdapter);
	}

	private void showEditNetDialog(final boolean isAdd) {

		View convertView = this.getLayoutInflater().inflate(
				R.layout.netset_dialog, null);
		final EditText editName = (EditText) convertView
				.findViewById(R.id.edit_name);
		final EditText editIp = (EditText) convertView
				.findViewById(R.id.edit_ip);
		final EditText editPort = (EditText) convertView
				.findViewById(R.id.edit_port);

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(false);
		dialog.setView(convertView)
				.setPositiveButton(
						Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String name = String.valueOf(editName.getText());
								String ip = String.valueOf(editIp.getText());
								String port = String.valueOf(editPort.getText());
							}
						})
				.setNegativeButton(
						Main3Activity.strCancel,
						null).create().show();

	}


	public static class MyHandler extends Handler {
		WeakReference<NetActivity> mActivity;

		MyHandler(NetActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				NetActivity theActivity = mActivity.get();
				if (msg.arg1 == 1) {
					if (null != theActivity.netAdapter) {
						theActivity.netAdapter.notifyDataSetChanged();
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler = null;
	}

	private void showPopUp(View v) {
		View layout = this.getLayoutInflater()
				.inflate(R.layout.layout_net_menu, null);
		TextView textEdit = (TextView) layout.findViewById(R.id.text_edit);
		TextView textDelete = (TextView) layout.findViewById(R.id.text_delete);

		final PopupWindow popupWindow = new PopupWindow(layout, Constant.getEleItemHeight() * 2,
				Constant.getEleItemHeight() * 2);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAsDropDown(v);
		// popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
		// location[1]-popupWindow.getHeight());
		textEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showEditNetDialog(false);
			}
		});
		textDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
	}
}
