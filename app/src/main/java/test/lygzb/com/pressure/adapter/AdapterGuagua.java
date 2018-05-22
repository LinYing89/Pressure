package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.guaguamouth.GuaGua;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdapterGuagua extends BaseAdapter {

	private Context context;
	private List<GuaGua> listGuagua;

	public AdapterGuagua(Context context, List<GuaGua> listGuagua){
		this.context = context;
		this.listGuagua = listGuagua;
	}

	@Override
	public int getCount() {
		return listGuagua.size();
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
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = vi.inflate(R.layout.adapter_list_loop, null);
		TextView textName  = (TextView) convertView.findViewById(R.id.text_loop_name);
		Switch switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);
		final GuaGua guaGua = listGuagua.get(position);
		textName.setText(guaGua.getName());
		switchEnable.setChecked(guaGua.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				synchronized (DeviceChainHelper.getIns()) {
					guaGua.setEnable(isChecked);
				}
			}
		});
		return convertView;
	}
}
