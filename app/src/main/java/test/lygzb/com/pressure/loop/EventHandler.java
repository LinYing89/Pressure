package test.lygzb.com.pressure.loop;

import java.util.ArrayList;
import java.util.List;

/**
 * 一级触发条件
 * Created by Administrator on 2016/5/30.
 */
public class EventHandler {

	private String name;
	private boolean enable;
	private List<Event> listEvent;
	private Event selectedEvent;

	/**
	 * 名称
	 * @return
	 */
	public String getName() {
		if(name == null){
			name = "默认";
		}
		return name;
	}

	/**
	 * 名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * 二级触发条件的集合
	 * @return
	 */
	public List<Event> getListEvent() {
		if(null == listEvent){
			listEvent = new ArrayList<>();
		}
		return listEvent;
	}

	/**
	 * 二级触发条件的集合
	 * @param listEvent
	 */
	public void setListEvent(List<Event> listEvent) {
		this.listEvent = listEvent;
	}

	public Event getSelectedEvent() {
		if(null == selectedEvent){
			if(!getListEvent().isEmpty()){
				selectedEvent = getListEvent().get(0);
			}
		}
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public void add(Event event){
		if(null == event){
			return;
		}
		if(!getListEvent().contains(event)){
			getListEvent().add(event);
		}
	}

	public boolean getEventResult(){
		Integer result = 1;
		if(getListEvent().isEmpty()){
			return false;
		}

		for(int i=0; i< getListEvent().size(); i++){
			Event event = getListEvent().get(i);
			Integer er = event.getResult();
			if(null == er){
				continue;
			}
			if(i == 0){
				result = er;
			}else{
				if(event.getEventStyle() == EventStyle.ADD){
					result *= er;
				}else{
					result += er;
				}
			}
		}

		return result > 0;
	}
}
