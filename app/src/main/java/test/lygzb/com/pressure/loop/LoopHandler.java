package test.lygzb.com.pressure.loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一级循环
 * Created by Administrator on 2016/5/30.
 */
public class LoopHandler {

	private static LoopHandler LOOP_HANDLER = new LoopHandler();

	//使能
	private boolean enable;
	//二级循环集合
	private List<Loop> listLoop;

	private Loop selectedLoop;

	private LoopHandler(){}

	public static LoopHandler getIns(){
		return LOOP_HANDLER;
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
	 * 二级循环集合
	 * @return
	 */
	public List<Loop> getListLoop() {
		if(null == listLoop){
			listLoop = Collections.synchronizedList(new ArrayList<Loop>());
		}
		return listLoop;
	}

	/**
	 * 二级循环集合
	 * @param listLoop
	 */
	public void setListLoop(List<Loop> listLoop) {
		this.listLoop = listLoop;
	}

	public Loop getSelectedLoop() {
		if(selectedLoop == null){
			if(!getListLoop().isEmpty()){
				selectedLoop = getListLoop().get(0);
			}
		}
		return selectedLoop;
	}

	public void setSelectedLoop(Loop selectedLoop) {
		this.selectedLoop = selectedLoop;
	}

	public void add(Loop loop){
		if(loop == null){
			return;
		}
		if(!getListLoop().contains(loop)){
			getListLoop().add(loop);
		}
	}
}
