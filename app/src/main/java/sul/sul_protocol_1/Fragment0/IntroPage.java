package sul.sul_protocol_1.Fragment0;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;

import sul.sul_protocol_1.R;

/**
 * Created by 은혜 on 2016-06-24.
 */
public class IntroPage extends FragmentActivity {
    UserDBHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage_layout);


        if(isCheckDB(this)){
            Log.i("db","yes");
           myDb=new UserDBHelper(this);
            Cursor res = myDb.getAllUserData();

            while(res.moveToNext()){
                String id = res.getString(1);
                UserBean.setId(id);
                Log.i("res.getString(0)", id);

            }

            //startActivity(new Intent(this, MainActivity.class));
            Intent intent = new Intent(this, LoadingPage.class);
            intent.putExtra("main",true);
            startActivity(intent);
            finish();
        }else{
            Log.i("db","no");
            //startActivity(new Intent(this, ServersqliteinsertActivity.class));
            Intent intent = new Intent(this, LoadingPage.class);
            intent.putExtra("main",false);
            startActivity(intent);
            finish();
        }


    }

    // DB가 있나 체크하기
    public boolean isCheckDB(Context mContext){
        String filePath = "/data/data/" + this.getPackageName() + "/databases/sul.db";
        File file = new File(filePath);

        if (file.exists()) {
            //file.delete();
            return true;
        }

        return false;

    }
}
