package test.lygzb.com.pressure.loop;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.Pressure;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * 二级触发条件
 * Created by Administrator on 2016/5/30.
 */
public class Event {
	private EventStyle eventStyle;
	private EventSymbol eventSymbol;
	private Device device;
	private int triggerValue;

	/**
	 *运算方式，ADD/OR
	 * @return
	 */
	public EventStyle getEventStyle() {
		if(eventStyle == null){
			eventStyle = EventStyle.OR;
		}
		return eventStyle;
	}

	/**
	 * 运算方式，ADD/OR
	 * @param eventStyle
	 */
	public void setEventStyle(EventStyle eventStyle) {
		this.eventStyle = eventStyle;
	}

	/**
	 * 比较符号，大于GREATER/等于EQUAL/小于/LESS
	 * @return
	 */
	public EventSymbol getEventSymbol() {
		if(device instanceof Electrical){
			eventSymbol = EventSymbol.EQUAL;
		}
		return eventSymbol;
	}

	/**
	 * 比较符号，大于GREATER/等于EQUAL/小于/LESS
	 * @param eventSymbol
	 */
	public void setEventSymbol(EventSymbol eventSymbol) {
		this.eventSymbol = eventSymbol;
	}

	public Device getDevice() {
		if(device == null){
			if(!UserHelper.getHomeMaster().getClimateButler().getListDevice().isEmpty()){
				device = UserHelper.getHomeMaster().getClimateButler().getListDevice().get(0);
			}else if(!UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice().isEmpty()){
				device = UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice().get(0);
			}
		}
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * 值，设备为开关电器时，值为0或1，为0表示关，为1表示开
	 * @return
	 */
	public int getTriggerValue() {
		return triggerValue;
	}

	/**
	 * 值，设备为开关电器时，值为0或1，为0表示关，为1表示开
	 * @param triggerValue
	 */
	public void setTriggerValue(int triggerValue) {
		this.triggerValue = triggerValue;
	}

	/**
	 * 计算条件的结果
	 * @return 1表示触发，0表示不触发
	 */
	public Integer getResult(){
		Integer result = null;
		if(null == getDevice()){
			return result;
		}
		double value = 0;
		if(device instanceof Electrical){
			//如果是电器，判断电器的状态
			value = device.isWorking() ? 1 : 0;
		}else if(device instanceof ClimateDevice){
			//如果是仪表，判断仪表的值
			ClimateDevice cd = (ClimateDevice)device;
			if(cd.getValue() == null){
				return null;
			}else {
				if (device instanceof Pressure) {
					value = ((Pressure) device).getPerValue();
				} else {
					value = ((ClimateDevice) device).getValue();
				}
			}
		}
		//电器值与触发值比较的结果
		boolean compara = false;
		switch (getEventSymbol()){
			case GREATER:
				compara = value > getTriggerValue();
				break;
			case EQUAL:
				compara = value == getTriggerValue();
				break;
			case LESS:
				compara = value < getTriggerValue();
				break;
		}
		result = compara ? 1 : 0;
		return result;
	}
}
