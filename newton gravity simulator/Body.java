import java.util.AbstractQueue;
import java.util.PriorityQueue;

//=============================================================================================================================================================
//
// DO NOT CHANGE THIS CLASS (PROVIDED BY PROFESSOR)
//
public class Body
{
   // =========================================================================================================================================================
   public class Pair
   {
      private double x;
      private double y;

      // ------------------------------------------------------------------------------------------------------------------------------------------------------
      public Pair(final double x, final double y)
      {
         this.x = x;
         this.y = y;
      }

      // ------------------------------------------------------------------------------------------------------------------------------------------------------
      public double getX()
      {
         return x;
      }

      // ------------------------------------------------------------------------------------------------------------------------------------------------------
      public double getY()
      {
         return y;
      }

      // ------------------------------------------------------------------------------------------------------------------------------------------------------
      @Override
      public String toString()
      {
         return ("[x=" + x + " y=" + y + "]");
      }
   }

   private static final boolean ENABLE_ATTRACTION = true;

   private static final double G = 500; // you don't want to live in this universe!

   private final String _id;

   private final Pair _position;

   private final Pair _velocity;

   private final Pair _force;

   private final double _mass;

   private final double _diameter;

   private final boolean _canCollide;

   private double _time = 0;

   private final AbstractQueue<Double> _fireTimesForward = new PriorityQueue<>();

   private final AbstractQueue<Double> _fireTimesReverse = new PriorityQueue<>();

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Body(final String id, final double x, final double y, final double vx, final double vy, final double diameter)
   {
      this(id, x, y, vx, vy, diameter, true);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Body(final String id, final double x, final double y, final double vx, final double vy, final double diameter, final boolean canCollide)
   {
      if ((id == null) || id.isEmpty())
      {
         throw new IllegalArgumentException("illegal id: [" + id + "]");
      }

      if (diameter <= 0)
      {
         throw new IllegalArgumentException("illegal diameter: " + diameter);
      }

      _id = id;

      _position = new Pair(x, y);

      _velocity = new Pair(vx, vy);

      _force = new Pair(0, 0);

      _mass = (((4 / 3) * Math.PI * Math.pow((diameter / 2), 3)) / 1e10); // mass is tied to volume for Task 3

      _diameter = diameter;

      _canCollide = canCollide;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   protected void addForce(final Body body)
   {
      if (body == null)
      {
         throw new NullPointerException("body");
      }

      if (ENABLE_ATTRACTION)
      {
         final double TWEAK = 1;

         double dx = (body._position.x - _position.x);
         double dy = (body._position.y - _position.y);

         double distance = Math.sqrt((dx * dx) + (dy * dy));

         double force = ((G * _mass * body._mass) / ((distance * distance) + (TWEAK * TWEAK)));

         _force.x += ((force * dx) / distance);
         _force.y += ((force * dy) / distance);
      }
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public double calculateDistance(final Body body)
   {
      if (body == null)
      {
         throw new NullPointerException("body");
      }

      double dx = (_position.x - body._position.x);
      double dy = (_position.y - body._position.y);

      return Math.sqrt((dx * dx) + (dy * dy));
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public boolean canCollide()
   {
      return _canCollide;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   private void fireThruster(final boolean isForward)
   {
      double direction = (isForward ? +1 : -1);

      final double acceleration = 0.1;

      _velocity.x += (direction * (_velocity.x * acceleration));
      _velocity.y += (direction * (_velocity.y * acceleration));
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public double getDiameter()
   {
      return _diameter;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Pair getForce()
   {
      return _force;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public String getID()
   {
      return _id;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public double getMass()
   {
      return _mass;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Pair getPosition()
   {
      return _position;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public Pair getVelocity()
   {
      return _velocity;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   protected void resetForce()
   {
      _force.x = 0.0;
      _force.y = 0.0;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   // forward increases current velocity by 10%; reverse decreases
   // simulation times in seconds; duplicates and non-increasing orders are ok
   public void scheduleThruster(final boolean isForward, final double... times)
   {
      if (times == null)
      {
         throw new NullPointerException("time");
      }

      if (times.length == 0)
      {
         throw new IllegalArgumentException("missing times");
      }

      for (double time : times)
      {
         if (time <= 0)
         {
            throw new IllegalArgumentException("illegal time: " + time);
         }

         System.out.println(" [" + _id + "] SCHEDULED " + (isForward ? "FORWARD" : "REVERSE") + " THRUSTER AT " + time);

         if (isForward)
         {
            _fireTimesForward.add(time);
         }
         else
         {
            _fireTimesReverse.add(time);
         }
      }
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   @Override
   public String toString()
   {
      return (_id + "," + _position.x + "," + _position.y + "," + _velocity.x + "," + _velocity.y + "," + _force.x + "," + _force.y + "," + _mass + ","
            + _diameter);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   protected void update(final double timeStep)
   {
      if (timeStep <= 0)
      {
         throw new NullPointerException("illegal timeStep: " + timeStep);
      }

      _velocity.x += ((timeStep * _force.x) / _mass);
      _velocity.y += ((timeStep * _force.y) / _mass);

      _position.x += (timeStep * _velocity.x);
      _position.y += (timeStep * _velocity.y);

      if (!_fireTimesForward.isEmpty())
      {
         double nextFireTime = _fireTimesForward.element();

         if (_time >= nextFireTime)
         {
            _fireTimesForward.remove();

            fireThruster(true);
         }
      }

      if (!_fireTimesReverse.isEmpty())
      {
         double nextFireTime = _fireTimesReverse.element();

         if (_time >= nextFireTime)
         {
            _fireTimesReverse.remove();

            fireThruster(false);
         }
      }

      _time += timeStep;
   }
}