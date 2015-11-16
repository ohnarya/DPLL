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
				System.out.println("Bye Bye~~~~~");
				return -1;
			}
			
			if(userinput.matches("\\d+"))
				mode = Integer.parseInt(userinput);
			
		} while(!Arrays.asList(modes).contains(mode));
		return mode;
	}
	
	/*
	 * write agents capabilities, constraints, and query
	 * prompt the mode for dpll
	 * read a file to run DPLL to get agents to complete work 
	 * 
	 * */
	public static ArrayList<Clause> getReady(String args[], HashSet<String> capabilities){
		ArrayList<Clause> clauses = new ArrayList<Clause>();
		
		/*only for job_agent*/
		if(args.length>1){
			if(capabilities.isEmpty())
				return clauses;
			
			if(!common.printcapability(args[0]))
				return clauses;
			if(!common.printjob_agent(args[0]))
				return clauses;
			if(!common.addQuery(args[0], args[1]))
				return clauses;
		}
		
		/*
		 * read a knowledge base from a file and set knowledge in clauses
		 * 
		 * */
		clauses = common.readFile(args, capabilities);
		return clauses;
	}
	
	/*
	 * check inputs
	 * 
	 * */
	public static boolean checkInput(String args[],HashSet<String> capabilities){
		int i=1;
		
		if(args.length ==1){
			i++;
			/*1. check if the file exists*/
			String filename = args[0];
			if(filename.isEmpty()){
				System.out.format("[%d]No filename!\nexample : xxxx.kb",i);  //2
				return false;
			}
			
			return true;
		}else if(args.length == 2){
			
			i++;
			/*1-0. check if the file exists*/
			String filename = args[0];
			if(filename.isEmpty()){
				System.out.format("[%d]No filename!\nexample : xxxx.kb \"lists of capability\"",i);  //2
				return false;
			}
			/*1-1. check the file format*/
			Pattern p = Pattern.compile("\\w+.kb");
			Matcher m = p.matcher(filename.toLowerCase());
			
			if(!m.find()){
				System.out.format("[%d]Invalid fileformat(%s)\nexample : xxxx.kb \"lists of capability\"",i,filename); //3
				return false;
			}
			
			i++;
			
			/*2-0. check if the capabilities exist*/
			String capability = args[1];
			if(capability.isEmpty()){
				System.out.format("[%d]No capabilites!\nexample : xxxx.kb \"lists of capability\"",i); //4
				return false;
			}
			i++;
			/*2-0. check if input capabilities are valid */
			for(String c : capability.split(" ")){
				if(!capabilities.contains(c)){
					System.out.format("[%d]Invalid capability(%s)\nexample : xxxx.kb \"lists of capability\"",i,c); //5
					return false;				
				}
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
			writer.println("#an agent can do certain jobs");
			writer.println("-a painter");
			writer.println("-a stapler");
			writer.println("-a recharger");
			writer.println("-a welder");
			writer.println("-b cutter");
			writer.println("-b sander");
			writer.println("-b welder");
			writer.println("-b stapler");
			writer.println("-c cutter");
			writer.println("-c painter");
			writer.println("-d sander");
			writer.println("-d welder");
			writer.println("-d recharger");
			writer.println("-e painter");
			writer.println("-e stapler");
			writer.println("-e welder");
			writer.println("-f stapler");
			writer.println("-f welder");
			writer.println("-f joiner");
			writer.println("-f recharger");
			writer.println("-g stapler");
			writer.println("-g gluer");
			writer.println("-g painter");
			writer.println("-f recharger");
			writer.println("-h cutter");
			writer.println("-g gluer");
			
			
			writer.println("");
			writer.println("# a job can be done by one or several agents");
			writer.println("-painter a c e g");
			writer.println("-stapler a b e f g");
			writer.println("-recharger a d f g");
			writer.println("-sander b d");
			writer.println("-welder a b d e f");
			writer.println("-cutter b c h");
			writer.println("-joiner f");
			writer.println("-gluer g h");
			writer.println("");

			
			writer.println("# at least one agent should be selected");
			writer.println("a b c d e f g h");
			
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
