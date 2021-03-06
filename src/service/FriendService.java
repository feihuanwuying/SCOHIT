package service;

import com.opensymphony.xwork2.ActionContext;
import dao.FriendDao;
import dao.InformDao;
import dao.UserDao;
import vo.Friend;
import vo.Inform;
import vo.User;

import java.util.Date;
import java.util.List;

/**
 * Created by ZouKaifa on 2016/10/29.
 */
public class FriendService extends BasicService {
    /**
     * 获得一个好友对象
     * @param username
     * @param friendName
     * @return
     */
    public Friend getFriend(int userId, int friendId) {
        FriendDao friendDao = new FriendDao();
        Friend friend = friendDao.getFriend(userId, friendId);
        friendDao.close();
        return friend;
    }

    /**
     * 获得某个用户的全部好友
     * @param username
     * @return
     */
    public List<Friend> getFriendList(int userId) {
        FriendDao friendDao = new FriendDao();
        List<Friend> friendList = friendDao.getFriendList(userId, pageNumber, pageSize);
        friendDao.close();
        return friendList;
    }

    /**
     * 获得好友列表的页数
     * @param username
     * @return
     */
    public long getFriendPageCount(int userId) {
        long friendCount = getFriendCount(userId);
        pageCount = friendCount % pageSize == 0? (friendCount/pageSize) : friendCount/pageSize+1;
        return pageCount;
    }

    /**
     * 获得某用户名的好友数
     * @param username
     * @return
     */
    public long getFriendCount(int userId) {
        FriendDao friendDao = new FriendDao();
        long count = friendDao.getFriendCount(userId);
        friendDao.close();
        return count;
    }

    /**
     * 双向添加好友
     * @param informId 同意的请求
     * @param remark 给好友的备注
     * @return
     */
    public boolean addFriend(int userId, int friendId, String remark, String remark2) {
        boolean success = true;
        InformDao informDao = new InformDao();
        UserDao userDao = new UserDao();
        FriendDao friendDao = new FriendDao();
        User user = userDao.getUser(userId);
        User friend = userDao.getUser(friendId);
        if (user == null || friend == null) {  //存在
            success = false;
        } else if (friendDao.getFriend(userId, friendId) != null
                || remark.length() > 30 || userId == friendId) {  //不是好友
            success = false;
        } else if (userId != (int)ActionContext.getContext().getSession().get("id")) {  //是当前用户
            success = false;
        } else {
            //userId, friendId, remark
            Friend friend1 = new Friend();
            friend1.setFriend(friend);
            friend1.setRemark(remark);
            friendDao.addFriend(userId, friend1);
            //friendId, userId, remark2
            Friend friend2 = new Friend();
            friend2.setFriend(user);
            friend2.setRemark(remark2);
            friendDao.addFriend(friendId, friend2);
        }
        userDao.close();
        friendDao.close();
        return success;
    }

    /**
     * 删除好友，双向
     * @param username
     * @param friendName
     * @return
     */
    public boolean deleteFriend(int userId, int friendId) {
        boolean success = true;
        UserDao userDao = new UserDao();
        FriendDao friendDao = new FriendDao();
        if (userDao.getUser(userId) == null  //存在
            || userDao.getUser(friendId) == null) {
            success = false;
        } else if (friendDao.getFriend(userId, friendId) == null
                || userId == friendId) {  //是好友
            success = false;
        } else if (userId != (int) ActionContext.getContext().getSession().get("id")) {  //当前用户
            success = false;
        } else {
            Friend friend = new Friend();
            friend.setFriend(userDao.getUser(friendId));
            friendDao.deleteFriend(userId, friend);
        }
        userDao.close();
        friendDao.close();
        return success;
    }

    /**
     * 更新备注
     * @param username
     * @param friendName
     * @param remark
     * @return
     */
    public boolean updateRemark(int userId, int friendId, String remark) {
        boolean success = true;
        UserDao userDao = new UserDao();
        FriendDao friendDao = new FriendDao();
        if (userDao.getUser(userId) == null
                || userDao.getUser(friendId) == null) {
            success = false;  //存在
        } else if (friendDao.getFriend(userId, friendId) == null
                || remark.length() > 30 || userId == friendId) {
            success = false;  //是好友
        } else if (userId != (int)ActionContext.getContext().getSession().get("id")) {
            success = false;  //是当前用户
        } else {
            friendDao.updateRemark(userId, friendId, remark);
        }
        userDao.close();
        friendDao.close();
        return success;
    }

