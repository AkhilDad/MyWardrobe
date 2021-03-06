package com.akhil.mywardrobe;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akhil.mywardrobe.adapter.ClothPagerAdapter;
import com.akhil.mywardrobe.database.MyWardrobeDatabase;
import com.akhil.mywardrobe.database.MyWardrobeDatabase.Column;
import com.akhil.mywardrobe.helper.AlarmHelper;
import com.akhil.mywardrobe.helper.ImageHelper;
import com.akhil.mywardrobe.helper.ImagePicker;
import com.akhil.mywardrobe.provider.MyWardrobeContentProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHIRTS_LOADER = 0;
    private static final int PANTS_LOADER = 1;
    private static final int FAVOURITES_LOADER = 2;
    private static final int PICK_IMAGE_ID_SHIRTS = 101; // any random number
    private static final int PICK_IMAGE_ID_PANTS = 102;


    @Bind(R.id.vp_shirts)
    protected ViewPager mShirtsVP;

    @Bind(R.id.vp_pants)
    protected ViewPager mPantsVP;

    @Bind(R.id.iv_add_pants)
    protected ImageView mAddPantsIV;

    @Bind(R.id.iv_add_shirts)
    protected ImageView mAddShirtsIV;

    @Bind(R.id.iv_fav)
    protected ImageView mFavIV;

    @Bind(R.id.tv_err_pants)
    protected TextView mPantsErrorTV;

    @Bind(R.id.iv_refresh)
    protected ImageView mRefreshIV;

    @Bind(R.id.tv_err_shirts)
    protected TextView mShirtsErrorTV;

    private int mCurrentShirtPosition;
    private int mCurrentPantPosition;

    private List<String> mShirtsPathList;
    private List<String> mPantsPathList;
    private boolean mIsShirtReset ;
    private boolean mIsPantsReset;

    @OnClick(R.id.iv_refresh)
    public void onRefreshClicked() {
        mRefreshIV.setImageResource(R.drawable.ic_action_refresh);
        mRefreshIV.setEnabled(false);
        mShirtsVP.setAdapter(mShirtsPagerAdapter);
        mPantsVP.setAdapter(mPantsPagerAdapter);
        getSupportLoaderManager().restartLoader(PANTS_LOADER, null, this);
        getSupportLoaderManager().restartLoader(SHIRTS_LOADER, null, this);

    }

    @OnClick(R.id.iv_add_pants)
    public void onAddShirtsClicked() {
        onPickImage(PICK_IMAGE_ID_PANTS);
    }

    @OnClick(R.id.iv_add_shirts)
    public void onAddPantsClicked() {
        onPickImage(PICK_IMAGE_ID_SHIRTS);
    }

    @OnClick(R.id.iv_fav)
    public void onFavClicked() {
        View v = mFavIV;
        String mCurrentShirtPath = mShirtsPathList.get(mCurrentShirtPosition);
        String mCurrentPantPath = mPantsPathList.get(mCurrentPantPosition);
        Log.e("Before On Fav clicked","---->"+v.isSelected());
        ContentValues contentValues = new ContentValues();
        contentValues.put(Column.SHIRT_PATH, mCurrentShirtPath);
        contentValues.put(Column.PANT_PATH, mCurrentPantPath);
        if(!v.isSelected()) {
            getContentResolver().insert(MyWardrobeContentProvider.CONTENT_URI_FAVOURITE, contentValues);
        } else {
            getContentResolver().delete(MyWardrobeContentProvider.CONTENT_URI_FAVOURITE, Column.SHIRT_PATH + " = ? AND " + Column.PANT_PATH + " = ?", new String[]{mCurrentShirtPath, mCurrentPantPath});
        }
        v.setSelected(!v.isSelected());
        Log.e("On Fav clicked","---->"+v.isSelected());
    }

    private ClothPagerAdapter mShirtsPagerAdapter;
    private ClothPagerAdapter mPantsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mShirtsPathList = new ArrayList<>();
        mPantsPathList = new ArrayList<>();
        mShirtsPagerAdapter = new ClothPagerAdapter(getApplicationContext(), null, R.layout.row_shirt_item);
        mPantsPagerAdapter = new ClothPagerAdapter(getApplicationContext(), null, R.layout.row_pant_item);


        mRefreshIV.setImageResource(R.drawable.ic_action_accept);
        getSupportLoaderManager().initLoader(PANTS_LOADER, null, this);
        getSupportLoaderManager().initLoader(SHIRTS_LOADER, null, this);
        mShirtsVP.setAdapter(mShirtsPagerAdapter);
        mPantsVP.setAdapter(mPantsPagerAdapter);
        mShirtsVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentShirtPosition = position;
                handleFavourite();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPantsVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPantPosition = position;
                handleFavourite();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        AlarmHelper.setAlarm(getApplicationContext());
    }

    private void handleFavourite() {
        mFavIV.setSelected(checkIfFavourite());
        mFavIV.setVisibility(View.VISIBLE);
    }

    private boolean checkIfFavourite() {
        boolean isFav = false;
        Cursor cursor = getContentResolver().query(MyWardrobeContentProvider.CONTENT_URI_FAVOURITE, new String[]{MyWardrobeDatabase.Column._ID}, Column.SHIRT_PATH + " = ? AND " + Column.PANT_PATH + " = ?", new String[]{mShirtsPathList.get(mCurrentShirtPosition), mPantsPathList.get(mCurrentPantPosition)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.e("----->","----->"+cursor.getCount());
            isFav = cursor.getCount() > 0;
        }
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PANTS_LOADER :
                return new CursorLoader(getApplicationContext(), MyWardrobeContentProvider.CONTENT_URI_PANTS, null, null, null, null);

            case SHIRTS_LOADER :
                return new CursorLoader(getApplicationContext(), MyWardrobeContentProvider.CONTENT_URI_SHIRTS, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("data--->"+(data != null ? data.getCount() : 0),"-------"+loader.getId());
        switch (loader.getId()) {
            case PANTS_LOADER :
                mIsPantsReset = true;
                iterateCursorAndFillListForPants(data, mPantsPathList);
                Cursor cursor = mPantsPagerAdapter.swapCursor(data);
                if (cursor != null) {
                    cursor.close();
                }
                break;
            case SHIRTS_LOADER :
                iterateCursorAndFillListForShirts(data, mShirtsPathList);
                cursor = mShirtsPagerAdapter.swapCursor(data);
                if (cursor != null) {
                    cursor.close();
                }
                mIsShirtReset = true;
                break;
        }
        Log.e("Pant reset =>"+mIsPantsReset, "Shirt Reset ->"+mIsShirtReset);
        if (mIsPantsReset && mIsShirtReset) {
            mRefreshIV.setImageResource(R.drawable.ic_action_accept);
            mRefreshIV.setEnabled(true);
        }

        checkForNoData();
    }

    private void iterateCursorAndFillListForShirts(Cursor cursor, List<String> pathList) {
        if (cursor.moveToFirst()) {
            do {
                pathList.add(cursor.getString(cursor.getColumnIndexOrThrow(Column.IMAGE_PATH)));
            } while (cursor.moveToNext());
        }
    }

    private void iterateCursorAndFillListForPants(Cursor cursor, List<String> pathList) {
        if (cursor.moveToFirst()) {
            do {
                pathList.add(cursor.getString(cursor.getColumnIndexOrThrow(Column.IMAGE_PATH)));
            } while (cursor.moveToNext());
        }
    }

    private synchronized void checkForNoData() {
        mFavIV.setVisibility(View.VISIBLE);
        mShirtsErrorTV.setVisibility(View.GONE);
        mPantsErrorTV.setVisibility(View.GONE);

        if (mShirtsPagerAdapter.getCount() <= 0 ) {
            mShirtsErrorTV.setText(R.string.err_add_more_shirts);
            mShirtsErrorTV.setVisibility(View.VISIBLE);
            mFavIV.setVisibility(View.GONE);
            return;
        }

        if (mPantsPagerAdapter.getCount() <= 0 ) {
            mPantsErrorTV.setText(R.string.err_add_more_pants);
            mPantsErrorTV.setVisibility(View.VISIBLE);
            mFavIV.setVisibility(View.GONE);
            return;
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case PANTS_LOADER :
                mIsPantsReset = false;
                mPantsPathList.clear();
                mPantsPagerAdapter.swapCursor(null);
                break;
            case SHIRTS_LOADER :
                mIsShirtReset = false;
                mShirtsPathList.clear();
                mShirtsPagerAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_ID_PANTS:
                case PICK_IMAGE_ID_SHIRTS:
                    Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    String path = ImageHelper.saveImage(getApplicationContext(), bitmap);
                    if (path != null) {
                        storeImageInDb(requestCode == PICK_IMAGE_ID_PANTS ? MyWardrobeContentProvider.CONTENT_URI_PANTS : MyWardrobeContentProvider.CONTENT_URI_SHIRTS, path);
                        onRefreshClicked();
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    private void storeImageInDb(Uri uri, String path) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyWardrobeDatabase.Column.IMAGE_PATH, path);
        getContentResolver().insert(uri, contentValues);
    }

    public void onPickImage(int reqCode) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, reqCode);
    }
}
