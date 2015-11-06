import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DPLLSat {
	static HashSet<String>    capabilities = new HashSet<String>();
	static ArrayList<Clause>  clauses      = new ArrayList<Clause>();
	static ArrayList<Symbol>  symbols      = new ArrayList<Symbol>();
	
	public static void main(String args[]){
		
		InitializeCapability();
		if(!checkInput(args))
			return;
		if(!readFile(args[0]))
			return;
		
		if(clauses.isEmpty())
			return;
		
		DPLLSat dpllsat = new DPLLSat();
		dpllsat.extractSymbols();
		dpllsat.printInitialclauses();
		dpllsat.dpll();
	}
	private boolean dpll(){
		for(Clause c : clauses)
			c.printElements();
		return false;
	}
	private void printInitialclauses(){
		System.out.println("Initial clauses:");
		int i=0;
		for(Clause c : clauses)
			System.out.format("%d: (%s)\n",i++, c.clause);
		
		System.out.println("-------------------");
	}
	private void printSymbols(){
		System.out.println("Props:");
		for(Symbol s:symbols)
			System.out.format("%s " , s.symbol );
		System.out.println("");
	}
	private void extractSymbols(){
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
		printSymbols();
	}

	private static boolean readFile(String filename){
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String curline;
			while((curline = br.readLine()) !=null ){
				/*ignore empty lines*/
				if(curline.trim().length()<1)
					continue;
				/*ignore comments*/
				Pattern p = Pattern.compile("[#]");
				Matcher m = p.matcher(curline);
				
				if(!m.find()){
					if(!clauses.contains(curline))
						clauses.add(new Clause(curline));
				}
			}
		}catch(IOException e){
			System.out.format("%s does not exist.\n",filename);
			return false;
		}
		return true;
	}
	private static void InitializeCapability(){
		capabilities.add("painter");
		capabilities.add("stapler");
		capabilities.add("recharger");
		capabilities.add("welder");
		capabilities.add("cutter");
		capabilities.add("sander");
		capabilities.add("joiner");
		capabilities.add("gluer");
	}
	private static boolean checkInput(String args[]){
		int i=1;
		if(args.length != 2){
			System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i);  //1
			return false;
		}
		i++;
		String filename = args[0];
		if(filename.isEmpty()){
			System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i);  //2
			return false;
		}
		Pattern p = Pattern.compile("\\w+.kb");
		Matcher m = p.matcher(filename.toLowerCase());
		
		if(!m.find()){
			System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i); //3
			return false;
		}
		
		i++;
		String capability = args[1];
		if(capability.isEmpty()){
			System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i); //4
			return false;
		}
		
		i++;
		String[] capa = capability.split(" ");
		if(capa.length<1){
			System.out.format("[%d] the lists of capabilities are empty\nInput : xxxx.kb \"lists of capability\"",i); //5
			return false;
		}
		i++;
		for(int k=0;k<capa.length;k++){
			if(!capabilities.contains(capa[k])){
				System.out.format("[%d] \"%s\" is not in the list of capabilities\nInput : xxxx.kb \"lists of capability\"",i,capa[k]);
				return false;
			}
		}
		
		return true;
		
	}
}


