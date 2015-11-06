public class Symbol implements Comparable<Symbol>{
	String  symbol;
	Boolean value;
	Symbol(String s){
		symbol = s;
	}
	public boolean equals(Object o){
		if(o instanceof Symbol){
			if(this.symbol.equals(((Symbol)o).symbol)){
				return true;
			}else
				return false;
		}else
			return false;
	}
	@Override
	public int compareTo(Symbol s) {
		// TODO Auto-generated method stub
		return this.symbol.compareTo(s.symbol);
	}
	public int hashCode(){
		return this.symbol.hashCode();
	}
	public String toSting(){
		return this.symbol+":"+value;
	}
}
