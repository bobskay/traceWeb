package wang.wangby.exception;

import java.text.MessageFormat;

//已知的异常信息,在cotroller层无需处理,可以直接显示给用户的
public class Message extends RuntimeException{
	private Exception cause;
	private String message;


	public Message(String message, Exception cause){
		super(message,cause);
		this.cause=cause;
	}

	public Message(String pattern, Object ... arguments){
		this.message= MessageFormat.format(pattern,arguments);
	}

	public Message(String message){
		this.message=message;
	}

	public Exception getCause(){
		return cause;
	}

	public String getMessage(){
		if(message==null){
			return super.getMessage();
		}
		return message;
	}


}
