package test.lygzb.com.pressure.guaguamouth;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.GuaGuaMouth;
import lygzb.zsmarthome.device.LinkedGuaguaMouth;
import lygzb.zsmarthome.device.collector.SimpleTrigger;
import lygzb.zsmarthome.device.collector.Trigger;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.main.UserHelper;

public class AddGuguaMouthActivity extends AppCompatActivity {

	private LinkedGuaguaMouth linkedGuaguaMouth;

	private List<Device> listOtherDevice;

	public static boolean ADD;

	private Spinner spinnerDevices;
	private EditText etxtSpeakCount;
	private EditText etxtSpeakContent;
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_gugua_mouth);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if(actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		findViews();
		setListeners();

		//listOtherDevice = new SceneActHelper().getListOtherEle(GuaguaHandler.getIns().getSelectedGuagua().getTrigger());
		listOtherDevice = getListOtherGuanguanMouth(GuaguaHandler.getIns().getSelectedGuagua().getTrigger());
		if(listOtherDevice.isEmpty()){
			Toast.makeText(this, "无呱呱嘴设备", Toast.LENGTH_SHORT).show();
			btnOk.setEnabled(false);
			return;
		}
		if(!ADD) {
			linkedGuaguaMouth = (LinkedGuaguaMouth)GuaguaHandler.getIns().getSelectedGuagua().getSelectedLinkedDevice();
			listOtherDevice.add(linkedGuaguaMouth.getDevice());
			setSpinnerDevices();
			spinnerDevices.setSelection(listOtherDevice.size() - 1);
			etxtSpeakCount.setText(String.valueOf(linkedGuaguaMouth.getSpeakCount()));
			etxtSpeakContent.setText(linkedGuaguaMouth.getAction());
		}else{
			setSpinnerDevices();
			spinnerDevices.setSelection(0);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	}

	private void findViews(){
		spinnerDevices = (Spinner)findViewById(R.id.spinnerDevice);
		etxtSpeakCount = (EditText)findViewById(R.id.etxtSpeakCount);
		etxtSpeakContent = (EditText)findViewById(R.id.etxtSpeakContent);
		btnOk = (Button)findViewById(R.id.btnOk);
		btnCancel = (Button)findViewById(R.id.btnCancel);
	}

	private void setListeners(){
		spinnerDevices.setOnItemSelectedListener(onItemSelectedListener);
		btnOk.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
	}

	private void setSpinnerDevices(){
		String[] names =new String[listOtherDevice.size()];
		for (int i=0; i< listOtherDevice.size(); i++){
			Device device = listOtherDevice.get(i);
			names[i] = device.getName();
		}
		spinnerDevices.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,names));
	}

	/**
	 * 获取trigger中没有的呱呱嘴设备
	 * @param trigger
	 * @return
	 */
	public List<Device> getListOtherGuanguanMouth(Trigger trigger){
		List<Device> listEleAll = UserHelper.getHomeMaster().getHouseKeeper().getListDevice();
		List<Device> listOther = new ArrayList<>();
		List<Device> listEle = trigger.getListDevice();
		for (Device device : listEleAll) {
			if (device instanceof GuaGuaMouth && !listEle.contains(device)) {
				listOther.add(device);
			}
		}
		return listOther;
	}

	private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(null == linkedGuaguaMouth) {
				linkedGuaguaMouth = new LinkedGuaguaMouth();
			}
			linkedGuaguaMouth.setDevice(listOtherDevice.get(position));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btnOk:
					if(null == linkedGuaguaMouth) {
						linkedGuaguaMouth = new LinkedGuaguaMouth();
					}
					linkedGuaguaMouth.setDevice(listOtherDevice.get(spinnerDevices.getSelectedItemPosition()));
					int count = Integer.parseInt(etxtSpeakCount.getText().toString());
					linkedGuaguaMouth.setSpeakCount(count);
					String content = etxtSpeakContent.getText().toString();
					if(content.length() > 20){
						Toast.makeText(AddGuguaMouthActivity.this, "播报内容长度不能超过20", Toast.LENGTH_LONG).show();
						return;
					}else if(content.isEmpty()){
						Toast.makeText(AddGuguaMouthActivity.this, "播报内容不能为空", Toast.LENGTH_LONG).show();
						return;
					}
					linkedGuaguaMouth.setAction(content);
					if(ADD){
						GuaguaHandler.getIns().getSelectedGuagua().getTrigger().addLinkedDevice(linkedGuaguaMouth);
					}
					if(ChildGuaguaActivity.handler != null){
						Message message = Message.obtain();
						message.arg1 = ChildGuaguaActivity.REFRESH_DEVICE_LIST;
						ChildGuaguaActivity.handler.sendMessage(message);
					}
					finish();
					break;
				case R.id.btnCancel:
					finish();
					break;
			}
		}
	};
}
