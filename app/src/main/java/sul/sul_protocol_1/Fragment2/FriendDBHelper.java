package sul.sul_protocol_1.Fragment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper is a helper class to manage database creation and version management.
 */
public class FriendDBHelper extends SQLiteOpenHelper {
    //DB
    public static final String DATABASE_NAME="sul.db";

    //테이블
    public static final String FRIEND_TABLE="sul_Friend";
    //   public static final String GROUP_TABLE="sul_Group";

    //Friend_table 속성
    public static final String COL_NAME = "FNAME";
    public static final String COL_ID = "FID";
    public static final String COL_ADRSS = "FADRSS";
    public static final String COL_X = "FHX";
    public static final String COL_Y = "FHY";
    public static final String COL_PIC = "FPIC";

    public FriendDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // db가 한번 생성된 이후엔 onCreat문이 실행되지 않으므로 초기 한번에 테이블 생성해줘야함.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+FRIEND_TABLE);
        onCreate(db);
    }

    public boolean insertData(String name, String id, String adrss, String x, String y, String pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME,name);
        contentValues.put(COL_ID, id);
        contentValues.put(COL_ADRSS, adrss);
        contentValues.put(COL_X, x);
        contentValues.put(COL_Y, y);
        contentValues.put(COL_PIC, pic);

        long result = db.insert(FRIEND_TABLE,null,contentValues);
        if(result == -1){
            return false;
        }else
            return true;
    }

    //테이블에 있는 데이터 불러오기
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select  * from "+FRIEND_TABLE, null);
        return res;
    }

}
