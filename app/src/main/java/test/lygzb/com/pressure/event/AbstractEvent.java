package test.lygzb.com.pressure.event;

import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;

/**
 * 条件抽象类
 * Created by Administrator on 2017/4/20.
 */

public abstract class AbstractEvent {

	public EventStyle getEventStyle() {
		if(null == eventStyle){
			eventStyle = EventStyle.OR;
		}
		return eventStyle;
	}

	public EventSymbol getEventSymbol() {
		if(null == eventSymbol){
			eventSymbol = EventSymbol.EQUAL;
		}
		return eventSymbol;
	}

	public void setEventStyle(EventStyle eventStyle) {
		this.eventStyle = eventStyle;
	}

	public void setEventSymbol(EventSymbol eventSymbol) {
		this.eventSymbol = eventSymbol;
	}

	//运算方式ADD/OR
	private EventStyle eventStyle;
	//运算符号>/=/<
	private EventSymbol eventSymbol;

	public abstract Integer getResult();
}
