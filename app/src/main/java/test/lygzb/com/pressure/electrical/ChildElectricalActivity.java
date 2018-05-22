package test.lygzb.com.pressure.electrical;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.ZEncoding;
import lygzb.zsmarthome.device.Controller;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.MainSecondTitleAdapter;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;

public class ChildElectricalActivity extends AppCompatActivity {

	public static int REFRESH_ELE_LIST = 3;

	public static Controller controller;
	public static MyHandler handler;

	private ListView listviewElectrical;
	private MainSecondTitleAdapter adapterEle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_electrical);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		handler = new MyHandler(this);
		listviewElectrical = (ListView)findViewById(R.id.list_electrical);
		listviewElectrical.setOnItemLongClickListener(remoteOnItemLongClickListener);
		setChildDeviceList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_child_electrical, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_add_electrical){
			startActivity(new Intent(ChildElectricalActivity.this, ChooseElectricalActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		controller = null;
		super.onDestroy();
	}

	private void setChildDeviceList() {
		List<ZEncoding> list = new ArrayList<>();
		for(ZEncoding ze : controller.getListElectrical()){
			list.add(ze);
		}
		adapterEle = new MainSecondTitleAdapter(this,list);
		listviewElectrical.setAdapter(adapterEle);
	}

	private AdapterView.OnItemLongClickListener remoteOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
			controller.setSelectedElectrical(arg2);
				showElectricalPopUp(arg1);
			return true;
		}
	};

	public void showElectricalPopUp(View v) {
		View layout = this.getLayoutInflater()
				.inflate(R.layout.pop_device_long_click, null);
		Button layoutRename = (Button) layout
				.findViewById(R.id.text_rename);
		Button btnAlias = (Button) layout
				.findViewById(R.id.text_alias);
		Button layoutDelete = (Button) layout
				.findViewById(R.id.text_delete);

		final PopupWindow popupWindow = new PopupWindow(layout, Constant.displayWidth,
				Constant.getEleHeight());

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
		layoutRename.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showRenameDialog(controller.getSelectedElectrical());
			}
		});
		btnAlias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showAliasDialog(controller.getSelectedElectrical());
			}
		});
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				deleteChildDeviceOperate();
			}
		});
	}

	private void deleteChildDeviceOperate() {
		Device device = UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().getSelectedDevice();
		if(device instanceof Controller) {
			Controller ctrl = (Controller) device;
			UserHelper.getHomeMaster().removeDevice(ctrl.getSelectedElectrical(), UserHelper.getUser());
			//adapterEle.notifyDataSetChanged();
		}
	}

	private void showRenameDialog(final ZEncoding ecoding) {
		final EditText edit_newName = new EditText(ChildElectricalActivity.this);
		edit_newName.setText(ecoding.getName());
		new AlertDialog.Builder(ChildElectricalActivity.this)
				.setTitle(
						ChildElectricalActivity.this.getString(R.string.input_or_choose_name))
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
		final EditText edit_newName = new EditText(ChildElectricalActivity.this);
		edit_newName.setText(device.getAlias());
		new AlertDialog.Builder(ChildElectricalActivity.this)
				.setTitle("位号")
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
		if(ecoding instanceof Electrical){
			if (nameIsRepeat(value)) {
				duplicateNameDialog(Constant.getString(ChildElectricalActivity.this, R.string.name_repeat));
			} else {
				ecoding.setName(value);
				//adapterEle.notifyDataSetChanged();
			}
		}
	}
	private boolean nameIsRepeat(String name) {
		for (Device ele :UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().getListDevice()) {
			if (ele.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	private void duplicateNameDialog(String title) {
		new AlertDialog.Builder(ChildElectricalActivity.this)
				.setTitle(title)
				.setNegativeButton(
						Constant.getString(ChildElectricalActivity.this, R.string.cancel), null).create().show();
	}

	public static class MyHandler extends Handler {
		WeakReference<ChildElectricalActivity> mActivity;

		MyHandler(ChildElectricalActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ChildElectricalActivity theActivity = mActivity.get();
			if (msg.arg1 == REFRESH_ELE_LIST) {
				theActivity.setChildDeviceList();
				//theActivity.adapterEle.notifyDataSetChanged();
			}
		}
	}
}
