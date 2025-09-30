package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.hero.Hero;

public class InventoryPanel extends HeroPanel {
   public InventoryPanel(World world, int x, int y, Hero hero) {
      super(world, x, y, hero, true);

      for (int xt = 0; xt < 3; xt++) {
         for (int yt = 0; yt < 4; yt++) {
            int slot = xt + yt * 3;
            this.addChild(new InventorySlot(world, hero, slot, x + 2 + 32 + 24 + xt * 16 + 1, y + 2 + yt * 16, 16, 16));
         }
      }

      for (int xt = 0; xt < 6; xt++) {
         for (int yt = 0; yt < 3; yt++) {
            int slot = xt + yt * 6 + 12;
            this.addChild(new InventorySlot(world, hero, slot, x + 2 + 32 + 24 + xt * 16 + 1 + 48 + 1, y + 2 + yt * 16 + 6, 16, 16));
         }
      }
   }

   @Override
   public void toggleInventory() {
      this.world.gui.closeInventory();
   }
}
