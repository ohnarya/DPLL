/*
 * Author : Jiyoung Hwang
 * Date   : 11/07/2015
 * Desc   : implement DPLL recursive backtracking search algorithm to satisfy propositional logic
 *          use 2 heuristic : pure symbol, unit clause
 *          baseline : backtracking search only
 * 
 * */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class DPLLSat {
	HashSet<String>          capabilities; 
	ArrayList<Clause>        clauses;      
	HashMap<String, Boolean> model ;  
	HashMap<String, Boolean> symbols;
    int mode;
	int iter;
	
	public static void main(String args[]){
		
		HashSet<String>   capabilities = common.InitializeCapability();
		ArrayList<Clause> clauses      = null;
		
		DPLLSat dpllsat                = null;
		int     mode;
		
		/*check inputs*/
		if(!common.checkInput(args,capabilities))
			return;
		
		/*run DPLL*/
		while(true){
			mode    = common.setMode();
			
			/*end condition*/
			if(mode<0)
				break;
			
			clauses = common.getReady(args, capabilities);
			dpllsat = new DPLLSat(capabilities, clauses,mode);
						
			dpllsat.run(args.length);				
		}
	}
	
	/*
	 * DPLL wrapper function
	 * 
	 * */
	public void run(int option){
		
		/*print initial clauses*/
		this.printInitialclauses();
		
		/*run DPLL*/
		this.dpll();
		
		/*print solution*/
		this.printsolution(option);		
	}

	/*
	 * run DPLL - recursive backtracking search algorithm
	 * 
	 * */
	private boolean dpll(){
		
		/*check all clauses are true*/
		if(isAllclausestrue(symbols))
			return true;
		
		if(isSomeclausefalse())
			return false;
			
		Element s = null;
		/*find pure symbol when mode = 3*/
		if(mode == 1) 
			s = findPuresymbol();
		
		if(s==null && mode < 3)
			s = findUnitclause();
		
		/*the case of using heuristics*/
		if(s!=null){
		
			symbols.put(s.symbol, true);
			model.put(s.symbol, s.value);
			printModel();
			
			if(!dpll()){
				/*s is not use any more*/
				symbols.put(s.symbol,false);
				/*remove symbol from model*/
				model.remove(s.symbol);
				
				/*revoke an unit clause to not an unit clause */
				propagateUnitclause(s.symbol);
				
				return false;
			}
		}
		/*no pure symbol and unit clauses*/
		else{
			s = findUnassignedsymbol();
	
			/*s is in use*/
			symbols.put(s.symbol,true);
			System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,true);
			model.put(s.symbol,true);
			printModel();
					
			//check true first
			if(!dpll()){	
				System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,false);
				model.put(s.symbol,false);
				
				printModel();
				
				// check false later
				if(!dpll()){
					/*s is not use any more*/
					symbols.put(s.symbol,false);
					/*remove symbol from model*/
					model.remove(s.symbol);
					
					/*revoke an unit clause to not an unit clause */
					if(mode<3)
						propagateUnitclause(s.symbol);

					System.out.format("backtracking\n");
					return false;
				}
			}
		}			
		
		return true;
		
	}
	
	/*
	 * find a Pure symbol : Heuristic function
	 * 
	 * */
	private Element findPuresymbol(){
		Boolean prevalue  = null;
		boolean isNotpure = true;
		Element e         = null;
		
		for(String key: symbols.keySet()){
			prevalue = null;
			
			/*exclude already assigned symbols*/
			if(symbols.get(key))
				continue;
			
			e = new Element(key);
			
			for(Clause c: clauses){
				
				/*exclude clauses not containing the symbol*/
				if(!c.clause.contains(key))
					continue;
				
				/*exclude true clauses*/
				if(c.isTrue(model)==1)
					continue;
				
				int index = c.clause.indexOf(key);

				if(c.clause.charAt(index>0? index-1:index)=='-'){
					e.value = false;
				}else 
					e.value = true;
				
				if(prevalue==null)
					prevalue = e.value;
				
				/*check pure by XOR*/
				isNotpure = prevalue ^ e.value;
				
				if(isNotpure)
					break;
				
				prevalue = e.value;
			}
			
			if(prevalue !=null && !(isNotpure)){
				System.out.format("[%d]Pure Symbol on %s=%b\n",++iter,e.symbol,e.value);
				return e;
			}
		}
		return null;
	}
	/*
	 * find an unit clause (heuristic)
	 * 
	 * what is a unit clause ?
	 * 1) has one literal
	 * 2) in DPLL context, all literals but one are already assigned false by the model
	 * 
	 * */
	private Element findUnitclause(){
		propagateUnitclause();
		for(Clause c: clauses){
			if(c.isUnitclause && !symbols.get(c.unitclause.symbol)){
				System.out.format("[%d]unit_clause on (%s) implies %s=%b\n",
			             ++iter,     c.clause,     c.unitclause.symbol, c.unitclause.value);
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
		Element unitclause;
		
		for(Clause c : clauses){	
			unit = c.elements.size();
			
			/*
			 * if a clauses has one literal, it is a unit clause
			 * so, among clauses has more than 1 literal, check if all literals but one 
			 * already assigned false by the model
			 * 
			 * */
			if(unit>1 && !c.isUnitclause){
				unitclause = null;
				
				for(Element e : c.elements){
					
					/*check literals if it is assigned to false */
					if(model.containsKey(e.symbol)){
					
						/*element is assigned false*/
						if(model.get(e.symbol) != e.value){
							unit--;
						}else{
							break;
						}
						
					}else{
						unitclause = e;
					}
				}
				
				//System.out.println(c.clause + " : " + unitclause);
				if(unitclause!=null && unit==1){
					c.isUnitclause = true;
					c.unitclause   = unitclause;
				
					break;
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
	 * find unassigned symbol - baseline (not using any heuristic functions
	 * 
	 * */
	private Element findUnassignedsymbol(){
		for(String key:symbols.keySet()){
			if(!symbols.get(key)){
				return new Element(key);
			}
		}
		return null;
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
		
		System.out.println("----------------------------");
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
	private void extractSymbols(){
		
		symbols = new HashMap<String, Boolean>();
		
		String tmp = null;
		for(int i=0;i<clauses.size();i++){
			tmp = clauses.get(i).clause;
			tmp = tmp.replace("-", "");
			
			for(String s : tmp.split("[ ]+")){
				if(!symbols.containsKey(s)){
					/*false : not used yet*/
					symbols.put(s,false);
				}
			}
		}
	
		printSymbols(symbols);
	}
	/*
	 * print model
	 * 
	 * */
	private void printModel(){
		System.out.print("model={");
		for(String key:model.keySet()){
			System.out.format("\'%s\':%b ",key,model.get(key));
		}
		System.out.println("}");
	}
	

	/*
	 * print solution
	 * 
	 * */
	private void printsolution(int isAgent){
		System.out.println("----------------------------");
		System.out.format("node searched=%d\n", iter);
		System.out.println("solution:");
		
		for(String key:model.keySet()){
			System.out.format("%s=%b\n",key,model.get(key));
		}
		printtrue(isAgent);
	}
	
	/*
	 * print literals with true value
	 * 
	 * */
	private void printtrue(int isAgent){
		System.out.println("----------------------------");
		
		if(isAgent==1){
			System.out.println("true props:");
			for(String key:model.keySet()){
				if(model.get(key))
					System.out.print(key+ " ");
			}
		}else{
			System.out.println("agent team:");
			for(String key:model.keySet()){
				if(!capabilities.contains(key) && model.get(key)){
					System.out.print(key+ " ");
				}
			}
		}
		System.out.println("\n----------------------------");
	}
	
	/*
	 * constructor
	 * 
	 * */
	DPLLSat(HashSet<String>   capabilities, ArrayList<Clause> clauses, int mode){
		this.capabilities = capabilities;
		this.clauses      = clauses;
		this.model        = new HashMap<String, Boolean>();
		this.mode         = mode;
		this.iter         = 0; 
		this.extractSymbols();
	}		
}


