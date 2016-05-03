package com.houfubao.doctor.logic.db;

import java.util.List;

import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.utils.QLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DoctorDatabase {
	private static final String TAG = "AppStoreDatabase";
	
	private Context mContext;
	private FeedReaderDbHelper mDbHelper;
	
	private QuestionEntry mQuestion;

	
	public DoctorDatabase(Context context) {
		mContext = context;
		mDbHelper = new FeedReaderDbHelper(mContext);
		
		mQuestion = new QuestionEntry();
	}
	
	//For Provider
	SQLiteOpenHelper getDbHelper() {
		return mDbHelper;
	}
    
    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "GameBoxLauncher.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
         
		@Override
        public void onCreate(SQLiteDatabase db) {
		   	QLog.i(TAG, "onCreate from :" + db.getVersion());
		   	
		   	mQuestion.createTable(db);
        }
		
		@Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            //1. new add table
		   	QLog.i(TAG, "onUpgrade from " + oldV + " to " + newV);
            for (int version = oldV + 1; version <= newV; version++) {
            	mQuestion.upgradeTo(db, version);
            }
        }
    }
    
    List<Question> queryQuestion(int start, int count) {
       	SQLiteDatabase db = mDbHelper.getReadableDatabase();
       	return mQuestion.query(db, start, count);
    }
   
    void insert(List<Question> list) {
       	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	mQuestion.insert(db, list);
    }
}
