import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import redis.clients.jedis.Jedis;


@Path("/status")
public class RedisService {
	
	  @GET
	  @Path("/{port}/online")
		@Produces(MediaType.APPLICATION_JSON)
		public OnlineStatus testIfUp(@PathParam("port")int port) {
		  OnlineStatus isOnline = new OnlineStatus();
		  try {
			  System.out.println(port);
			  Jedis jedis = new Jedis("localhost",port);

			  if(jedis.ping().toUpperCase().equals(Constants.PONG)){
				  isOnline.setstatus("up");
				  return isOnline;
				  
			  }
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		  isOnline.setstatus("down");
		return isOnline;
	  	}
	  @GET
	  @Path("/employee/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public Employee getEmployeeById(@PathParam("id")int id) {
		  Employee emp = RedisUtils.getEmployeeById(id);
		return emp;
	  	}
	  @GET
	  @Path("/{port}/keyValues")
		@Produces(MediaType.APPLICATION_JSON)
		public List<String> getAllKeyValues(@PathParam("port")int port) {
		  ArrayList<String> result = new ArrayList<String>();
		  try {
			  System.out.println(port);
			  Jedis jedis = new Jedis("localhost",port);
			  if(jedis.ping().toUpperCase().equals(Constants.PONG)){
				  Set<String> keySpaceInfo = jedis.keys("*");
				  for(String key:keySpaceInfo){
					  result.add(jedis.get(key));
				  }
				 // return keySpaceInfo.toString()
			  }
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return result;
	  	}
	  @GET
	  @Path("/{port}/keys")
		@Produces(MediaType.APPLICATION_JSON)
		public int getNumberOfKeys(@PathParam("port")int port) {
		  try {
			  	System.out.println(port);
			  	Jedis jedis = new Jedis("localhost",port);
			  	// jedis.clusterInfo();
			  	if(jedis.ping().toUpperCase().equals(Constants.PONG)){
			  		String keySpaceInfo[] = jedis.info("keyspace").split(",");
			  		//get the key value
			  		String res = keySpaceInfo[0].split(":")[1].split("=")[1];
			  		//check if the number of keys exceed max limit
			  		return Integer.parseInt(res);
			  }
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  } 
		return 0;
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
	  @Path("/getAllNodes")
	  @Produces(MediaType.APPLICATION_JSON)
	  public ArrayList<NodeInfo> postNewNode(){
		  String csvContents = readFromCSV();
		  ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();
		  NodeInfo nodeInfo = new NodeInfo();
		  for(String node:csvContents.split("\n")){
			  if(node.contains(",")){
			  String nodeVal[] =node.split(",");
			  nodeInfo.setName(nodeVal[2]);
			  nodeInfo.setIp(nodeVal[0]);
			  nodeInfo.setPort(nodeVal[1]);
			  nodeInfo.setRole(nodeVal[3].split(":")[0]);
			  String child = node.split(":")[1];
				ArrayList<NodeInfo.Slave> slaveList =  new ArrayList<NodeInfo.Slave>();
				for(int i =0;i<child.split(",").length;i++){
				NodeInfo.Slave slave = new NodeInfo.Slave();
				if(i%2==0)
					slave.setSalveIp(child.split(",")[i]);
				else
					slave.setport(child.split(",")[i]);
				slaveList.add(slave);
				}
			 	nodeInfo.setSlaves(slaveList);
			 	nodes.add(nodeInfo);
			  }
		  }
		  return nodes;
	  }
	  
	  
	  @POST
	  @Path("/newNode")
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  public NodeInfo postNewNode(NodeInfo node){
		  try {
		      File file = new File("redis-server-info.csv");
			  System.out.println("name:"+file.getName()+"path"+file.getAbsolutePath());
		      if(!file.exists())
		       file.createNewFile();
			  String content = node.getIp()+","+node.getPort()+","+node.getName()+","+node.getRole()+":";
			  for(NodeInfo.Slave slave:node.getSlaves()){
				  content = content +slave.getSlaveIp()+","+ slave.getPort();
			  }
			  content = content +"\n";
		        FileWriter writer =new FileWriter(file.getName(),true);
		        BufferedWriter bufferWritter = new BufferedWriter(writer);
    	        bufferWritter.write(content);
    	        bufferWritter.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		  return node;
	  }
	  
	  @POST
	  @Path("/largeData")
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  public void createLargeRecords(NodeInfo node){
		  RedisUtils.createLargeRecords();
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
	  @DELETE
	  @Path("deleteNode/{port}")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response deleteNode(@PathParam("port")int port){
		  String csvContents = readFromCSV();
			  try{
			  File inputFile = new File("redis-server-info.csv");
			  System.out.println("Deleting from..:"+inputFile.getName());
			  File tempFile = new File("myTempFile.txt");

			  BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			  BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			  String currentLine;

			  while((currentLine = reader.readLine()) != null) {
			       if(currentLine.contains(String.valueOf(port))){ 
			    		  System.out.println("skipping...");
			    		  continue;
			    	  }
			      System.out.println("Writing"+currentLine);
			      writer.write(currentLine + System.getProperty("line.separator"));
			  }
			  writer.close(); 
			  reader.close(); 
			  tempFile.renameTo(inputFile);
			  new RedisUtils().deleteServer(new Jedis("127.0.0.1",port));
			  }catch (Exception e) {
				e.printStackTrace();
			}
		  return Response.ok().build();
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
				 	
				 	for(String s:jedis.info("server").split("\n")){
				 		if(s.contains("tcp")){
				 			nodeInfo.setPort(s.split(":")[1].trim());
				 		}
				 	}
				 	nodeInfo.setSlaves(slaves);
				 	nodeInfo.setIp("127.0.0.1");
				 	
			   return nodeInfo;
		  	   }catch (Exception e) {
		  		   	e.printStackTrace();
		  }
		return null;
	  	}
	  public String readFromCSV(){
	        StringBuilder result = new StringBuilder("");
	        //Get file from resources folder
	        //ClassLoader classLoader = getClass().getClassLoader();
	       // File file = new File(classLoader.getResource("redis-server-info.csv").getFile());
		      File file = new File("redis-server-info.csv");
	        try {
	            Scanner scanner = new Scanner(file);
	            while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                result.append(line).append("\n");
	            }
	            scanner.close();
	        }catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("result"+result.toString());
	        return result.toString();
	    }
	      
	 
}
