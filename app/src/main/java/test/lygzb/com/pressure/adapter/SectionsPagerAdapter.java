package test.lygzb.com.pressure.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import test.lygzb.com.pressure.main.ClimateFragment;
import test.lygzb.com.pressure.main.ElectricalCtrlFragment;

/**
 * Created by Administrator on 2016/5/29.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class below).
		Fragment fragment = null;
		//position = 1;
		switch (position){
			case 0 :
				fragment = ElectricalCtrlFragment.newInstance(position);
				break;
			case 1 :
				fragment = ClimateFragment.newInstance(position);
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "电器列表";
			case 1:
				return "仪表列表";
		}
		return null;
	}
}