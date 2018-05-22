package test.lygzb.com.pressure.application;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2016/4/12.
 */
public class BackListener implements View.OnClickListener {
	private Activity act;

	public BackListener(Activity act){
		this.act = act;
	}
	public void onClick(View v) {
		act.finish();
	}
}
