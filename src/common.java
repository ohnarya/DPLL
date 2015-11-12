/*
 * Author : Jiyoung Hwang
 * Date   : 11/07/2015
 * Desc   : implement common functionalities which are not directly related to the main logic
 * 
 * */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class common {
	
	private static Scanner reader;
	
	
	/*
	 * set mode from a user input
	 * include the functionality to check if the user's input is valid 
	 * 
	 * */
	
	public static int setMode(){
		Integer[] modes = {1,2,3};
		int mode=0;
		
		do{
			
			reader = new Scanner(System.in);
			System.out.println("Enter a number for a heuristic function\n"+
			                   "    1>:backtracking with Unit and Pure hueristics,\n"+
					           "    2>:backtracking with Unit heuristic\n"+
			                   "    3>:backtracking alone \n\n" +
			                   "    q>:finish the program >> ");
			
			String userinput = reader.nextLine();
			
			if(userinput.equals("q")){
				System.out.println("Bye Bye~");
				return -1;
			}
			
			if(userinput.matches("\\d+"))
				mode = Integer.parseInt(userinput);
			
		} while(!Arrays.asList(modes).contains(mode));
		return mode;
	}
	
	/*
	 * check inputs
	 * 
	 * */
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
	/*
	 * initialize capabilities
	 * 
	 * */
	public static  HashSet<String> InitializeCapability(){
		HashSet<String> capabilities = new HashSet<String>();
		
		capabilities.add("painter");   //painter
		capabilities.add("stapler");   //stapler
		capabilities.add("recharger"); //recharger
		capabilities.add("welder");    //welder
		capabilities.add("cutter");    //cutter
		capabilities.add("sander");    //sander
		capabilities.add("joiner");    //joiner
		capabilities.add("gluer");     //gluer
		
		return capabilities;
		
	}
	/*
	 * add query before running DPLL
	 * 
	 * */
	public static boolean addQuery(String filename, String querys){
		PrintWriter writer = null;
		boolean ret = true;
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter(filename,true)));
			writer.println();
			writer.println("# add queries.");
			for(String s : querys.split(" ")){
				writer.format("%s\r\n",s);
			}
			writer.close();
		}catch(IOException ioe){
			ret = false;
			System.out.println(ioe.getMessage());
			
		}finally{
			if(writer!=null)
				writer.close();
		}
		return ret;
	}
	/*
	 * read an input file
	 * 
	 * */
	public static ArrayList<Clause> readFile(String args[],HashSet<String> capabilities){
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		
		/*agent_job*/
		if(args.length>1){
			String [] capability = args[1].split(" ");
			for(String c : capability){
				
				if(!capabilities.contains(c)){
					System.out.format("Wrong capability(%s) was entered\n",c);
					return null;
				
				}
				/*add query into kb*/
				clauses.add(new Clause(c));
			}
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(args[0]))){
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
			System.out.format("%s does not exist.\n",args[0]);
			return null;
		}
			
		return clauses;
	}
	public static boolean printcapability(String filename){
		PrintWriter writer = null;
		boolean ret = true;
		try{
			writer = new PrintWriter(filename,"UTF-8");
			writer.println("# agents capabilities.");
			writer.println("-a painter stapler recharger welder");
			writer.println("-b cutter sander welder stapler");
			writer.println("-c cutter painter");
			writer.println("-d sander welder recharger");
			writer.println("-e painter stapler welder");
			writer.println("-f stapler welder joiner recharger");
			writer.println("-g stapler gluer painter recharger");
			writer.println("-h cutter gluer");
			writer.println("");
			writer.println("# agents can do a certain job");
			writer.println("-painter a c e g");
			writer.println("-stapler a b e f g");
			writer.println("-recharger a d f g");
			writer.println("-sander b d");
			writer.println("-welder a b d e f");
			writer.println("-cutter b c h");
			writer.println("-joiner f");
			writer.println("-gluer g h");
			writer.println("");
		
			writer.close();
		}catch(IOException ioe){
			ret = false;
			System.out.println(ioe.getMessage());
		}finally{
			try{
				if(writer!=null)
					writer.close();
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}	
		return ret;
	}
	public static boolean printjob_agent(String filename){
		PrintWriter writer = null;
		boolean ret = true;
		
		String [] agents = {"a","b","c","d","e","f","g","h"};
		
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter(filename,true)));
			writer.println("# 4 agents cannot be true at the same time");
			for(int i = 0;i<agents.length-3;i++){
				for(int j=i+1;j<agents.length-2;j++){
					for(int k=j+1;k<agents.length-1;k++){
						for(int l=k+1;l<agents.length;l++){
							
							writer.format("-%s -%s -%s -%s\r\n",agents[i],agents[j],agents[k],agents[l]);
						}
					}
				}
			}
			writer.close();
		}catch(IOException ioe){
			ret = false;
			System.out.println(ioe.getMessage());
		}finally{
			try{
				if(writer!=null)
					writer.close();
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		}
		return ret;
	}
}
