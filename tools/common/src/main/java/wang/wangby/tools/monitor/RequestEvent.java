package wang.wangby.tools.monitor;


import wang.wangby.tools.monitor.dto.TimeStatist;

public class RequestEvent implements Runnable{
	private TimeMonitor monitor;
	private String uri;
	private long consume;

	public RequestEvent(String uri,long consume,TimeMonitor monitor){
		this.monitor=monitor;
		this.uri=uri;
		this.consume=consume;
	}

	@Override
	public void run() {
		if(!monitor.getKeys().contains(uri)){
			synchronized(monitor.getKeys()){
				if(!monitor.getKeys().contains(uri)){
					TimeStatist rs=new TimeStatist(uri,monitor.getAccuracy());
					monitor.getMap().put(uri,rs);
					monitor.getKeys().add(uri);
				}
			}
		}
		TimeStatist rs=monitor.getMap().get(uri);
		rs.add(consume);
		monitor.getTotal().add(consume);
	}

}
