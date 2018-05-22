package test.lygzb.com.pressure.network;

import android.os.Message;

import test.lygzb.com.pressure.main.Main3Activity;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * Created by Administrator on 2016/3/20.
 */
public class UploadFileThread extends WebFileBase {

	@Override
	public void run() {
		super.run();
		setCsocket(createConnection());
		if (null != getCsocket()) {
			FileIo fio = new FileIo(getCsocket().getDis(), getCsocket().getDos());
			boolean upload = fio.sendFiles(UserHelper.getUser().getName() + "_" + UserHelper.getUser().getPsd());
			close();
			//上传结束
			if(null != Main3Activity.handler) {
				Message msg = Message.obtain();
				msg.arg1 = Main3Activity.UPLOAD;
				if(upload) {
					msg.arg2 = 1;
				}else{
					msg.arg2 = 2;
				}
				Main3Activity.handler.sendMessage(msg);
			}
		}else{
			if(null != Main3Activity.handler) {
				Message msg = Message.obtain();
				msg.arg1 = Main3Activity.UPLOAD;
				msg.arg2 = 0;
				Main3Activity.handler.sendMessage(msg);
			}
		}
	}
}
