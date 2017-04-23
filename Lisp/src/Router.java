
public class Router {

	private String password,username,ip,command;
	
	Router(){
		
	}
	Router(String password){
		this.password = password;
	}
	Router(String username, String ip, String command){
		this.username = username;
		this.ip = ip;
		this.command = command;
	}
	
	public String getUsername(){
		return username;
	}
	public String getIP(){
		return ip;
	
	}
	public String getCommand(){
	return command;	
	}
	public String getPassword(){
		return password;
	}
}
