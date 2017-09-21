package sul.sul_protocol_1.Fragment4;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import sul.sul_protocol_1.Fragment1.DBHelper;
import sul.sul_protocol_1.Fragment1.DBHelper_User;
import sul.sul_protocol_1.R;



/**
 * Created by EOM on 2015-08-20.
 */
public class Fragment4 extends Fragment {

    // 내 정보 상위에 띄워주기 위해 필요한 변수 선언
      DBHelper_User myDb;
    //UserDBHelper myDb;

    private ImageView image = null;
    ListView myProfileList2;
    ListView myProfileList3;
    ListView myProfileList4;
    ListView myProfileList5;

    ArrayAdapter myAdapter2;
    ArrayAdapter myAdapter3;
    ArrayAdapter myAdapter4;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("onCreate test", "*");
        myDb = new DBHelper_User(this.getContext());
        myAdapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
        myAdapter3 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
        myAdapter4 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);

    }

    // @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);
        myProfileList2 = (ListView) rootView.findViewById(R.id.myname);
        myProfileList3 = (ListView) rootView.findViewById(R.id.myid);
        myProfileList4 = (ListView) rootView.findViewById(R.id.myaddress);
        myProfileList5 = (ListView) rootView.findViewById(R.id.myphone);

        //---내정보 수정 버튼 클릭시
        Button myinfobtn =(Button)rootView.findViewById(R.id.buttonMyInfo);
        myinfobtn.setOnClickListener(new ViewGroup.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ServersqliteUpdateActivity.class);
                startActivity(intent);
            }
        });

        //---이미지 클릭 리스너
        image = (ImageView)rootView.findViewById(R.id.imageItem);
        image.setOnClickListener(new ViewGroup.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UploadPhoto.class);
                startActivity(intent);
            }

        });

        // sqlite에 회원 정보 추가해야 할 때만 주석 풀어서 쓰고 나중에 지우기
        //insertInfo();

       // updateInfo();
        viewMyInfo(myDb);

        return rootView;

    }

    //public void updateInfo(){myDb.updateData("최은혜", "test", u_pwd, u_adrss, u_phone);}

    public void viewMyInfo(DBHelper db) {
       // ArrayAdapter adapter;

        Cursor res = db.getAllData();
        if (res.getCount() == 0) {
            // 친구가 한명도 없을 때 보여줄 화면 꾸미면 됨!!
        }
           // adapter = myAdapter2;

        while (res.moveToNext()) {
            // 0: id, 1: name
            myAdapter2.add(res.getString(0)); //id
            myAdapter2.add(res.getString(1)); //name
            myAdapter3.add(res.getString(4)); //phone
            myAdapter4.add(res.getString(3)); //adrss
        } // end while

        showList();

    } // end viewMyInfo

    protected void showList() {
        myProfileList2.setAdapter(myAdapter2); //----
        myProfileList3.setAdapter(myAdapter2); //----
        myProfileList4.setAdapter(myAdapter3); //----
        myProfileList5.setAdapter(myAdapter4); //----


        // myProfileList2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    } // end showList
}