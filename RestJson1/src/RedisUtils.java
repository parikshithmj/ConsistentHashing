import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import redis.clients.jedis.Jedis;

public class RedisUtils {
	final static int MAX_KEYS_ALLOWED = 135;
    final static int INTERVAL_TO_CHECK_KEYS_SIZE_LIMIT = 1;
    static SortedMap<Long, Long> hashKeyhashServ = null;
    static Set<Long> hashKeyhashServKeys = null;
    static SortedMap<Long, Jedis> hashServ = null;
	public static Employee getEmployeeById(int id){//;,SortedMap<Long, Long> hashKeyhashServ,SortedMap<Long, Jedis> hashServ){
      Employee emp = new Employee(null, null, 0);
		byte[] hashBytes= getDigest().digest(ByteBuffer.allocate(4).putInt(String.valueOf(id).hashCode()).array());
        Long hashSlot = java.nio.ByteBuffer.wrap(hashBytes).getLong();
        for(Long hashKeyhashServVal:hashKeyhashServ.keySet()){
        	Long val = hashKeyhashServ.get(hashKeyhashServVal);
                            if(hashSlot < val){
                            	String tmp = String.valueOf(hashSlot);
                            	Jedis tmpNode = hashServ.get(val);
                               System.out.println(tmp+"Hashslot is "+hashSlot +" servKey is "+tmpNode.get(tmp));//+"INFO"+tmpNode.info("keyspace")); 
                              //format ->title=engineer45, name=emp:45, salary=45000
                               String[] tmpStr = tmpNode.get(tmp).split(","); 
                               emp.setName(tmpStr[0].split("=")[1]);
                                emp.setSalary(Integer.parseInt(tmpStr[2].split("=")[1]));
                                emp.setTitle(tmpStr[1].split("=")[1]);
                               break;
                            }
                        }
        return emp;
      }
    
    //reads the redis nodes info from CSV file
    public String readFromCSV(){
        StringBuilder result = new StringBuilder("");
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("redis-server-info.csv").getFile());
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
    //returns the list of Redis nodes
    public static List<Jedis> getRedisNodes(String results){
        String host;
        int port;
        String[] hostsAndPorts = results.split("\n");
        List<Jedis> redisNodes = new ArrayList<Jedis>();
        for(String hostAndPort:hostsAndPorts){
            host =hostAndPort.split(",")[0];
            System.out.println("**********"+host);
            port =Integer.parseInt(hostAndPort.split(",")[1]);
            Jedis jedis = new Jedis(host,port);
            redisNodes.add(jedis);
        }
        return redisNodes;

    }
    public static void createLargeRecords(){
    	  Employee empArr[] =new Employee[1000005];
          
          SortedMap<Long,Employee> keyHashMap = hashAndInsertDataToMap(empArr);
          RedisUtils rc=new RedisUtils();
          List<Jedis> redisNodes = getRedisNodes(rc.readFromCSV());
          hashServ= hashAndInsertNodesToMap(redisNodes);
          hashKeyhashServ = hashAndInsertToServMap(empArr,hashServ);
          hashKeyhashServKeys = hashKeyhashServ.keySet();
          int i=1;
          Jedis node = null;
          Employee emp = null;
          for(Long hashKeyhashServKey :hashKeyhashServKeys){
          	System.out.println(i+"final Hashkeyhashserve "+ keyHashMap.get(hashKeyhashServKey)+"So "
                    + "going to"+hashKeyhashServ.get(hashKeyhashServKey));
              node = hashServ.get(hashKeyhashServ.get(hashKeyhashServKey));
              emp = keyHashMap.get(hashKeyhashServKey);
              byte[] hashBytes= getDigest().digest(ByteBuffer.allocate(4).putInt(String.valueOf(i).hashCode()).array());
              node.set(hashKeyhashServKey+"",emp.toString());
              i++;
          }
    }
    //returns the MessageDigest of SHA-256
    public static MessageDigest getDigest(){
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");       
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
    }
   
