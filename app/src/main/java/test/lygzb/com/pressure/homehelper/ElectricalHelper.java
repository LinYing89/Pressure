package test.lygzb.com.pressure.homehelper;

import android.widget.ImageView;

import lygzb.zsmarthome.ZEncoding;
import lygzb.zsmarthome.device.electrical.ECustomRemote;
import lygzb.zsmarthome.device.electrical.ElectricalCodes;
import lygzb.zsmarthome.device.electrical.IAircon;
import lygzb.zsmarthome.device.electrical.ICurtain;
import lygzb.zsmarthome.device.electrical.ITelevision;
import lygzb.zsmarthome.device.electrical.ThreeStateEle;
import test.lygzb.com.pressure.R;

/**
 * electrical factory
 * 
 * @author linqiang
 * 
 */
public class ElectricalHelper {
	
	public static void setImageBackground(ZEncoding ele, ImageView image){
		if(ele.getCode().equals(ElectricalCodes.LAMP)) {
			image.setBackgroundResource(R.mipmap.lamp);
		}else if(ele instanceof ThreeStateEle){
			image.setBackgroundResource(R.mipmap.custom);
		}else if(ele instanceof ICurtain){
			image.setBackgroundResource(R.mipmap.curtain);
		}else if(ele instanceof IAircon){
			image.setBackgroundResource(R.mipmap.aircon);
		}else if(ele instanceof ITelevision){
			image.setBackgroundResource(R.mipmap.tv);
		}else if(ele instanceof ECustomRemote){
			image.setBackgroundResource(R.mipmap.custom);
		}else {
			image.setBackgroundResource(R.mipmap.custom);
		}
	}
}
