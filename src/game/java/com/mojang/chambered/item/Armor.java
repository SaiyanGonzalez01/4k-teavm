package com.mojang.chambered.item;

public class Armor extends Item {
   public static final int AMULET = 0;
   public static final int RING = 1;
   public static final int QUIVER = 2;
   public static final int HELMET = 3;
   public static final int SHIRT = 4;
   public static final int PANTS = 5;
   public static final int BOOTS = 6;
   public static final int SHIELD = 7;
   public int type;

   public Armor(int type) {
      this.type = type;
   }

   @Override
   public boolean canPlaceIn(ItemInstance itemInstance, int slot) {
      if (slot == 1 && this.type == 3) {
         return true;
      } else if (slot == 0 && this.type == 0) {
         return true;
      } else if ((slot == 6 || slot == 9) && this.type == 1) {
         return true;
      } else if (slot == 2 && this.type == 2) {
         return true;
      } else if (slot == 4 && this.type == 4) {
         return true;
      } else if (slot == 7 && this.type == 5) {
         return true;
      } else {
         return slot == 10 && this.type == 6 ? true : super.canPlaceIn(itemInstance, slot);
      }
   }
}
