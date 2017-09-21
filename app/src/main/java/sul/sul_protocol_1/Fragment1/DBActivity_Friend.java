package sul.sul_protocol_1.Fragment1;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import sul.sul_protocol_1.R;

public class DBActivity_Friend extends Activity {
    FindFriendActivity findFriendActivity = (FindFriendActivity) FindFriendActivity.FFindFriendActivity;
    DBHelper_Friend myDb;

    String fName;
    String fId;
    String fadrss;
    String fHX;
    String fHY;
    String fPic;
    String fphone;
    Integer farrival;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        myDb = new DBHelper_Friend(this);

        fName = Friend.getName();
        fId = Friend.getId();
        fadrss = Friend.getAdrss();
        fHX = Friend.getX();
        fHY = Friend.getY();
        fPic = Friend.getPic();
        fphone = Friend.getPhone();
        farrival = Friend.getArrival();


       // 친구 추가하기전에 한번 물어봄
        AlertDialog.Builder alert = new AlertDialog.Builder(DBActivity_Friend.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                AddData(); // 친구 추가
                dialog.dismiss(); //닫기
                finish();
                findFriendActivity.finish();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //닫기
                finish();
                findFriendActivity.finish();
            }
        });
        alert.setMessage(fName + "님을 친구로 추가하시겠습니까?");
        alert.show();

    }

    public void AddData() {

        boolean isInserted = myDb.insertData(fName, fId, fadrss, fHX, fHY, fphone);
        if (isInserted == true) {
            Toast.makeText(DBActivity_Friend.this, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            // list에 추가해주기
        }
        else
            Toast.makeText(DBActivity_Friend.this, "이미 친구입니다.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_create_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
