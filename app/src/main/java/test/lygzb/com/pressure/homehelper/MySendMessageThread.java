package test.lygzb.com.pressure.homehelper;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.SendMessageThread;
import lygzb.zsmarthome.device.SendResult;
import test.lygzb.com.pressure.network.WebClient;

/**
 * Created by Administrator on 2016/5/27.
 */
public class MySendMessageThread extends SendMessageThread {

	public MySendMessageThread(Device device){
		super(device);
	}

	public MySendMessageThread(Device device, SendResult sendResult){
		this(device);
		setSendResult(sendResult);
	}

	@Override
	public void send(String order) {
		WebClient.getInstance().sendMessage(order);
	}
}
