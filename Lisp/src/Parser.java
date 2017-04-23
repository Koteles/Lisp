import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


public class Parser {
	
  public static void main(String[] arg){  
	  
	  Router router = new Router("kote", "192.168.137.119", "sh lisp site");
	  Router router2 = new Router("bob", "192.168.137.20", "sh lisp site");
	  
	  initialize(router);    	  
    
  }  
  
  public static void initialize(Router router) {
	  
	  try{ 
		  
		  Mapping map = new Mapping();
		  
	      JSch jsch=new JSch();  

	      Session session=jsch.getSession(router.getUsername(), router.getIP(), 22);
	     
	      // username and password will be given via UserInfo interface.
	      UserInfo ui=new MyUserInfo();
	      //session.setPassword(router.getPassword());
	      session.setUserInfo(ui); //password query
	      session.connect();

	      String command = router.getCommand();
	    
	      Channel channel=session.openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);
	     
	      channel.setInputStream(null);
	    
	      ((ChannelExec)channel).setErrStream(System.err);

	      InputStream in = channel.getInputStream();
	      
	      channel.connect();
	    
	      inputStreamToString(in, map);
	     
	      map.getMappings();  
	      
	      channel.disconnect();
	      
	      session.disconnect();
	    }
	    
	    catch(Exception e) {    	
	      System.out.println(e);
	    }
	  
  }
  
  public static void inputStreamToString(InputStream in, Mapping map) 
		  throws IOException {
		 
	  		
		    String text = IOUtils.toString(in, StandardCharsets.UTF_8.name());		    
		    
		    String EID = 
		            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\/)(?:3[0-2]|[12][0-9]|[1-9])";
		     	            
		 	
		    String RLOC = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		    
		    String ipv6Compressed = "((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::(((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)|(?:\\/)([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))$";
		    String ipv6Standard = "(((?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4})(?:\\/)([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8]))$";
		  
		    
		    
		    List<String> tokens = new ArrayList<String>();
		    tokens.add(EID);
		    tokens.add(RLOC);
		    tokens.add(ipv6Compressed);
		    tokens.add(ipv6Standard);
		    //tokens.add(ipv6Pattern);
		    //tokens.add(ipv4WithMask);
		    
		    String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
		    
		    Pattern pattern = Pattern.compile(patternString);
		    
		    //Matcher matcher = pattern.matcher(new StringBuilder(text).reverse());
		    Matcher matcher = pattern.matcher(text);  
		    
		    //List<String> list = new ArrayList<>();
		    String rloc = "";
		  
		    while (matcher.find()) {			    	
		    	
		    	//System.out.println(new StringBuilder(matcher.group(1)).reverse());
		    	if(matcher.group(1).contains("/")){		//daca este eid at bag in hashmap
		    		map.addMapping(matcher.group(1), rloc);    //key = eid, value = rlocs
		    		rloc = "";
		    	//map.add(matcher.group(1));
		    	}
		    	else {
		    		//list.add(matcher.group(1)); 	//daca este rloc il bag in lista. Avem mai multi rloc
		    		rloc = matcher.group(1);
		    	}
		    	
		    } 	    	 
		} 

  public static class MyUserInfo implements UserInfo {
	  
	  String passwd;
	  JTextField passwordField=(JTextField)new JPasswordField(20);
	    
    public String getPassword(){ return passwd; }
    
    public boolean promptYesNo(String str){
      /*Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;*/
    	return true;
    }    

    public String getPassphrase(){ return null; }
    
    public boolean promptPassphrase(String message){ return true; }
    
    public boolean promptPassword(String message){      
    	Router router = new Router("cisco");
    	passwd = router.getPassword();
    	return true;       	
    }
    public void showMessage(String message){ }      
    }  
}
