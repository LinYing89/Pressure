package test.lygzb.com.pressure.homehelper;

import android.os.Message;

import lygzb.zsmarthome.device.Controller;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.SendResult;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.network.WebClient;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MySendResult extends SendResult {

	@Override
	public void sendStart() {
		//refreshState();
	}

	@Override
	public void netErr(Device device) {
//		if(!WebClient.getInstance().getNetPot().isConnected()) {
//			sendFail(device);
//		}
	}

	@Override
	public void sendFail(Device device){
//		device.setReceived(false);
//		if(device instanceof Controller){
//			Controller controller = (Controller)device;
//			for(Electrical ele : controller.getListElectrical()){
//				ele.setReceived(false);
//			}
//		}
//		refreshState();
	}

	private void refreshState(){
		if(null != ElectricalCtrlFragment.handler){
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
	}
}
