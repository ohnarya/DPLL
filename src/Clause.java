import java.util.HashMap;


public class Clause {
	String  clause;
	boolean satisfied;
	HashMap<String, Boolean> elements = new HashMap<String, Boolean>();
	
	Clause(String c){
		this.clause = c;
		buildElements();
	}
	public void printElements(){
		for(String key:elements.keySet()){
			System.out.format("%s(%b)/", key, elements.get(key));
		}
		System.out.println("");
	}
	private void buildElements(){
		String[] clauses = clause.split(" ");
		for(String c:clauses){
			if(c.substring(0,1).equals("-"))
				elements.put(c.substring(1), false);
			else
				elements.put(c, true);
		}
	}
	
	public boolean equals(Object o){
		if(o instanceof Clause){
			if(this.clause.equals(((Clause)o).clause)){
				return true;
			}else
				return false;
		}else
			return false; 
	}
	public int hashCode(){
		return clause.hashCode();
	}
	public String toString(){
		return clause + ":" + satisfied;
	}
}
