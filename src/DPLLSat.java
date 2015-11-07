import java.util.ArrayList;
import java.util.Collections;
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
				
			ArrayList<Symbol> symbols = dpllsat.extractSymbols();
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
	private void printsolution(){
		System.out.println("----------------------------");
		System.out.format("node searched=%d\n", iter);
		System.out.println("solution:");
		for(String key:model.keySet()){
			System.out.format("%s=%b\n",key,model.get(key));
		}
		printtrue();
	}
	private void printtrue(){
		System.out.println("true props:");
		for(String key:model.keySet()){
			if(model.get(key))
				System.out.println(key);
		}
	}
	private boolean dpll(ArrayList<Symbol> symbols){
		/*check all clauses are true*/
		if(isAllclausestrue(symbols))
			return true;
		
		if(isSomeclausefalse())
			return false;
		
		Symbol s;
		
		//s = findUnitclause(symbols);
		
		s = findUnassignedsymbol(symbols);
		s.isUsed = true;
		printModel();
		System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,true);
		model.put(s.symbol,true);
		propagateUnitclause();
		//check true first
		if(!dpll(symbols)){
			
			System.out.println("backtracking");
			printModel();
			System.out.format("[%d]trying on %s=%b\n",++iter,s.symbol,false);
			model.put(s.symbol,false);
			
			// check false later
			if(!dpll(symbols)){
				
				s.isUsed = false;
				model.remove(s.symbol);
				propagateUnitclause(s.symbol);
				System.out.println("backtracking");
				return false;
				
			}
				
		}
		/*do code*/
		return true;
		
	}
//	private Symbol findUnitclause(ArrayList<Symbol> symbols){
//		for(Clause c: clauses){
//			if(c.isUnitclause && )
//		}
//	}
	private void propagateUnitclause(){
		int unit;
		
		for(Clause c : clauses){	
			if(!c.isUnitclause){
				unit = c.elements.size();
				for(Element e : c.elements){
					if(model.containsKey(e.symbol)){
						unit--;
						if(unit==1)
							c.isUnitclause = true;
					}
				}
			}
		}
	}
	private void propagateUnitclause(String symbol){
		for(Clause c : clauses){	
			if(c.isUnitclause){
				for(Element e : c.elements){
					if(e.symbol.equals(symbol) && c.isUnitclause){
						c.isUnitclause = false;
					}
				}
			}
		}		
	}
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
	private Symbol findUnassignedsymbol(ArrayList<Symbol> symbols){
		for(Symbol s : symbols){
			if(!s.isUsed){
				return s;
			}
		}
		return null;
	}
	private boolean isAllclausestrue(ArrayList<Symbol> symbols){
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
	 * */
	private void printSymbols(ArrayList<Symbol> symbols){
		System.out.println("Props:");
		for(Symbol s:symbols)
			System.out.format("%s " , s.symbol );
		System.out.println("");
	}
	
	/*
	 * extract Symbols from clauses
	 * */
	private ArrayList<Symbol> extractSymbols(){
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		String tmp = null;
		for(int i=0;i<clauses.size();i++){
			tmp = clauses.get(i).clause;
			tmp = tmp.replace("-", "");
			
			for(String s : tmp.split(" ")){
				Symbol S = new Symbol(s);
				if(!symbols.contains(S)){
					symbols.add(S);
				}
			}
		}
		/*sort symbols*/
		Collections.sort(symbols);
		printSymbols(symbols);
		return symbols;
	}
	private void printModel(){
		System.out.print("model={");
		for(String key:model.keySet()){
			System.out.format("(%s:%b)",key,model.get(key));
		}
		System.out.println("}");
	}

}


