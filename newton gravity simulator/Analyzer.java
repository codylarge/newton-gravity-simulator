import java.util.HashMap;
import java.util.Map;
//=============================================================================================================================================================
/**
 * Made by Cody Large
 */

/* Use link list because no random data access */
class DataList {

   static class DataNode {
      double data;
      DataNode next;

      public DataNode(double data) {
         this.data = data;
         this.next = null;
      }
   }

   private DataNode head;
   private DataNode tail;
   private int size;

   public DataList() {
      this.head = null;
      this.tail = null;
      this.size = 0; // Initialize size to 0
   }

   public DataList(double d) {
      this.head = new DataNode(d);
      this.tail = head;
      this.size = 1; // Increment size for initial element
   }

   public DataNode getHead()
   {
      return this.head;
   }

   public DataNode getTail()
   {
        return this.tail;
   }

   public DataList append(double data) {
      DataNode newNode = new DataNode(data);

      if (head == null) {
         head = newNode;
         tail = newNode;
      } else {
         tail.next = newNode;
         tail = newNode;
      }
      this.size++; // Increment size when adding a new element
      return this;
   }

   public int size() {
      return size;
   }

   public String toString()
   {
      String s = "";
      DataNode current = head;
      while (current != null)
      {
         s += current.data + ", ";
         //System.out.print(current.data + " -> ");
         current = current.next;
      }
      s +="null";
      return s;
   }
   // Testing
}

public class Analyzer
{
   // Main Hashmap represents each body...
   // Within body Hashmap is another Hashmap that contains a key & value for each double value passed into addEntry with keys: "time", "x", "y"
   HashMap<String, HashMap<String, DataList>> bodies;

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Analyzer()
   {
       bodies = new HashMap<>();
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public void addEntry(final long step, final double time, final String id, final double x, final double y)
   {
      HashMap<String, DataList> body; // Inner hashmap that contains the values for each body

      /*//For tests with no collision
      if(step >= 1000000)
      {
         printResults();
         System.exit(0);
      }
      */

      if (!bodies.containsKey(id))
      {
         DataList d = new DataList();
         // If the ID doesn't exist in the bodies HashMap, create a new nested HashMap
         body = new HashMap<>();
         body.put("time", new DataList(time));
         body.put("x", new DataList(x));
         body.put("y", new DataList(y));
         body.put("velocity", new DataList(0.0));
         body.put("acceleration", new DataList(0.0));
         body.put("distance", new DataList(0.0));
         bodies.put(id, body);
         return;
      }

      // If the ID already exists, update the values of time, x, and y
      // assume current id's hashmap already has 1 entry if program reaches this point
      body = bodies.get(id);
      double prevX = body.get("x").getTail().data;
      double prevY = body.get("y").getTail().data;
      double prevTime = body.get("time").getTail().data;
      double prevVelocity = body.get("velocity").getTail().data;

      // Add new values to map
      body.get("time").append(time);
      body.get("x").append(x);
      body.get("y").append(y);

      //double velX = (x - lastX) / (timeChange);
      //double velY = (y - lastY) / (timeChange);
      // Velocity
      double timeChange = time - prevTime;
      double distanceChange = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2));
      body.get("distance").append(distanceChange);
      double velocity = distanceChange / timeChange; // Distance formula to calculate total position change / time = avg velocity
      body.get("velocity").append(velocity); // add the velocity to the body HashMap

      // Acceleration
      double acceleration = (velocity - prevVelocity) / timeChange;
      body.get("acceleration").append(acceleration); // add the acceleration to the body HashMap
   }

   public void printResults()
   {
      for (String body : bodies.keySet())
      {
         System.out.println("Body: " + body);

         DataList times = bodies.get(body).get("time");
         DataList velocities = bodies.get(body).get("velocity");
         DataList accelerations = bodies.get(body).get("acceleration");

         double time = times.getTail().data - times.getHead().data;
         double minVelocity = findMin(velocities);
         double maxVelocity = findMax(velocities);
         double avgVelocity = calculateAverage(velocities);
         double distance = avgVelocity * time;
         double stdDevVelocity = calculateStdDev(velocities);
         double minAcceleration = findMin(accelerations);
         double maxAcceleration = findMax(accelerations);
         double avgAcceleration = calculateAverage(accelerations);
         double stdDevAcceleration = calculateStdDev(accelerations);

         System.out.println("Time: " + time);
         System.out.println("Distance: " + distance);
         System.out.println("\nVelocity");
         System.out.println("min: " + minVelocity);
         System.out.println("max: " + maxVelocity);
         System.out.println("avg: " + avgVelocity);
         System.out.println("std: " + stdDevVelocity);
         System.out.println("\nAcceleration");
         System.out.println("min: " + minAcceleration);
         System.out.println("max: " + maxAcceleration);
         System.out.println("avg: " + avgAcceleration);
         System.out.println("std: " + stdDevAcceleration);
         System.out.println();
      }
   }

   private double calculateAverage(DataList list)
   {
      double sum = 0;
      DataList.DataNode current = list.getHead();
      while (current != null)
      {
         sum += current.data;
         current = current.next;
      }
      return list.size() > 0 ? sum / list.size() : 0.0;
   }

   private double calculateStdDev(DataList list)
   {
      double sum = 0;
      double mean = calculateAverage(list);
      int count = 0;
      DataList.DataNode current = list.getHead();
      while (current != null)
      {
         sum += Math.pow(current.data - mean, 2);
         count++;
         current = current.next;
      }
      double variance = count > 0 ? sum / count : 0.0;
      return Math.sqrt(variance);
   }

   private double findMin(DataList list)
   {
      DataList.DataNode current = list.getHead();
      double min = Double.MAX_VALUE;
      while (current != null)
      {
         min = Math.min(min, current.data);
         current = current.next;
      }
      return min;
   }

   private double findMax(DataList list)
   {
      DataList.DataNode current = list.getHead();
      double max = Double.MIN_VALUE;
      while (current != null)
      {
         max = Math.max(max, current.data);
         current = current.next;
      }
      return max;
   }
}
