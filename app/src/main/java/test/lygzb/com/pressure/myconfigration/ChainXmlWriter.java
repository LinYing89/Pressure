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
import test.lygzb.com.pressure.loop.Event;

/**
 * Created by Administrator on 2016/6/3.
 */
public class ChainXmlWriter {

	public static void writeXml(String filePath, ChainHandler chainHandler, String encoding) {
		// create root element
		Element rootElement = DocumentHelper
				.createElement(MyXmlHelper.NODE_CHAIN_HANDLER);
		Document document = DocumentHelper.createDocument(rootElement);

		rootElement.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(chainHandler.isEnable()));

		for (Chain chain : chainHandler.getListChain()) {
			writeChainToElement(rootElement, chain);
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

	public static void writeXml(String filePath, ChainHandler chainHandler) {
		writeXml(filePath, chainHandler, XmlHelper.ENCODING);
	}

	private static void writeChainToElement(Element rootElement, Chain chain){
		Element elementChain = rootElement.addElement(MyXmlHelper.NODE_CHAIN);
		elementChain.addAttribute(XmlHelper.ATTR_NAME, chain.getName());
		elementChain.addAttribute(MyXmlHelper.ATTR_ENABLE, String.valueOf(chain.isEnable()));
		writeEventToChainElement(elementChain, chain.getListEvent());
		XmlTrigger.writeSimpleTriggerToElement(elementChain, chain.getTrigger());
	}

	private static void writeEventToChainElement(Element elementChain, List<Event> listEvent){
		for(Event event : listEvent){
			Element elementEvent = elementChain.addElement(MyXmlHelper.NODE_EVENT);
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
