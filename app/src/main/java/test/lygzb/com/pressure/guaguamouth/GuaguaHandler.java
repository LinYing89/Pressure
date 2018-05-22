package test.lygzb.com.pressure.guaguamouth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class GuaguaHandler {

	private static GuaguaHandler guaguaHandler = new GuaguaHandler();

	//使能
	private boolean enable;

	private List<GuaGua> listGuagua;

	private GuaGua selectedGuagua;

	public static GuaguaHandler getIns(){
		return guaguaHandler;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<GuaGua> getListGuagua() {
		if(null == listGuagua){
			listGuagua = Collections.synchronizedList(new ArrayList<GuaGua>());
		}
		return listGuagua;
	}

	public void setListGuagua(List<GuaGua> listGuagua) {
		this.listGuagua = listGuagua;
	}

	public GuaGua getSelectedGuagua() {
		if(null== selectedGuagua){
			if(!getListGuagua().isEmpty()){
				selectedGuagua = getListGuagua().get(0);
			}
		}
		return selectedGuagua;
	}

	public void setSelectedGuagua(GuaGua selectedGuagua) {
		this.selectedGuagua = selectedGuagua;
	}

	public void add(GuaGua guaGua){
		if(null == guaGua){
			return;
		}
		if(!listGuagua.contains(guaGua)){
			listGuagua.add(guaGua);
		}
	}
}
