import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CLIParser {
	
	private static Router router = new Router();
	
  public static void main(String[] arg) {	  
	  
    try{
    	
      JSch jsch=new JSch();  

      String host=null;
      if(arg.length>0){
        host=arg[0];
      }
      else{
    	  router = new Router("kote", "192.168.137.119", "sh lisp site");   	  
      }   

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
    
      inputStreamToString(in);
          
      channel.disconnect();
      session.disconnect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }
  public void initialize(Router router){
	  
  }
  public static void inputStreamToString(InputStream in) 
		  throws IOException {
		    		 
		    String text = IOUtils.toString(in, StandardCharsets.UTF_8.name());		    
		    
		    String EID = 
		            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?:\\/)(?:3[0-2]|[12][0-9]|[1-9])";
		           
		            
		 	String RLOC = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		    List<String> tokens = new ArrayList<String>();
		    tokens.add(EID);
		    tokens.add(RLOC);
		    //tokens.add(ipv6Pattern);
		    //tokens.add(ipv4WithMask);
		    
		    String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
		    Pattern pattern = Pattern.compile(patternString);
		    
		    Matcher matcher = pattern.matcher(text);
		  		  
		    while (matcher.find()) {		    	
		        System.out.println(matcher.group(1));	    	     
		    	
		    } 
		    	 
		} 

  public static class MyUserInfo implements UserInfo {
	  
	  String passwd;
	  JTextField passwordField=(JTextField)new JPasswordField(20);
	    
    public String getPassword(){ return passwd; }
    
    public boolean promptYesNo(String str){
      Object[] options={ "yes", "no" };
      int foo=JOptionPane.showOptionDialog(null, 
             str,
             "Warning", 
             JOptionPane.DEFAULT_OPTION, 
             JOptionPane.WARNING_MESSAGE,
             null, options, options[0]);
       return foo==0;
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
