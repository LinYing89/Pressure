package test.lygzb.com.pressure.network;

import android.util.Log;

import java.io.DataOutputStream;

import lygzb.zsmarthome.net.NetPot;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * connect to the web server
 * Created by Administrator on 2016/1/14.
 */
public class WebClient {

	public WebConnectProgress webConnectProgress = WebConnectProgress.USER;
    public static HeartType HEART_TYPE = HeartType.HEART;
	public int hartCount;

    private static WebClient webClient;
	public static String URL_ROOT = "http://051801.cn/sd";

	/**
	 * 心跳，不同步数据
	 */
	private final String HEART_NOT_SYN="$HEART#";
	/**
	 * 心跳，同步数据
	 */
	private final String HEART_SYN="$HEART_S#";
	private Thread thread;

    private NetPot netPot;
	private DataOutputStream dos;

    private WebClient(){
		URL_ROOT = "http://" + UserHelper.getServerConfig().SERVER_IP + "/sd";
		UserHelper.getServerConfig().SERVER_MSG_PORT = 4047;
		netPot = new NetPot();
		netPot.setIp(UserHelper.getServerConfig().SERVER_IP);
		netPot.setPort(UserHelper.getServerConfig().SERVER_MSG_PORT);
    }

    public static WebClient getInstance() {
        if (webClient == null) {
            webClient = new WebClient();
        }
        return webClient;
    }

	public static String getLoginUrl(){
		return URL_ROOT + "/servlet/ClientLoginServlet";
	}

	public static String getVersionUrl(){
		return URL_ROOT + "/servlet/VersionServlet";
	}

	public static String getDownloadUrl(){
		return URL_ROOT + "/download/hama.apk";
	}
    /**
     * get client net pot
     * @return
     */
    public NetPot getNetPot() {
        if(netPot == null){
			netPot = new NetPot();
		}
		return netPot;
    }

    /**
     * set client net pot
     * @param netPot
     */
    public void setNetPot(NetPot netPot) {
        this.netPot = netPot;
    }

	public void setDos(DataOutputStream dos){
		this.dos = dos;
	}

	public DataOutputStream getDos(){
		return dos;
	}

    public void sendMessage(String msg) {
        if(HEART_TYPE == HeartType.HEART){
            return;
        }
		sendMsg(msg);
    }

	public void sendMsg(String msg){
		if(null == getDos()){
			return;
		}
		try {
			getDos().writeUTF(msg);

			getDos().flush();
			//getNetPot().sendMessage(msg);
			//Log.e("webclient sendok :", msg);
		}catch (Exception e){
			release();
			e.printStackTrace();
		}
	}

	/**
	 * 连接服务器
	 */
    public synchronized void startConnectThread() {
		webConnectProgress = WebConnectProgress.USER;
		hartCount = 0;
		getNetPot().startConnectThread(new MyWebConnectResult(new MyWebMessageAnalysiser(UserHelper.getUser())));
    }

	public void startSendUserMsgThread(){
		Log.e("WebClient", "startSendUserMsgThread");
		webConnectProgress = WebConnectProgress.USER;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (webConnectProgress != null) {
						if (webConnectProgress == WebConnectProgress.USER) {
							hartCount++;
							if (hartCount > 8) {
								release();
								return;
							}
							String user = "$UN" + UserHelper.getUser().getName() + "_" + UserHelper.getUser().getPsd();
							sendMsg(user);
							Thread.sleep(2000);
						} else if (webConnectProgress == WebConnectProgress.HART) {
							hartCount++;
							if (hartCount > 5) {
								release();
								return;
							}
							sendMsg(getHeartStr());
							Thread.sleep(6000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					release();
				}
			}
		});
		thread.start();
	}

	private String getHeartStr(){
		String strHeart = HEART_NOT_SYN;
		switch (HEART_TYPE){
			case HEART:
				strHeart = HEART_NOT_SYN;
				break;
			case HEART_S:
				strHeart = HEART_SYN;
				break;
		}
		return strHeart;
	}

    public void release() {
		hartCount = 0;
		if(null != getDos()){
			try {
				getDos().close();
				setDos(null);
			}catch (Exception e){
				setDos(null);
				e.printStackTrace();
			}
		}
		if(null != thread){
			thread.interrupt();
			thread = null;
		}
		getNetPot().release();
    }
}
