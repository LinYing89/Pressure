package test.lygzb.com.pressure.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lygzb.zsmarthome.net.NetPot;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.main.UserHelper;

public class NetPointAdapter extends BaseAdapter {

	private Context context;
	private List<NetPot> listNetPot;

	public NetPointAdapter(Context context){
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return listNetPot.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.search_device_list, null);
		TextView deviceName = (TextView)convertView.findViewById(R.id.changer);
		final ImageView redGreen = (ImageView)convertView.findViewById(R.id.red_green);
		
		final NetPot device = listNetPot.get(position);
		deviceName.setText(device.getName() + " " + device.toString());
		
		if(device.isConnecting()){
			redGreen.setBackgroundResource(R.mipmap.wifi_link_err);
			Animation anim = AnimationUtils.loadAnimation(
					context, R.anim.refresh_rotate);
			redGreen.startAnimation(anim);
		}else{
			if(device.isConnected()){
				redGreen.setBackgroundResource(R.mipmap.wifi_link_ok);
			}else{
				redGreen.setBackgroundResource(R.mipmap.wifi_link_err);
			}
		}
		return convertView;
	}

}
