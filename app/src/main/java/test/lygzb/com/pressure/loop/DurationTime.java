package test.lygzb.com.pressure.loop;

/**
 * Created by Administrator on 2016/5/30.
 */
public class DurationTime {

	private int hour;
	private int minute;
	private int second;

	public DurationTime(){}

	public DurationTime(String strTime){
		setXmlTime(strTime);
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	/**
	 * 获取时长的毫秒数
	 */
	public long getDurationMS(){
		return getDurationMS(getHour(), getMinute(), getSecond());
	}

	/**
	 * 获取时长的毫秒数
	 */
	public static long getDurationMS(int hour, int minute, int second){
		return (((hour * 60) + minute) * 60 + second) * 1000;
	}

	public void setXmlTime(String strTime){
		try{
			strTime = strTime.trim();
			String[] arrayTime = strTime.split(",");
			if(arrayTime.length == 1){
				setSecond(Integer.parseInt(arrayTime[0]));
			}else if(arrayTime.length == 2){
				setMinute(Integer.parseInt(arrayTime[0]));
				setSecond(Integer.parseInt(arrayTime[1]));
			}else if(arrayTime.length == 3){
				setHour(Integer.parseInt(arrayTime[0]));
				setMinute(Integer.parseInt(arrayTime[1]));
				setSecond(Integer.parseInt(arrayTime[2]));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(getHour() != 0){
			sb.append(getHour() < 10 ? "0" + getHour() : getHour());
			sb.append("时");
		}
		if(getMinute() != 0){
			sb.append(getMinute() < 10 ? "0" + getMinute() : getMinute());
			sb.append("分");
		}
		sb.append(getSecond() < 10 ? "0" + getSecond() : getSecond());
		sb.append("秒");
		return sb.toString();
	}

	public String toXmlString(){
		return getHour() + "," + getMinute() + "," + getSecond();
	}
}
