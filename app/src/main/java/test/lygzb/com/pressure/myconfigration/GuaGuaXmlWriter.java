package test.lygzb.com.pressure.myconfigration;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lygzb.zsmarthome.configration.XmlHelper;
import lygzb.zsmarthome.configration.XmlTrigger;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.event.AbstractEvent;
import test.lygzb.com.pressure.event.EventDevice;
import test.lygzb.com.pressure.event.EventTime;
import test.lygzb.com.pressure.guaguamouth.GuaGua;
import test.lygzb.com.pressure.guaguamouth.GuaguaHandler;
import test.lygzb.com.pressure.loop.Event;

/**
 * Created by Administrator on 2017/4/22.
 */

public class GuaGuaXmlWriter {
	public static void writeXml(String filePath, GuaguaHandler guaguaHandler, String encoding) {
		// create root element
		Element rootElement = DocumentHelper
				.createElement(MyXmlHelper.NODE_GUAGUA_HANDLER);
		Document document = DocumentHelper.createDocument(rootElement);

		rootElement.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(guaguaHandler.isEnable()));

		for (GuaGua guaGua : guaguaHandler.getListGuagua()) {
			writeGuaguaToElement(rootElement, guaGua);
		}

		OutputFormat format = new OutputFormat("    ", true);
		if(encoding == null || encoding.isEmpty()){
			encoding = XmlHelper.ENCODING;
		}
		format.setEncoding(encoding);
		XMLWriter outWriter;
		try {
			FileOutputStream foStream = new FileOutputStream(
					new File(filePath).getAbsoluteFile());
			// outWriter = new XMLWriter(new FileWriter(filePath), format);
			outWriter = new XMLWriter(foStream, format);
			try {
				outWriter.write(document);
				outWriter.flush();
			} finally {
				outWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeXml(String filePath, GuaguaHandler guaguaHandler) {
		writeXml(filePath, guaguaHandler, XmlHelper.ENCODING);
	}

	private static void writeGuaguaToElement(Element rootElement, GuaGua guaGua){
		Element elementChain = rootElement.addElement(MyXmlHelper.NODE_GUAGUA);
		elementChain.addAttribute(XmlHelper.ATTR_NAME, guaGua.getName());
		elementChain.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(guaGua.isEnable()));
		writeEventToChainElement(elementChain, guaGua.getListEvent());
		XmlTrigger.writeSimpleTriggerToElement(elementChain, guaGua.getTrigger());
	}

	private static void writeEventToChainElement(Element elementGuagua, List<AbstractEvent> listEvent){
		for(AbstractEvent event : listEvent){
			Element elementEvent = elementGuagua.addElement(MyXmlHelper.NODE_EVENT);
			elementEvent.addAttribute(MyXmlHelper.ATTR_STYLE, String.valueOf(event.getEventStyle()));
			elementEvent.addAttribute(MyXmlHelper.ATTR_SYMBOL, String.valueOf(event.getEventSymbol()));
			if(event instanceof EventDevice) {
				EventDevice eventDevice = (EventDevice)event;
				elementEvent.addAttribute(MyXmlHelper.ATTR_EVENT_STYLE, "device");
				Device device = eventDevice.getDevice();
				if (device instanceof Electrical) {
					Electrical ele = (Electrical) device;
					elementEvent.addAttribute(MyXmlHelper.ATTR_DEVICE, ele.getController().getCoding() + device.getCode() + "_" + device.getNum());
				} else {
					elementEvent.addAttribute(MyXmlHelper.ATTR_DEVICE, eventDevice.getDevice().getCoding());
				}
				elementEvent.addAttribute(MyXmlHelper.ATTR_VALUE, String.valueOf(eventDevice.getTriggerValue()));
			}else{
				EventTime eventTime = (EventTime)event;
				elementEvent.addAttribute(MyXmlHelper.ATTR_EVENT_STYLE, "timing");
				elementEvent.addAttribute(MyXmlHelper.ATTR_VALUE, String.valueOf(eventTime.getTimerAlarm().getDurationTime()));
			}
		}
	}
}
