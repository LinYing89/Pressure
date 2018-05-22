package test.lygzb.com.pressure.chain;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import test.lygzb.com.pressure.adapter.AdapterGuagua;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.guaguamouth.ChildGuaguaActivity;
import test.lygzb.com.pressure.guaguamouth.GuaguaHandler;
import test.lygzb.com.pressure.homehelper.MyFileHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GuaguaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuaguaFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";

	public static GuaguaFragment.MyHandler handler;
	public static final int REFRESH_LIST = 1;

	private CheckBox checkBoxEnable;
	private Button btnAdd;
	private ListView listViewGuagua;

	private AdapterGuagua adapterGuagua;

	public GuaguaFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment GuaguaFragment.
	 */
	public static GuaguaFragment newInstance(int param1) {
		GuaguaFragment fragment = new GuaguaFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guagua, container, false);
		checkBoxEnable = (CheckBox)view.findViewById(R.id.check_enable);
		btnAdd = (Button)view.findViewById(R.id.btn_add);
		listViewGuagua = (ListView)view.findViewById(R.id.list_guagua);
		checkBoxEnable.setChecked(GuaguaHandler.getIns().isEnable());

		handler= new GuaguaFragment.MyHandler(this);
		return view;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListener();
		setListChain();
	}

	private void setListener(){
		checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
		btnAdd.setOnClickListener(onClickListener);
		listViewGuagua.setOnItemClickListener(onItemClickListener);
		listViewGuagua.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListChain(){
		adapterGuagua = new AdapterGuagua(GuaguaFragment.this.getContext(), GuaguaHandler.getIns().getListGuagua());
		listViewGuagua.setAdapter(adapterGuagua);
	}

	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			GuaguaHandler.getIns().setEnable(isChecked);
		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ChildGuaguaActivity.ADD = true;
			GuaguaFragment.this.startActivity(new Intent(GuaguaFragment.this.getContext(), ChildGuaguaActivity.class));
		}
	};

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GuaguaHandler.getIns().setSelectedGuagua(GuaguaHandler.getIns().getListGuagua().get(position));
			ChildGuaguaActivity.ADD = false;
			GuaguaFragment.this.startActivity(new Intent(GuaguaFragment.this.getContext(), ChildGuaguaActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			GuaguaHandler.getIns().setSelectedGuagua(GuaguaHandler.getIns().getListGuagua().get(position));
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
				GuaguaHandler.getIns().getListGuagua().remove(GuaguaHandler.getIns().getSelectedGuagua());
				adapterGuagua.notifyDataSetChanged();
			}
		});
	}

	public static class MyHandler extends Handler {
		WeakReference<GuaguaFragment> mActivity;

		MyHandler(GuaguaFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final GuaguaFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_LIST:
					theActivity.adapterGuagua.notifyDataSetChanged();
					break;
			}

		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MyFileHelper.saveGuaguaHandlerXml(GuaguaHandler.getIns());
		handler = null;
	}
}
