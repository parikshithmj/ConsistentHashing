import java.util.ArrayList;
import java.util.List;

public class NodeInfo {
	String name;
	String role;
	String ip;
	String port;
	//can be used if needed
	//String connected_slaves;
	public static class Slave{
		String slaveIp;
		String port;
		
		public void setSalveIp(String slaveIp){
			this.slaveIp = slaveIp;
		}
		public String getSlaveIp(){
			return slaveIp;
		}
		public void setport(String port){
			this.port = port;
		}
		
		public String getPort(){
			return port;
		}
		
	}
	ArrayList<Slave> slaves;
	public void setSlaves(ArrayList<Slave> slaves){
		this.slaves = slaves;
	}
	public ArrayList<Slave> getSlaves(){
		return slaves;
	}
	public void setRole(String role){
		this.role = role;
	}
	public String getRole(){
		return role;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setIp(String ip){
		this.ip = ip;
	}
	public String getIp(){
		return ip;
	}
	public void setPort(String port){
		this.port = port;
	}
	public String getPort(){
		return port;
	}
	
}
