package com.houfubao.doctor.logic.db;


import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.online.Question;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
/**
 * 消息状态存取
 * @author sevenzhu
 *
 */
public class QuestionEntry extends BaseEntry {
	
	private static final String TAG = "QuestionEntry";
    public static final String TABLE_NAME = "question";
    
    
	public QuestionEntry(){

	}
	
    public class QuestionColumns implements BaseColumns {
	  public static final String COLUMN_QID = "qid";
	  public static final String COLUMN_TITLE = "title";
	  public static final String COLUMN_OPTION = "option";
	  public static final String COLUMN_ANSWER = "sAnswer";
	  public static final String COLUMN_ATTR = "lAttr";
	  public static final String COLUMN_CHAPTER = "iChapter";
	  public static final String COLUMN_PICTURE = "sPicture";
	  public static final String COLUMN_DETAIL_ANALYSIS = "sDetailAnalysis";
	  public static final String COLUMN_ORDER = "iOrder";
	  public static final String COLUMN_UPDATE_AT = "updatedAt";
    }

    //创建表SQL
    private static final String SQL_CREATE_TABLE = 
            "CREATE TABLE " + TABLE_NAME +" (" +
            		QuestionColumns._ID + " INTEGER PRIMARY KEY," +
            		QuestionColumns.COLUMN_QID + INTEGER_TYPE + COMMA_SEP +
            		QuestionColumns.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
            		QuestionColumns.COLUMN_OPTION + TEXT_TYPE + COMMA_SEP +
                    QuestionColumns.COLUMN_ANSWER + TEXT_TYPE + COMMA_SEP + 
                    QuestionColumns.COLUMN_ATTR + INTEGER_TYPE + COMMA_SEP +
                    QuestionColumns.COLUMN_CHAPTER + INTEGER_TYPE +  COMMA_SEP +
                    QuestionColumns.COLUMN_PICTURE + TEXT_TYPE + COMMA_SEP +
                    QuestionColumns.COLUMN_DETAIL_ANALYSIS + TEXT_TYPE + COMMA_SEP +
                    QuestionColumns.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                    QuestionColumns.COLUMN_UPDATE_AT + BIGINT_TYPE +
                    " )";

    @Override
    public  void createTable(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void upgradeTo(SQLiteDatabase db, int version){
    	switch (version) {
        case 1:
        	//       	createTable(db);
        	break;

		default:
			break;
		}
    }

	public List<Question> query(SQLiteDatabase db, int start, int count) {
	  	ArrayList<Question> l = new ArrayList<Question>();
	
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				QuestionColumns._ID,
				QuestionColumns.COLUMN_QID,
				QuestionColumns.COLUMN_TITLE,
				QuestionColumns.COLUMN_OPTION,
				QuestionColumns.COLUMN_ANSWER,
				QuestionColumns.COLUMN_ATTR,
				QuestionColumns.COLUMN_CHAPTER,
				QuestionColumns.COLUMN_PICTURE,
				QuestionColumns.COLUMN_DETAIL_ANALYSIS,
				QuestionColumns.COLUMN_ORDER,
				QuestionColumns.COLUMN_UPDATE_AT
	        };
		
		final String selection = QuestionColumns.COLUMN_ORDER + " >=  ? AND " 
				+ QuestionColumns.COLUMN_ORDER + " < ?";
		final String[] selectionArgs = { String.valueOf(start), String.valueOf(start + count)};
		
		Cursor cursor = null;
		try {
			cursor = db.query(
				    TABLE_NAME,  // The table to query
				    projection,                               // The columns to return
				    selection,                                // The columns for the WHERE clause
				    selectionArgs,                            // The values for the WHERE clause
				    null,                                     // don't group the rows
				    null,                                     // don't filter by row groups
				    null                                 // The sort order
				    );
			
			while (cursor.moveToNext()) {
				Question q = new Question();
				
				long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(QuestionColumns._ID));
				q.setQId(cursor.getInt((cursor.getColumnIndex(QuestionColumns.COLUMN_QID))));
				q.setTitle(cursor.getString((cursor.getColumnIndex(QuestionColumns.COLUMN_TITLE))));
				q.setOption(cursor.getString((cursor.getColumnIndex(QuestionColumns.COLUMN_OPTION))));
				q.setAnswer(cursor.getString((cursor.getColumnIndex(QuestionColumns.COLUMN_ANSWER))));
				q.setAttr(cursor.getInt((cursor.getColumnIndex(QuestionColumns.COLUMN_ATTR))));
				q.setChapter(cursor.getInt((cursor.getColumnIndex(QuestionColumns.COLUMN_CHAPTER))));
				q.setPicture(cursor.getString((cursor.getColumnIndex(QuestionColumns.COLUMN_PICTURE))));
				q.setAnalysis(cursor.getString((cursor.getColumnIndex(QuestionColumns.COLUMN_DETAIL_ANALYSIS))));
				q.setPos(cursor.getInt((cursor.getColumnIndex(QuestionColumns.COLUMN_ORDER))));
				q.setUpdateAt(cursor.getLong((cursor.getColumnIndex(QuestionColumns.COLUMN_UPDATE_AT))));
				
				l.add(q);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
    	return l;
	}
	
	public void insert(List<Question> list) {
		
	}

}
