package test.lygzb.com.pressure.network;

import android.os.Message;

import lygzb.zsmarthome.device.DefaultController;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.net.ConnectResult;
import lygzb.zsmarthome.net.MessageAnalysiser;
import lygzb.zsmarthome.net.NetPot;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * Created by Administrator on 2016/3/10.
 */
public class MyConnectResult extends ConnectResult {

	public MyConnectResult(MessageAnalysiser messageAnalysis) {
		super(messageAnalysis);
	}

	@Override
	public void connectFall() {
		//Log.e("MyConnectResult", getNetPot().toString() + "connectFall");
//		Snackbar.make(null, getNetPot() + " connectFall", Snackbar.LENGTH_LONG)
//				.setAction("Action", null).show();
	}

	@Override
	public void connected() {
		//Log.e("MyConnectResult", "connected");
		//startReceiver();
		updateNetState();
		refreshState(getNetPot());
	}

	private void updateNetState(){
		if(null != NetActivity.handler){
			Message msg = Message.obtain();
			msg.arg1 = 1;
			NetActivity.handler.sendMessage(msg);
		}
	}

	private void refreshState(NetPot np){
		if(null != UserHelper.getHomeMaster() && UserHelper.getHomeMaster().getHouseKeeper() != null) {
			for (Device device : UserHelper.getHomeMaster().getHouseKeeper().getListDevice()) {
				if (device instanceof DefaultController) {
				}
			}
		}
	}
}
