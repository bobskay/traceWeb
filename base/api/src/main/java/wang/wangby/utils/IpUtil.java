package wang.wangby.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

//获取本机ip的工具类
public class IpUtil {
	private static Logger log = LoggerFactory.getLogger(IpUtil.class);
	private static volatile String currentIp;

	/**
	 * @param ipRegex
	 * 由于有多个网卡,只返回符合该表达式的ip
	 * */
	public static String getIp(String ipRegex) {
		if(currentIp!=null){
			return currentIp;
		}
		synchronized (IpUtil.class) {
			if(currentIp!=null){
				return currentIp;
			}
			currentIp=getIpImpl(ipRegex);
			return currentIp;
		}
	}

	private static String getIpImpl(String ipRegex) {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new RuntimeException("获得网卡信息时出错", e);
		}
		
		if(StringUtil.isEmpty(ipRegex)){
			ipRegex="127.0.0.1";
		}
		StringBuilder error=new StringBuilder();
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> ips = ni.getInetAddresses();
			while (ips.hasMoreElements()) {
				String tempIp = ips.nextElement().getHostAddress();
				error.append(tempIp+"\n");
				log.debug("准备匹配:" + tempIp);
				if (match(tempIp, ipRegex)) {
					log.info("找到匹配的,当前服务器ip为:" + tempIp);
					return tempIp;
				}
			}
		}
		throw new RuntimeException("配置文件错误,无法获取当前服务器ip,配置值为:" + ipRegex+",可用ip\n"+error);
	}

	/** 判断当前ip和配置的是否一样 */
	private static boolean match(String ip, String reg) {
		if (reg == null) {
			return false;
		}
		if (ip.replaceAll(reg, "").length() == 0) {
			return true;
		}
		return false;
	}
}
