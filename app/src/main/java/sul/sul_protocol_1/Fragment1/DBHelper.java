package sul.sul_protocol_1.Fragment1;

import android.database.Cursor;

/**
 * 다형성을 위해 Interface생성
 */
public interface DBHelper {
    //public boolean insertData();
    public Cursor getAllData();
    public String getName();
}
