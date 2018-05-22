package test.lygzb.com.pressure.application;

import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.device.DefaultController;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.electrical.Electrical;
import lygzb.zsmarthome.net.NetHelper;
import test.lygzb.com.pressure.homehelper.MySendMessageThread;
import test.lygzb.com.pressure.homehelper.MySendResult;
import test.lygzb.com.pressure.main.ClimateFragment;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.network.WebClient;

/**
 * Created by Administrator on 2016/3/13.
 */
public class SendMsgHelper {

	private static SendDeviceThread sendDeviceThread;

	public static void sendMessage(Device device){
		sendMessage(device, false);
	}

	public static void sendMessage(String message){
		if(null != message) {
			NetHelper.getIns().send(message);
		}
	}

	public static void sendMessage(Device device, boolean repeat){
//		if(device.getNetPot().isConnected()) {
//			sendMessageLocal(device, repeat);
//		}else{
//			sendMessageWeb(device, repeat);
//		}
		if(device.getDeviceModel() == EDeviceModel.LOCAL) {
			sendMessageLocal(device, repeat);
		}else{
			WebClient.getInstance().sendMsg(device.createFinalOrder(device.getOrder()));
		}
	}

	public static void sendMessageLocal(Device device){
		sendMessageLocal(device, true);
	}

	private static void sendMessageLocal(Device device, boolean repeat){
//		MySendResult mySendResult = new MySendResult();
//		device.sendMessage(repeat, mySendResult);
		device.sendMessage(false);
		//seekControlDeviceState(device);
	}

	public static void sendMessageWeb(Device device){
		//sendMessageWeb(device, true);
	}

	public static void sendMessageWeb(Device device, boolean repeat){
		if(!device.isSending()) {
			device.setSending(true);
			device.setReceived(false);
			MySendMessageThread smt = new MySendMessageThread(device, new MySendResult());
			smt.start();
		}
	}

//	public static void sendMessage(List<Device> listDevice){
//		sendMessage(listDevice, true);
//	}

//	public static void sendMessage(List<Device> listDevice, boolean repeat){
//		if(null == listDevice || listDevice.isEmpty()){
//			return;
//		}
//		SendDeviceThread sendT = new SendDeviceThread(listDevice, repeat);
//		sendT.start();
//
////		if(null != sendDeviceThread && sendDeviceThread.isAlive()){
////			return;
////		}else{
////			sendDeviceThread = new SendDeviceThread(listDevice, repeat);
////			sendDeviceThread.start();
////		}
//	}

	public static void refreshState(boolean all){
		Log.e("SendMsgHelper", "refreshState()");
		if(null != UserHelper.getHomeMaster() && UserHelper.getHomeMaster().getHouseKeeper() != null) {
			for (Device device : UserHelper.getHomeMaster().getHouseKeeper().getListDevice()) {
				if(!device.isNormal()){
					WebClient.getInstance().sendMessage("$I" + device.getCoding() + ":24#00");
					continue;
				}
				if (device instanceof DefaultController) {
					device.turnQueryStatus();
					sendMessage(device.createFinalOrder(device.getOrder()));
					seekControlDeviceState(device);
					//SendMsgHelper.sendMessage(device);
					DefaultController dc = (DefaultController)device;
					if(all) {
						for (Electrical ele : dc.getListElectrical()) {
							String state = "$G" + dc.getCoding() + ":" + ele.getGear() + ele.getNum() + "#00";
							WebClient.getInstance().sendMsg(state);
						}
					}
				}else if(device instanceof ClimateDevice){
					ClimateDevice cd = (ClimateDevice)device;
					cd.turnQueryOrder();
					sendMessage(cd.createFinalOrder(cd.getQueryOrder()));
					seekControlDeviceState(device);
				}
				try {
					Thread.sleep(300);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			//sendMessage(listDevice, false);
		}
	}

	private static void seekControlDeviceState(Device device){
		boolean isNormalBefore = device.isNormal();
		device.setSendCount(device.getSendCount() + 1);
		boolean isNormalAfter = device.isNormal();

		if(isNormalBefore != isNormalAfter){
			if(device instanceof DefaultController){
				DefaultController dc = (DefaultController)device;
				for(Electrical ele : dc.getListElectrical()){
					ele.setNormal(isNormalAfter);
				}
			}
			if(null != ElectricalCtrlFragment.handler){
				Message msg = Message.obtain();
				msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
				ElectricalCtrlFragment.handler.sendMessage(msg);
			}
			if(null != ClimateFragment.handler){
				Message msg = Message.obtain();
				msg.arg1 = ClimateFragment.REFRESH_VALUE;
				ClimateFragment.handler.sendMessage(msg);
			}
		}
	}

	static class SendDeviceThread extends Thread{
		private List<Device> listDevice;
		private boolean repeat = false;

		public SendDeviceThread(List<Device> list){
			listDevice = list;
		}

		public SendDeviceThread(List<Device> list, boolean repeat){
			listDevice = list;
			this.repeat = repeat;
		}
		@Override
		public void run() {
			super.run();
			if(null == listDevice || listDevice.isEmpty()){
				return;
			}
			try {
				for (Device dev : listDevice) {
					sendMessage(dev, repeat);
					//WebClient.getInstance().sendMessage("$" + dev.getName() + dev.getOrder() + "#00 chain");
					try {
						sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
