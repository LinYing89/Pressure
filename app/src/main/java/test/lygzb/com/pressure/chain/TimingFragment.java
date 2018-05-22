package test.lygzb.com.pressure.chain;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterTiming;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.homehelper.MyFileHelper;
import test.lygzb.com.pressure.timing.ChildTimingActivity;
import test.lygzb.com.pressure.timing.TimingHandler;

public class TimingFragment extends Fragment {
	private static final String ARG_PARAM1 = "param1";

	public static MyHandler handler;
	public static final int REFRESH_LIST = 1;

	private CheckBox checkBoxEnable;
	private Button btnAdd;
	private ListView listViewTiming;

	private AdapterTiming adapterTiming;

	public TimingFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment TimingFragment.
	 */
	public static TimingFragment newInstance(int param1) {
		TimingFragment fragment = new TimingFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loop, container, false);
		checkBoxEnable = (CheckBox)view.findViewById(R.id.check_enable);
		btnAdd = (Button)view.findViewById(R.id.btn_add);
		listViewTiming = (ListView)view.findViewById(R.id.list_loop);
		checkBoxEnable.setChecked(TimingHandler.getIns().isEnable());
		setListener();
		setListTiming();
		handler= new MyHandler(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MyFileHelper.saveTimingHandlerXml(TimingHandler.getIns());
		handler = null;
	}

	private void setListener(){
		checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
		btnAdd.setOnClickListener(onClickListener);
		listViewTiming.setOnItemClickListener(onItemClickListener);
		listViewTiming.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListTiming(){
		adapterTiming = new AdapterTiming(TimingFragment.this.getContext(), TimingHandler.getIns().getListMyTiming());
		listViewTiming.setAdapter(adapterTiming);
	}

	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			TimingHandler.getIns().setEnable(isChecked);
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChildTimingActivity.ADD = true;
			TimingFragment.this.startActivity(new Intent(TimingFragment.this.getContext(), ChildTimingActivity.class));
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TimingHandler.getIns().setSelectedTiming(TimingHandler.getIns().getListMyTiming().get(position));
			ChildTimingActivity.ADD = false;
			TimingFragment.this.startActivity(new Intent(TimingFragment.this.getContext(), ChildTimingActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			TimingHandler.getIns().setSelectedTiming(TimingHandler.getIns().getListMyTiming().get(position));
			showElectricalPopUp(view);
			return true;
		}
	};

	public void showElectricalPopUp(View v) {
		Button layoutDelete = new Button(this.getContext());
		layoutDelete.setText("删除");
		final PopupWindow popupWindow = new PopupWindow(layoutDelete, Constant.displayWidth,
				Constant.getEleHeight());

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation(this.getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				TimingHandler.getIns().getListMyTiming().remove(TimingHandler.getIns().getSelectedTiming());
				adapterTiming.notifyDataSetChanged();
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<TimingFragment> mActivity;

		MyHandler(TimingFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			final TimingFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_LIST:
					theActivity.adapterTiming.notifyDataSetChanged();
					break;
			}

		}
	};
}
