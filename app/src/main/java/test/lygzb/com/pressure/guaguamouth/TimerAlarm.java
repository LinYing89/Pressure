package test.lygzb.com.pressure.guaguamouth;

import lygzb.zsmarthome.linkage.WeekHelper;
import test.lygzb.com.pressure.loop.DurationTime;

/**
 * 报警定时类
 * Created by Administrator on 2017/4/20.
 */

public class TimerAlarm {

	public DurationTime getDurationTime() {
		return durationTime;
	}

	public WeekHelper getWeekHelper() {
		return weekHelper;
	}

	public void setDurationTime(DurationTime durationTime) {
		this.durationTime = durationTime;
	}

	public void setWeekHelper(WeekHelper weekHelper) {
		this.weekHelper = weekHelper;
	}

	private DurationTime durationTime;
	private WeekHelper weekHelper;
}
