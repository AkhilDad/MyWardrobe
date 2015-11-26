package com.akhil.mywardrobe.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.akhil.mywardrobe.R;
import com.akhil.mywardrobe.database.MyWardrobeDatabase;
import com.akhil.mywardrobe.helper.ImageHelper;

/**
 * Created by akhil on 24/11/15.
 */
public class ClothPagerAdapter extends PagerAdapter {

    private Cursor mCursor;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ClothPagerAdapter(Context context, Cursor cursor) {
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
        mContext = context;
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
        View view = mLayoutInflater.inflate(R.layout.row_cloth_item, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_cloth_image);
        if (mCursor.moveToPosition(position)) {
            String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MyWardrobeDatabase.Column.IMAGE_PATH));
            Log.e("Path-->"+path,ImageHelper.getImageFromPath(mContext, path)+"position -> "+position);
            imageView.setImageBitmap(ImageHelper.getImageFromPath(mContext, path));
            imageView.setBackgroundResource(R.drawable.ic_action_favorite);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container.findViewById(R.id.ll_container));
    }
}