    public boolean applyFriend(int friendId, String message, String remark) {
        boolean success = true;
        UserDao userDao = new UserDao();
        FriendDao friendDao = new FriendDao();
        int userId = (int)ActionContext.getContext().getSession().get("id");
        User friend = userDao.getUser(friendId);
        if (friend == null) {  //存在
            success = false;
        } else if (friendDao.getFriend(userId, friendId) != null
                || remark.length() > 30 || userId == friendId
                || message.length() > 100) {  //不是好友
            success = false;
        } else {
            //通知到被添加人
            Friend friend1 = new Friend();
            friend1.setFriend(userDao.getUser(userId));
            friend1.setRemark(remark);
            Inform inform = new Inform();
            inform.setInformType(Inform.ADD_FRIEND);
            inform.setUser(friend);
            inform.setFriend(friend1);
            inform.setFriendMessage(message);
            inform.setTime(new Date());
            inform.setTreatment(0);
            InformDao informDao = new InformDao();
            informDao.addInform(inform);
            informDao.close();
        }
        userDao.close();
        friendDao.close();
        return success;
    }

    public boolean denyFriend(int informId, String message) {
        InformDao informDao = new InformDao();
        Inform inform = informDao.getInform(informId);
        //验证
        int userId = (int)ActionContext.getContext().getSession().get("id");
        if (inform == null
                ||inform.getUser().getId() != userId
                || inform.getInformType() != Inform.ADD_FRIEND
                || inform.getTreatment() == 2
                || inform.getTreatment() == 3
                || message.length() > 100
                ) {
            informDao.close();
            return false;
        }
        informDao.updateTreatment(informId, 3);
        //再通知到申请人
        Inform newInform = new Inform();
        newInform.setInformType(Inform.ADD_FAIL);
        User user = new User();
        user.setId(userId);
        //user为被拒绝的人
        newInform.setUser(inform.getFriend().getFriend());
        Friend friend = new Friend();
        friend.setFriend(user);
        newInform.setFriend(friend);
        newInform.setFriendMessage(message);
        newInform.setTime(new Date());
        newInform.setTreatment(0);
        informDao.addInform(newInform);
        informDao.close();
        return true;
    }

    public boolean agreeFriend(int informId, String remark) {
        InformDao informDao = new InformDao();
        Inform inform = informDao.getInform(informId);
        //验证
        int userId = (int)ActionContext.getContext().getSession().get("id");
        if (inform == null
                ||inform.getUser().getId() != userId
                || inform.getInformType() != Inform.ADD_FRIEND
                || inform.getTreatment() == 2
                || inform.getTreatment() == 3
                || remark.length() > 30
                ) {
            informDao.close();
            return false;
        }
        //先添加好友
        int friendId = inform.getFriend().getFriend().getId();
        if (! addFriend(userId, friendId, remark, inform.getFriend().getRemark())) {
            informDao.close();
            return false;
        }
        informDao = new InformDao();
        informDao.updateTreatment(informId, 2);
        //通知申请人
        Inform newInform = new Inform();
        newInform.setInformType(Inform.ADD_SUCCESS);
        User user = new User();
        user.setId(userId);
        //user为申请的人
        newInform.setUser(inform.getFriend().getFriend());
        Friend friend = new Friend();
        friend.setFriend(user);
        newInform.setFriend(friend);
        newInform.setTime(new Date());
        newInform.setTreatment(0);
        informDao.addInform(newInform);
        informDao.close();
        return true;
    }

    public List<Friend> getAllFriendList(int userId) {
        FriendDao friendDao = new FriendDao();
        List<Friend> friendList = friendDao.getFriendList(userId);
        friendDao.close();
        return friendList;
    }

    public boolean sendMessage(int friendId, String message) {
        UserDao userDao = new UserDao();
        int userId = (int)ActionContext.getContext().getSession().get("id");
        Inform inform = new Inform();
        inform.setUser(userDao.getUser(friendId));
        Friend friend = new Friend();
        friend.setFriend(userDao.getUser(userId));
        inform.setFriend(friend);
        inform.setFriendMessage(message);
        inform.setTime(new Date());
        inform.setTreatment(0);
        inform.setInformType(Inform.FRIEND_CHAT);
        InformDao informDao = new InformDao();
        informDao.addInform(inform);
        informDao.close();
        userDao.close();
        return true;
    }


}
