package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lygzb.zsmarthome.ZEncoding;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.application.Constant;

public class MainSecondTitleAdapter extends BaseAdapter {

	private Context context;
	private List<ZEncoding> listElectrical;
	
	public MainSecondTitleAdapter(Context context, List<ZEncoding> listElectrical){
		this.context = context;
		this.listElectrical = listElectrical;
	}
	public int getCount() {
		return listElectrical.size();
	}

	public Object getItem(int arg0) {
		return arg0;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();

			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.adapter_child_electrical, parent, false);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constant.getEleItemHeight());
			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_name);
			mViewHolder.textState  = (TextView) convertView.findViewById(R.id.text_num);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		ZEncoding device = listElectrical.get(position);
		mViewHolder.textName.setText(device.getName());
		mViewHolder.textState.setText(device.getNum());
		return convertView;
	}

	static class ViewHolder {
		private TextView textName;
		private TextView textState;
	}

}
