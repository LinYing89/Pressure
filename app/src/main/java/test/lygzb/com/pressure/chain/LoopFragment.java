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
import test.lygzb.com.pressure.adapter.AdapterLoop;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.homehelper.MyFileHelper;
import test.lygzb.com.pressure.loop.ChildLoopActivity;
import test.lygzb.com.pressure.loop.LoopHandler;

public class LoopFragment extends Fragment {

	private static final String ARG_PARAM1 = "param1";

	public static MyHandler handler;
	public static final int REFRESH_LIST = 1;

	private CheckBox checkBoxEnable;
	private Button btnAdd;
	private ListView listViewLoop;

	private AdapterLoop adapterLoop;

	public LoopFragment() {
		// Required empty public constructor
	}

	public static LoopFragment newInstance(int param1) {
		LoopFragment fragment = new LoopFragment();
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
		listViewLoop = (ListView)view.findViewById(R.id.list_loop);
		checkBoxEnable.setChecked(LoopHandler.getIns().isEnable());
		setListener();
		setListLoop();
		handler= new MyHandler(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MyFileHelper.saveLoopHandlerXml(LoopHandler.getIns());
		handler = null;
	}

	private void setListener(){
		checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
		btnAdd.setOnClickListener(onClickListener);
		listViewLoop.setOnItemClickListener(onItemClickListener);
		listViewLoop.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListLoop(){
		adapterLoop = new AdapterLoop(LoopFragment.this.getContext(), LoopHandler.getIns().getListLoop());
		listViewLoop.setAdapter(adapterLoop);
	}

	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			LoopHandler.getIns().setEnable(isChecked);
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChildLoopActivity.ADD = true;
			LoopFragment.this.startActivity(new Intent(LoopFragment.this.getContext(), ChildLoopActivity.class));
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			LoopHandler.getIns().setSelectedLoop(LoopHandler.getIns().getListLoop().get(position));
			ChildLoopActivity.ADD = false;
			LoopFragment.this.startActivity(new Intent(LoopFragment.this.getContext(), ChildLoopActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			LoopHandler.getIns().setSelectedLoop(LoopHandler.getIns().getListLoop().get(position));
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
				LoopHandler.getIns().getListLoop().remove(LoopHandler.getIns().getSelectedLoop());
				adapterLoop.notifyDataSetChanged();
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<LoopFragment> mActivity;

		MyHandler(LoopFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			final LoopFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_LIST:
					theActivity.adapterLoop.notifyDataSetChanged();
					break;
			}

		}
	};
}
