package wang.wangby.tools.shell;

public interface ShellPoolConfig{

	/**
	 * shell线程池大小
	 */
	public int getShellSize();

	/**
	 * shell客户端空闲多久就关闭
	 */
	public Long getShellMaxActive();

	/**
	 * 默认执行shell的机器
	 */
	public String getDefaultHost();

	/**
	 * 默认执行呢shll机器的用户名
	 */
	public String getUsername();

	/**
	 * shell服务器密码
	 */
	public String getPassword();
}
