package sul.sul_protocol_1.Fragment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * 친구 관련 db관리하는 helper
 * AddFriendDBActivity가 사용할 것
 */
public class DBHelper_Friend extends SQLiteOpenHelper implements DBHelper, Subject {
    // Observer를 위한 번수 선언
    private final static ArrayList<Observer> observers = new ArrayList<Observer>();
    private String dataid;
    private String dataname;

    //DB
    public static final String DATABASE_NAME = "sul.db";

    //테이블
    public static final String TABLE_NAME = "sul_Friend";
    //   public static final String GROUP_TABLE="sul_Group";

    //Friend table 속성
    public static final String COL_NAME = "FNAME";
    public static final String COL_ID = "FID";
    public static final String COL_ADRSS = "FADRSS";
    public static final String COL_HX = "FHX";
    public static final String COL_HY = "FHY";
    public static final String COL_PIC = "FPIC";
    public static final String COL_ARRIVAL = "FARRIVAL";
    public static final String COL_PHONE = "FPHONE";

    public DBHelper_Friend(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // db가 한번 생성된 이후엔 onCreat문이 실행되지 않으므로 초기 한번에 테이블 생성해줘야함.

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String id, String adrss, String x, String y, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_ID, id);
        contentValues.put(COL_ADRSS, adrss);
        contentValues.put(COL_HX, x);
        contentValues.put(COL_HY, y);
        contentValues.put(COL_PHONE, phone);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            setData(id, name);
            return true;
        }
    }

    //테이블에 있는 데이터 불러오기
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select  * from " + TABLE_NAME, null);
        return res;
    }

    // 친구 삭제
    public void delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from " + TABLE_NAME + " where FID='" + id + "'");
        db.delete(TABLE_NAME, COL_ID + "=?", new String[] {id});
        Log.i("delete query", id);
    }

    @Override
    public String getName() {
        return "Friend";
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
        Log.i("register test", "성공");
        Log.i("o.leng test1", String.valueOf(observers.size()));
    }

    @Override
    public void removeObserver(Observer o) {
        if (observers.indexOf(o) > 0) observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        Log.i("o.leng test2", String.valueOf(observers.size()));
        for (Observer o : observers) {
            o.update(dataid, dataname);
            Log.i("notify test", "성공");
        }
    }

    public void setData(String i, String n) {
        Log.i("setData test", "성공");
        this.dataid = i;
        this.dataname = n;
        notifyObservers();
    }


}
