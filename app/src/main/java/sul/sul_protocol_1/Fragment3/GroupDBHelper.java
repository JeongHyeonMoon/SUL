package sul.sul_protocol_1.Fragment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class GroupDBHelper extends SQLiteOpenHelper implements Subject {

    //DB
    public static final String DATABASE_NAME="sul.db";

    //테이블
    public static final String GROUP_TABLE="sul_Group";
    public static final String FRIEND_TABLE="sul_Friend";
    public static final String USER_TABLE="sul_User";

    //Group table 속성
    public static final String COL_GID="GID";
    public static final String COL_FNAME="FNAME";
    public static final String COL_FID="FID";
    public static final String COL_GTIME="GTIME"; // 아직 구현 안함
    public static final String COL_GPOSITION="GPOSITION";
    public static final String COL_GX="GX";
    public static final String COL_GY="GY";

    //Friend table 속성
    public static final String COL_FFID="FID";
    public static final String COL_ARRIVAL = "FARRIVAL";
    public static final String COL_FHX = "FHX";
    public static final String COL_FHY = "FHY";
    public static final String COL_FPHONE = "FPHONE";

    // User table 속성
    public static final String COL_UID = "UID";
    public static final String COL_UHX = "UHX";
    public static final String COL_UHY = "UHX";


    private final static ArrayList<Observer> observers = new ArrayList<Observer>();
    private String dataid;
   // private String dataname;


    public GroupDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    // 이미 sul db가 생성되어있으므로 실행되지 않음
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+GROUP_TABLE);
        onCreate(db);
    }

    // 아마도 안쓰 일 듯
    public boolean insertData(String gid, String name, String id){
        System.out.println("insertData open test success");
        SQLiteDatabase db = this.getWritableDatabase();
        //System.out.println("getWritableDatabase test success");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GID, gid);
        contentValues.put(COL_FNAME,name);
        contentValues.put(COL_FID,id);


        long result = db.insert(GROUP_TABLE,null,contentValues);
        if(result == -1){
            return false;
        }else{
            setData(gid);
            return true;
        }
    }




    public boolean insertData(String gid, String fname, String fid, String gtime, String gposition,String gx, String gy){
        System.out.println("insertData open test success");
        SQLiteDatabase db = this.getWritableDatabase();
        //System.out.println("getWritableDatabase test success");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GID, gid);
        contentValues.put(COL_FNAME,fname);
        contentValues.put(COL_FID,fid);
        contentValues.put(COL_GTIME,gtime);
        contentValues.put(COL_GPOSITION, gposition);
        contentValues.put(COL_GX,gx);
        contentValues.put(COL_GY,gy);


        long result = db.insert(GROUP_TABLE,null,contentValues);
        if(result == -1){
            return false;
        }else {
            setData(gid);
            return true;
        }
    }



    //테이블에 있는 데이터 불러오기
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select  * from "+GROUP_TABLE, null);
        return res;
    }

    //약속장소 이름 가져오기
    public Cursor getplacename(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct GPOSITION from "+GROUP_TABLE+ " where GID = ?", new String[] {gid});
        return res;
    }

    // 중복되지 않는 gid 불러오기
    public Cursor getgidData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct GID from " + GROUP_TABLE, null);
        return res;
    }

    // 테이블에서 gid 가 같은 친구
    public Cursor getgroupData(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        // Cursor res = db.rawQuery("select DISTINCT GID, FNAME, FUD, DISTINCT GTIME, DISTINCT GPOSITION, DISTINCT GX, DISTINCT GY from "+ GROUP_TABLE + " where GID = ?", new String[] {gid});
        Cursor res = db.rawQuery("select * from "+ GROUP_TABLE + " where GID = ?", new String[] {gid});
        return res;
    }

    public boolean updateData(String gid, String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FNAME,name);
        contentValues.put(COL_FID,id);
        db.update(GROUP_TABLE, contentValues, "GID = ?", new String[] {gid} ); //장소를 기준으로 업데이트됌..!
        return true;
    }

    // 선택한 gid에 해당하는 gx를 반환
    public Cursor getXY(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+COL_GX+ " , "+COL_GY+ " from "+ GROUP_TABLE + " where GID = ?", new String[]{gid});
        return res;
    }

    // 그룹테이블과 친구테이블 조인해서 친구의 도착여부 정보 가져오기
    public Cursor getfarrival(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("getfarrival","yes");
        Cursor res = db.rawQuery("select "+FRIEND_TABLE+"."+COL_FFID+ " , "+COL_ARRIVAL+ " from "+ GROUP_TABLE +" , "+ FRIEND_TABLE+" where "+GROUP_TABLE+".FID = "+FRIEND_TABLE+".FID and "+ "GID = ?", new String[]{gid});
        return res;
    }

    // 그룹 테이블에 속한 친구들의 집 위도 경도
    public Cursor getfhome(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+FRIEND_TABLE+"."+COL_FHX+ " , "+FRIEND_TABLE+"."+COL_FHY+ " from "+ GROUP_TABLE +" , "+ FRIEND_TABLE+" where "+GROUP_TABLE+".FID = "+FRIEND_TABLE+".FID and "+ "GID = ?", new String[]{gid});

        return res;
    }

    // 그룹 테이블에 속한 친구들의 집 위도 경도
    public Cursor getmyhome(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res2 = db.rawQuery("select "+USER_TABLE+"."+COL_UHX+ " , "+USER_TABLE+"."+COL_UHY+ " from "+ GROUP_TABLE +" , "+ USER_TABLE+" where "+GROUP_TABLE+".FID = "+USER_TABLE+".UID and "+ "GID = ?", new String[]{gid});
        return res2;
    }

    // 친구 정보를 업데이트 시켜줌
    public boolean updateArivalData(String fid, int farrival) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_ARRIVAL, farrival);
        Log.i("farrival", String.valueOf(farrival));
        db.update(FRIEND_TABLE, contentValues, "FID = ?", new String[]{fid});
        return true;
    }

    // 친구들 핸드폰 번호 가져오기
    public Cursor getphone(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res2 = db.rawQuery("select "+FRIEND_TABLE+"."+COL_FPHONE+ " from "+ GROUP_TABLE +" , "+ FRIEND_TABLE+" where "+GROUP_TABLE+".FID = "+FRIEND_TABLE+".FID and "+ "GID = ?", new String[]{gid});
        return res2;
    }

    public Cursor getallfriend(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res2 = db.rawQuery("select  * from "+FRIEND_TABLE, null);
        return res2;
    }


    @Override
    public void registerObserver(Observer o) {

        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {

        if (observers.indexOf(o) > 0) observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(dataid);
            Log.i("notify test", "성공");
        }
    }


    public void setData(String i) {
        Log.i("setData test", "성공");
        this.dataid = i;
        //this.dataname = n;
        notifyObservers();
    }


    public void delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from " + TABLE_NAME + " where FID='" + id + "'");
        db.delete(GROUP_TABLE, COL_GID + "=?", new String[] {id});
        Log.i("delete query", id);
    }
/*
    public boolean setToggle(String gid, int i){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("GToggle", i);

        long result = db.insert(GROUP_TABLE,null,contentValues);
        if(result == -1){
            return false;
        }else{
            setData(gid);
            return true;
        }
    }

    public int getToggle(String gid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select GToggle from " + GROUP_TABLE + " where " + COL_GID + "='" + gid + "'", null);
    }*/
}
