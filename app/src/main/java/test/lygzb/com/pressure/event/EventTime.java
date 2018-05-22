package test.lygzb.com.pressure.event;

import java.util.Calendar;

import test.lygzb.com.pressure.guaguamouth.TimerAlarm;
import test.lygzb.com.pressure.loop.DurationTime;

/**
 * Created by Administrator on 2017/4/20.
 */

public class EventTime extends AbstractEvent {

	public TimerAlarm getTimerAlarm() {
		return timerAlarm;
	}

	public void setTimerAlarm(TimerAlarm timerAlarm) {
		this.timerAlarm = timerAlarm;
	}

	private TimerAlarm timerAlarm;

	@Override
	public Integer getResult() {
		Integer result = null;
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if(!timerAlarm.getWeekHelper().getListWeek().contains(week)){
			return 0;
		}

		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		long ms = DurationTime.getDurationMS(hour, minute, second);
		long myMs = timerAlarm.getDurationTime().getDurationMS();
		boolean compare = false;
		switch (getEventSymbol()){
			case GREATER:
				compare = ms > myMs;
				break;
			case EQUAL:
				compare = ms == myMs;
				break;
			case LESS:
				compare = ms < myMs;
				break;
		}
		result = compare ? 1: 0;
		return result;
	}
}
