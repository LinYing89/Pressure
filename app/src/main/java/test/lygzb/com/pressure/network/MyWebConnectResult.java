package test.lygzb.com.pressure.network;

import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;

import lygzb.zsmarthome.net.ConnectResult;
import lygzb.zsmarthome.net.MessageAnalysiser;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;

/**
 * Created by Administrator on 2016/3/13.
 */
public class MyWebConnectResult extends ConnectResult {

	public MyWebConnectResult(MessageAnalysiser messageAnalysis) {
		super(messageAnalysis);
	}

	@Override
	public void connectFall() {
		//Log.e("MyWebConnectResult", "connectFall");
		//Constant.showToast("web server connect fall");
		if(null != ElectricalCtrlFragment.handler) {
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.SERVER_STATE;
			msg.obj = "服务器连接失败";
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
	}

	@Override
	public void connected() {
		//Log.e("MyWebConnectResult", "connected");
		//Constant.showToast("web server connect success");
		if(null != ElectricalCtrlFragment.handler) {
			Message msg = Message.obtain();
			msg.arg1 = ElectricalCtrlFragment.SERVER_STATE;
			msg.obj = "OK";
			ElectricalCtrlFragment.handler.sendMessage(msg);
		}
		try {
			WebClient.getInstance().setDos(new DataOutputStream(new BufferedOutputStream(getNetPot().getSocket().getOutputStream())));
			WebClient.getInstance().startSendUserMsgThread();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
