package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.hero.Hero;

public class HandButton extends InventorySlot {
   public HandButton(World world, Hero hero, int slot, int x, int y, int width, int height) {
      super(world, hero, slot, x, y, width, height);
      this.col = 6254195;
      this.br = 40;
   }
}
