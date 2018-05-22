package test.lygzb.com.pressure.event;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.collector.Collector;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.Pressure;
import lygzb.zsmarthome.device.electrical.Electrical;

/**
 * 设备触发条件类
 * Created by Administrator on 2017/4/20.
 */

public class EventDevice extends AbstractEvent {

	public void setDevice(Device device) {
		this.device = device;
	}

	public void setTriggerValue(double triggerValue) {
		this.triggerValue = triggerValue;
	}

	public Device getDevice() {
		return device;
	}

	public double getTriggerValue() {
		return triggerValue;
	}

	//触发的设备
	private Device device;
	//触发值
	private double triggerValue;

	@Override
	public Integer getResult() {
		Integer result = null;
		if(null == device){
			return null;
		}
		if(!device.isNormal()){
			return null;
		}
		double value = 0;
		if(device instanceof Electrical){
			//如果是电器，判断电器的状态
			value = device.isWorking() ? 1 : 0;
		}else if(device instanceof ClimateDevice){
			if(device instanceof Pressure){
				value = ((Pressure)device).getPerValue();
			}else{
				value = ((ClimateDevice)device).getValue();
			}
		}
		boolean compare = false;
		switch (getEventSymbol()){
			case GREATER:
				compare = value > triggerValue;
				break;
			case EQUAL:
				compare = value == triggerValue;
				break;
			case LESS:
				compare = value < triggerValue;
				break;
		}
		result = compare ? 1 : 0;
		return result;
	}
}
