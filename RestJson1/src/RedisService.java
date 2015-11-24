import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import redis.clients.jedis.Jedis;


@Path("/status")
public class RedisService {
	
	  @GET
	  @Path("/{port}/online")
		@Produces(MediaType.APPLICATION_JSON)
		public String testIfUp(@PathParam("port")int port) {
		  try {
			  System.out.println(port);
			  Jedis jedis = new Jedis("localhost",port);
			  
			  if(jedis.ping().toUpperCase().equals(Constants.PONG));
			  		return "UP";
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return "DOWN";
	  	}
	  @GET
	  @Path("/{port}/keys")
		@Produces(MediaType.APPLICATION_JSON)
		public Long getNumberOfKeys(@PathParam("port")int port) {
		  try {
			  System.out.println(port);
			  Jedis jedis = new Jedis("localhost",port);
			  jedis.clusterInfo();
			  if(jedis.ping().toUpperCase().equals(Constants.PONG));
			  		return jedis.dbSize();
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		 
		return null;
	  	}
	  @GET
	  @Path("{port}/clusterInfo")
		@Produces(MediaType.APPLICATION_JSON)
		public ClusterInfo getClusterInfo(@PathParam("port")int port) {
		  try {
			   Jedis jedis = new Jedis("localhost",port);
			   
			   String result[] = jedis.clusterInfo().split("\n");
			   for(String s:result)
				   System.out.println(s); 
			   ClusterInfo info = new ClusterInfo();//result[0].split(":")[1],
						   //result[1].split(":")[1]);
			   info.setcluster_state(result[0].split(":")[1].trim());
			   info.setCluster_slots_assigned(result[1].split(":")[1].trim());
			   info.setcluster_known_nodes(result[5].split(":")[1].trim());
			   info.setcluster_slots_ok(result[2].split(":")[1].trim());
			   info.setcluster_size(result[6].split(":")[1].trim());
			   return info;
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return null;
	  	}
	  
	  @GET
	  @Path("{port}/clusterNodes") 
		@Produces(MediaType.APPLICATION_JSON)
		public List<ClusterNodes> getClusterNodes(@PathParam("port")int port) {
		  try {
			  List<ClusterNodes> list = new ArrayList<ClusterNodes>();
			   Jedis jedis = new Jedis("localhost",port);
			   for(String tmp : jedis.clusterNodes().split("\n")){
			   String result[] = tmp.split(" ");
			   for(String s:result)
				   System.out.println(s); 
			   ClusterNodes nodes = new ClusterNodes();
			   nodes.setIpAddress(result[1].trim());
			   nodes.setRole(result[2].trim());
			   nodes.setConnectionStatus(result[7].trim());
			   list.add(nodes);
			   }
			   return list;
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return null;
	  	}
	  
	  @GET
	  @Path("{port}/nodeInfo")
		@Produces(MediaType.APPLICATION_JSON)
		public NodeInfo getNodeInfo(@PathParam("port")int port) {
		  try {
			  ArrayList<NodeInfo.Slave> slaves = new ArrayList<NodeInfo.Slave>();
			  NodeInfo nodeInfo = new NodeInfo(); 
			  Jedis jedis = new Jedis("localhost",port);
				 //System.out.println(jedis.info("replication"));
				 	for(String s:jedis.info("replication").split("\n")){
				 		String[] res = s.split(",");
				 		//System.out.println(res[0]);
					 if(res[0].contains("slave") && res[0].contains("=")){
						 NodeInfo.Slave slave = new NodeInfo.Slave();
						 slave.setport(res[1].split("=")[1]);
						 slave.setSalveIp(res[0].split("=")[1]);
						 slaves.add(slave);
					 }	 
					 if(res[0].contains("role")){
						 nodeInfo.setRole(res[0].split(":")[1].trim());
					 }
				 	}
				 	
				 	nodeInfo.setSlaves(slaves);
			   return nodeInfo;
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return null;
	  	}
	  
//	  @PUT
//	  @Path("/{id}")
//	  @Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//	public Response put(@PathParam("id") UUID id,Track myclass) throws IOException {
//		  
//		  for(Track my:list){
//			  if(id.equals(my.getId()))
//				  my.setName(myclass.getName());
//		  }
//		
//	    return Response.ok().entity(myclass).build();
//
//	}
//	@GET
//	@Path("/{id}")
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//	public Track getCustomerInXML(@PathParam("id") UUID id) {
//		for(Track myclass:list){
//			if(id.equals(myclass.getId())){
//				return myclass;
//			}
//		}
//		return null;
//		
//	}
//  
//	 @POST
//	  @Consumes(MediaType.APPLICATION_JSON)
//	  public Response create(Track my) throws IOException {
//		  
//		  Track myclass = new Track();
//		  myclass.setId(UUID.randomUUID());
//		  myclass.setName(my.getName());
//		  list.add(myclass);
//		  
//	      return Response.ok().entity(myclass).build();
//
//	  }
//	 
//	 @DELETE
//	  @Path("/{id}")
//	  @Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//	public Response delete(@PathParam("id") UUID id,Track myclass) throws IOException {
//		  int ctr=0;
//		  for(Track my:list){
//			  if(id.equals(my.getId()))
//				  list.remove(ctr);
//			  ctr++;
//
//		  }
//		
//	    return Response.ok().entity(myclass).build();
//
//	}
	 
}
