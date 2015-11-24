


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClusterInfo {

		String cluster_state;
		String  cluster_slots_assigned;
		String cluster_slots_ok;
		//String cluster_slots_pfail;
		String cluster_slots_fail;
		String cluster_known_nodes;
		String cluster_size;
	//	String cluster_current_epoch;
	//	String cluster_my_epoch;
	//	String cluster_stats_messages_sent;
	//	String cluster_stats_messages_received;
		
		public ClusterInfo() {
			// TODO Auto-generated constructor stub
		}
		public void setcluster_state(String cluster_state){
			this.cluster_state = cluster_state;
		}
		
		public String getState(){
			return cluster_state;
		}
		public void setCluster_slots_assigned(String cluster_slots_assigned){
			this.cluster_slots_assigned = cluster_slots_assigned;
		}
		
		public String getCluster_slots_assigned(){
			return cluster_slots_assigned;
		}
//		public void setcluster_stats_messages_received(String cluster_stats_messages_received){
//			this.cluster_stats_messages_received = cluster_stats_messages_received;
//		}
		
//		public String getcluster_stats_messages_received(){
//			return cluster_stats_messages_received;
//		}
		public void setcluster_size(String cluster_size){
			this.cluster_size = cluster_size;
		}
		
		public String getcluster_size(){
			return cluster_size;
		}
		public void setcluster_known_nodes(String cluster_known_nodes){
			this.cluster_known_nodes = cluster_known_nodes;
		}
		
		public String getcluster_known_nodes(){
			return cluster_known_nodes;
		}
		
		public String getcluster_slots_ok(){
			return cluster_slots_ok;
		}
		public void setcluster_slots_ok(String cluster_slots_ok){
			this.cluster_slots_ok = cluster_slots_ok;
		}
		
}