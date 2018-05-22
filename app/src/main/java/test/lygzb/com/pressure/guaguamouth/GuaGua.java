package test.lygzb.com.pressure.guaguamouth;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.GuaGuaMouth;
import lygzb.zsmarthome.device.LinkedDevice;
import lygzb.zsmarthome.device.LinkedGuaguaMouth;
import lygzb.zsmarthome.device.collector.SimpleTrigger;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.event.AbstractEvent;
import test.lygzb.com.pressure.network.WebClient;

/**
 * Created by Administrator on 2017/4/20.
 */

public class GuaGua {

	//使能
	private boolean enable;
	//是否触发过了
	private boolean trigged;

	//名称
	private String name;

	private List<AbstractEvent> listEvent;

	private SimpleTrigger trigger;

	private AbstractEvent selectedEvent;

	private LinkedDevice selectedLinkedDevice;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isTrigged() {
		return trigged;
	}

	public void setTrigged(boolean trigged) {
		this.trigged = trigged;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AbstractEvent> getListEvent() {
		if(null == listEvent){
			listEvent = Collections.synchronizedList(new ArrayList<AbstractEvent>());
		}
		return listEvent;
	}

	public void setListEvent(List<AbstractEvent> listEvent) {
		this.listEvent = listEvent;
	}

	public SimpleTrigger getTrigger() {
		if(trigger == null){
			trigger = new SimpleTrigger();
		}
		return trigger;
	}

	public void setTrigger(SimpleTrigger trigger) {
		this.trigger = trigger;
	}

	public AbstractEvent getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(AbstractEvent selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public LinkedDevice getSelectedLinkedDevice() {
		return selectedLinkedDevice;
	}

	public void setSelectedLinkedDevice(LinkedDevice selectedLinkedDevice) {
		this.selectedLinkedDevice = selectedLinkedDevice;
	}

	public void addEvent(AbstractEvent event){
		if(null == event){
			return;
		}
		if(!getListEvent().contains(event)){
			getListEvent().add(event);
		}
	}

	public boolean getEventResult(){
		if(listEvent.isEmpty()){
			return  false;
		}
		Integer result = null;
		for (AbstractEvent event :listEvent) {
			Integer er = event.getResult();
			if(null == er){
				continue;
			}
			if(null == result){
				result = er;
			}else {
				switch (event.getEventStyle()){
					case ADD:
						result *= er;
						break;
					case OR :
						result += er;
						break;
				}
			}
		}
		return result != null && result > 0;
	}

	public void run(){
//		if(!enable || listEvent.isEmpty() || trigger.getListLinkedDevice().isEmpty()){
//			return;
//		}
		if(!enable || listEvent.isEmpty()){
			return;
		}
		if(getEventResult()){
			if(!trigged){
				trigged = true;
				for(LinkedDevice linkedDev : trigger.getListLinkedDevice()){
					if(linkedDev.getDevice() instanceof GuaGuaMouth){
						GuaGuaMouth guagua = (GuaGuaMouth)(linkedDev.getDevice());
						String order = guagua.speak(linkedDev.getAction());
						String finalOrder = guagua.createFinalOrder(order);
						for(int i=0; i < ((LinkedGuaguaMouth)linkedDev).getSpeakCount(); i++) {
							if (guagua.getDeviceModel() == EDeviceModel.LOCAL) {
								SendMsgHelper.sendMessage(finalOrder);
							} else {
								WebClient.getInstance().sendMsg(finalOrder);
							}
							try {
								TimeUnit.MILLISECONDS.sleep(200);
							}catch (Exception e){}
						}
					}
				}
			}
		}else{
			if(trigged){
				trigged = false;
			}
		}
	}
}
