package test.lygzb.com.pressure.homehelper;

import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.IClimateQueryProcess;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.main.ClimateFragment;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MyClimateQueryProcess implements IClimateQueryProcess {

	@Override
	public void started() {

	}

	@Override
	public void queryDevice(ClimateDevice climateDevice) {
//		boolean isNormalBefore = climateDevice.isNormal();
//		climateDevice.setSendCount(climateDevice.getSendCount() + 1);
//		//Log.e(this.toString(), climateDevice.getSendCount() + "?");
//		boolean isNormalAfter = climateDevice.isNormal();
//		if(isNormalBefore != isNormalAfter){
//			if(null != ClimateFragment.handler){
//				Message msg = Message.obtain();
//				msg.arg1 = ClimateFragment.REFRESH_VALUE;
//				ClimateFragment.handler.sendMessage(msg);
//			}
//		}
		SendMsgHelper.sendMessage(climateDevice.createFinalOrder(climateDevice.getQueryOrder()));
	}

	@Override
	public void catchException(String s) {

	}

	@Override
	public void stoped() {

	}
}
