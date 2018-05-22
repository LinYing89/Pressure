package test.lygzb.com.pressure.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import test.lygzb.com.pressure.chain.ChainFragment;
import test.lygzb.com.pressure.chain.GuaguaFragment;
import test.lygzb.com.pressure.chain.LoopFragment;
import test.lygzb.com.pressure.chain.TimingFragment;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ChainSectionsPagerAdapter extends FragmentPagerAdapter {

	public ChainSectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;
		switch (position){
			case 0 :
				fragment = ChainFragment.newInstance(position);
				break;
			case 1:
				fragment = TimingFragment.newInstance(position);
				break;
			case 2:
				fragment = LoopFragment.newInstance(position);
				break;
			case 3:
				fragment = GuaguaFragment.newInstance(position);
				break;
			default:
				fragment = ChainFragment.newInstance(position);
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "连锁";
			case 1:
				return "定时";
			case 2:
				return "循环";
			case 3:
				return "呱呱";
		}
		return null;
	}
}
