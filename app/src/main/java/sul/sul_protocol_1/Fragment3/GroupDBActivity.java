package sul.sul_protocol_1.Fragment3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import sul.sul_protocol_1.R;

// sqlite에 값을 저장하기 위해 만듦.
public class GroupDBActivity extends Activity {
    GroupDBHelper myDb;

    String GName;
    int ffdsize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getgroupid);
        myDb = new GroupDBHelper(this);

        // 친구 수 가져오기
        ffdsize= GroupBean.getlengh();
        // 그룹 이름 가져오기
        GName =GroupBean.getgid();

        // 그룹 추가하기전에 한번 물어봄
        AlertDialog.Builder alert = new AlertDialog.Builder(GroupDBActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                AddData(); // 친구 추가
                viewAll(); // 친구 추가 완료 확인창
                dialog.dismiss(); //닫기

                //intent
               // Intent intent = new Intent(getApplicationContext(), );//장소추가 액티비티)
                //startActivity(intent);
            }
        });
        alert.setMessage("" + GName + "그룹을 만드시겠습니까?");
        alert.show();

        //AddData();
        //viewAll();
        //dateData();
    }

    public void AddData() {
        for(int i=0; i<ffdsize; i++) {
            boolean isInserted = myDb.insertData(GroupBean.getgid(),GroupBean.getname(i),GroupBean.getid(i));

            System.out.println("getid test: " + GroupBean.getid(i));
            if (isInserted == true)
                Toast.makeText(GroupDBActivity.this, "그룹이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(GroupDBActivity.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
        } // end fir
    }

    public void viewAll() {
        //DatabaseHelper인 myDb객체를 가져와 mainactivity의 getAlldata 함수를 호출
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            //show message
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("GID :" + res.getString(0) + "\n");
            buffer.append("FNAME :" + res.getString(1) + "\n");
            buffer.append("FID :" + res.getString(2) + "\n");
            //System.out.println("res.getString GID test: " + res.getString(2));
        }
        //show all data
        //showMessage("sul_Group Data", buffer.toString());
        showMessage("그룹 생성 완료", GName + "그룹이 생성되었습니다.");

    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    /**
     * GroupDBHelper에서  Cursor getgroupData(String gid)함수 이용해 같은 그룹의 친구 아이디, 이름 검색
     */
    /*
    public void viewGroup() {
        //GroupdBHelper인 myDb객체를 가져와 mainactivity의 getgroupData 함수를 호출
        Cursor res = myDb.getgroupData();
        if (res.getCount() == 0) {
            //show message
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("GID :" + res.getString(0) + "\n");
            buffer.append("FNAME :" + res.getString(1) + "\n");
            buffer.append("FID :" + res.getString(2) + "\n");
            System.out.println("res.getString FID test: " + res.getString(2));
        }
        //show all data
        showMessage("sul_Group Data", buffer.toString());
    }
*/

    //content_main.xml의 해당버튼에 가서 android:onClick="btnviewFriendclicked" OnClick 방법 (리스너등록)
   /*
    public void btnviewFriendclicked(View v){
        Intent intent = new Intent(getApplicationContext(), FriendList.class);
        startActivity(intent);
    }*/

    /*
   public void UpdateData() {

        boolean isUpdate = myDb.updateData(editName.getText().toString(),
                editPlace.getText().toString(),
                editDate.getText().toString());
        if (isUpdate == true)
            Toast.makeText(GroupDBActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(GroupDBActivity.this, "Data not Updated", Toast.LENGTH_SHORT).show();


    }*/
}
