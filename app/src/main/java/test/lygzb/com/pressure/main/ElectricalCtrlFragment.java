package test.lygzb.com.pressure.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterElectrical;
import test.lygzb.com.pressure.application.SendMsgHelper;

public class ElectricalCtrlFragment extends Fragment {

	private static final String ARG_PARAM1 = "param1";
	public static final int REFRESH_ELE_STATE = 1;
	public static final int REFRESH_ELE = 2;
	public static final int REFRESH_SORT= 3;
	public static final int SERVER_STATE= 4;
    public static final int SHOW_ALERT_DIALOG= 6;

	public static MyHandler handler;

	private AdapterElectrical adapterElectrical;
	private ListView listViewElectrical;

    //显示连接服务器状态
	private TextView textServerState;

	public ElectricalCtrlFragment() {
	}

	public static ElectricalCtrlFragment newInstance(int sectionNumber2) {
		ElectricalCtrlFragment fragment = new ElectricalCtrlFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, sectionNumber2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_electrical_ctrl, container, false);
		handler = new MyHandler(this);
		textServerState = (TextView)view.findViewById(R.id.text_server_state);
		listViewElectrical = (ListView)view.findViewById(R.id.listview_electrical);
//		listViewElectrical.setOnItemClickListener(setOnEleItemClickListener);
		listViewElectrical.setOnItemLongClickListener(onItemLongClickListener);
		setGridViewElectrical();
		return view;
	}

	public void setGridViewElectrical() {
		if(null != UserHelper.getHomeMaster()) {
			List<Device> listDev = UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice();
			Collections.sort(listDev);
			adapterElectrical = new AdapterElectrical(this.getContext(), listDev);
			listViewElectrical.setAdapter(adapterElectrical);

			for(Device device : listDev){
				device.addStateChangedListener(onStateChangedListener);
			}
		}
	}

	private Device.OnStateChangedListener onStateChangedListener = new Device.OnStateChangedListener() {
		@Override
		public void onStateChanged(Device device, String s) {
			refreshAdapter(device, AdapterElectrical.STATE);
		}

		@Override
		public void onGateChanged(Device device, int i) {
			refreshAdapter(device, AdapterElectrical.AUTO);
		}

		@Override
		public void onCtrlModelChanged(Device device, EDeviceModel eDeviceModel) {
			refreshAdapter(device, AdapterElectrical.CTRL_MODEL);
		}
	};

	private void refreshAdapter(Device device, int which){
		if(null != AdapterElectrical.handler) {
			Message msg = Message.obtain();
			msg.obj = device;
			msg.arg1 = which;
			AdapterElectrical.handler.sendMessage(msg);
		}
	}

	private AdapterView.OnItemClickListener setOnEleItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Device dev = UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice().get(position);
			UserHelper.getHomeMaster().getHouseKeeper().setSelectedDevice(dev);
			UserHelper.getHomeMaster().getHouseKeeper().getSelectedDevice().turnState();
			SendMsgHelper.sendMessage(UserHelper.getHomeMaster().getHouseKeeper().getSelectedDevice());
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
			//电器列表长按
            Device dev = UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice().get(i);
            if(dev.isNormal()) {
//                if(dev instanceof Electrical){
//                    UserHelper.getHomeMaster().getHouseKeeper().setSelectedDevice(((Electrical)dev).getController());
//                }else {
//                    UserHelper.getHomeMaster().getHouseKeeper().setSelectedDevice(dev);
//                }
//                showDevicePopUp(view, UserHelper.getHomeMaster().getHouseKeeper().getSelectedDevice());
            }else{
                Snackbar.make(view, "设备异常", Snackbar.LENGTH_SHORT).show();
            }
			return false;
		}
	};

	public static class MyHandler extends Handler {
		WeakReference<ElectricalCtrlFragment> mActivity;

		MyHandler(ElectricalCtrlFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			ElectricalCtrlFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_ELE_STATE:
					//theActivity.adapterElectrical.notifyDataSetChanged();
					break;
				case REFRESH_ELE :
					theActivity.setGridViewElectrical();
					break;
				case REFRESH_SORT :
					Collections.sort(UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice());
					theActivity.adapterElectrical.notifyDataSetChanged();
					break;
				case SERVER_STATE :
					String str = (String) msg.obj;
					if(str.equals("OK")){
						theActivity.textServerState.setVisibility(View.GONE);
					}else{
						theActivity.textServerState.setVisibility(View.VISIBLE);
						theActivity.textServerState.setText(str);
					}
					break;
                case SHOW_ALERT_DIALOG:
                    theActivity.showAlertDialog(msg.obj.toString());
                    break;
			}
		}
	}

    private void showAlertDialog(String text){
        new AlertDialog.Builder(this.getContext()).setTitle("提示")
                .setMessage(text)
                .setPositiveButton("确定", null)
                .show();
    }
}
