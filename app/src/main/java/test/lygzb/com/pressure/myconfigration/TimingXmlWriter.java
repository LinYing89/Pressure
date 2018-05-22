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
import test.lygzb.com.pressure.timing.MyTiming;
import test.lygzb.com.pressure.timing.Timer;
import test.lygzb.com.pressure.timing.TimingHandler;

/**
 * Created by Administrator on 2016/6/14.
 */
public class TimingXmlWriter {

	public static void writeXml(String filePath, TimingHandler timingHandler, String encoding) {
		// create root element
		Element rootElement = DocumentHelper
				.createElement(MyXmlHelper.NODE_MYTIMING_HANDLER);
		Document document = DocumentHelper.createDocument(rootElement);

		rootElement.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(timingHandler.isEnable()));

		for (MyTiming myTiming : timingHandler.getListMyTiming()) {
			writeTimingToElement(rootElement, myTiming);
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

	public static void writeXml(String filePath, TimingHandler timingHandler) {
		writeXml(filePath, timingHandler, XmlHelper.ENCODING);
	}

	private static void writeTimingToElement(Element rootElement, MyTiming timing){
		Element elementTiming = rootElement.addElement(MyXmlHelper.NODE_MYTIMING);
		elementTiming.addAttribute(XmlHelper.ATTR_NAME, timing.getName());
		elementTiming.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(timing.isEnable()));
		writeTimerToTimingElement(elementTiming, timing.getListTimer());
		XmlTrigger.writeSimpleTriggerToElement(elementTiming, timing.getTrigger());
	}

	private static void writeTimerToTimingElement(Element elementTiming, List<Timer> listTimer){
		for(Timer timer : listTimer) {
			Element elementTimer = elementTiming.addElement(MyXmlHelper.NODE_TIMER);
			elementTimer.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(timer.isEnable()));
			elementTimer.addAttribute(MyXmlHelper.ATTR_ON_TIME, timer.getOnTime().toXmlString());
			elementTimer.addAttribute(MyXmlHelper.ATTR_OFF_TIME, timer.getOffTime().toXmlString());
			elementTimer.addAttribute(XmlHelper.ATTR_WEEK, timer.getWeekHelper().getWeeksNum());
		}
	}
}
