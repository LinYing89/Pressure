package test.lygzb.com.pressure.network;

import android.os.Message;
import android.util.Log;

import lygzb.zsmarthome.User;
import lygzb.zsmarthome.device.Coordinator;
import lygzb.zsmarthome.device.DefaultController;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.DeviceAssistent;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.StudyRemote;
import lygzb.zsmarthome.device.collector.AlarmDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.electrical.Electrical;
import lygzb.zsmarthome.net.MessageAnalysiser;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.systemset.SearchActivity;

/**
 * 分析服务器返回的信息
 * Created by Administrator on 2016/3/13.
 */
class MyWebMessageAnalysiser extends MessageAnalysiser {

	MyWebMessageAnalysiser(User user) {
		super(user);
	}

	@Override
	public boolean judgeMsgFormate(String msg) {
		return true;
	}

	@Override
	public void analysisReceiveMessage(String msg) {
		analysissAllMsg(msg);
	}

	@Override
	public synchronized void analysisSingleMsg(String strData) {
		String msg = strData.replaceAll("\n", "").replaceAll("\r", "").trim();
		if(msg.contains("#")) {
			msg = msg.substring(0, msg.indexOf("#"));
		}
		//String msg = strData;
		//Log.e("MyWebMsg strData", "#" + strData+"?");
		//Log.e("MyWebMsg msg", msg.length()+"!");
		if(msg.equals("UNOK")) {
			WebClient.getInstance().hartCount = 0;
			WebClient.getInstance().webConnectProgress = WebConnectProgress.HART;
		}else if(msg.contains("HEART")) {
			if(msg.equals(HeartType.HEART.toString())){
                if(WebClient.HEART_TYPE != HeartType.HEART){
                    WebClient.HEART_TYPE = HeartType.HEART;
                }
            }else if(msg.equals(HeartType.HEART_S.toString())){
                if(WebClient.HEART_TYPE != HeartType.HEART_S){
                    WebClient.HEART_TYPE = HeartType.HEART_S;
                }
            }
			WebClient.getInstance().hartCount = 0;
		}else if(msg.startsWith("RF")){
			//更新状态
			SendMsgHelper.refreshState(true);
		}else if(msg.startsWith("C")) {
			//网页控制命令"C"表示控制命令, like: CB10001:31
			if (msg.length() >= 5) {
				String devMsg = msg.substring(1);
				Device eqDev = UserHelper.getHomeMaster().getEqualsDeviceFromFeedback(devMsg);
				if (null != eqDev) {

					//控制命令，更改档位
					if (msg.contains(":")) {
						try {
                            //通知网页收到，让网页改变档位
                            WebClient.getInstance().sendMessage("$G" + msg.substring(1) + "#00");
							int index = msg.indexOf(":") + 1;
							String state = msg.substring(index, index + 1);
							if(!state.equals("0")){
                                //不是自动信息
								if(eqDev.getDeviceModel() == EDeviceModel.LOCAL) {
									SendMsgHelper.sendMessage("$" + msg + "#00");
								}
                            }
							if(eqDev instanceof DefaultController){
								String num = msg.substring(index + 1);
								Device dd = ((DefaultController) eqDev).getElectrical(Integer.parseInt(num));
								dd.setGear(Integer.valueOf(state));
							}else {
								eqDev.setGear(Integer.valueOf(state));
							}
//							if (null != SearchActivity.handler) {
//								Message message = Message.obtain();
//								message.arg1 = SearchActivity.REFRESH_ELE_LIST;
//								SearchActivity.handler.sendMessage(message);
//							}
							if (null != ElectricalCtrlFragment.handler) {
								Message message = Message.obtain();
								message.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
								ElectricalCtrlFragment.handler.sendMessage(message);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}else if(msg.startsWith("Q")) {
			//"Q"表示查询命令 like: QB10001:31
			if (msg.length() >= 5) {
				String devMsg = msg.substring(1);
				Device eqDev = UserHelper.getHomeMaster().getEqualsDeviceFromFeedback(devMsg);
				if (null != eqDev) {
					SendMsgHelper.sendMessage("$" + msg + "#00");
				}
			}
		}else if(msg.startsWith("I")){
			//推送或响应命令 like: IB10001:81
			Log.e("MyWebMessage服务器返回", msg);
            int index = msg.indexOf(":");
            if(index != -1 && (index + 2) <= msg.length()){
                String state = msg.substring(index + 1);
				String stateHead = state.substring(0, 1);
				String devCoding = msg.substring(1);
				Device myDev = UserHelper.getHomeMaster().getEqualsDeviceFromFeedback(devCoding);
				if(null == myDev){
					return;
				}

                if(stateHead.equals("a")) {
                    Log.e("MyWebMessage模式返回", msg);
                    //设置模式时服务器响应命令
                    if (null != SearchActivity.deviceModelHelper) {
						if(myDev == SearchActivity.deviceModelHelper.getDevToSet()
								&& null != SearchActivity.handler) {
							Log.e("MyWebMessage模式返回", "通知电器列表界面");
							Message message = Message.obtain();
							message.arg1 = SearchActivity.SET_MODEL_RESPONSE;
							message.arg2 = 0;
							SearchActivity.handler.sendMessage(message);
						}
                    }
                }else if(stateHead.equals("9")){
//                    String devCoding = msg.substring(1, index);
//                    Device myDev = UserHelper.getHomeMaster().getEqualsDeviceFromFeedback(devCoding);
//                    if(null != myDev){
//                        String stateValue = state.substring(1, 2);
//                        if(stateValue.equals("1")){
//                            myDev.setGear(-1);
//                        }else{
//                            myDev.setGear(4);
//                        }
//                        if(null != ElectricalCtrlFragment.handler){
//                            Message message = Message.obtain();
//                            message.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
//                            ElectricalCtrlFragment.handler.sendMessage(message);
//                        }
//                        WebClient.getInstance().sendMessage("$" + msg);
//                    }
                }else{
                    //心跳等其他报文
                    if(null != SearchActivity.deviceModelHelper){
						if(SearchActivity.deviceModelHelper.getToDeviceModel() == EDeviceModel.REMOTE
								&& SearchActivity.deviceModelHelper.getDevToSet() == myDev
								&& null != SearchActivity.handler) {
							Message message = Message.obtain();
							message.arg1 = SearchActivity.SET_MODEL_RESPONSE;
							message.arg2 = 1;
							SearchActivity.handler.sendMessage(message);
						}
                    }
                    //如果设备不是远程模式，设为远程模式
                    if(myDev.getDeviceModel() == EDeviceModel.LOCAL){
                        myDev.setDeviceModel(EDeviceModel.REMOTE);
                    }
                    super.analysisSingleMsg(devCoding + "#00");
                }
            }
		}else if(msg.contains("LOG1")){
			Log.e("MyWebMsg", "LOG1");
			Main3Activity.LOG_REC = true;
		}else if(msg.contains("LOG2")){
			Log.e("MyWebMsg", "LOG2");
			Main3Activity.LOG_REC = false;
		}else if(msg.contains("LOG3")){
			Log.e("MyWebMsg", "LOG3");
			Main3Activity.LOG_SED = true;
		}else if(msg.contains("LOG4")){
			Log.e("MyWebMsg", "LOG4");
			Main3Activity.LOG_SED = false;
		} else{
			//Log.e("MyWebMsg msg", "#" + msg+"???");
			//super.analysisSingleMsg(msg + "#");
			//analysisSingleMsg(msg);
		}
	}

//	private void sendMyDevices(){
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append("$!");
//		for(Device device : UserHelper.getHomeMaster().getHouseKeeper().getListDevice()){
//			stringBuilder.append(":");
//			stringBuilder.append(device.getCoding());
//		}
//		stringBuilder.append("#00");
//		WebClient.getInstance().sendMessage(stringBuilder.toString());
//		Log.e("MyWeb sendMyDevcies", stringBuilder.toString());
//	}

	@Override
	public void coordinatorFeedback(Coordinator coordinator, String feedbackMsg) {

	}

	@Override
	public void simpleDeviceFeedback(Device device, String feedbackMsg) {
		Log.e("MyWebMessageAnalysiser", "simpleDeviceFeedback " + feedbackMsg);
        if(ElectricalCtrlFragment.handler != null){
            Message msg = Message.obtain();
            msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
            ElectricalCtrlFragment.handler.sendMessage(msg);
        }
		if(device instanceof DefaultController){
			DefaultController dc = (DefaultController)device;
			for (Electrical ele : dc.getListElectrical()) {
				if (ele.isGearChanged()) {
					String state = "$G" + ele.getController().getCoding() + ":" + ele.getGear() + ele.getNum() + "#00";
					WebClient.getInstance().sendMessage(state);
					ele.setGearChanged(false);
				}
			}
		}
	}

	@Override
	public void alarmDeviceFeedback(AlarmDevice alarmDevice, String feedbackMsg) {

	}

	@Override
	public void climateDeviceFeedback(ClimateDevice climateDevice, String feedbackMsg) {

	}

	@Override
	public void studyRemoteFeedback(StudyRemote device, String s) {

	}

	@Override
	public void stateFeedback(String s) {

	}

	@Override
	public void heartbeat(String s) {

	}

	@Override
	public void allMessageEnd() {
		if(null != SearchActivity.handler){
			Message message = Message.obtain();
			message.arg1 = SearchActivity.REFRESH_ELE_LIST;
			SearchActivity.handler.sendMessage(message);
		}
	}

	@Override
	public void singleMessageEnd(Device device, String s) {

	}
}
