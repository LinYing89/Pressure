package test.lygzb.com.pressure.loop;

/**
 * Created by Administrator on 2016/6/15.
 */
public class Duration {

	private DurationTime onTime;
	private DurationTime offTime;

	public DurationTime getOnTime() {
		if(null == onTime){
			onTime = new DurationTime();
		}
		return onTime;
	}

	public void setOnTime(DurationTime onTime) {
		this.onTime = onTime;
	}

	public DurationTime getOffTime() {
		if(null == offTime){
			offTime = new DurationTime();
		}
		return offTime;
	}

	public void setOffTime(DurationTime offTime) {
		this.offTime = offTime;
	}
}
