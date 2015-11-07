import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DPLLSat {
	static HashSet<String>    capabilities = new HashSet<String>();
	static ArrayList<Clause>  clauses      = new ArrayList<Clause>();
	
	static HashMap<String, Boolean>   model        = new HashMap<String, Boolean>();
	int iter = 0;
	
	public static void main(String args[]){

		/*check inputs*/
		if(!common.checkInput(args))
			return;
		/*dpll satisfiability*/
		if(args.length==1){
			DPLLSat dpllsat = new DPLLSat();
			/*readfile*/
			clauses = common.readFile(args[0]);
			if(clauses!=null && clauses.isEmpty() )
				return;
				
			HashMap<String, Boolean> symbols = dpllsat.extractSymbols();
			dpllsat.printInitialclauses();
			dpllsat.dpll(symbols);
			dpllsat.printsolution();
			
		/*dpll job_agent satisfiability*/	
		}else if(args.length==2){
//			DPLLjob dplljob = new DPLLjob();
		}else{
			return;
		}
	}
	/*
	 * print solution
	 * 
	 * */
	private void printsolution(){
		System.out.println("----------------------------");
		System.out.format("node searched=%d\n", iter);
		System.out.println("solution:");
		for(String key:model.keySet()){
			System.out.format("%s=%b\n",key,model.get(key));
		}
		printtrue();
	}
	
	/*
	 * print literals with true value
	 * 
	 * */
	private void printtrue(){
		System.out.println("true props:");
		for(String key:model.keySet()){
			if(model.get(key))
				System.out.println(key);
		}
	}
	
	/*
	 * run DPLL - recursive backtracking search algorithm
	 * 
	 * */
	private boolean dpll(HashMap<String, Boolean> symbols){
//		if(iter>30)
//			System.exit(42);
		/*check all clauses are true*/
		if(isAllclausestrue(symbols))
			return true;
		
		if(isSomeclausefalse())
			return false;
		
		String s;
		boolean isunit = false;
		s = findUnitclause(symbols);
		
		/*no unit clauses*/
		if(s==null){
			s = findUnassignedsymbol(symbols);
		}else{
			isunit = true;
		}
		
		/*s is in use*/
		symbols.put(s,true);
		printModel();
		System.out.format("[%d]trying on %s=%b\n",++iter,s,true);
		model.put(s,true);
		
		/*set a unit clause based on a selected model*/
		propagateUnitclause();
		//check true first
		if(!dpll(symbols)){	
			System.out.println("backtracking");
			printModel();
			System.out.format("[%d]trying on %s=%b\n",++iter,s,false);
			model.put(s,false);
			
			// check false later
			if(!dpll(symbols)){
				/*s is not use any more*/
				symbols.put(s,false);
				/*remove symbol from model*/
				model.remove(s);
				
				/*revoke an unit clause to not an unit clause */
				propagateUnitclause(s);
				System.out.println("backtracking");
				return false;
			}
		}
		/*do code*/
		return true;
		
	}
	/*
	 * find an unit clause (heuristic)
	 * 
	 * */
	private String findUnitclause(HashMap<String, Boolean> symbols){
		for(Clause c: clauses){
			if(c.isUnitclause && !symbols.get(c.unitclause)){
				return c.unitclause;
			}
		}
		return null;
	}
	/*
	 * propagate unit clauses when a literal has been added to a model
	 * 
	 * */
	private void propagateUnitclause(){
		int unit;
		String unitclause=null;
		for(Clause c : clauses){	
			if(!c.isUnitclause){
				unit = c.elements.size();
				for(Element e : c.elements){
					
					if(model.containsKey(e.symbol)){
						if(unit==1)
							break;
						unit--;						
					}else{
						unitclause = e.symbol;
					}
				}
				if(unit==1){
					c.isUnitclause = true;
					c.unitclause   = unitclause;
					System.out.format("unit_clause on (%s)\n",c.clause);
				}
			}
		}
		
	}
	
	/*
	 * revoke propagation for unit clauses when a literal has been removed from model
	 *  
	 * */
	private void propagateUnitclause(String symbol){
		for(Clause c : clauses){	
			if(c.elements.size()>1 && c.isUnitclause){
				for(Element e : c.elements){
					
					if(e.symbol.equals(symbol) && c.isUnitclause){
						c.isUnitclause = false;
						c.unitclause   = null;
					}
				}
			}
		}		
	}
	
	/*
	 * check if there is a not-satisfying clause with a model
	 * 
	 * true : some clauses are false
	 * false: when model hasn't been set, all clauses are satisfying
	 * 
	 * */
	private boolean isSomeclausefalse(){
		
		if(model.size()<1)
			return false;
		
		/*if model is complete*/
		for(Clause c : clauses){
			//System.out.println(c);
			if(c.isTrue(model)== -1){
				System.out.format("not satisfying on (%s)\n", c.clause);
				return true;
			}
		}
		/*if all clauses are true*/
		return false;		
	}
	
	/*
	 * find unassigned symbol
	 * 
	 * */
	private String findUnassignedsymbol(HashMap<String, Boolean> symbols){
		for(String key:symbols.keySet()){
			if(!symbols.get(key)){
				return key;
			}
		}
		return "";
	}
	/*
	 * check all clauses satisfy a model when a model is fully set
	 * 
	 * true : success
	 * false: the model hasn't been fully set, there are unsatisfying clauses for the model
	 * 
	 * */
	private boolean isAllclausestrue(HashMap<String, Boolean> symbols){
		if(model.size()<1)
			return false;

		if(model.size()!=symbols.size()){
			//System.out.println("Not fully go");
			return false;
		}
		/*if model is complete*/
		for(Clause c : clauses){
			if(c.isTrue(model)!=1){
				//System.out.format("%s is not true\n",c.clause);
				return false;
			}
		}
		/*if all clauses are true*/
		return true;
	}
	/*
	 * print initial clauses
	 * 
	 * */
	private void printInitialclauses(){
		System.out.println("Initial clauses:");
		int i=0;
		for(Clause c : clauses)
			System.out.format("%d: (%s)\n",i++, c.clause);
		
		System.out.println("-------------------");
	}
	
	/*
	 * print symbols
	 * 
	 * */
	private void printSymbols(HashMap<String,Boolean> symbols){
		System.out.println("Props:");
		for(String key:symbols.keySet())
			System.out.format("%s " , key );
		System.out.println("");
	}
	
	/*
	 * extract Symbols from clauses
	 * 
	 * */
	private HashMap<String, Boolean> extractSymbols(){
		HashMap<String, Boolean> symbols = new HashMap<String, Boolean> ();
		String tmp = null;
		for(int i=0;i<clauses.size();i++){
			tmp = clauses.get(i).clause;
			tmp = tmp.replace("-", "");
			
			for(String s : tmp.split(" ")){				
				if(!symbols.containsKey(s)){
					symbols.put(s,false);
				}
			}
		}
		printSymbols(symbols);
		return symbols;
	}
	/*
	 * print model
	 * 
	 * */
	private void printModel(){
		System.out.print("model={");
		for(String key:model.keySet()){
			System.out.format("(%s:%b)",key,model.get(key));
		}
		System.out.println("}");
	}

}


