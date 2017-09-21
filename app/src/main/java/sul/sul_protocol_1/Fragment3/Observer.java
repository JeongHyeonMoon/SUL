package sul.sul_protocol_1.Fragment3;

/**
 * 친구를 추가하면 list view를 바꿔주기 위한 Observer
 * Subject가 보낸 자료를 처리
 */
public interface Observer {
    public void update(String gId);
}
