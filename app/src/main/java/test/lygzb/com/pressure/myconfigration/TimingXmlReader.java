package test.lygzb.com.pressure.myconfigration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.HomeMaster;
import lygzb.zsmarthome.configration.XmlHelper;
import lygzb.zsmarthome.configration.XmlTrigger;
import test.lygzb.com.pressure.loop.DurationTime;
import test.lygzb.com.pressure.timing.MyTiming;
import test.lygzb.com.pressure.timing.Timer;
import test.lygzb.com.pressure.timing.TimingHandler;

/**
 * Created by Administrator on 2016/6/14.
 */
public class TimingXmlReader {
	public static HomeMaster homeMaster;

	public static TimingHandler readXml(HomeMaster hm, String fileName, String encoding){
		homeMaster = hm;

		TimingHandler linkageMaster = TimingHandler.getIns();
		linkageMaster.getListMyTiming().clear();
		try {
			SAXReader reader = new SAXReader();
			if(encoding == null || encoding.isEmpty()){
				encoding = XmlHelper.ENCODING;
			}
			reader.setEncoding(encoding);
			Document document = reader.read(new File(fileName));
			Element node = document.getRootElement();
			linkageMaster.setEnable(Boolean.valueOf(node.attributeValue(MyXmlHelper.ATTR_ENABLE)));

			List<Element> listElementTiming = node.elements(MyXmlHelper.NODE_MYTIMING);
			for(Element elementTiming : listElementTiming){
				MyTiming loop = getLoopInElement(elementTiming);
				linkageMaster.getListMyTiming().add(loop);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return linkageMaster;
	}

	public static TimingHandler readXml(HomeMaster hm, String fileName){
		return readXml(hm, fileName, XmlHelper.ENCODING);
	}

	private static MyTiming getLoopInElement(Element elementTiming){
		MyTiming myTiming = new MyTiming();
		myTiming.setName(elementTiming.attributeValue(XmlHelper.ATTR_NAME));
		myTiming.setEnable(Boolean.valueOf(elementTiming.attributeValue(MyXmlHelper.ATTR_ENABLE)));

		List<Element> listTimerElement = elementTiming.elements(MyXmlHelper.NODE_TIMER);
		myTiming.setListTimer(getTimerInElement(listTimerElement));

		myTiming.setTrigger(XmlTrigger.readElementToSimpleTrigger(homeMaster, elementTiming));
		return myTiming;
	}

	private static List<Timer> getTimerInElement(List<Element> listTimerElement){
		List<Timer> listTimer = new ArrayList<>();
		for(Element element : listTimerElement) {
			Timer timer = new Timer();
			timer.setEnable(Boolean.valueOf(element.attributeValue(MyXmlHelper.ATTR_ENABLE)));
			DurationTime onTime = new DurationTime(element.attributeValue(MyXmlHelper.ATTR_ON_TIME));
			timer.setOnTime(onTime);
			DurationTime offTime = new DurationTime(element.attributeValue(MyXmlHelper.ATTR_OFF_TIME));
			timer.setOffTime(offTime);
			String strWeeks =  element.attributeValue(XmlHelper.ATTR_WEEK);
			timer.getWeekHelper().setWeeks(strWeeks);
			listTimer.add(timer);
		}
		return listTimer;
	}
}
