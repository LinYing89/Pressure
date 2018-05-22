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
import test.lygzb.com.pressure.adapter.AdapterChain;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.homehelper.MyFileHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChainFragment extends Fragment {

	private static final String ARG_PARAM1 = "param1";

	public static MyHandler handler;
	public static final int REFRESH_LIST = 1;

	private CheckBox checkBoxEnable;
	private Button btnAdd;
	private ListView listViewChain;

	private AdapterChain adapterChain;

	public ChainFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment ChainFragment.
	 */
	public static ChainFragment newInstance(int param1) {
		ChainFragment fragment = new ChainFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chain2, container, false);
		checkBoxEnable = (CheckBox)view.findViewById(R.id.check_enable);
		btnAdd = (Button)view.findViewById(R.id.btn_add);
		listViewChain = (ListView)view.findViewById(R.id.list_chain);
		checkBoxEnable.setChecked(ChainHandler.getIns().isEnable());
		setListener();
		setListChain();
		handler= new MyHandler(this);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MyFileHelper.saveChainHandlerXml(ChainHandler.getIns());
		handler = null;
	}

	private void setListener(){
		checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
		btnAdd.setOnClickListener(onClickListener);
		listViewChain.setOnItemClickListener(onItemClickListener);
		listViewChain.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListChain(){
		adapterChain = new AdapterChain(ChainFragment.this.getContext(), ChainHandler.getIns().getListChain());
		listViewChain.setAdapter(adapterChain);
	}

	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ChainHandler.getIns().setEnable(isChecked);
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChildChainActivity.ADD = true;
			ChainFragment.this.startActivity(new Intent(ChainFragment.this.getContext(), ChildChainActivity.class));
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ChainHandler.getIns().setSelectedChain(ChainHandler.getIns().getListChain().get(position));
			ChildChainActivity.ADD = false;
			ChainFragment.this.startActivity(new Intent(ChainFragment.this.getContext(), ChildChainActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			ChainHandler.getIns().setSelectedChain(ChainHandler.getIns().getListChain().get(position));
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
//		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
		layoutDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				ChainHandler.getIns().getListChain().remove(ChainHandler.getIns().getSelectedChain());
				adapterChain.notifyDataSetChanged();
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<ChainFragment> mActivity;

		MyHandler(ChainFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChainFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_LIST:
					theActivity.adapterChain.notifyDataSetChanged();
					break;
			}

		}
	}
}
