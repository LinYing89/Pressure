package test.lygzb.com.pressure.network;

import android.util.Log;

import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.main.Main3Activity;

/**
 * Created by Administrator on 2016/3/28.
 */
public class CheckNetThread extends Thread{

	private int sleepTime = 10000;
	private boolean checking = true;

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean isChecking() {
		return checking;
	}

	public void setChecking(boolean checking) {
		this.checking = checking;
	}
	@Override
	public void run() {
		while(isChecking()){
			try {
				if(!Main3Activity.IS_ADMIN) {
					if (!WebClient.getInstance().getNetPot().isConnected() && !WebClient.getInstance().getNetPot().isConnecting()) {
						WebClient.getInstance().release();
						if(WebClient.getInstance().getNetPot().getSocket() == null || WebClient.getInstance().getNetPot().getSocket().isClosed()) {
							WebClient.getInstance().startConnectThread();
						}
					}
				}
				//SendMsgHelper.refreshState(false);
				//Log.e("CheckNetThread", "check");
				sleep(getSleepTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		super.run();
	}

	public void close(){
		setChecking(false);
		interrupt();
	}

}