    //hash the Record and return the map
    public static SortedMap<Long, Employee> hashAndInsertDataToMap(Employee[] empArr){
        SortedMap<Long,Employee> keyHashMap = new TreeMap<Long,Employee>();
       
        for(int i=1;i<400;i++){
            empArr[i]=new Employee("engineer"+String.valueOf(i),"emp:"+String.valueOf(i),i*1000);
            byte[] hashBytes= getDigest().digest(ByteBuffer.allocate(4).putInt(String.valueOf(i).hashCode()).array());
            System.out.println(i+" hash val is"+java.nio.ByteBuffer.wrap(hashBytes).getLong());
           // System.out.println(empArr[i]);
            keyHashMap.put(java.nio.ByteBuffer.wrap(hashBytes).getLong(),empArr[i]);
        }
        return keyHashMap;
    }
   
    //hash the nodes and return the map.
    public static SortedMap<Long,Jedis> hashAndInsertNodesToMap(List<Jedis> redisNodes){
        SortedMap<Long, Jedis> hashServ = new TreeMap<Long,Jedis>();
        for(Jedis jedis:redisNodes){
            for(int i=0;i<20;i++){
                byte[] hashBytes= getDigest().digest(ByteBuffer.allocate(4).putInt((jedis.toString()+String.valueOf(i)).hashCode()).array());
                hashServ.put(java.nio.ByteBuffer.wrap(hashBytes).getLong(), jedis);
                System.out.println(jedis+"Jedis nodes hash"+java.nio.ByteBuffer.wrap(hashBytes).getLong());
            }
        }
        return hashServ;
    }
   
    //hashed record/data mapped to the appropriate Redis node.
    public static SortedMap<Long, Long> hashAndInsertToServMap(Employee[] empArr,SortedMap<Long, Jedis> hashServ){
        Set<Long> servKeys = hashServ.keySet();
        SortedMap<Long, Long> hashKeyhashServ = new TreeMap<Long,Long>();
        Long tempVal;
        for(int i=1;i<400;i++){
        int ind =0;
       
        for(Long servKey:servKeys){       
                byte[] hashBytes= getDigest().digest(ByteBuffer.allocate(4).putInt(String.valueOf(i).hashCode()).array());;
                tempVal = java.nio.ByteBuffer.wrap(hashBytes).getLong();
                if(tempVal < servKey && !hashKeyhashServ.containsKey(tempVal)){
                    System.out.println(i+"tmpval is   less..going   "+tempVal +" servKey is "+servKey);
                    hashKeyhashServ.put(tempVal, servKey);
                    break;
                }
                //put to first again,to complete the ring
                if(ind==servKeys.size()-1 && tempVal >servKey ){
                    System.out.println(i+"tmpval is   greater..going   "+tempVal +" servKey is "+servKey);
                    hashKeyhashServ.put(tempVal, servKeys.iterator().next());
                }
                ind++;
            }
        }
        return hashKeyhashServ;
    }
   
    public static void rebalanceTheSize(SortedMap<Long, Jedis> HASH_SERV){
    	List<Jedis> redisNodes = getRedisNodes(new RedisUtils().readFromCSV());
        int maxSize = Integer.MIN_VALUE;
        Jedis maxNode = null;
        int minSize = Integer.MAX_VALUE;
        Jedis minNode = null;
        for(Jedis node:redisNodes){
          Set<String> e = node.keys("*");
          System.out.println("Node : "+node.getClient().getPort()+"| Size : "+e.size());
          if(e.size()>maxSize){
               maxSize = e.size();
               maxNode = node;
         }
          if (e.size()<minSize){
               minSize = e.size();
               minNode = node;
         }
        }
        System.out.println("Max size Node : "+maxNode.getClient().getPort()+"| Size : "+maxSize);
        System.out.println("Min size Node : "+minNode.getClient().getPort()+"| Size : "+minSize);
        Long replaceKey=null;
        Long prevKey = (Long)Arrays.asList(HASH_SERV.keySet().toArray()).get((HASH_SERV.size()-1));
        for(Long key:HASH_SERV.keySet()){
         if(HASH_SERV.get(key).getClient().getPort()==maxNode.getClient().getPort()){
              replaceKey = key;
              break;
         }
         prevKey = key;
        }
        System.out.println(maxNode.getClient().getPort()+" | "
               +minNode.getClient().getPort()+" | "
               +prevKey+" | "+replaceKey);
        new RedisUtils().migrate(maxNode, minNode, prevKey, replaceKey);
        HASH_SERV.put(replaceKey, minNode);
        System.out.println("Rebalancing completed");
       
     }
    
