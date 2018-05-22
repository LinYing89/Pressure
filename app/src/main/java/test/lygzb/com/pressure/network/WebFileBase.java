package test.lygzb.com.pressure.network;

import test.lygzb.com.pressure.main.UserHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public class WebFileBase extends Thread {

	private ClientSocket csocket;

	public ClientSocket getCsocket(){
		return csocket;
	}

	public void setCsocket(ClientSocket csocket){
		this.csocket = csocket;
	}

	@Override
	public void run() {
		super.run();
	}

	public ClientSocket createConnection() {
		UserHelper.getServerConfig().SERVER_FILE_PORT = 4048;
		csocket = new ClientSocket(UserHelper.getServerConfig().SERVER_IP, UserHelper.getServerConfig().SERVER_FILE_PORT);
		try {
			csocket.CreateConnection();
			System.out.print("连接服务器成功!" + "\n");
			return csocket;
		} catch (Exception e) {
			System.out.print("连接服务器失败!" + "\n");
			return null;
		}
	}

	public void close(){
		if(null != csocket){
			csocket.shutDownConnection();
		}
	}
}
