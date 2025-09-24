package com.mojang.chambered.phys;

public abstract class Collideable {
   public static final long MASK_WALL = 1L;
   public static final long MASK_MONSTER = 2L;
   public static final long MASK_PLAYER = 4L;
   public static final long MASK_THROWN_ITEM = 8L;
   public static final long MASK_UNKNOWN = 16L;
   public static final long MASK_OBSTACLE = 32L;
   public static final long MASK_DEFAULT = 39L;
   public boolean wasStuck = false;

   public abstract double getCollisionTime(CollisionSphere var1, Vec var2, Vec var3);

   public abstract void collide(CollisionSphere var1);

   public abstract boolean isInside(CollisionSphere var1);
}
