
public class Model {
	String  variable;
	Boolean value;
	
	Model(String v){
		this.variable = v;
	}
	public boolean equals(Object o){
		if(o instanceof Model){
			if(this.variable.equals(((Model)o).variable)){
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}
	}
	public int hashCode(){
		return variable.hashCode();
	}
}
