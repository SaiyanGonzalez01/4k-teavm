package com.mojang.chambered.item;

import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.hero.Hero;

public class Item {
   public static final Item[] items = new Item[256];
   public static final Item sword = new Weapon().init(0, "Sword", "/items/items.png", 0, 1);
   public static final Item apple = new Food().init(1, "Red apple", "/items/items.png", 0, 0);
   public static final Item amulet = new Armor(0).init(2, "Gold amulet", "/items/items.png", 0, 2);
   public static final Item ring = new Armor(1).init(3, "Gold ring", "/items/items.png", 0, 3);
   public static final Item quiver = new Armor(2).init(4, "Quiver", "/items/items.png", 0, 4);
   public static final Item helmet = new Armor(3).init(5, "Leather helmet", "/items/items.png", 0, 5);
   public static final Item shirt = new Armor(4).init(6, "Leather shirt", "/items/items.png", 0, 6);
   public static final Item pants = new Armor(5).init(7, "Leather pants", "/items/items.png", 0, 7);
   public static final Item boots = new Armor(6).init(8, "Leather boots", "/items/items.png", 0, 8);
   public static final Item shield = new Armor(7).init(9, "Iron shield", "/items/items.png", 0, 9);
   public static final Item goldCoin = new Item().init(10, "Gold coin", "/items/items.png", 1, 0);
   public int id;
   public String name;
   private int xImage;
   private int yImage;
   private String imageName;

   protected Item init(int id, String name, String imageName, int xImage, int yImage) {
      if (items[id] != null) {
         System.out.println("### WARNING: Duplicate item in items[" + id + "]: " + items[id].name + " and " + name);
      }

      items[id] = this;
      this.name = name;
      this.id = id;
      this.xImage = xImage;
      this.yImage = yImage;
      this.imageName = imageName;
      return this;
   }

   public SpriteImage getInventorySprite(ItemInstance itemInstance) {
      return new SpriteImage(BitmapCache.get(this.imageName), this.xImage * 48, this.yImage * 16, 16, 16);
   }

   public SpriteImage getEntitySprite(ItemInstance itemInstance) {
      return new SpriteImage(BitmapCache.get(this.imageName), this.xImage * 48 + 16, this.yImage * 16, 32, 16, 0.5, 1.0, -2.0);
   }

   public boolean useOn(ItemInstance source, ItemInstance target, Hero hero, int slot) {
      return false;
   }

   public boolean useFrom(ItemInstance itemInstance, Hero hero, int slot) {
      return false;
   }

   public boolean consume(ItemInstance itemInstance, Hero hero) {
      return false;
   }

   public boolean canPlaceIn(ItemInstance itemInstance, int slot) {
      if (slot == 3) {
         return true;
      } else if (slot == 5) {
         return true;
      } else if (slot == 8) {
         return true;
      } else {
         return slot == 11 ? true : slot >= 12;
      }
   }
}
