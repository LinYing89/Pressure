package test.lygzb.com.pressure.timing;

import lygzb.zsmarthome.linkage.WeekHelper;
import test.lygzb.com.pressure.loop.DurationTime;

/**
 * Created by Administrator on 2016/6/14.
 */
public class Timer {
	//使能
	private boolean enable;
	//开启时间
	private DurationTime onTime;
	//关闭时间
	private DurationTime offTime;
	//星期助手
	private WeekHelper weekHelper;

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
	 * 开启时间
	 * @return
	 */
	public DurationTime getOnTime() {
		if(onTime == null){
			onTime = new DurationTime();
		}
		return onTime;
	}

	/**
	 * 开启时间
	 * @param onTime
	 */
	public void setOnTime(DurationTime onTime) {
		this.onTime = onTime;
	}

	/**
	 * 关闭时间
	 * @return
	 */
	public DurationTime getOffTime() {
		if(offTime == null){
			offTime = new DurationTime();
		}
		return offTime;
	}

	/**
	 * 关闭时间
	 * @param offTime
	 */
	public void setOffTime(DurationTime offTime) {
		this.offTime = offTime;
	}

	/**
	 * 星期助手
	 * @return
	 */
	public WeekHelper getWeekHelper() {
		if(null == weekHelper){
			weekHelper = new WeekHelper();
		}
		return weekHelper;
	}

	/**
	 * 星期助手
	 * @param weekHelper
	 */
	public void setWeekHelper(WeekHelper weekHelper) {
		this.weekHelper = weekHelper;
	}
}
