import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mapping {
	
	private List<String> eids = new ArrayList<>();
	private List<String> rlocs = new ArrayList<>();
	private HashMap<String, String> hm = new HashMap<>();
	
	public void add(String ip) {
	
		rlocs.add(ip);
		
	}
	public void getMappings(){
		
		System.out.println("     " + "EID" + "  	               " + "RLOC");
		for (String key: hm.keySet()){

            String value = hm.get(key);  
            System.out.println(key + "  	     " + value);  
} 
	}
	public void addMapping(String eid, String rloc) {
		
		hm.put(eid, rloc);
		
	}
}
