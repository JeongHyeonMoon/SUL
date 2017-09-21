package sul.sul_protocol_1.Fragment3;

/**
 * 옵저버를 관리하기 위한 인터페이스
 */
public interface Subject {
    public void registerObserver(Observer o);
    public void removeObserver(Observer o);


    // 등록된 Observer들에게 데이터를 전달
    public void notifyObservers();
}
