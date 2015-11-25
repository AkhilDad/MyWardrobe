package com.akhil.mywardrobe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

/**
 * This class is used to perform all the database operations
 * such as saving, deleting and updating data used in this application
 * @author Lipika 
 */
public class MyWardrobeDatabase extends SQLiteOpenHelper
{
	public interface DEFAULTVALUES {
		String ACCOUNT_PASSWORD 		=	"12345";
		String ACCOUNT_TYPE		 		=	"0";
		String UPDATED_TIMESTAP 		= 	"-1";
	}



	private static MyWardrobeDatabase sAppDbHelper;
	public static final String DATABASE_NAME = "expensedb";
	private static final int DATABASE_VERSION = 1;

	public interface Table{
		String FAVOURITE 					=	"table_favourite";
		String SHIRTS 						=	"table_shirts";
		String PANTS						=	"table_pants";

	}

	public interface Column{
		String _ID								=	"_id";
		String IMAGE_PATH						=	"image_path";
		String SHIRT_PATH						=	"shirt_path";
		String PANT_PATH						=	"pant_path";
		String CREATED_TIMESTAMP				=	"created_timestamp";
		String FAV_NAME							=	"fav_name";

	}

	private final String CREATE_TABLE_FAVOURITE			= "CREATE TABLE "
			+ Table.FAVOURITE
			+ "("
			+ Column._ID		 						+ " INTEGER PRIMARY KEY AUTOINCREMENT , " 
			+ Column.SHIRT_PATH		 				+ " VARCHAR NOT NULL , "
			+ Column.PANT_PATH		 				+ " VARCHAR NOT NULL , "
			+ Column.FAV_NAME		 				+ " VARCHAR "
			+ Column.CREATED_TIMESTAMP 					+ " INTEGER DEFAULT CURRENT_TIMESTAMP  "
			+ ")";
	
	private final String CREATE_TABLE_SHIRTS			= "CREATE TABLE "
			+ Table.SHIRTS
			+ "("
			+ Column._ID		 						+ " INTEGER PRIMARY KEY AUTOINCREMENT , " 
			+ Column.IMAGE_PATH				 			+ " VARCHAR NOT NULL , "
			+ Column.CREATED_TIMESTAMP 					+ " INTEGER DEFAULT CURRENT_TIMESTAMP  "
			+ ")";

	private final String CREATE_TABLE_PANTS			= "CREATE TABLE "
			+ Table.PANTS
			+ "("
			+ Column._ID		 						+ " INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ Column.IMAGE_PATH				 			+ " VARCHAR NOT NULL , "
			+ Column.CREATED_TIMESTAMP 					+ " INTEGER DEFAULT CURRENT_TIMESTAMP  "
			+ ")";


	
	/**
	 * {@link Constructor}
	 * @param context
	 */
	private MyWardrobeDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	/**
	 * This is synchronized method used to return instance of this class once it is not created
	 * @param context
	 * @return {@link MyWardrobeDatabase}
	 */
	public static synchronized MyWardrobeDatabase getInstance(Context context)
	{
		if (sAppDbHelper == null) 
		{
			sAppDbHelper = new MyWardrobeDatabase(context);
		}

		return sAppDbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(CREATE_TABLE_FAVOURITE);
		db.execSQL(CREATE_TABLE_SHIRTS);
		db.execSQL(CREATE_TABLE_PANTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		clearDatabase();
		onCreate(db);
	}

	/**
	 * This method is used to insert data into table on behalf of table name
	 * @param contentValues
	 * @param tablename
	 * @return void
	 * @throws SQLException on insert error
	 */
	public long insertQuery(ContentValues contentValues, String tablename)throws SQLException 
	{
		long val=-1;
		try 
		{
			final SQLiteDatabase writableDatabase = getWritableDatabase();
			val= writableDatabase.insert(tablename, null, contentValues);

		}
		catch (SQLiteException e) 
		{
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * This method is used to insert replace data on table on behalf of table name
	 * @param contentValues
	 * @param tablename
	 * @return void
	 * @throws SQLException on insert error
	 */
	public void insertReplaceQuery(ContentValues contentValues, String tablename)throws SQLException 
	{
		try 
		{
			final SQLiteDatabase writableDatabase = getWritableDatabase();
			writableDatabase.insertOrThrow(tablename, null, contentValues);

		}
		catch (SQLiteException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to update record by ID with contentValues
	 * @param contentValues
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return void
	 */
	public int updateQuery(ContentValues contentValues, String tableName,String whereClause, String[] whereArgs) 
	{
		int val=-1;
		try 
		{
			final SQLiteDatabase writableDatabase = getWritableDatabase();
			val=writableDatabase.update(tableName, contentValues, whereClause,whereArgs);
		}
		catch (SQLiteException e) 
		{
			e.printStackTrace();

		}
		return val;
	}

	/**
	 * This method is used to fetch data from database on behalf of query
	 * @param query
	 * @return {@link Cursor}
	 */
	public Cursor fetchQuery(String query) 
	{
		final SQLiteDatabase readableDatabase = getReadableDatabase();
		final Cursor cursor = readableDatabase.rawQuery(query, null);

		if (cursor != null) 
		{
			cursor.moveToFirst();
		}

		return cursor;
	}

	/**
	 * This method is used to fetch data from database on behalf of query and selectionArgs
	 * @param query
	 * @param selectionArgs
	 * @return {@link Cursor}
	 */
	public Cursor fetchQuery(String query, String[] selectionArgs) 
	{
		final SQLiteDatabase readableDatabase = getReadableDatabase();
		final Cursor cursor = readableDatabase.rawQuery(query, selectionArgs);

		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 * This method is used to delete table by table name
	 * @param table
	 * @return void
	 */	
	public void delete(String table)
	{
		getWritableDatabase().delete(table, null, null);
	}

	/**
	 * This method is used to delete data from table by table name on behalf of selection and where clause
	 * @param table
	 * @param whereClause
	 * @param selectionArgs
	 */
	public int delete(String table, String whereClause, String[] selectionArgs)
	{
		return getWritableDatabase().delete(table, whereClause, selectionArgs);
	}


	/**
	 * This method is used to clear all database
	 * @param
	 * @return void
	 */
	public void clearDatabase()
	{
		getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + Table.PANTS);
		getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + Table.SHIRTS);
		getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + Table.FAVOURITE);
	}

	public static void copyToSdCard(Context context) throws IOException {
		String DB_PATH = Environment.getDataDirectory().getAbsolutePath() + "/data/"+context.getPackageName()+"/databases/";
		Log.e("-----------", "-----------" + DB_PATH);
		byte[] buffer = new byte[1024];
		int length;
//		String outFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+context.getApplicationInfo().name+".sqlite";
		String outFileName = "sdcard/"+DATABASE_NAME+".sqlite";
		OutputStream myOutput = new FileOutputStream(outFileName);
		InputStream myInput;
		// Open your local db as the input stream

		// myInput = myContext.getAssets().open("esdata.sqlite");
		myInput = new FileInputStream(DB_PATH + DATABASE_NAME);
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myInput.close();

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

}