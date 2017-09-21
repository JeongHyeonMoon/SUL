package sul.sul_protocol_1.Fragment1;
import java.util.ArrayList;

/**
 * 그룹 테이블 생성할 때 사용할 자바 빈
 */
public class FriendForGroupBean {
    // property help us to keep data
    public static String gid;
    public static ArrayList<String> name = new ArrayList<String>();
    public static ArrayList<String> id = new ArrayList<String>();
    //public static String name;
    //public static String id;

    //public static HashMap<String, String> ffdhash = new HashMap<String, String>();

    /**
     * setter
     * @param
     */
    public static void setgid(String g){
        gid=g;
    }

    public static void setname(String n){
        name.add(n);
    }

    public static void setid(String i){
        id.add(i);
    }

    /**
     * getter
     * @return
     */
    public static String getgid(){
        return gid;
    }

    public static String getname(int i){
        return name.get(i);
    }

    public static String getid(int i){
        return id.get(i);
    }

    public static int getlengh(){
        return name.size();
    }

    /**
     * clear
     */
    public static void clearname(){name.clear();}

    public static void clearid(){id.clear();}
}
