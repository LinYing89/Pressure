package test.lygzb.com.pressure.electrical;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.DefaultConfig;
import lygzb.zsmarthome.device.Controller;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.electrical.Electrical;
import lygzb.zsmarthome.device.electrical.ElectricalAssistent;
import lygzb.zsmarthome.device.electrical.ElectricalCodes;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterAddElectricalGrid;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.main.UserHelper;

public class ChooseElectricalActivity extends AppCompatActivity {

	private GridView girdElectricals;
	private AdapterAddElectricalGrid adapter;
	private List<String> listCodes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_electrical);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		findViews();
		setListener();
		setGridEles();
	}
	private void findViews() {
		girdElectricals = (GridView) findViewById(R.id.eles_gridview);
	}

	private void setListener() {
		girdElectricals.setOnItemClickListener(gridOnItemClickListener);
	}

	private void setGridEles(){
		listCodes = new ArrayList<>(DefaultConfig.getInstance().getMapElectricalCode().keySet());
		Collections.sort(listCodes);
		adapter = new AdapterAddElectricalGrid(this, listCodes);
		girdElectricals.setAdapter(adapter);
	}

	private AdapterView.OnItemClickListener gridOnItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			String key = listCodes.get(arg2);
			showDeviceNumDialog(key, DefaultConfig.getInstance().getMapElectricalCode().get(key));
		}
	};

	private void showDeviceNumDialog(final String eleCode,
									 final String eleCodeCn) {
//		final String[] array_lamp;
//		if (eleCode.equals(ElectricalCodes.LAMP))
//			array_lamp = this.getResources().getStringArray(
//					R.array.default_lamp);
//		else if (eleCode.equals(ElectricalCodes.CURTAIN))
//			array_lamp = this.getResources().getStringArray(
//					R.array.default_curtain);
//		else
//			array_lamp = new String[] { eleCodeCn };
		final EditText edit_newName = new EditText(this);
//		edit_newName.setText(array_lamp[0]);
		new AlertDialog.Builder(this)
				.setTitle("输入名称")
				.setView(edit_newName)
//				.setSingleChoiceItems(array_lamp, 0,
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//												int which) {
//								edit_newName.setText(array_lamp[which]);
//							}
//						})
				.setPositiveButton(Constant.getString(ChooseElectricalActivity.this, R.string.ensure),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String value = edit_newName.getText()
										.toString();
								if (nameIsRepeat(value)) {
									Toast.makeText(ChooseElectricalActivity.this, Constant.getString(ChooseElectricalActivity.this, R.string.name_repeat), Toast.LENGTH_LONG).show();
									showDeviceNumDialog(eleCode, eleCodeCn);
								} else {
									addLampOperate(value, eleCode);
								}
							}
						}).setNegativeButton(Constant.getString(ChooseElectricalActivity.this, R.string.cancel), null).create().show();
	}

	private boolean nameIsRepeat(String name) {
		for (Device device : UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().getListDevice()) {
			if(name.equals(device.getName())){
				return true;
			}
			if(device instanceof Controller){
				Controller controller = (Controller)device;
				for(Electrical electrical : controller.getListElectrical()){
					if(electrical.getName().equals(name)){
						return true;
					}
				}
			}
		}
		return false;
	}

	/** add lamp device operate */
	private void addLampOperate(String value, String eleCode) {
		Controller eh = (Controller)UserHelper.getHomeMaster().getHouseKeeper().getSelectedAddress().getSelectedDevice();
		Electrical ele = ElectricalAssistent.getElectrical(UserHelper.getUser(),eh, eleCode);
		ele.setName(value);
		if (!eh.getListElectrical().contains(ele)) {
			UserHelper.getHomeMaster().addDevice(ele);
			eh.add(ele);
			Message msg = Message.obtain();
			msg.arg1 = ChildElectricalActivity.REFRESH_ELE_LIST;
			ChildElectricalActivity.handler.sendMessage(msg);
			finish();
		}
	}

}
