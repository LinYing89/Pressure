package test.lygzb.com.pressure.chain;

import lygzb.zsmarthome.device.DevState;
import lygzb.zsmarthome.device.Device;

/**
 * 对device类的封装
 * Created by Administrator on 2016/6/4.
 */
public class DeviceChain {

	private Device device;
	private int iChainTem = -1;
	private int iTimingTem = -1;
	private int chain = -1;
	private int timing = -1;
	private int loop = -1;
	private boolean first = true;

	public DeviceChain(){
		init();
		first = true;
	}

	public void init(){
		chain = -1;
		timing = -1;
		loop = -1;
		iChainTem = -1;
		iTimingTem = -1;
	}

	public void initIChainTem(){
		iChainTem = -1;
		iTimingTem = -1;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public int getiChainTem() {
		return iChainTem;
	}

	public void setiChainTem(int iChainTem) {
		this.iChainTem = iChainTem;
	}

	public int getChain() {
		return chain;
	}

	public void setChain(int chain) {
		this.chain = chain;
	}

	public int getiTimingTem() {
		return iTimingTem;
	}

	public void setiTimingTem(int iTimingTem) {
		this.iTimingTem = iTimingTem;
	}

	public int getTiming() {
		return timing;
	}

	public void setTiming(int timing) {
		this.timing = timing;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		this.loop = loop;
	}

	public boolean sendAble(){
		//Log.w("DeviceChain","sendAble");
		String state;
		if(getChain() == -1 && getTiming() == -1 && getLoop() == -1){
			return false;
		}
		//计算目标状态
		int iS = 1;
		if(getChain() != -1){
			iS = getChain();
		}
		if(getTiming() != -1){
			iS *= getTiming();
		}
		if(getLoop() != -1){
			iS *= getLoop();
		}
		/*String log = getDevice().getName() + ": " +getChain() + "," + getTiming() + "," + getLoop();
		WebClient.getInstance().sendMessage(log);*/
		//Log.e("DeviceChain",log);

		//第一次无论如何要发，以后根据设备状态发，如果和设备的状态一样就不用发了
		if(iS == 1){
			state = DevState.ON;
		}else{
			state = DevState.OFF;
		}
		if(first){
			first = false;
			device.handleLinked(state);
			return true;
		}

		if(device.getState().equals(state)){
			return false;
		}else{
			device.handleLinked(state);
			return true;
		}
	}

	@Override
	public String toString() {
		return getDevice().getCoding() + getDevice().getName() + ": " +getChain() + "," + getTiming() + "," + getLoop();
	}
}
