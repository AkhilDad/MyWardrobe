package com.akhil.mywardrobe.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akhil.mywardrobe.R;
import com.akhil.mywardrobe.database.MyWardrobeDatabase;
import com.akhil.mywardrobe.helper.ImageHelper;

/**
 * Created by akhil on 24/11/15.
 */
public class ClothPagerAdapter extends PagerAdapter {

    private final int mResId;
    private Cursor mCursor;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ClothPagerAdapter(Context context, Cursor cursor, int resId) {
        mLayoutInflater = LayoutInflater.from(context);
        mCursor = cursor;
        mContext = context;
        mResId = resId;
    }

    @Override
    public int getCount() {
        return mCursor != null && mCursor.moveToFirst() ? mCursor.getCount() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Cursor swapCursor(Cursor data) {
        Cursor cursor = mCursor;
        mCursor = data;
        notifyDataSetChanged();
        return cursor;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(mResId, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_cloth_image);
        Log.e("instantiateItem-->","position -> "+position);
        if (!mCursor.isClosed() && mCursor.moveToPosition(position)) {
            String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MyWardrobeDatabase.Column.IMAGE_PATH));
            Log.e("Path-->"+path,ImageHelper.getImageFromPath(mContext, path)+"position -> "+position);
            imageView.setImageBitmap(ImageHelper.getImageFromPath(mContext, path));
            imageView.setBackgroundResource(R.drawable.ic_action_favorite);
        } else {
            Log.e("************", "*****Cursor closed******");
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
