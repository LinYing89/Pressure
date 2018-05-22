package test.lygzb.com.pressure.loop;

import android.util.Log;

import lygzb.zsmarthome.device.DevState;

/**
 * Created by Administrator on 2016/5/30.
 */
public class LoopThread extends Thread {

	private Loop loop;
	private boolean isRunning;

	public LoopThread(){

	}

	public LoopThread(Loop loop){
		setLoop(loop);
	}

	public Loop getLoop() {
		return loop;
	}

	public void setLoop(Loop loop) {
		this.loop = loop;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	@Override
	public void run() {
		if(getLoop() == null){
			return;
		}
		if(isRunning()){
			return;
		}
		if(loop.getListDuration().isEmpty()){
			return;
		}
		setRunning(true);
		try {
			//count == -1 表示无限循环
			int count = getLoop().getLoopCount();
			int i = 0;
			Duration duration;
			while (LoopHandler.getIns().isEnable() && loop.isEnable()  && getLoop().getResult()){
				//Log.e("LoopThread", "?" + count);
				if(count != -1 && count <= 0){
					break;
				}
				synchronized (getLoop().getListDuration()) {
					if (i >= getLoop().getListDuration().size()) {
						i = 0;
					}
					duration = loop.getListDuration().get(i);
				}
				loop.execute(DevState.ON);
				sleep(duration.getOnTime().getDurationMS());
				if(!loop.isEnable()){
					break;
				}
				loop.execute(DevState.OFF);
				sleep(duration.getOffTime().getDurationMS());
				if(count != -1){
					count--;
				}
				i++;
			}
			Log.e("LoopThread", "over");
			loop.execute(null);
			setRunning(false);
		}catch (Exception e){
			//Log.e("LoopThread", "?Exception");
			e.printStackTrace();
			setRunning(false);
			loop.execute(null);
		}
		setRunning(false);
	}
}
