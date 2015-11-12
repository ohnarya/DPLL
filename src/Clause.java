/*
 * Author : Jiyoung Hwang
 * Date   : 11/07/2015
 * Desc   : deals with clauses including figuring out elements, if the clause is true, unknown, or false
 * 
 * */

import java.util.ArrayList;
import java.util.HashMap;


public class Clause {
	String             clause;
	int                satisfied;
	boolean            isUnitclause;
	Element            unitclause;
	
	boolean            isPureclause;
	ArrayList<Element> elements      = new ArrayList<Element>();
	
	Clause(String c){
		this.clause       = c;
		this.isUnitclause = false;
		this.unitclause   = null;
		this.isPureclause = false;
		buildElements();
	}
	/*
	 * check a clause satisfies model
	 * return 
	 * 1 : satisfy
	 * 0 : unknown - it means that there are other symbols in clause
	 * -1: not satisfy 
	 * 
	 * */
	public int isTrue(HashMap<String, Boolean> model){
		if(model.size()<1)
			return 0;

		int ret = 1;
		for(Element e : elements){
			/*model has a key*/			
			if(model.containsKey(e.symbol)){
				if(e.value!=model.get(e.symbol)){
					/*it has opposite value -> false, but previously it is unknow, it is unknown*/
					ret = ret==0?0:-1;
					
				}else{
					ret = 1;
					break;
				}
				/*if the clause satisfy : stay in satisfy(1)*/
			}else{
			/*model doesn't have a key : unknown*/
				ret = 0;	
			}
		}
		return ret;
	}
	/*
	 * print elements
	 * 
	 * */
	public void printElements(){
		for(Element e:elements){
			System.out.format("%s(%b)/", e.symbol, e.value);
		}
		System.out.println("");
	}
	
	/*
	 * build elements from a string type clause; 
	 * 
	 */
	 
	private void buildElements(){
		String[] clauses = clause.split(" ");
		for(String c:clauses){
			Element e;
			
			/*negative*/
			if(c.substring(0,1).equals("-")){
				e = new Element(c.substring(1));
				e.value = false;
				elements.add(e);
			}
			else{
				e = new Element(c);
				e.value = true;
				elements.add(e);
			}
		}
		
		/*set unit clause when the clause has only one literal*/
		if(elements.size() == 1){
			this.isUnitclause = true;
			this.unitclause   = elements.get(0);
		}
	}
	
	/*
	 * override basic function for compare/hashing 
	 * 
	 * */
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
		return clause;
	}
}
/*
 * inner class for elements
 * 
 * */
class Element{
	String symbol;
	boolean value;
	
	Element(String s){
		this.symbol = s;
	}
	/*
	 * override basic function for compare/hashing 
	 * */
	public boolean equals(Object o){
		if(o instanceof Element){
			if(this.symbol.equals(((Element)o).symbol)){
				return true;
			}else
				return false;
		}else
			return false; 
	}
	public int hashCode(){
		return this.symbol.hashCode();
	}
	public String toString(){
		return this.symbol + ":" + this.value;
	}	
}