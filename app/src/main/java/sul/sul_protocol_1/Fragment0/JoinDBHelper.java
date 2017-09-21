package sul.sul_protocol_1.Fragment0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JoinDBHelper extends SQLiteOpenHelper {

    // db
    public static final String DATABASE_NAME="sul.db";

    // table
    public static final String TABLE_NAME="sul_User";

    // 속성
    public static final String COL_ID = "UID";
    public static final String COL_NAME = "UNAME";
    public static final String COL_PWD = "UPWD";
    public static final String COL_ADRSS = "UADRSS";
    public static final String COL_PHONE = "UPHONE";
    public static final String COL_HX = "UHX";
    public static final String COL_HY = "UHY";
    public static final String COL_PIC = "UPIC";
    public static final String COL_ARRIVAL = "UARRIVAL";

    public JoinDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    // 인자 8개
    public boolean insertData(String id, String name, String pwd, String adrss, String phone, String hx, String hy, String pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // 사용자에게 받아오는 값
        contentValues.put(COL_ID,id);
        contentValues.put(COL_NAME,name);
        contentValues.put(COL_PWD,pwd);
        contentValues.put(COL_ADRSS, adrss);
        contentValues.put(COL_PHONE, phone);

        // 집 위치의 초기값 : 덕성여대로 했음 <- 지오코딩을 바뀌어야 할 부분
        contentValues.put(COL_HX,hx);
        contentValues.put(COL_HY,hy);

        // 사진 파일 명
        contentValues.put(COL_PIC,pic);

        // 초기값 도착 안함
        contentValues.put(COL_ARRIVAL, 0);

        // 이미 존재하는 아이디로 회원가입 시 SQLiteConstraintException 발생하는 곳
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else
            return true;
    }


    // user table 에 있는 정보가져오기
    public Cursor getAllUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select  * from "+TABLE_NAME, null);
        return res;
    }

}
