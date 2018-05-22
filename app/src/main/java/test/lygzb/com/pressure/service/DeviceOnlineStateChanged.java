package test.lygzb.com.pressure.service;

import android.os.Message;

import lygzb.zsmarthome.device.Coordinator;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.IDeviceOnlineStateChanged;
import test.lygzb.com.pressure.main.ClimateFragment;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;
import test.lygzb.com.pressure.network.WebClient;
import test.lygzb.com.pressure.systemset.SearchActivity;

/**
 * Created by linqiang on 2017/3/14.
 */

public class DeviceOnlineStateChanged implements IDeviceOnlineStateChanged {
    @Override
    public void changed() {
        if(ElectricalCtrlFragment.handler != null){
            Message msg = Message.obtain();
            msg.arg1 = ElectricalCtrlFragment.REFRESH_ELE_STATE;
            ElectricalCtrlFragment.handler.sendMessage(msg);
        }else if(null != ClimateFragment.handler) {
            //logger.info("climateDeviceFeedback ClimateFragment.handler.sendMessage");
            Message msg = Message.obtain();
            msg.arg1 = ClimateFragment.REFRESH_VALUE;
            ClimateFragment.handler.sendMessage(msg);
        }
        if(null != SearchActivity.handler){
			Message message = Message.obtain();
			message.arg1 = SearchActivity.REFRESH_ELE_LIST;
			SearchActivity.handler.sendMessage(message);
		}
    }

	@Override
	public void changed(Device device) {
		if(device instanceof Coordinator){
			//协调器
			return;
		}
		if(device.getDeviceModel() == EDeviceModel.REMOTE){
			//远程
			return;
		}
		WebClient.getInstance().sendMessage("$I" + device.getCoding() + ":24#00");
	}
}
