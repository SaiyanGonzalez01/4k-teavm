package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.hero.Hero;
import com.mojang.chambered.hero.InventoryListener;
import com.mojang.chambered.item.ItemInstance;

public class InventorySlot extends Component implements InventoryListener {
   private static Bitmap hudBitmap = BitmapCache.get("/gui/hud.png");
   protected int col = 5003099;
   protected int br = -40;
   private Hero hero;
   private int slot;

   public InventorySlot(World world, Hero hero, int slot, int x, int y, int width, int height) {
      super(world, x, y, width, height);
      hero.addInventoryListener(this);
      this.hero = hero;
      this.slot = slot;
   }

   @Override
   public void render(Bitmap target) {
      target.drawBox(this.x, this.y, this.width, this.height, this.col, this.br);
      ItemInstance itemInstance = this.hero.getInventory(this.slot);
      if (itemInstance != null) {
         SpriteImage spriteImage = itemInstance.getInventorySprite();
         target.blit(spriteImage, this.x + this.width / 2, this.y + this.height / 2 + 8);
      } else if (this.slot < 12) {
         int xSlot = this.slot % 3;
         int ySlot = this.slot / 3;
         target.alphaBlit(hudBitmap, this.x + (this.width - 16) / 2, this.y + (this.height - 16) / 2, xSlot * 16, ySlot * 16, 16, 16);
      }
   }

   @Override
   public boolean mouseDown(int x, int y, int button) {
      ItemInstance itemInstance = this.hero.getInventory(this.slot);
      if (button == 1
         && (this.world.gui.carried != null || itemInstance != null)
         && (this.world.gui.carried == null || this.world.gui.carried.canPlaceIn(this.slot))) {
         ItemInstance tmp = itemInstance;
         itemInstance = this.world.gui.carried;
         this.world.gui.carried = tmp;
         this.hero.setInventory(this.slot, itemInstance);
      }

      if (button == 3) {
         if (this.world.gui.carried != null && itemInstance != null) {
            if (this.world.gui.carried.useOn(itemInstance, this.hero, this.slot)) {
               return true;
            }

            if (itemInstance.useOn(this.world.gui.carried, this.hero, -1)) {
               return true;
            }
         }

         if (itemInstance != null && itemInstance.useFrom(this.hero, this.slot)) {
            return true;
         }
      }

      return true;
   }

   @Override
   public void slotChanged(Hero hero, int slot) {
      if (slot == this.slot) {
         this.setDirty();
      }
   }
}
