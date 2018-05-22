package test.lygzb.com.pressure.network;

import android.os.Message;

import lygzb.zsmarthome.net.SearchDeviceResult;
import test.lygzb.com.pressure.systemset.SearchActivity;

/**
 * Created by Administrator on 2016/4/10.
 */
public class MySearchDeviceResult extends SearchDeviceResult{
	public static final int NO_MESSAGE = 0;
	public static final int REGRESH_OK = 1;

	@Override
	public void unSearched() {
		if(SearchActivity.handler != null) {
			Message msg = Message.obtain();
			msg.arg1 = NO_MESSAGE;
			SearchActivity.handler.sendMessage(msg);
		}
	}

	@Override
	public void updateUI() {
		if(SearchActivity.handler != null) {
			Message msg = Message.obtain();
			msg.arg1 = REGRESH_OK;
			SearchActivity.handler.sendMessage(msg);
		}
	}
}
