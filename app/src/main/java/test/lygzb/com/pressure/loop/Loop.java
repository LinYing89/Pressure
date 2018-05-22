package test.lygzb.com.pressure.loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.device.LinkedDevice;
import lygzb.zsmarthome.device.collector.SimpleTrigger;
import test.lygzb.com.pressure.chain.DeviceChainHelper;

/**
 * 二级循环
 * Created by Administrator on 2016/5/30.
 */
public class Loop {

	//循环名称
	private String name;
	//使能
	private boolean enable;
	//循环次数,-1表示无限循环
	private int loopCount;
	//时长
	private List<Duration> listDuration;
	//一级触发条件集合
	private List<EventHandler> listEventHandler;
	//受控设备集合
	private SimpleTrigger trigger;

	private EventHandler selectedEventHandler;
	private LinkedDevice selectedLinkedDevice;

	private LoopThread loopThread;

	/**
	 * 循环名称
	 * @return
	 */
	public String getName() {
		if(null == name){
			name = "默认";
		}
		return name;
	}

	/**
	 * 循环名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 使能
	 * @return
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * 使能
	 * @param enable
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * 循环次数,-1表示无限循环
	 * @return
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * 循环次数,-1表示无限循环
	 * @param loopCount
	 */
	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public List<Duration> getListDuration() {
		if(null == listDuration){
			listDuration = Collections.synchronizedList(new ArrayList<Duration>());
		}
		return listDuration;
	}

	public void setListDuration(List<Duration> listDuration) {
		this.listDuration = listDuration;
	}

	/**
	 *一级触发条件集合
	 * @return
	 */
	public List<EventHandler> getListEventHandler() {
		if(null == listEventHandler){
			listEventHandler = new ArrayList<>();
		}
		return listEventHandler;
	}

	/**
	 *一级触发条件集合
	 * @param listEventHandler
	 */
	public void setListEventHandler(List<EventHandler> listEventHandler) {
		this.listEventHandler = listEventHandler;
	}

	public SimpleTrigger getTrigger() {
		if(null == trigger){
			trigger = new SimpleTrigger();
		}
		return trigger;
	}

	public void setTrigger(SimpleTrigger trigger) {
		this.trigger = trigger;
	}

	public EventHandler getSelectedEventHandler() {
		if(null == selectedEventHandler){
			if(!getListEventHandler().isEmpty()){
				selectedEventHandler = getListEventHandler().get(0);
			}
		}
		return selectedEventHandler;
	}

	public void setSelectedEventHandler(EventHandler selectedEventHandler) {
		this.selectedEventHandler = selectedEventHandler;
	}

	public LinkedDevice getSelectedDevice() {
		if(null == selectedLinkedDevice){
			if(!getTrigger().getListLinkedDevice().isEmpty()){
				selectedLinkedDevice = getTrigger().getListLinkedDevice().get(0);
			}
		}
		return selectedLinkedDevice;
	}

	public void setSelectedDevice(LinkedDevice selectedLinkedDevice) {
		this.selectedLinkedDevice = selectedLinkedDevice;
	}

	public void addEventHandler(EventHandler eventHandler){
		if(null == eventHandler){
			return;
		}
		if(!getListEventHandler().contains(eventHandler)){
			getListEventHandler().add(eventHandler);
		}
	}

	public void addDuration(Duration duration){
		if(null != duration && !getListDuration().contains(duration)){
			getListDuration().add(duration);
		}
	}

	public void addAffectedDevice(LinkedDevice device){
		if(null == device){
			return;
		}
		if(!getTrigger().getListLinkedDevice().contains(device)){
			getTrigger().getListLinkedDevice().add(device);
		}
	}

	/**
	 * 遍历所有二级循环，有一个为false则返回false，否则返回true
	 * @return
	 */
	public boolean getResult(){
		for(EventHandler eventHandler : getListEventHandler()){
			if(eventHandler.isEnable()) {
				if (!eventHandler.getEventResult()) {
					return false;
				}
			}
		}
		return true;
	}

	public void execute(String state){
		for(LinkedDevice linkedDevice : getTrigger().getListLinkedDevice()){
//			if(null != state) {
//				linkedDevice.setAction(state);
//			}
			DeviceChainHelper.getIns().setChain(linkedDevice.getDevice(), DeviceChainHelper.LOOP, state);
		}
	}

	public void run(){
		if(!isEnable() || getListDuration().isEmpty() || getTrigger().getListLinkedDevice().isEmpty()){
			return;
		}
		if(getLoopCount() != -1 && getLoopCount() <= 0){
			return;
		}
		if(getResult()){
			if(loopThread != null && loopThread.isRunning()){
				return;
			}
			loopThread = new LoopThread(this);
			loopThread.start();
		}
	}
}
