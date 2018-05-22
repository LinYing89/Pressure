package test.lygzb.com.pressure.loop;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.chain.DeviceChain;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.guaguamouth.GuaGua;
import test.lygzb.com.pressure.guaguamouth.GuaguaHandler;
import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.timing.MyTiming;
import test.lygzb.com.pressure.timing.TimingHandler;

/**
 * Created by Administrator on 2016/5/30.
 */
public class CheckLoopThread extends Thread {


	private int logCount;

	public CheckLoopThread() {
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {

			}
		});
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					while (DeviceChainHelper.SCAN_OVER) {
						wait();
					}
				}
				//一级循环未使能，不往下走
				if (LoopHandler.getIns().isEnable()) {
					for (Loop loop : LoopHandler.getIns().getListLoop()) {
						loop.run();
					}
				} else {
					for (DeviceChain deviceChain : DeviceChainHelper.getIns().getListDeviceChain()) {
						//如果临时变量值为-1，不进行赋值
						deviceChain.setLoop(-1);
					}
				}
				//遍历开始时将连锁与定时临时变量置为-1
				for (DeviceChain deviceChain : DeviceChainHelper.getIns().getListDeviceChain()) {
					deviceChain.initIChainTem();
				}

				//遍历连锁
				if (ChainHandler.getIns().isEnable()) {
					//Log.e("CheckLoopThread", "ChainHandler is enable");
					//遍历过程中更改iChainTem的值
					for (Chain chain : ChainHandler.getIns().getListChain()) {
						chain.run();
					}
				} else {
					for (DeviceChain deviceChain : DeviceChainHelper.getIns().getListDeviceChain()) {
						//如果临时变量值为-1，不进行赋值
						deviceChain.setChain(-1);
					}
				}
				//遍历定时
				if (TimingHandler.getIns().isEnable()) {
					//Log.e("CheckLoopThread", "TimingHandler is enable");
					for (MyTiming timing : TimingHandler.getIns().getListMyTiming()) {
						timing.run();
					}
				} else {
					for (DeviceChain deviceChain : DeviceChainHelper.getIns().getListDeviceChain()) {
						//如果临时变量值为-1，不进行赋值
						deviceChain.setTiming(-1);
					}
				}

				//遍历结束后将连锁与定时临时变量的值赋给chain与timing
				for (DeviceChain deviceChain : DeviceChainHelper.getIns().getListDeviceChain()) {
					//如果临时变量值为-1，不进行赋值
					if (deviceChain.getiChainTem() != -1) {
						deviceChain.setChain(deviceChain.getiChainTem());
					}
					//					if(deviceChain.getiTimingTem() != -1){
					deviceChain.setTiming(deviceChain.getiTimingTem());
					//					}
				}

				if (logCount++ >= 20) {
					logCount = 0;
				}
				//if(null != Main3Activity.sendChainThread && Main3Activity.sendChainThread.isAlive()) {
					synchronized (Main3Activity.sendChainThread) {
						DeviceChainHelper.SCAN_OVER = true;
						Main3Activity.sendChainThread.notify();
					}
				//}

				//报警
				if(GuaguaHandler.getIns().isEnable()){
					for(GuaGua guaGua : GuaguaHandler.getIns().getListGuagua()){
						guaGua.run();
					}
				}

				TimeUnit.MILLISECONDS.sleep(200);
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
		}
	}

	public void close() {
		this.interrupt();
	}
}
