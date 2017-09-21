package sul.sul_protocol_1.Fragment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteOpenHelper is a helper class to manage database creation and version management.
 */

/**
 * 친구목록 탭에서 상위에 내 정보를 보여주기 위해 User 테이블에서 정보를 가져오기 위한 DBHelper
 * 로그인 시 입력된 정보를 가져와서 이용할 것임.
 */
public class DBHelper_User extends SQLiteOpenHelper implements DBHelper {
    //DB
    public static final String DATABASE_NAME = "sul.db";

    //테이블
    public static final String TABLE_NAME = "sul_User";
    SQLiteDatabase db;

    //Friend table 속성
    public static final String COL_ID = "UID";
    public static final String COL_NAME = "UNAME";
    public static final String COL_PWD = "UPWD";
    public static final String COL_ADRSS = "UADRSS";
    public static final String COL_PHONE = "UPHONE";
    public static final String COL_HX = "UHX";
    public static final String COL_HY = "UHY";
   //  public static final String COL_PIC = "UPIC";

   // Context context;

    public DBHelper_User(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("db created test success");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public Cursor getUserId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " +  COL_ID  + " from " + TABLE_NAME, null);
        return res;
    }

    // 중요한 함수임!! 지우면 안됨
    // Fragment1에서 본인 정보 가져오기 위해 사용할 함수
    // 테이블에 있는 데이터 불러오기
    public Cursor getAllData() {
        //SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }


    public boolean updateData(String id, String name, String pwd, String adrss, String x, String y,String phone) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PWD, pwd);
        contentValues.put(COL_ADRSS, adrss);
        contentValues.put(COL_HX, x);
        contentValues.put(COL_HY, y);
        contentValues.put(COL_PHONE, phone);
        //  contentValues.put(COL_PIC, pic);

        Log.i("id update", id);
        //일단 id 가 test인것만..
        db.update(TABLE_NAME, contentValues, "UID = ?", new String[]{id});
        return true;
    }

    @Override
    public String getName() {
        return "My";
    }

    public boolean updateData(String id, String name, String password, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PWD, password);
        contentValues.put(COL_ADRSS, address);
        contentValues.put(COL_PHONE, phone);
        //  contentValues.put(COL_PIC, pic);

        Log.i("id update", id);
        //일단 id 가 test인것만..
        db.update(TABLE_NAME, contentValues, "UID = ?", new String[]{id});
        return true;

    }
}
