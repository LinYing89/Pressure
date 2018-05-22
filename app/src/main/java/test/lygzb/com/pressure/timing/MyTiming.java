package test.lygzb.com.pressure.timing;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lygzb.zsmarthome.device.LinkedDevice;
import lygzb.zsmarthome.device.collector.SimpleTrigger;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.loop.DurationTime;

/**
 * Created by Administrator on 2016/6/14.
 */
public class MyTiming {
	//使能
	private boolean enable;
	//定时名称
	private String name;
	//定时时间集合
	private List<Timer> listTimer;
	//触发器
	private SimpleTrigger trigger;
	private Timer selectedTimer;
	private LinkedDevice selectedLinkDevice;

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
	 * 定时名称
	 * @return
	 */
	public String getName() {
		if(null == name){
			name="默认";
		}
		return name;
	}

	/**
	 * 定时名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 定时时间集合
	 * @return
	 */
	public List<Timer> getListTimer() {
		if(null == listTimer){
			listTimer = new ArrayList<>();
		}
		return listTimer;
	}

	/**
	 * 定时时间集合
	 * @param listTimer
	 */
	public void setListTimer(List<Timer> listTimer) {
		this.listTimer = listTimer;
	}

	/**
	 * 触发器
	 * @return
	 */
	public SimpleTrigger getTrigger() {
		if(null == trigger){
			trigger = new SimpleTrigger();
		}
		return trigger;
	}

	/**
	 * 触发器
	 * @param trigger
	 */
	public void setTrigger(SimpleTrigger trigger) {
		this.trigger = trigger;
	}

	public Timer getSelectedTimer() {
		if(null == selectedTimer){
			if(!getListTimer().isEmpty()){
				selectedTimer = getListTimer().get(0);
			}
		}
		return selectedTimer;
	}

	public void setSelectedTimer(Timer selectedTimer) {
		this.selectedTimer = selectedTimer;
	}

	public LinkedDevice getSelectedLinkDevice() {
		if(selectedLinkDevice == null){
			if(!getTrigger().getListLinkedDevice().isEmpty()){
				selectedLinkDevice = getTrigger().getListLinkedDevice().get(0);
			}
		}
		return selectedLinkDevice;
	}

	public void setSelectedLinkDevice(LinkedDevice selectedLinkDevice) {
		this.selectedLinkDevice = selectedLinkDevice;
	}

	public void add(Timer timer){
		if(null != timer && !getListTimer().contains(timer)){
			getListTimer().add(timer);
		}
	}

	public void run(){
		if(!isEnable() || getListTimer().isEmpty() || getTrigger().getListLinkedDevice().isEmpty()){
			return;
		}
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		long ms = DurationTime.getDurationMS(hour, minute, second);
		boolean timed = false;
		for(Timer timer : getListTimer()){
			if(!timer.isEnable()){
				continue;
			}
			if(timer.getWeekHelper().getListWeek().contains(week)){
				if(ms > timer.getOnTime().getDurationMS() && ms < timer.getOffTime().getDurationMS()){
					timed = true;
					break;
				}
			}
		}
		if(timed){
			Log.e("MyTiming", "timed trigger");
			for(LinkedDevice linkedDevice : getTrigger().getListLinkedDevice()) {
				DeviceChainHelper.getIns().setChain(linkedDevice.getDevice(), DeviceChainHelper.TIMING, 1);
			}
		}else{
			for(LinkedDevice linkedDevice : getTrigger().getListLinkedDevice()) {
				DeviceChainHelper.getIns().setChain(linkedDevice.getDevice(), DeviceChainHelper.TIMING, 0);
			}
		}
	}
}
