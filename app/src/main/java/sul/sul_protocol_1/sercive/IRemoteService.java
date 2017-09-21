package sul.sul_protocol_1.sercive;

/**
 * Created by 은혜 on 2016-06-27.
 */
// 서비스는 콜백 Function을 등록한 모든 액티비티에 메소드를 call
public interface IRemoteService {
    void registerCallback(IRemoteService cb);
    void unregisterCallback(IRemoteService cd);
    String onService(int msg);
}