    public static void periodicCheck(List<Jedis> redisNodes,SortedMap<Long, Jedis> hashServ){
        TimerTask task = new TimerTask() {
              @Override
              public void run() {
                for(Jedis node:redisNodes){
                    String[] keySpaceInfo = node.info("keyspace").split(",");
                    //get the key value
                     String res = keySpaceInfo[0].split(":")[1].split("=")[1];
                     //check if the number of keys exceed max limit
                     if(Integer.parseInt(res) >MAX_KEYS_ALLOWED){
                         System.out.println("Rebalancing...");
                         rebalanceTheSize(hashServ);
                     }
                }
               
              }
            };
        Timer timer = new Timer();
        long delay = 0;
        //check n minutes
        long intevalPeriod = INTERVAL_TO_CHECK_KEYS_SIZE_LIMIT* 1000;
        timer.scheduleAtFixedRate(task, delay,intevalPeriod);
     
    }
	//Method to deleter server and migrate the data
	public void deleteServer(Jedis ser) {
		List<Object> serverSlot = Arrays.asList(hashServ.keySet().toArray());
		System.out.println("Deletion Server called :");
		// To get the starting slot value.
		Long prev=(Long)serverSlot.get((serverSlot.size()-1));
		Jedis next = null;
		// For each server slot, check whether it is required to be deleted.
		for(int i = 0;i<serverSlot.size();i++){
			//to check whether we reached end of the server slot 
			boolean flag = false;

			Long key = (Long)serverSlot.get(i);
			Jedis temp = hashServ.get(key);
			if(temp.getClient().getPort()==ser.getClient().getPort()){
				// If the same sever entry is next then loop till you get different server
				while(ser.getClient().getPort()==hashServ.get((Long)serverSlot.get(i)).getClient().getPort()){
					i++;
					if(i>=serverSlot.size()){
						i=0;
						flag=true;
					}
				}
				// To get the destination server
				next=hashServ.get((Long)serverSlot.get(i));
				// If the current server is in 1st entry, then get the last entry of array.
				if((i-1)<0){
					key = (Long)serverSlot.get(serverSlot.size()-1);
				}else{
					key = (Long)serverSlot.get((i-1));
				}
				// Call migrate with source jedis, destination jedis, Start slot and end slot.
				migrate(ser, next, prev, key);
				// If the loop has already reached end, break
				if(flag){
					break;
				}
			}
			// Update prev
			prev = key;
		}
		
	}
	//data migration method
		private void migrate(Jedis source, Jedis destination, Long startInd, Long endInd) {
			System.out.println("Migrate called : \n\t "+source.getClient().getPort()+" | "+destination.getClient().getPort()+" | "+startInd+" | "+endInd);
			Set<String> keys = source.keys("*");
			// case for removing first entry in hasServ map
			if(startInd>endInd){
				for(String key:keys){
					if(Long.parseLong(key) >= startInd || Long.parseLong(key) <= endInd){
						String employeeRecord = source.get(key);
						destination.set(key, employeeRecord);
						source.del(key);
					}
				}
			} else {
				for(String key:keys){
					System.out.println("Key : "+key);
					if(Long.parseLong(key) >= startInd && Long.parseLong(key) <= endInd){
						String employeeRecord = source.get(key);
						destination.set(key, employeeRecord);
						source.del(key);
					}
					
				}
			}
			System.out.println("Migration completed");
		}


}
