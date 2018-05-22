package test.lygzb.com.pressure.myconfigration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.HomeMaster;
import lygzb.zsmarthome.configration.XmlHelper;
import lygzb.zsmarthome.configration.XmlTrigger;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.DeviceAssistent;
import test.lygzb.com.pressure.loop.Duration;
import test.lygzb.com.pressure.loop.DurationTime;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventHandler;
import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;
import test.lygzb.com.pressure.loop.Loop;
import test.lygzb.com.pressure.loop.LoopHandler;

/**
 * Created by Administrator on 2016/5/31.
 */
public class LoopXmlReader {

	public static HomeMaster homeMaster;

	public static LoopHandler readXml(HomeMaster hm, String fileName, String encoding){
		homeMaster = hm;

		LoopHandler linkageMaster = LoopHandler.getIns();
		linkageMaster.getListLoop().clear();
		try {
			SAXReader reader = new SAXReader();
			if(encoding == null || encoding.isEmpty()){
				encoding = XmlHelper.ENCODING;
			}
			reader.setEncoding(encoding);
			Document document = reader.read(new File(fileName));
			Element node = document.getRootElement();
			linkageMaster.setEnable(Boolean.valueOf(node.attributeValue(MyXmlHelper.ATTR_ENABLE)));

			List<Element> listElementLoop = node.elements(MyXmlHelper.NODE_LOOP);
			for(Element elementLoop : listElementLoop){
				Loop loop = getLoopInElement(elementLoop);
				linkageMaster.getListLoop().add(loop);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return linkageMaster;
	}

	public static LoopHandler readXml(HomeMaster hm, String fileName){
		return readXml(hm, fileName, XmlHelper.ENCODING);
	}

	private static Loop getLoopInElement(Element elementLoop){
		Loop loop = new Loop();
		loop.setName(elementLoop.attributeValue(XmlHelper.ATTR_NAME));
		loop.setEnable(Boolean.valueOf(elementLoop.attributeValue(MyXmlHelper.ATTR_ENABLE)));
		loop.setLoopCount(Integer.parseInt(elementLoop.attributeValue(MyXmlHelper.ATTR_LOOP_COUNT)));

		List<Element> listDurationElement = elementLoop.elements(MyXmlHelper.NODE_DURATION);
		loop.setListDuration(getListDurationInElement(listDurationElement));

		List<Element> listEventElement = elementLoop.elements(MyXmlHelper.NODE_EVENT_HANDLER);
		loop.setListEventHandler(getEventHandlerInElement(listEventElement));

		loop.setTrigger(XmlTrigger.readElementToSimpleTrigger(homeMaster, elementLoop));
		return loop;
	}

	private static List<Duration> getListDurationInElement(List<Element> listDurationElement){
		List<Duration> listDuration = Collections.synchronizedList(new ArrayList<Duration>());
		for(Element element : listDurationElement) {
			Duration duration = new Duration();
			DurationTime onDurationTime = new DurationTime(element.attributeValue(MyXmlHelper.ATTR_ON_TIME));
			duration.setOnTime(onDurationTime);
			DurationTime offDurationTime = new DurationTime(element.attributeValue(MyXmlHelper.ATTR_OFF_TIME));
			duration.setOffTime(offDurationTime);
			listDuration.add(duration);
		}
		return listDuration;
	}

	private static List<EventHandler> getEventHandlerInElement(List<Element> listEventHandlerElement){
		List<EventHandler> listEventHandler = new ArrayList<>();
		for(Element element : listEventHandlerElement) {
			EventHandler eventHandler = new EventHandler();
			eventHandler.setName(element.attributeValue(XmlHelper.ATTR_NAME));
			eventHandler.setEnable(Boolean.valueOf(element.attributeValue(MyXmlHelper.ATTR_ENABLE)));
			List<Element> listEventElement = element.elements(MyXmlHelper.NODE_EVENT);
			eventHandler.setListEvent(getEventInElement(listEventElement));
			listEventHandler.add(eventHandler);
		}
		return listEventHandler;
	}

	private static List<Event> getEventInElement(List<Element> listEventElement){
		List<Event> listEvent = new ArrayList<>();
		for(Element element : listEventElement){
			Event event = new Event();
			event.setEventStyle(Enum.valueOf(EventStyle.class, element.attributeValue(MyXmlHelper.ATTR_STYLE)));
			String coding = element.attributeValue(MyXmlHelper.ATTR_DEVICE);
			Device device = DeviceAssistent.getDeviceFromXml(coding);
			if(device == null){
				continue;
			}
			Device eqDevice = homeMaster.getEqulasDevice(device);
			if(eqDevice == null){
				continue;
			}
			event.setDevice(eqDevice);
			event.setEventSymbol(Enum.valueOf(EventSymbol.class, element.attributeValue(MyXmlHelper.ATTR_SYMBOL)));
			event.setTriggerValue(Integer.parseInt(element.attributeValue(MyXmlHelper.ATTR_VALUE)));
			listEvent.add(event);
		}
		return listEvent;
	}
}
