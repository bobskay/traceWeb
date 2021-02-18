package wang.wangby.web.webfilter.permission;


public class CurrentUserHolder {
    private static CurrentUserHolder INSTANCE=new CurrentUserHolder();

    public ThreadLocal<PermissionUser> userThreadLocal=new ThreadLocal<>();

    public static  void setUser(PermissionUser currentUser){
        INSTANCE.userThreadLocal.set(currentUser);
    }

    public static PermissionUser getUser(){
        return INSTANCE.userThreadLocal.get();
    }

    public static void clear(){
        INSTANCE.userThreadLocal.remove();
    }
}
