package test.lygzb.com.pressure.application;

import android.graphics.Color;
import android.view.View;

/**
 *
 * @author linqiang
 *
 */
public class BackgroundHelper {

	private View view_back;

	public void changeBackgroundColor(View view){
		if(null == view_back){
			view_back = view;
			view.setBackgroundResource(android.R.color.holo_blue_light);
		}else{
			view_back.setBackgroundColor(Color.TRANSPARENT);
			view_back = view;
		}
	}
	
}
