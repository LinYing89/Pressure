package test.lygzb.com.pressure.loop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.main.Main3Activity;

/**
 * Created by Administrator on 2016/6/5.
 */
public class SendChainThread extends Thread {

	private int logCount;

	public SendChainThread() {
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {

			}
		});
	}

	@Override
	public void run() {
		super.run();
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					while (!DeviceChainHelper.SCAN_OVER) {
						wait();
					}
				}
				if (logCount++ >= 10) {
					logCount = 0;
				}
				DeviceChainHelper.getIns().sendMessage();
				TimeUnit.MILLISECONDS.sleep(200);
				if (null != Main3Activity.checkLoopThread && Main3Activity.checkLoopThread.isAlive()) {
					synchronized (Main3Activity.checkLoopThread) {
						DeviceChainHelper.SCAN_OVER = false;
						Main3Activity.checkLoopThread.notify();
					}
				}
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
		}
	}

	public void close() {
		this.interrupt();
	}
}
