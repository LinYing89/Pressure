package test.lygzb.com.pressure.main;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.Butler;
import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.address.Address;
import lygzb.zsmarthome.device.Device;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterSortElectrical;
import test.lygzb.com.pressure.application.BackListener;

public class SortActivity extends AppCompatActivity {

	private ListView listViewElectrical;

	private List<Device> listDevice;

	private AdapterSortElectrical adapterSortElectrical;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sort);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		listDevice = new ArrayList<>();
		listDevice.addAll(UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice());
		Collections.sort(UserHelper.getHomeMaster().getClimateButler().getListChildDevice());
		listDevice.addAll(UserHelper.getHomeMaster().getClimateButler().getListChildDevice());
		for(int i = 0; i < listDevice.size(); i++){
			listDevice.get(i).setIndex(i);
		}
		Collections.sort(listDevice);
		findViews();
		setListener();
		setListViewElectrical();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sort, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if(id == R.id.action_up){
			move(0);
		}else if(id == R.id.action_down){
			move(1);
		}

		return super.onOptionsItemSelected(item);
	}

	private void findViews(){
		listViewElectrical = (ListView)findViewById(R.id.list_electrical);
	}

	private void setListener(){
		listViewElectrical.setOnItemClickListener(electricalOnItemClickListener);
	}

	private void setListViewElectrical(){
		Collections.sort(listDevice);
		adapterSortElectrical = new AdapterSortElectrical(this, listDevice);
		listViewElectrical.setAdapter(adapterSortElectrical);
	}

	private void move(int forward){
		if(listDevice == null || listDevice.isEmpty()){
			return;
		}

		if(null == adapterSortElectrical || null == adapterSortElectrical.selectedDevice){
			return;
		}

		for(int i = 0; i < listDevice.size(); i++){
			listDevice.get(i).setIndex(i);
		}
		int sIndex = adapterSortElectrical.selectedDevice.getIndex();

		if(forward == 0){
			moveUp(sIndex);
		}else{
			moveDown(sIndex);
		}

		Collections.sort(listDevice);
		adapterSortElectrical.notifyDataSetChanged();
	}

	private void moveUp(int sIndex){
		if(sIndex == 0){
			Snackbar.make(listViewElectrical, "已经在最前面了", Snackbar.LENGTH_SHORT).show();
			return;
		}
		adapterSortElectrical.selectedDevice.setIndex(sIndex - 1);
		Device upDevice = listDevice.get(sIndex - 1);
		upDevice.setIndex(upDevice.getIndex() + 1);
	}

	private void moveDown(int sIndex){
		if(sIndex == listDevice.size() - 1){
			Snackbar.make(listViewElectrical, "已经在最后面了", Snackbar.LENGTH_SHORT).show();
			return;
		}
		adapterSortElectrical.selectedDevice.setIndex(sIndex + 1);
		Device dwonDevice = listDevice.get(sIndex + 1);
		dwonDevice.setIndex(dwonDevice.getIndex() - 1);
	}

	private AdapterView.OnItemClickListener electricalOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			adapterSortElectrical.selectedDevice = listDevice.get(arg2);
			adapterSortElectrical.notifyDataSetChanged();
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		for(Address address : UserHelper.getHomeMaster().getHouseKeeper().getListAddresses()) {
			Collections.sort(address.getListCtrlableDevice());
		}
		for(Address address : UserHelper.getHomeMaster().getClimateButler().getListAddress()) {
			Collections.sort(address.getListCtrlableDevice());
		}
		for(Butler butler : UserHelper.getHomeMaster().getSimpleButler().getListButler()) {
			Collections.sort(butler.getListDevice());
		}

		FileHelper.saveDeviceXml(UserHelper.getHomeMaster());
		if(null != ElectricalCtrlFragment.handler) {
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.REFRESH_SORT;
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
		if(null != ClimateFragment.handler) {
			Message msg = Message.obtain();
			msg.arg1 = ClimateFragment.REFRESH_SORT;
			ClimateFragment.handler.sendMessage(msg);
		}
	}

}
