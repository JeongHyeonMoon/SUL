package sul.sul_protocol_1.Fragment0;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import sul.sul_protocol_1.R;

public class UserDBActivity extends FragmentActivity {

    UserDBHelper myDb;
    //JoinDBHelper myDb;

    // create sul_User table : id, name, pwd, adrss, uphone, uhx, uhy, pic, uarrival
    String uId;
    String uname;
    String upwd;
    String uadrss;
    String uphone;

    // 이 변수들은 쓰이지 않음
    String uhX;
    String uhY;
    String uPic;
    Integer uarrival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serversqliteinsert);
        myDb = new UserDBHelper(this);

        // create sul_User table : id, name, pwd, adrss, uphone, uhx, uhy, pic, uarrival
        uname = UserBean.getName();
        uId = UserBean.getId();
        upwd = UserBean.getPwd();
        uadrss = UserBean.getAdrss();
        uphone = UserBean.getPhone();
        uhX = UserBean.getX();
        uhY = UserBean.getY();
        uPic = UserBean.getPic();
        uarrival = UserBean.getArrival(); // 초기값은 0으로 되어있음

        AddData();
    }

    public void AddData() {
        // user table에 8개 insert 시킴
        boolean isInserted = myDb.insertData(uname,uId,upwd,uadrss,uphone,uhX,uhY,uPic);
        // 이미 있는 id인지 검사
        if (isInserted == true)
            Toast.makeText(UserDBActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(UserDBActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
    }
}