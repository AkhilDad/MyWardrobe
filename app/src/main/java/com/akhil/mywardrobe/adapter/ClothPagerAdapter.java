package com.akhil.mywardrobe.adapter;

import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by akhil on 24/11/15.
 */
public class ClothPagerAdapter extends PagerAdapter {

    private Cursor mCursor;

    public ClothPagerAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public int getCount() {
        return mCursor != null && mCursor.moveToFirst() ? mCursor.getCount() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
