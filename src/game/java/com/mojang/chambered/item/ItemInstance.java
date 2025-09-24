package com.mojang.chambered.item;

import com.mojang.chambered.World;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.hero.Hero;

public class ItemInstance {
   World world;
   private SpriteImage inventorySprite;
   private SpriteImage entitySprite;
   private Item item;

   public ItemInstance(World world, Item item) {
      this.world = world;
      this.item = item;
      this.inventorySprite = item.getInventorySprite(this);
      this.entitySprite = item.getEntitySprite(this);
   }

   public SpriteImage getInventorySprite() {
      return this.inventorySprite;
   }

   public SpriteImage getEntitySprite() {
      return this.entitySprite;
   }

   public boolean take() {
      if (this.world.gui.carried != null) {
         return false;
      } else {
         this.world.gui.carried = this;
         return true;
      }
   }

   public String getName() {
      return this.item.name;
   }

   public boolean canPlaceIn(int slot) {
      return this.item.canPlaceIn(this, slot);
   }

   public boolean useOn(ItemInstance itemInstance, Hero hero, int slot) {
      return this.item.useOn(this, itemInstance, hero, slot);
   }

   public boolean useFrom(Hero hero, int slot) {
      return this.item.useFrom(this, hero, slot);
   }

   public boolean consume(Hero hero) {
      return this.item.consume(this, hero);
   }
}
