package test.lygzb.com.pressure.main;

import lygzb.zsmarthome.HomeMaster;
import lygzb.zsmarthome.ServerConfig;
import lygzb.zsmarthome.SystemConfig;
import lygzb.zsmarthome.User;
import lygzb.zsmarthome.device.collector.SecurityConfig;
import lygzb.zsmarthome.linkage.LinkageMaster;
import lygzb.zsmarthome.net.NetPotHelper;

/**
 * Created by Administrator on 2016/7/3.
 */
public class UserHelper {

	private static User USER;
	private static  ServerConfig serverConfig;

	public UserHelper(){
	}

	public static User getUser() {
		if(USER == null){
			USER = new User();
		}
		return USER;
	}

	public static void setUser(User user) {
		USER = user;
	}

	public static HomeMaster getHomeMaster(){
		return getUser().getHomeMaster();
	}

	public static LinkageMaster getLinkageMaster(){
		return getUser().getLinkageMaster();
	}

	public static SecurityConfig getSecurityConfig(){
		return getUser().getSecurityConfig();
	}

	public static SystemConfig getSystemConfig(){
		return getUser().getSystemConfig();
	}

	public static ServerConfig getServerConfig(){
		if(null == serverConfig){
			serverConfig = new ServerConfig();
		}
		return serverConfig;
	}
}
