package sul.sul_protocol_1.Fragment1;

/**
 * 친구 검색 시 사용 할 자바 빈 객체
 */
public class FriendBean {
    public static String name;
    public static String id;
    public static String adrss;
    public static String x;
    public static String y;
    public static String pic;
    public static String phone;
    public static Integer arrival;

    /**
     * setter
     * @param n
     */
    // sqlite에 저장될 8개의 속성들
    public void setName(String n){
        name = n;
    }
    public void setId(String i){
        id = i;
    }
    public void setAdrss(String a){
        adrss=a;
    }
    public void setX(String x){
        this.x = x;
    }
    public void setY(String y){
        this.y=y;
    }
    public void setPic(String p){
        pic=p;
    }
    public void setPhone(String p){
        phone=p;
    }
    public void setArrival(Integer p){
        arrival=p;
    }

    /**
     * getter
     * @return
     */
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
    public static String getPhone(){
        return phone;
    }
    public static Integer getArrival(){
        return arrival;
    }
}
