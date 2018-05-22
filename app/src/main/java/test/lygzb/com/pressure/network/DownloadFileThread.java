package test.lygzb.com.pressure.network;

import android.os.Message;

import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * Created by Administrator on 2016/3/20.
 */
public class DownloadFileThread  extends WebFileBase{

	@Override
	public void run() {
		super.run();
		setCsocket(createConnection());
		if (null != getCsocket()) {
			FileIo fio = new FileIo(getCsocket().getDis(), getCsocket().getDos());
			//User.getIns().setName("zqn");
			//User.getIns().setPsd("123");
			boolean readed = fio.readFile(UserHelper.getUser().getName() + "_" + UserHelper.getUser().getPsd());
			close();
			if(null != Main3Activity.handler) {
				Message msg = Message.obtain();
				msg.arg1 = Main3Activity.DOWNLOAD;
				if(readed) {
					msg.arg2 = 1;
				}else{
					msg.arg2 = 2;
				}
				Main3Activity.handler.sendMessage(msg);
			}
		}else{
			if(null != Main3Activity.handler) {
				Message msg = Message.obtain();
				msg.arg1 = Main3Activity.DOWNLOAD;
				msg.arg2 = 0;
				Main3Activity.handler.sendMessage(msg);
			}
		}
	}
}
