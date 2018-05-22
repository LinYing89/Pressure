package test.lygzb.com.pressure.chain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.device.DevState;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.ICtrlableDevice;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.network.WebClient;

/**
 *
 * Created by Administrator on 2016/6/4.
 */
public class DeviceChainHelper {

	private static DeviceChainHelper deviceChainHelper = new DeviceChainHelper();

	/**
	 * 扫描设备列表，更改目标状态，是否扫描完成标志，扫描完成通知发送线程发送控制命令，
	 * 发送完控制命令将此变量置为false，通知扫描线程再次扫描
	 */
	public static boolean SCAN_OVER = false;

	public static final int CHAIN = 1;
	public static final int TIMING = 2;
	public static final int LOOP = 3;

	private List<DeviceChain> listDeviceChain;

	private int logCount;

	private DeviceChainHelper(){

	}

	public static DeviceChainHelper getIns(){
		return deviceChainHelper;
	}

	public void init(){
		for(DeviceChain deviceChain : getListDeviceChain()){
			deviceChain.init();
		}
	}
	public List<DeviceChain> getListDeviceChain() {
		if(null == listDeviceChain){
			listDeviceChain = Collections.synchronizedList(new ArrayList<DeviceChain>());
		}
		return listDeviceChain;
	}

	public void setListDeviceChain(List<DeviceChain> listDeviceChain) {
		this.listDeviceChain = listDeviceChain;
	}

	/**
	 * 设置某个连锁的标志位
	 * @param device 设备
	 * @param which 哪个连锁CHAIN/TIMING/LOOP
	 * @param event 目标状态，1或0
	 */
	public void setChain(Device device, int which, int event){
		DeviceChain eqDevice = getEqDeviceChain(device);

		if(null != eqDevice){
			switch (which){
				case CHAIN :
					if(eqDevice.getiChainTem() == -1){
						eqDevice.setiChainTem(event);
					}else{
						eqDevice.setiChainTem(eqDevice.getiChainTem() * event);
					}
//					eqDevice.setChain(event);
					break;
				case TIMING :
					if(eqDevice.getiTimingTem() == -1){
						eqDevice.setiTimingTem(event);
					}else{
						eqDevice.setiTimingTem(eqDevice.getiTimingTem() * event);
					}
					//eqDevice.setTiming(event);
					break;
				case LOOP :
					eqDevice.setLoop(event);
					break;
			}
		}else{
			DeviceChain deviceChain = new DeviceChain();
			deviceChain.setDevice(device);
			addDeviceChain(deviceChain);
			//setChain(device, which, event);
		}
	}

	private void addDeviceChain(DeviceChain deviceChain){
		boolean have = false;
		for(DeviceChain deviceChain1 : getListDeviceChain()){
			if(deviceChain.getDevice() == deviceChain1.getDevice() || deviceChain.getDevice().equals(deviceChain1.getDevice())){
				have = true;
				break;
			}
		}
		if(!have){
			getListDeviceChain().add(deviceChain);
		}
	}

	public void setChain(Device device, int which, String event){
		if(event == null){
			setChain(device, which, -1);
		}else if(event.equals(DevState.ON)){
			setChain(device, which, 1);
		}else{
			setChain(device, which, 0);
		}
	}

	public DeviceChain getEqDeviceChain(Device device){
		DeviceChain eqDevice = null;
		List<DeviceChain> list = new ArrayList<>();
		list.addAll(getListDeviceChain());
		for(DeviceChain deviceChain : list){
			if(deviceChain.getDevice() == device){
				eqDevice = deviceChain;
			}
		}
		if(null == eqDevice){
			for(DeviceChain deviceChain : list){
				if(deviceChain.getDevice().equals(device)){
					eqDevice = deviceChain;
				}
			}
		}
		return eqDevice;
	}

	public void sendMessage() throws InterruptedException{
		for(DeviceChain deviceChain : getListDeviceChain()){
			if(logCount++ >= 20){
				logCount = 0;
				//String log = deviceChain.toString();
				//WebClient.getInstance().sendMessage(log);
			}
			Device device = deviceChain.getDevice();
			if(!(device instanceof ICtrlableDevice)){
				continue;
			}
			if(device.getGear() == 0){
				//自动档
				if(deviceChain.sendAble()) {
					String order = device.createFinalOrder(deviceChain.getDevice().getOrder());
					if(device.getDeviceModel() == EDeviceModel.LOCAL) {
						//本地模式
						SendMsgHelper.sendMessage(order);
					}else{
						//远程模式
						WebClient.getInstance().sendMsg(order);
					}
					Thread.sleep(300);
				}
			}else if(device instanceof Electrical &&
					(device.getState().equals(DevState.ON) || device.getState().equals(DevState.OFF)) &&
					!String.valueOf(device.getGear()).equals(device.getState())){
				//手动档
				device.handleLinked(String.valueOf(device.getGear()));
				String order = device.createFinalOrder(device.getOrder());
				if(device.getDeviceModel() == EDeviceModel.LOCAL) {
					//本地模式
					SendMsgHelper.sendMessage(order);
				}else{
					//远程模式
					WebClient.getInstance().sendMsg(order);
				}
				Thread.sleep(300);
			}
		}
	}
}
