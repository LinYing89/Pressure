package test.lygzb.com.pressure.homehelper;

import java.util.Map;

import lygzb.zsmarthome.DefaultConfig;
import lygzb.zsmarthome.DefaultConfigIniter;
import lygzb.zsmarthome.linkage.WeekHelper;

/**
 *
 * Created by Administrator on 2016/3/4.
 */
public class MyDefaultConfigIniter extends DefaultConfigIniter {

	public MyDefaultConfigIniter(){
		WeekHelper.ARRAY_WEEKS = new String[]{"日", "一", "二", "三", "四", "五", "六"};
	}
	@Override
	public void initMapAddress() {
		Map<String, String> mapAddr = DefaultConfig.getInstance().getMapAddressCode();
		mapAddr.clear();
		mapAddr.put("1", "客厅");
		mapAddr.put("2", "卧室");
		mapAddr.put("3", "书房");
		mapAddr.put("4", "厨房");
		mapAddr.put("5", "餐厅");
		mapAddr.put("6", "卫生间");
		mapAddr.put("7", "阳台");
		mapAddr.put("8", "走廊");
		mapAddr.put("9", "车库");
		mapAddr.put("A", "门口");
		mapAddr.put("B", "办公室");
		mapAddr.put("C", "会议室");
	}

	@Override
	public void initMapDevice() {
		Map<String, String> mapCtrl = DefaultConfig.getInstance().getMapDeviceCode();
		mapCtrl.clear();
		mapCtrl.put("A1", "协调器");
		mapCtrl.put("B1", "一路开关");
		mapCtrl.put("B2", "两路开关");
		mapCtrl.put("B3", "三路开关");
		mapCtrl.put("C1", "三态开关");
		mapCtrl.put("D1", "学习型遥控器");
		mapCtrl.put("R1", "呱呱嘴");
		mapCtrl.put("a1", "多功能安防");
		mapCtrl.put("a2", "多功能安防ZB");
		mapCtrl.put("c1", "门磁");
		mapCtrl.put("x1", "多功能采集器");
		mapCtrl.put("y1", "气压传感液位计");
		mapCtrl.put("z1", "燃气探测器");
		mapCtrl.put("3", "Jack");
		mapCtrl.put("4", "Infrared");
		mapCtrl.put("5", "RF");
		mapCtrl.put("6", "Study");

		mapCtrl.put("51", "Temperature");
		mapCtrl.put("52", "Humidity");
		mapCtrl.put("53", "Doorsensor");
		mapCtrl.put("59", "COz");
		mapCtrl.put("60", "HCHO");
		mapCtrl.put("61", "Beam");
		mapCtrl.put("62", "UV");
		mapCtrl.put("56", "GAS");
		mapCtrl.put("65", "Door_Card");
		mapCtrl.put("67", "TEMHUM");
		mapCtrl.put("80", "Compound_Collector");
	}

	@Override
	public void initMapElectrical() {
		Map<String, String> mapEle = DefaultConfig.getInstance().getMapElectricalCode();
		mapEle.clear();
		mapEle.put("1", "窗帘");
		mapEle.put("2", "电视");
		mapEle.put("3", "空调");
		mapEle.put("4", "投影仪");
		mapEle.put("5", "幕布");
		mapEle.put("6", "升降架");
		mapEle.put("7", "自定义电器");
		mapEle.put("10", "灯");
		mapEle.put("11", "窗户");
		mapEle.put("12", "阀");
		mapEle.put("13", "Jack");
		mapEle.put("14", "Refrigerator");
		mapEle.put("15", "washer");
		mapEle.put("16", "Microwave");
		mapEle.put("17", "Stereo");
		mapEle.put("18", "Tap");
	}

	@Override
	public void initMapAction() {
		Map<String, String> mapAction = DefaultConfig.getInstance().getMapActionText();
		mapAction.clear();
		mapAction.put("2", "STATE");
		mapAction.put("3", "开");
		mapAction.put("4", "关");
		mapAction.put("5", "停");
		mapAction.put("6", "ON_KEEP_TIME");
		mapAction.put("7", "STATUS");
	}

	@Override
	public void initMapCurtainAction() {
		Map mapAction = DefaultConfig.getInstance().getMapCurtainAction();
		mapAction.clear();
		mapAction.put("01", "开");
		mapAction.put("02", "关");
		mapAction.put("03", "停");
	}

	@Override
	public void initMapScene() {
		Map<String, String> mapScene = DefaultConfig.getInstance().getMapDefaultScene();
		mapScene.clear();
		mapScene.put("00", "回家");
		mapScene.put("01", "就寝");
		mapScene.put("02", "离家");
	}

}
