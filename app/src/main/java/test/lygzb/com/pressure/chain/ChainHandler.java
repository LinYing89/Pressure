package test.lygzb.com.pressure.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/6/3.
 */
public class ChainHandler {

	private static ChainHandler CHAIN_HANDLER = new ChainHandler();

	private boolean enable;
	private List<Chain> listChain;
	private Chain selectedChain;

	private ChainHandler(){}

	public static ChainHandler getIns(){
		return CHAIN_HANDLER;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<Chain> getListChain() {
		if(listChain == null){
			listChain = Collections.synchronizedList(new ArrayList<Chain>());
		}
		return listChain;
	}

	public void setListChain(List<Chain> listChain) {
		this.listChain = listChain;
	}

	public Chain getSelectedChain() {
		if(null == selectedChain){
			if(!getListChain().isEmpty()){
				selectedChain = getListChain().get(0);
			}
		}
		return selectedChain;
	}

	public void setSelectedChain(Chain selectedChain) {
		this.selectedChain = selectedChain;
	}

	public void add(Chain chain){
		if(null == chain){
			return;
		}
		if(!getListChain().contains(chain)){
			getListChain().add(chain);
		}
	}
}
