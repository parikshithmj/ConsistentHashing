
public class ClusterNodes {
	//192d90877d0cd55635b91 127.0.0.1:30004 slave e7d1eecce10fd6bb5eb35b9f99a514335d9ba9ca 0 1426238317239 4 connected
	String ipAddress;
	String role;
	String connectionStatus;
	
	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}
	
	public String getipAddress(){
		return ipAddress;
	}
	public void setRole(String role){
		this.role = role;
	}
	
	public String getRole(){
		return role;
	}
	public void setConnectionStatus(String connectionStatus){
		this.connectionStatus = connectionStatus;
	}
	
	public String getConnectionStatus(){
		return connectionStatus;
	}
}
