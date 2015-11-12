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
	HashSet<String>          capabilities = new HashSet<String>();
	ArrayList<Clause>        clauses      = new ArrayList<Clause>();
	HashMap<String, Boolean> model        = new HashMap<String, Boolean>();
	int mode   = 0;
	int iter   = 0;
	int atmost = 3;
	
	public static void main(String args[]){

		/*check inputs*/
		if(!common.checkInput(args))
			return;
		/*dpll satisfiability*/
		if(args.length==1){
			DPLLSat dpllsat = new DPLLSat();
			if(!dpllsat.getReady(args))
				return;
			
			HashMap<String, Boolean> symbols = dpllsat.extractSymbols();
			dpllsat.printInitialclauses();
			dpllsat.dpll(symbols);
			dpllsat.printsolution();
			
		/*dpll job_agent satisfiability*/	
		}else if(args.length==2){

			DPLLSat dplljob = new DPLLSat();
			
			if(!dplljob.getReady(args))
				return;	
			
			HashMap<String, Boolean> symbols = dplljob.extractSymbols();
			dplljob.printInitialclauses();
			dplljob.dpll(symbols);
			dplljob.printsolution();
		
		}else{
			return;
		}
	}
	DPLLSat(){
		
	}
	/*
	 * write agents capabilities, constraints, and query
	 * prompt the mode for dpll
	 * read a file to run DPLL to get agents to complete work 
	 * 
	 * */
	private boolean getReady(String args[]){

		capabilities = common.InitializeCapability();
		if(capabilities.isEmpty())
			return false;
		
		if(!common.printcapability(args[0]))
			return false;
		if(!common.printjob_agent(args[0]))
			return false;
		if(!common.addQuery(args[0], args[1]))
			return false;
		
		mode = common.setMode();
		if(mode < 0)
			return false;
		/*
		 * read a knowledge base from a file and set knowledge in clauses
		 * 
		 * */
		clauses = common.readFile(args, capabilities);
		if(clauses==null || clauses.isEmpty() )
			return false;
		else
			return true;
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
			
		Element s = null;
		/*find pure symbol when mode = 3*/
		if(mode == 1) 
			s = findPuresymbol(symbols);
		
		if(s==null && mode < 3)
			s = findUnitclause(symbols);
		
		if(s!=null){
		
			symbols.put(s.symbol, true);
			model.put(s.symbol, s.value);
			printModel();
			
			if(model.size()!=symbols.size())
				propagateUnitclause();
			
			if(!dpll(symbols
					)){
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
			s = findUnassignedsymbol(symbols);
	
			/*s is in use*/
			symbols.put(s.symbol,true);
			System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,true);
			model.put(s.symbol,true);
			printModel();
			/*set a unit clause based on a selected model*/
			if(model.size()!=symbols.size() && mode <3)
				propagateUnitclause();
			
			//check true first
			if(!dpll(symbols)){	
				System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,false);
				model.put(s.symbol,false);
				
				printModel();
				
				// check false later
				if(!dpll(symbols)){
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
		
		/*do code*/
		return true;
		
	}
	
	private Element findPuresymbol(HashMap<String, Boolean> symbols){
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
	private Element findUnitclause(HashMap<String, Boolean> symbols){
		for(Clause c: clauses){
			if(c.isUnitclause && !symbols.get(c.unitclause.symbol)){
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
					
					System.out.format("[%d]unit_clause on (%s) implies %s=%b\n",++iter,c.clause,c.unitclause.symbol,c.unitclause.value);
					return;
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
	private Element findUnassignedsymbol(HashMap<String, Boolean> symbols){
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
		if(clauses == null)
			return null;
		
		HashMap<String, Boolean> symbols = new HashMap<String, Boolean> ();
		
		String tmp = null;
		for(int i=0;i<clauses.size();i++){
			tmp = clauses.get(i).clause;
			tmp = tmp.replace("-", "");
			
			for(String s : tmp.split("[ ]+")){
				if(s.equals("r"))
					System.out.println("=====>"+s + "  " + tmp);
				if(!symbols.containsKey(s)){
					/*false : not used yet*/
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
			System.out.format("\'%s\':%b ",key,model.get(key));
		}
		System.out.println("}");
	}
}


