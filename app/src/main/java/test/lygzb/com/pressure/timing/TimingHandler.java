package test.lygzb.com.pressure.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
public class TimingHandler {

	private static TimingHandler TIMING_HANDLER = new TimingHandler();

	//使能
	private boolean enable;
	//定时集合
	private List<MyTiming> listMyTiming;

	private MyTiming selectedTiming;

	private TimingHandler(){}

	public static TimingHandler getIns(){
		return TIMING_HANDLER;
	}

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
	 * 定时集合
	 * @return
	 */
	public List<MyTiming> getListMyTiming() {
		if(null == listMyTiming){
			listMyTiming = Collections.synchronizedList(new ArrayList<MyTiming>());
		}
		return listMyTiming;
	}

	/**
	 * 定时集合
	 * @param listMyTiming
	 */
	public void setListMyTiming(List<MyTiming> listMyTiming) {
		this.listMyTiming = listMyTiming;
	}

	public MyTiming getSelectedTiming() {
		if(null == selectedTiming){
			if(!getListMyTiming().isEmpty()){
				selectedTiming = getListMyTiming().get(0);
			}
		}
		return selectedTiming;
	}

	public void setSelectedTiming(MyTiming selectedTiming) {
		this.selectedTiming = selectedTiming;
	}

	public void add(MyTiming timing){
		if(null != timing && !getListMyTiming().contains(timing)){
			getListMyTiming().add(timing);
		}
	}
}
