package wang.wangby.tools.shell;


import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShellEvent {

	private Shell shell;
	private String command;
	private ShellOutput out;
	
	public ShellEvent(Shell shell, String command, ShellOutput out) throws JSchException {
		this.shell=shell;
		this.out=out;
		this.command=command;
	}


	public void execute() throws Exception {
		try{
			shell.run(command, out);
		}catch(Exception ex){
			log.info(ex.getMessage(),ex);
		}
	}
	
}
