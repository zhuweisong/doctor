package com.houfubao.doctor.logic.db;


import java.util.ArrayList;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * 消息状态存取
 * @author sevenzhu
 *
 */
public class MessageEntry extends BaseEntry {
	
	private static final String TAG = "MessageEntry";
	
	public final static int MESSAGE_STATUS_NOT_READ = 1;
	public final static int MESSAGE_STATUS_READED = 2;
	public final static int MESSAGE_STATUS_DELETED = 4;
	public final static int MESSAGE_STATUS_ALL = MESSAGE_STATUS_DELETED * 2 -1;
	
	//消息类型
	public final static int MSG_TYPE_COMMON = 1; //普通消息
	public final static int MSG_TYPE_RECOMMEND_BIT = 2; //推广位消息
	
    public static final String TABLE_NAME = "message";
    
	public MessageEntry(){
		
	}
	
    public class MessageColumns implements BaseColumns {
        
        public static final String COLUMN_NAME_MSG_STATUS= "status";  //客户端用的状态
        public static final String COLUMN_NAME_MSG_ID = "iMsgID"; ////消息id
        
        public static final String COLUMN_NAME_MSG_TYPE = "iMsgType"; //消息类型
        public static final String COLUMN_NAME_ONLINE_TYPE = "iOnLineType";    //在线状态
        public static final String COLUMN_NAME_IMG = "sImage";  //图片
        public static final String COLUMN_NAME_TITLE = "sTitle"; //标题
        public static final String COLUMN_NAME_DETAIL = "sDetail"; //内容
        public static final String COLUMN_NAME_BTN_TEXT= "sBtnText"; //按钮文字
        public static final String COLUMN_NAME_BTN_ACTION = "iBtnAction";  //按钮动作
        public static final String COLUMN_NAME_TIME_OF_MODIFY = "lTimeOfLastModify";   //最后一次修改时间,单位秒
        public static final String COLUMN_NAME_ACTIVIE_ID = "iActiveId";   //最后一次修改时间,单位秒
    }

    //创建表SQL
    private static final String SQL_CREATE_TABLE = 
            "CREATE TABLE " + TABLE_NAME +" (" +
            		MessageColumns._ID + " INTEGER PRIMARY KEY," +
            		MessageColumns.COLUMN_NAME_MSG_STATUS + INTEGER_TYPE + COMMA_SEP +
            		MessageColumns.COLUMN_NAME_MSG_ID + INTEGER_TYPE + COMMA_SEP +
            		MessageColumns.COLUMN_NAME_MSG_TYPE + INTEGER_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_ONLINE_TYPE + INTEGER_TYPE + COMMA_SEP + 
                    MessageColumns.COLUMN_NAME_IMG + TEXT_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_TITLE + TEXT_TYPE +  COMMA_SEP +
                    MessageColumns.COLUMN_NAME_DETAIL + TEXT_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_BTN_TEXT + TEXT_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_BTN_ACTION + INTEGER_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_TIME_OF_MODIFY + BIGINT_TYPE + COMMA_SEP +
                    MessageColumns.COLUMN_NAME_ACTIVIE_ID + INTEGER_TYPE + 
                    " )";

    @Override
    public  void createTable(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void upgradeTo(SQLiteDatabase db, int version){
    	switch (version) {
        case 6:
        	createTable(db);
        	break;

		default:
			break;
		}
    }

   

}
