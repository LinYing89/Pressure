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
import test.lygzb.com.pressure.loop.Duration;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventHandler;
import test.lygzb.com.pressure.loop.Loop;
import test.lygzb.com.pressure.loop.LoopHandler;

/**
 * Created by Administrator on 2016/5/31.
 */
public class LoopXmlWriter {

	public static void writeXml(String filePath, LoopHandler loopHandler, String encoding) {
		// create root element
		Element rootElement = DocumentHelper
				.createElement(MyXmlHelper.NODE_LOOP_HANDLER);
		Document document = DocumentHelper.createDocument(rootElement);

		rootElement.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(loopHandler.isEnable()));

		for (Loop loop : loopHandler.getListLoop()) {
			writeLoopToElement(rootElement, loop);
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

	public static void writeXml(String filePath, LoopHandler loopHandler) {
		writeXml(filePath, loopHandler, XmlHelper.ENCODING);
	}

	private static void writeLoopToElement(Element rootElement, Loop loop){
		Element elementLoop = rootElement.addElement(MyXmlHelper.NODE_LOOP);
		elementLoop.addAttribute(XmlHelper.ATTR_NAME, loop.getName());
		elementLoop.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(loop.isEnable()));
		elementLoop.addAttribute(MyXmlHelper.ATTR_LOOP_COUNT, String.valueOf(loop.getLoopCount()));
		writeDurationToLoopElement(elementLoop, loop.getListDuration());
		writeEventHandlerToLoopElement(elementLoop, loop.getListEventHandler());
		XmlTrigger.writeSimpleTriggerToElement(elementLoop, loop.getTrigger());
	}

	private static void writeDurationToLoopElement(Element elementLoop, List<Duration> listDuration){
		for(Duration duration : listDuration) {
			Element elementDuration = elementLoop.addElement(MyXmlHelper.NODE_DURATION);
			elementDuration.addAttribute(MyXmlHelper.ATTR_ON_TIME, duration.getOnTime().toXmlString());
			elementDuration.addAttribute(MyXmlHelper.ATTR_OFF_TIME, duration.getOffTime().toXmlString());
		}
	}

	private static void writeEventHandlerToLoopElement(Element elementLoop, List<EventHandler> listEventHandler){
		for(EventHandler eventHandler : listEventHandler) {
			Element elementEventHandler = elementLoop.addElement(MyXmlHelper.NODE_EVENT_HANDLER);
			elementEventHandler.addAttribute(XmlHelper.ATTR_NAME, eventHandler.getName());
			elementEventHandler.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(eventHandler.isEnable()));
			writeEventToEventHandlerElement(elementEventHandler, eventHandler.getListEvent());
		}
	}

	private static void writeEventToEventHandlerElement(Element elementEventHandler, List<Event> listEvent){
		for(Event event : listEvent){
			Element elementEvent = elementEventHandler.addElement(MyXmlHelper.NODE_EVENT);
			elementEvent.addAttribute(MyXmlHelper.ATTR_STYLE, String.valueOf(event.getEventStyle()));
			Device device = event.getDevice();
			if(device instanceof Electrical){
				Electrical ele = (Electrical)device;
				elementEvent.addAttribute(MyXmlHelper.ATTR_DEVICE, ele.getController().getCoding() + device.getCode() + "_" + device.getNum());
			}else{
				elementEvent.addAttribute(MyXmlHelper.ATTR_DEVICE, event.getDevice().getCoding());
			}
			elementEvent.addAttribute(MyXmlHelper.ATTR_SYMBOL, String.valueOf(event.getEventSymbol()));
			elementEvent.addAttribute(MyXmlHelper.ATTR_VALUE, String.valueOf(event.getTriggerValue()));
		}
	}
}
