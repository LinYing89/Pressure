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
import lygzb.zsmarthome.device.LinkedDevice;
import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;

/**
 * Created by Administrator on 2016/6/3.
 */
public class ChainXmlReader {

	public static HomeMaster homeMaster;

	public static ChainHandler readXml(HomeMaster hm, String fileName, String encoding){
		homeMaster = hm;

		ChainHandler chainHandler = ChainHandler.getIns();
		chainHandler.getListChain().clear();
		try {
			SAXReader reader = new SAXReader();
			if(encoding == null || encoding.isEmpty()){
				encoding = XmlHelper.ENCODING;
			}
			reader.setEncoding(encoding);
			Document document = reader.read(new File(fileName));
			Element node = document.getRootElement();
			chainHandler.setEnable(Boolean.valueOf(node.attributeValue(MyXmlHelper.ATTR_ENABLE)));

			List<Element> listElementLoop = node.elements(MyXmlHelper.NODE_CHAIN);
			for(Element elementLoop : listElementLoop){
				Chain chain = getChainInElement(elementLoop);
				chainHandler.getListChain().add(chain);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return chainHandler;
	}

	public static ChainHandler readXml(HomeMaster hm, String fileName){
		return readXml(hm, fileName, XmlHelper.ENCODING);
	}

	private static Chain getChainInElement(Element elementChain){
		Chain chain = new Chain();
		chain.setName(elementChain.attributeValue(XmlHelper.ATTR_NAME));
		chain.setEnable(Boolean.valueOf(elementChain.attributeValue(MyXmlHelper.ATTR_ENABLE)));
		List<Element> listEventElement = elementChain.elements(MyXmlHelper.NODE_EVENT);
		chain.setListEvent(getEventInElement(listEventElement));

		chain.setTrigger(XmlTrigger.readElementToSimpleTrigger(homeMaster, elementChain));
		return chain;
	}

	private static List<Event> getEventInElement(List<Element> listEventElement){
		List<Event> listEvent = Collections.synchronizedList(new ArrayList<Event>());
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

	private static List<LinkedDevice> getAffectedDeviceInElement(List<Element> listAffectedDeviceElement){
		List<LinkedDevice> listDevice = new ArrayList<>();
		for(Element element : listAffectedDeviceElement){
			String coding = element.attributeValue(MyXmlHelper.ATTR_CODING);
			Device device = DeviceAssistent.getDeviceFromXml(coding);
			if(device == null){
				continue;
			}
			Device eqDevice = homeMaster.getEqulasDevice(device);
			if(eqDevice == null){
				continue;
			}
			LinkedDevice linkedDevice = new LinkedDevice();
			linkedDevice.setDevice(eqDevice);
			linkedDevice.setAction(element.getText());
			listDevice.add(linkedDevice);
		}
		return listDevice;
	}
}
