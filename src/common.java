import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class common {
	public static HashSet<String> InitializeCapability(){
		HashSet<String> capabilities = new HashSet<String>();
		
		capabilities.add("painter");
		capabilities.add("stapler");
		capabilities.add("recharger");
		capabilities.add("welder");
		capabilities.add("cutter");
		capabilities.add("sander");
		capabilities.add("joiner");
		capabilities.add("gluer");
		return capabilities;
	}	
	
	public static boolean checkInput(String args[]){
		int i=1;
		if(args.length ==1){
			i++;
			/*1. check if the file exists*/
			String filename = args[0];
			if(filename.isEmpty()){
				System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i);  //2
				return false;
			}
			
			return true;
		}else if(args.length == 2){
			
			i++;
			/*1. check if the file exists*/
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
			
			/*2. check if the capabilities exist*/
			String capability = args[1];
			if(capability.isEmpty()){
				System.out.format("[%d] Input : xxxx.kb \"lists of capability\"",i); //4
				return false;
			}			
			/*if pass every thing*/
			return true;

		}else{
			/*input arguement error*/
			return false;
		}
	}
	public static ArrayList<Clause> readFile(String filename){
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		
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
			return null;
		}
		return clauses;
	}
	
}
