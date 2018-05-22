package test.lygzb.com.pressure.chain;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.device.LinkedDevice;
import lygzb.zsmarthome.device.collector.SimpleTrigger;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventStyle;

/**
 * Created by Administrator on 2016/6/3.
 */
public class Chain {

	private boolean enable;
	private String name;
	private List<Event> listEvent;
	private SimpleTrigger trigger;
	private Event selectedEvent;
	private LinkedDevice selectedLinkDevice;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<Event> getListEvent() {
		if(null == listEvent){
			listEvent = Collections.synchronizedList(new ArrayList<Event>());
		}
		return listEvent;
	}

	public void setListEvent(List<Event> listEvent) {
		this.listEvent = listEvent;
	}

	public Event getSelectedEvent() {
		if(null == selectedEvent){
			if(!getListEvent().isEmpty()){
				selectedEvent = getListEvent().get(0);
			}
		}
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
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

	public String getName() {
		if(null == name){
			name = "默认";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addEvent(Event event){
		if(event == null){
			return;
		}
		if(!getListEvent().contains(event)){
			getListEvent().add(event);
		}
	}
	public boolean getEventResult(){
		Integer result = null;
		if(getListEvent().isEmpty()){
			return false;
		}

		for(int i=0; i< getListEvent().size(); i++){
			Event event = getListEvent().get(i);
			//获取每个条件的结果
			Integer er = event.getResult();
			if(er == null){
				continue;
			}
			if(result == null){
				result = er;
			}else{
				if(event.getEventStyle() == EventStyle.ADD){
					result *= er;
				}else{
					result += er;
				}
			}
		}
		return  result != null && result > 0;
	}

	public void run(){
		if(!isEnable() || getListEvent().isEmpty() || getTrigger().getListLinkedDevice().isEmpty()){
			return;
		}
		//Log.e("Chain", "Chain is run");
		if(getEventResult()){
			for(LinkedDevice linkedDevice : getTrigger().getListLinkedDevice()) {
				DeviceChainHelper.getIns().setChain(linkedDevice.getDevice(), DeviceChainHelper.CHAIN, linkedDevice.getAction());
			}
		}
	}

}
