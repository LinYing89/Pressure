package test.lygzb.com.pressure.network;

import android.os.Message;
import android.util.Log;

import lygzb.zsmarthome.User;
import lygzb.zsmarthome.device.Coordinator;
import lygzb.zsmarthome.device.DefaultController;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.StudyRemote;
import lygzb.zsmarthome.device.collector.AlarmDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.Pressure;
import lygzb.zsmarthome.device.electrical.Electrical;
import lygzb.zsmarthome.net.MessageAnalysiser;
import test.lygzb.com.pressure.main.ClimateFragment;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.systemset.SearchActivity;

/**
 * Created by Administrator on 2016/3/10.
 */
public class MyMessageAnalysiser extends MessageAnalysiser {
	private String og;

	public MyMessageAnalysiser(User user){
		super(user);
	}

//	@Override
//	public synchronized void analysisSingleMsg(String msg) {
//		Log.e("MyMessageAnalysiser rec", msg);
//		if(null == msg){
//            return;
//        }
//		super.analysisSingleMsg(msg);
//	}

	@Override
	public void addMsg(String msg) {
		if(Main3Activity.LOG_REC) {
			og = msg.replace('$', 'o');
			og = og.replace('#', '?');
			//Log.e("ADDMSG", "send to web og: " + "$og:" + msg.replace('$', 'o') + " , " + getQueueMsg().size() + "#00");
			WebClient.getInstance().sendMsg("$ogr:" + og + " , " + getQueueMsg().size() + "#00");
		}
		super.addMsg(msg);
		//Log.e("addMsg", "getQueueMsg" + msg + getQueueMsg().size());
	}

	@Override
	public void coordinatorFeedback(Coordinator coordinator, String msg) {
		//Log.e("MyMessageAnalysiser", "coordinatorFeedback" + coordinator);
	}

	@Override
	public void simpleDeviceFeedback(Device device, String feedbackMsg) {
		//Log.e("MyMessageAnalysiser", "simpleDeviceFeedback" + device + "back:" + feedbackMsg);
		WebClient.getInstance().sendMessage("$" + feedbackMsg);
//		if(ElectricalCtrlFragment.handler != null){
//			Message msg = Message.obtain();
//			msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
//			ElectricalCtrlFragment.handler.sendMessage(msg);
//		}
		if(device instanceof DefaultController){
			DefaultController dc = (DefaultController)device;
			for (Electrical ele : dc.getListElectrical()) {
				if (ele.isGearChanged()) {
					String state = "$G" + ele.getController().getCoding() + ":" + ele.getGear() + ele.getNum() + "#00";
					WebClient.getInstance().sendMessage(state);
					ele.setGearChanged(false);
				}
			}
		}
	}

	@Override
	public void alarmDeviceFeedback(AlarmDevice alarmDevice, String msg) {
		//Log.e("MyMessageAnalysiser", "alarmDeviceFeedback" + alarmDevice);
	}

	@Override
	public void climateDeviceFeedback(ClimateDevice climateDevice, String feedbackMsg) {
		//Log.e("MyMessageAnalysiser", "climateDeviceFeedback" + feedbackMsg);
		WebClient.getInstance().sendMessage("$" + feedbackMsg);
		if(climateDevice instanceof Pressure){
//			List<Device> list = climateDevice.getListUnTriggeredDevice();
//			if(null != list){
//				SendMsgHelper.sendMessage(list);
//			}
			//Pressure pressure = (Pressure)climateDevice;
			//WebClient.getInstance().sendMessage("$I" + pressure.getCoding() + ":" + pressure.getPerValue() + "#");
//			if(null != ClimateFragment.handler) {
//				//logger.info("climateDeviceFeedback ClimateFragment.handler.sendMessage");
//				Message msg = Message.obtain();
//				msg.arg1 = ClimateFragment.REFRESH_VALUE;
//				ClimateFragment.handler.sendMessage(msg);
//			}
		}
	}

	@Override
	public void studyRemoteFeedback(StudyRemote studyRemote, String s) {

	}

	@Override
	public void stateFeedback(String s) {
		WebClient.getInstance().sendMessage(s);
	}

	@Override
	public void heartbeat(String s) {

	}

	@Override
	public void allMessageEnd() {
		//Log.e("MyMessageAnalaysiser", "allMessageEnd");
		if(null != SearchActivity.handler){
			Message message = Message.obtain();
			message.arg1 = SearchActivity.REFRESH_ELE_LIST;
			SearchActivity.handler.sendMessage(message);
		}
		//Log.e("MyMessageAnalaysiser", "quene size: " + getQueueMsg().size());
	}

	@Override
	public void singleMessageEnd(Device device, String s) {
		//Log.e("MyMessageAnalaysiser", s);
		if(null == device){
			return;
		}
		if(null != SearchActivity.deviceModelHelper){
			if(device == SearchActivity.deviceModelHelper.getDevToSet()
					&& SearchActivity.deviceModelHelper.getToDeviceModel() == EDeviceModel.LOCAL) {
				if (null != SearchActivity.handler) {
					Message message = Message.obtain();
					message.arg1 = SearchActivity.SET_MODEL_RESPONSE;
					message.arg2 = 1;
					SearchActivity.handler.sendMessage(message);
				}
			}
		}
		//如果设备不是本地模式，设为本地模式
		if(device.getDeviceModel() == EDeviceModel.REMOTE){
			device.setDeviceModel(EDeviceModel.LOCAL);
		}
	}
}
