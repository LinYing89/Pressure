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
import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.event.AbstractEvent;
import test.lygzb.com.pressure.event.EventDevice;
import test.lygzb.com.pressure.event.EventTime;
import test.lygzb.com.pressure.guaguamouth.GuaGua;
import test.lygzb.com.pressure.guaguamouth.GuaguaHandler;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;

/**
 * Created by Administrator on 2017/4/22.
 */

public class GuaGuaXmlReader {

	public static HomeMaster homeMaster;

	public static GuaguaHandler readXml(HomeMaster hm, String fileName, String encoding){
		homeMaster = hm;

		GuaguaHandler guaguaHandler = GuaguaHandler.getIns();
		guaguaHandler.getListGuagua().clear();
		try {
			SAXReader reader = new SAXReader();
			if(encoding == null || encoding.isEmpty()){
				encoding = XmlHelper.ENCODING;
			}
			reader.setEncoding(encoding);
			Document document = reader.read(new File(fileName));
			Element node = document.getRootElement();
			guaguaHandler.setEnable(Boolean.valueOf(node.attributeValue(MyXmlHelper.ATTR_ENABLE)));

			List<Element> listElementLoop = node.elements(MyXmlHelper.NODE_GUAGUA);
			for(Element elementLoop : listElementLoop){
				GuaGua guaGua = getGuaguaInElement(elementLoop);
				guaguaHandler.getListGuagua().add(guaGua);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return guaguaHandler;
	}

	public static GuaguaHandler readXml(HomeMaster hm, String fileName){
		return readXml(hm, fileName, XmlHelper.ENCODING);
	}

	private static GuaGua getGuaguaInElement(Element elementGuagua){
		GuaGua guaGua = new GuaGua();
		guaGua.setName(elementGuagua.attributeValue(XmlHelper.ATTR_NAME));
		guaGua.setEnable(Boolean.valueOf(elementGuagua.attributeValue(MyXmlHelper.ATTR_ENABLE)));
		List<Element> listEventElement = elementGuagua.elements(MyXmlHelper.NODE_EVENT);
		guaGua.setListEvent(getEventInElement(listEventElement));

		guaGua.setTrigger(XmlTrigger.readElementToSimpleTrigger(homeMaster, elementGuagua));
		return guaGua;
	}

	private static List<AbstractEvent> getEventInElement(List<Element> listEventElement){
		List<AbstractEvent> listEvent = Collections.synchronizedList(new ArrayList<AbstractEvent>());
		for(Element element : listEventElement){
			AbstractEvent event = null;
			String eventStyle = element.attributeValue("eventStyle");
			if(eventStyle.equals("device")){
				event = new EventDevice();
				EventDevice eventDevice = (EventDevice)event;
				String coding = element.attributeValue(MyXmlHelper.ATTR_DEVICE);
				Device device = DeviceAssistent.getDeviceFromXml(coding);
				if(device == null){
					continue;
				}
				Device eqDevice = homeMaster.getEqulasDevice(device);
				if(eqDevice == null){
					continue;
				}
				eventDevice.setDevice(eqDevice);
				eventDevice.setTriggerValue(Double.parseDouble(element.attributeValue(MyXmlHelper.ATTR_VALUE)));
			}else{
				event = new EventTime();
				String time = element.attributeValue(MyXmlHelper.ATTR_VALUE);
			}
			event.setEventStyle(Enum.valueOf(EventStyle.class, element.attributeValue(MyXmlHelper.ATTR_STYLE)));
			event.setEventSymbol(Enum.valueOf(EventSymbol.class, element.attributeValue(MyXmlHelper.ATTR_SYMBOL)));
			listEvent.add(event);
		}
		return listEvent;
	}
}
