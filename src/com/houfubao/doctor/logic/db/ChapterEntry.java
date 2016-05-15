package com.houfubao.doctor.logic.db;


import java.util.ArrayList;
import java.util.List;

import com.houfubao.doctor.logic.db.QuestionEntry.QuestionColumns;
import com.houfubao.doctor.logic.online.Chapter;
import com.houfubao.doctor.logic.online.Question;
import com.houfubao.doctor.logic.utils.QLog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ChapterEntry extends BaseEntry {
	private static final String TAG = "ChapterEntry";
    public static final String TABLE_NAME = "chapter";

    
     public static class ChapterColumns implements BaseColumns {
	  public static final String COLUMN_CID = "cid";
	  public static final String COLUMN_LEVEL = "level";
	  public static final String COLUMN_DESC = "desc";
	  public static final String COLUMN_ORDER = "iOrder";
	  public static final String COLUMN_QUESTION_COUNT = "iQuestionCount";
	  public static final String COLUMN_UPDATE_AT = "updatedAt";
    }
    
	private static final String SQL_CREATE_TABLE = 
			"CREATE TABLE " + TABLE_NAME +" (" +
					ChapterColumns._ID + " INTEGER PRIMARY KEY," +
					ChapterColumns.COLUMN_CID + INTEGER_TYPE + COMMA_SEP +
					ChapterColumns.COLUMN_LEVEL + TEXT_TYPE + COMMA_SEP +
					ChapterColumns.COLUMN_DESC + TEXT_TYPE + COMMA_SEP +
					ChapterColumns.COLUMN_ORDER + TEXT_TYPE + COMMA_SEP + 
					ChapterColumns.COLUMN_QUESTION_COUNT + INTEGER_TYPE + COMMA_SEP +
					ChapterColumns.COLUMN_UPDATE_AT + BIGINT_TYPE + 
					" )";
	
    @Override
    public  void createTable(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE);
    	QLog.i(TAG, "createTable ");
    }

    @Override
    public void upgradeTo(SQLiteDatabase db, int version){
    	switch (version) {
        case 1:
        	createTable(db);
        	break;

		default:
			break;
		}
    }
    

	  
	public List<Chapter> query(SQLiteDatabase db) {
	  	ArrayList<Chapter> l = new ArrayList<Chapter>();
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				ChapterColumns._ID,
				ChapterColumns.COLUMN_CID,
				ChapterColumns.COLUMN_LEVEL,
				ChapterColumns.COLUMN_DESC,
				ChapterColumns.COLUMN_ORDER,
				ChapterColumns.COLUMN_QUESTION_COUNT,
				ChapterColumns.COLUMN_UPDATE_AT
	        };
		
		String orderby = ChapterColumns.COLUMN_ORDER  + " ASC";
		
		Cursor cursor = null;
		try {
			cursor = db.query(
				    TABLE_NAME,  // The table to query
				    projection,                               // The columns to return
				    null,                                // The columns for the WHERE clause
				    null,                            // The values for the WHERE clause
				    null,                                     // don't group the rows
				    null,                                     // don't filter by row groups
				    orderby                                 // The sort order
				    );
			
			while (cursor.moveToNext()) {
				Chapter q = new Chapter();
				
				long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ChapterColumns._ID));
				q.setCId(cursor.getInt((cursor.getColumnIndex(ChapterColumns.COLUMN_CID))));
				q.setLevel(cursor.getInt(cursor.getColumnIndex(ChapterColumns.COLUMN_LEVEL)));
				q.setDesc(cursor.getString((cursor.getColumnIndex(ChapterColumns.COLUMN_DESC))));
				q.setOrder(cursor.getInt((cursor.getColumnIndex(ChapterColumns.COLUMN_ORDER))));
				q.setQuestionCount(cursor.getInt((cursor.getColumnIndex(ChapterColumns.COLUMN_QUESTION_COUNT))));
				q.setUpdateAt(cursor.getLong((cursor.getColumnIndex(ChapterColumns.COLUMN_UPDATE_AT))));
				
				QLog.i(TAG, "query chapter: "+ q.getOrder());
				
				l.add(q);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		QLog.i(TAG, "query chapter: " + l.size());
    	return l;
		
	}
	
	public void insert(SQLiteDatabase db, List<Chapter> list) {
		if (list== null || list.size()==0) {
			return;
		}
		
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add(getDeleteSQL(list));
		getInsertSQL(sqls, list);
		
		execSqlBatch(db, sqls);
	}
	
    private static final String SQL_INSERT =
    "INSERT INTO " + TABLE_NAME + " ( " 
    	+ ChapterColumns.COLUMN_CID + COMMA_SEP
    	+ ChapterColumns.COLUMN_LEVEL + COMMA_SEP 
    	+ ChapterColumns.COLUMN_DESC + COMMA_SEP 
    	+ ChapterColumns.COLUMN_ORDER + COMMA_SEP 
    	+ ChapterColumns.COLUMN_QUESTION_COUNT + COMMA_SEP 
    	+ ChapterColumns.COLUMN_UPDATE_AT + " ) VALUES(";
    
	private void getInsertSQL(List<String> sqls, List<Chapter> list) {
		int size = list.size();
		if (size <= 0) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i<size; i++) {
			Chapter chapter = list.get(i);
			sb.append(SQL_INSERT);
			sb.append(chapter.getCId());
			sb.append(",'");	
			sb.append(chapter.getLevel());
			sb.append("','");	
			sb.append(chapter.getDesc());
			sb.append("','");	
			sb.append(chapter.getOrder());
			sb.append("',");	
			sb.append(chapter.getQuestionCount());
			sb.append(",");	
			sb.append(chapter.getUpdateAt());
			sb.append(");");	
			sqls.add(sb.toString());
			sb.delete(0, sb.length()); //清空
		}
	}
	

	final static String DELETE_SQL = "DELETE FROM " + TABLE_NAME + " WHERE "  
			+ ChapterColumns.COLUMN_CID + 
		    " IN ( ";

	private String getDeleteSQL(List<Chapter> list) {
		int size = list.size();
		if (list.size() <= 0) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder(DELETE_SQL);
		for (int i = 0; i<size; i++) {
			Chapter question = list.get(i);
			sb.append(question.getCId());
			if (i < size-1) {
				sb.append(",");
			}else {
				sb.append(")");
			}
		}
		
		return sb.toString();
	}; 
	
}
