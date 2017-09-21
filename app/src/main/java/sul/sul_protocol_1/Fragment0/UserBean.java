package sul.sul_protocol_1.Fragment0;

public class UserBean {

    // user table 의 전체 속성
    public static String id;
    public static String name;
    public static String pwd;
    public static String adrss;
    public static String x;
    public static String y;
    public static String pic;
    public static String phone;
    public static Integer arrival;

    // set
    public static void setName(String n){
        name = n;
    }
    public static void setId(String i){
        id = i;
    }
    public static void setPwd(String i) {pwd = i;};
    public static void setAdrss(String a){
        adrss=a;
    }

    // 지금은 따로 사용자에게 받지 않고 임의의 값을 넣어 주었으므로 쓰이지 않음
    public static void setX(String x){x = x;}
    public static void setY(String y){
        y=y;
    }
    public static void setPic(String p){
        pic=p;
    }
    public static void setPhone(String p){
        phone=p;
    }
    public static void setArrival(Integer p){
        arrival=p;
    }

    //get
    public static String getName(){
        return name;
    }
    public static String getId(){
        return id;
    }
    public static String getAdrss(){
        return adrss;
    }
    public static String getX(){
        return x;
    }
    public static String getY() {
        return y;
    }
    public static String getPic(){
        return pic;
    }
    public static String getPwd(){return pwd;}
    public static String getPhone(){return phone;}
    public static Integer getArrival(){return arrival;}

}
