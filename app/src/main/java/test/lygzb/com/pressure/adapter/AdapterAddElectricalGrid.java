package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lygzb.zsmarthome.DefaultConfig;
import lygzb.zsmarthome.device.electrical.ElectricalCodes;
import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2016/4/10.
 */
public class AdapterAddElectricalGrid extends BaseAdapter{
	private Context context;
	private List<String> listCodes;

	public AdapterAddElectricalGrid(Context context, List<String> listCodes) {
		this.context = context;
		this.listCodes = listCodes;
	}

	@Override
	public int getCount() {
		return listCodes.size();
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
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();

			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.adapter_add_electricals, parent, false);
			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text);
			mViewHolder.imageView  = (ImageView) convertView.findViewById(R.id.imageEle);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		String code = listCodes.get(position);
		mViewHolder.textName.setText(DefaultConfig.getInstance().getMapElectricalCode().get(code));
		//mViewHolder.textName.setVisibility(View.GONE);

		switch (code){
			case ElectricalCodes.LAMP :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.lamp);
				break;
			case ElectricalCodes.CURTAIN :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.curtain);
				break;
			case ElectricalCodes.AIRCON :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.aircon);
				break;
			case ElectricalCodes.TV :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.tv);
				break;
			case ElectricalCodes.REFRIGERATOR :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.refrigerator);
				break;
			case ElectricalCodes.WASHER :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.washer);
				break;
			case ElectricalCodes.TAP :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.tap);
				break;
			case ElectricalCodes.JACK :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.jack);
				break;
			case ElectricalCodes.SCREEN :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.screen);
				break;
			case ElectricalCodes.WINDOW :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.window);
				break;
			case ElectricalCodes.CUSTOM_LAYOUT_ELECTRICAL :
				mViewHolder.imageView.setBackgroundResource(R.mipmap.custom);
				break;
			default:
				mViewHolder.imageView.setBackgroundResource(R.mipmap.custom);
		}
		return convertView;
	}

	static class ViewHolder {
		private  TextView textName;
		private ImageView imageView;
	}
}
