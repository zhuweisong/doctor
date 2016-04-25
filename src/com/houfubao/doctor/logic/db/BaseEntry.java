package com.houfubao.doctor.logic.db;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class BaseEntry {
	private static final String TAG = "BaseEntry";
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String DOUBLE_TYPE = " DOUBLE";
	public static final String BIGINT_TYPE = " BIGINT";
	public static final String COMMA_SEP = ",";

	/**
	 * Upgrade database from (version - 1) to version.
	 */
	public abstract void upgradeTo(SQLiteDatabase db, int version);

	public abstract void createTable(SQLiteDatabase db);

	/**
	 * Add a column to a table using ALTER TABLE.
	 * 
	 * @param dbTable
	 *            name of the table
	 * @param columnName
	 *            name of the column to add
	 * @param columnDefinition
	 *            SQL for the column definition
	 */
	protected void addColumn(SQLiteDatabase db, String dbTable,
			String columnName, String columnDefinition) {
		db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
				+ columnDefinition);
	}

	protected void execSqlBatch(SQLiteDatabase db, ArrayList<String> sqls) {
		if (db == null || sqls == null) {
			Log.e(TAG, "BaseEntry error");
			return;
		}
		db.beginTransaction();
		try {
			for (String s : sqls) {
				if (s != null) {
					db.execSQL(s);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
}
