package test.lygzb.com.pressure.electrical;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lygzb.zsmarthome.device.DevState;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.GuaGuaMouth;
import lygzb.zsmarthome.device.LinkedDevice;
import lygzb.zsmarthome.device.collector.Trigger;
import lygzb.zsmarthome.device.electrical.CustomButton;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.chain.ChildChainActivity;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * Created by Administrator on 2016/4/10.
 */
public class SceneActHelper {

	private List<Device> listEleAll;

	private List<Device> listEleOther;

	public SceneActHelper() {

	}

	public List<Device> getListOtherEle(Trigger trigger) {
		listEleAll = UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice();
		listEleOther = new ArrayList<>();
		List<Device> listEle = trigger.getListDevice();
		for (Device electrical : listEleAll) {
			if (!listEle.contains(electrical)) {
				listEleOther.add(electrical);
			}
		}
		return listEleOther;
	}

	/**
	 * 获取trigger中没有的呱呱嘴设备
	 * @param trigger
	 * @return
	 */
	public List<Device> getListOtherGuanguanMouth(Trigger trigger){
		List<Device> listEleAll = UserHelper.getHomeMaster().getHouseKeeper().getListDevice();
		List<Device> listOther = new ArrayList<>();
		List<Device> listEle = trigger.getListDevice();
		for (Device device : listEleAll) {
			if (device instanceof GuaGuaMouth && !listEle.contains(device)) {
				listOther.add(device);
			}
		}
		return listOther;
	}

//	public void showElectricalDialog(final Context context,
//									 final Trigger trigger, final Handler handler) {
//		final List<Device> listEle = getListOtherEle(trigger);
//		final String[] arryEle = new String[listEle.size()];
//		for (int i = 0; i < listEle.size(); i++) {
//			Device ele = listEle.get(i);
//			arryEle[i] = ele.getAddress().getName() + " " + ele.getName();
//		}
//		final LinkedDevice linkedDevice = new LinkedDevice();
//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//		dialog.setTitle(context.getString(R.string.input_or_choose_name))
//				.setSingleChoiceItems(arryEle, 0,
//						new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog,
//												int which) {
//								Device selectEle = listEle.get(which);
//								linkedDevice.setDevice(selectEle);
//								showEleActionDialog(context, linkedDevice, trigger, handler);
//								dialog.dismiss();
//							}
//						}).show();
//	}

	public void showElectricalDialog(final Context context,
									 final Trigger trigger, final Handler handler) {
		final List<Device> listEle = getListOtherEle(trigger);
		final String[] arryEle = new String[listEle.size()];
		for (int i = 0; i < listEle.size(); i++) {
			Device ele = listEle.get(i);
			arryEle[i] = ele.getName();
		}
		final Set<Integer> setSelected = new HashSet<>();
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("选择设备")
				.setMultiChoiceItems(arryEle, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
						if(b){
							setSelected.add(i);
						}else{
							setSelected.remove(i);
						}
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						for( int si : setSelected){
							LinkedDevice linkedDevice = new LinkedDevice();
							linkedDevice.setDevice(listEle.get(si));
							linkedDevice.setAction(DevState.OFF);
							if(null != trigger) {
								trigger.addLinkedDevice(linkedDevice);
							}
							if (handler != null) {
								Message message = Message.obtain();
								message.arg1 = ChildChainActivity.REFRESH_DEVICE_LIST;
								handler.sendMessage(message);
							}
						}
					}
				})
				.setNegativeButton("取消", null).show();
	}

	public void showGuaguaMouthDialog(final Context context,
									  final Trigger trigger, final Handler handler){
		final List<Device> listEle = getListOtherGuanguanMouth(trigger);
		final String[] arryEle = new String[listEle.size()];
		for (int i = 0; i < listEle.size(); i++) {
			Device ele = listEle.get(i);
			arryEle[i] = ele.getName();
		}
		final Set<Integer> setSelected = new HashSet<>();
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("选择呱呱嘴设备")
				.setMultiChoiceItems(arryEle, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i, boolean b) {
						if(b){
							setSelected.add(i);
						}else{
							setSelected.remove(i);
						}
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						for( int si : setSelected){
							LinkedDevice linkedDevice = new LinkedDevice();
							linkedDevice.setDevice(listEle.get(si));
							linkedDevice.setAction(DevState.OFF);
							if(null != trigger) {
								trigger.addLinkedDevice(linkedDevice);
							}
							if (handler != null) {
								Message message = Message.obtain();
								message.arg1 = ChildChainActivity.REFRESH_DEVICE_LIST;
								handler.sendMessage(message);
							}
						}
					}
				})
				.setNegativeButton("取消", null).show();
	}

	public void showEleActionDialog(Context context, final LinkedDevice selectEle, final Trigger trigger,
									final Handler handler) {
		final List<CustomButton> listButtons = selectEle.getDevice().getElectricalActions();
		final String[] arryEle = getActionArry(listButtons);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(context.getString(R.string.input_or_choose_name))
				.setSingleChoiceItems(arryEle, 0,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								selectEle.setAction(listButtons.get(which).getId());
								selectEle.setActionText(arryEle[which]);
								if(null != trigger) {
									trigger.addLinkedDevice(selectEle);
								}
								if (handler != null) {
									Message message = Message.obtain();
									message.arg1 = ChildChainActivity.REFRESH_DEVICE_LIST;
									handler.sendMessage(message);
								}
								dialog.dismiss();
							}
						}).show();
	}

	private String[] getActionArry(List<CustomButton> listButtons) {
		// listButtons = ElectricalHelper.getElectricalActions(selectEle);
		String[] arry = new String[listButtons.size()];
		for (int i = 0; i < listButtons.size(); i++) {
			arry[i] = listButtons.get(i).getText();
		}
		return arry;
	}

}
