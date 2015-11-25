package com.akhil.mywardrobe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.akhil.mywardrobe.adapter.ClothPagerAdapter;
import com.akhil.mywardrobe.helper.ImagePicker;
import com.akhil.mywardrobe.provider.MyWardrobeContentProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHIRTS_LOADER = 0;
    private static final int PANTS_LOADER = 1;
    private static final int PICK_IMAGE_ID = 234; // any random number

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

    @OnClick(R.id.iv_refresh)
    public void onRefreshClicked() {

    }

    @OnClick(R.id.iv_add_pants)
    public void onAddShirtsClicked() {
        onPickImage();
    }

    @OnClick(R.id.iv_add_shirts)
    public void onAddPantsClicked() {
        onPickImage();
    }

    @OnClick(R.id.iv_fav)
    public void onFavClicked() {

    }

    private ClothPagerAdapter mShirtsPagerAdapter;
    private ClothPagerAdapter mPantsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mShirtsPagerAdapter = new ClothPagerAdapter(null);
        mPantsPagerAdapter = new ClothPagerAdapter(null);

        mShirtsVP.setAdapter(mShirtsPagerAdapter);
        mPantsVP.setAdapter(mPantsPagerAdapter);

        getSupportLoaderManager().initLoader(PANTS_LOADER, null, this);
        getSupportLoaderManager().initLoader(SHIRTS_LOADER, null, this);

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
        switch (loader.getId()) {
            case PANTS_LOADER :
                mPantsPagerAdapter.swapCursor(data);
                break;
            case SHIRTS_LOADER :
                mShirtsPagerAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case PANTS_LOADER :
                mPantsPagerAdapter.swapCursor(null);
                break;
            case SHIRTS_LOADER :
                mShirtsPagerAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                // TODO store bitmap and save path in db
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void onPickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
}
