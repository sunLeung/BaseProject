package common.logger;

import java.time.LocalDateTime;

public class Log {
	private LocalDateTime logDateTime;
	private String outputString;
	
	public Log(String outputString,LocalDateTime logDateTime){
		this.logDateTime=logDateTime;
		this.outputString=outputString;
	}

	public LocalDateTime getLogDateTime() {
		return logDateTime;
	}
	public void setLogDateTime(LocalDateTime logDateTime) {
		this.logDateTime = logDateTime;
	}
	
	public String getOutputString() {
		return outputString;
	}

	public void setOutputString(String outputString) {
		this.outputString = outputString;
	}

	public void clear(){
		this.logDateTime=null;
		this.outputString=null;
	}
}
