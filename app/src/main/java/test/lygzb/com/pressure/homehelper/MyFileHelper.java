package test.lygzb.com.pressure.homehelper;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import lygzb.zsmarthome.FileHelper;
import lygzb.zsmarthome.User;
import lygzb.zsmarthome.device.Device;
import test.lygzb.com.pressure.chain.ChainHandler;
import test.lygzb.com.pressure.chain.DeviceChain;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.guaguamouth.GuaguaHandler;
import test.lygzb.com.pressure.loop.LoopHandler;
import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.myconfigration.ChainXmlReader;
import test.lygzb.com.pressure.myconfigration.ChainXmlWriter;
import test.lygzb.com.pressure.myconfigration.GuaGuaXmlReader;
import test.lygzb.com.pressure.myconfigration.GuaGuaXmlWriter;
import test.lygzb.com.pressure.myconfigration.LoopXmlReader;
import test.lygzb.com.pressure.myconfigration.LoopXmlWriter;
import test.lygzb.com.pressure.myconfigration.TimingXmlReader;
import test.lygzb.com.pressure.myconfigration.TimingXmlWriter;
import test.lygzb.com.pressure.timing.TimingHandler;

/**
 * Created by Administrator on 2016/3/4.
 */
public class MyFileHelper {

	public static final String LOOP_FILE_NAME = "loop.xml";
	public static final String CHAIN_FILE_NAME = "chain.xml";
	public static final String TIMING_FILE_NAME = "timing.xml";
	public static final String GUAGUA_FILE_NAME = "guauga.xml";

	public static void saveLoopHandlerXml(LoopHandler loopHandler){
		LoopXmlWriter.writeXml(getLoopXmlPath(), loopHandler);
	}

	public static void saveChainHandlerXml(ChainHandler chainHandler){
		ChainXmlWriter.writeXml(getChainXmlPath(), chainHandler);
	}

	public static void saveTimingHandlerXml(TimingHandler timingHandler){
		TimingXmlWriter.writeXml(getTimingXmlPath(), timingHandler);
	}

	public static void saveGuaguaHandlerXml(GuaguaHandler guaguaHandler){
		GuaGuaXmlWriter.writeXml(getGuaguaXmlPath(), guaguaHandler);
	}

	public static LoopHandler getLoopHandler(){
		return LoopXmlReader.readXml(UserHelper.getHomeMaster(), getLoopXmlPath());
	}

	public static ChainHandler getChainHandler(){
		return ChainXmlReader.readXml(UserHelper.getHomeMaster(), getChainXmlPath());
	}

	public static TimingHandler getTimingHandler(){
		return TimingXmlReader.readXml(UserHelper.getHomeMaster(), getTimingXmlPath());
	}

	public static GuaguaHandler getGuaguaHandler(){
		return GuaGuaXmlReader.readXml(UserHelper.getHomeMaster(), getGuaguaXmlPath());
	}

	public static String getLoopXmlPath(){
		return FileHelper.ROOT_PATH + File.separator + LOOP_FILE_NAME;
	}

	public static String getChainXmlPath(){
		return FileHelper.ROOT_PATH + File.separator + CHAIN_FILE_NAME;
	}

	public static String getTimingXmlPath(){
		return FileHelper.ROOT_PATH + File.separator + TIMING_FILE_NAME;
	}

	public static String getGuaguaXmlPath(){
		return FileHelper.ROOT_PATH + File.separator + GUAGUA_FILE_NAME;
	}

	public static void myInitConfig(User user){
		if(null == user){
			return;
		}
		//user.getHomeMaster().getClimateButler().stopQueryThread();
		FileHelper.initConfig(user);
		getLoopHandler();
		getChainHandler();
		getTimingHandler();
		getGuaguaHandler();
		for (Device device : user.getHomeMaster().getHouseKeeper().getListCtrlableDevice()) {
			DeviceChain dc = new DeviceChain();
			dc.setDevice(device);
			DeviceChainHelper.getIns().getListDeviceChain().add(dc);
		}
		//user.getHomeMaster().getClimateButler().startQueryThread(new MyClimateQueryProcess());
	}
	/**
	 * get the file path of root
	 * @param context
	 * @return
	 */
	public static String getRootFilePath(Context context){
		return context.getFilesDir().getPath();
	}

	public static String getSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString() + File.separator;
		}
		return null;
	}

	/**
	 * get zhibo file path in sdcard
	 * @return
	 */
	public static String getZhiBoFile(){
		String sdDir = getSDPath();
		String zhiboFilePath = null;
		if(null != sdDir){
			zhiboFilePath = sdDir+"zhibo" + File.separator;
		}
		return zhiboFilePath;
	}

	/**
	 * get remote file path in zhibo directory
	 * @return
	 */
	public static String getRemoteFilePath(){
		String sdDir = getZhiBoFile();
		String remotePath = null;
		if(null != sdDir){
			remotePath = sdDir + "遥控器" + File.separator;
		}
		return remotePath;
	}

	public static String getLogPath(){
		String sdDir = getZhiBoFile();
		String logPath = null;
		if(null != sdDir){
			logPath = sdDir + "logTxt.txt";
		}
		return  logPath;
	}

	/**
	 * get backup directory in zhibo directory
	 * @return
	 */
	public String getSDBackupsPath(){
		String sdDir = getZhiBoFile();
		String backupsPath = null;
		if(null != sdDir){
			backupsPath = sdDir + "backups" + File.separator;
		}
		return backupsPath;
	}

	/**
	 * get the backup file directory for the file name
	 * @param name
	 * @return
	 */
	public String getSDBackupsName(String name){
		String sdDir = getSDBackupsPath();
		String backupsNamePath = null;
		if(null != sdDir){
			backupsNamePath = sdDir + name + File.separator;
		}
		return backupsNamePath;
	}

	/**
	 * get projector directory
	 * @param context
	 * @return
	 */
	public static String getProjectFilePath(Context context){
		String dataPath = context.getFilesDir().getPath();
		return dataPath;
	}

	/**
	 * get shared_pref director
	 * @param context
	 * @return
	 */
	public static String getSharedPath(Context context){
		String dataPath = getProjectFilePath(context) + File.separator + "shared_prefs" + File.separator;
		return dataPath;
	}

	public boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);

		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return deleteFile(sPath);
			} else {
				return deleteDirectory(sPath);
			}
		}
	}

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			}
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}
