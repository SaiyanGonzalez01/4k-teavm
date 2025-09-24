package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Font;
import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.gui.particle.GuiParticle;
import com.mojang.chambered.hero.Hero;
import com.mojang.chambered.item.ItemInstance;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gui extends Component {
   public static final int ACTION_OPEN_INVENTORY_1 = 1;
   public static final int ACTION_OPEN_INVENTORY_2 = 2;
   public static final int ACTION_OPEN_INVENTORY_3 = 3;
   public static final int ACTION_OPEN_INVENTORY_4 = 4;
   public static final int ACTION_CLOSE_INVENTORIES = 5;
   private BufferedImage image;
   private Bitmap bitmap;
   private Bitmap staticBitmap;
   public ItemInstance carried;
   private Bitmap bottomBg = BitmapCache.get("/gui/panel.png");
   private HeroPanel[] heroPanels = new HeroPanel[4];
   private InventoryPanel inventoryPanel = null;
   private Map<Hero, InventoryPanel> inventoryPanelMap = new HashMap<>();
   private boolean dirty = true;
   private CursorManager cursorManager;
   private boolean inventoryOpen = false;
   private List<GuiParticle> particles = new ArrayList<>();

   public Gui(World world, CursorManager cursorManager, int width, int height) {
      super(world, 0, 0, width, 240);
      this.cursorManager = cursorManager;
      this.image = new BufferedImage(width, height, 2);
      this.bitmap = new Bitmap(this.image);
      this.staticBitmap = new Bitmap(width, height);

      for (int i = 0; i < 4; i++) {
         this.inventoryPanelMap.put(world.party.heroes[i], new InventoryPanel(world, 4, height - 69, world.party.heroes[i]));
      }

      for (int i = 0; i < 4; i++) {
         int xp = width / 2 + (i - 2) * 60;
         int yp = height - 64 + (i - 1 & 2) * 5 - 5;
         this.heroPanels[i] = new HeroPanel(world, xp, yp, world.party.heroes[i]);
         this.addChild(this.heroPanels[i]);
      }
   }

   private void redrawStatic() {
      this.staticBitmap.blit(this.bottomBg, 0, this.height - this.bottomBg.height);
      this.renderChildren(this.staticBitmap);
   }

   @Override
   public void tick(int currentTick) {
      super.tick(currentTick);

      for (int i = 0; i < this.particles.size(); i++) {
         boolean alive = this.particles.get(i).tick(currentTick);
         if (!alive) {
            this.particles.remove(i--);
         }
      }
   }

   public void addParticleOnPortrait(Hero hero, GuiParticle particle, int xo, int yo) {
      if (this.inventoryOpen) {
         if (this.inventoryPanel.hero == hero) {
            this.inventoryPanel.addParticleOnPortrait(particle, xo, yo);
            this.addParticle(particle);
         }
      } else {
         for (int i = 0; i < this.heroPanels.length; i++) {
            if (this.heroPanels[i].hero == hero) {
               this.heroPanels[i].addParticleOnPortrait(particle, xo, yo);
               this.addParticle(particle);
            }
         }
      }
   }

   public void addParticle(GuiParticle particle) {
      this.particles.add(particle);
   }

   public void render(int xMouse, int yMouse, int currentTick, double alpha) {
      if (this.dirty) {
         this.redrawStatic();
         this.dirty = false;
      }

      this.renderDirtyChildren(this.staticBitmap);
      this.bitmap.blit(this.staticBitmap, 0, 0);

      for (int i = 0; i < this.particles.size(); i++) {
         this.particles.get(i).render(this.bitmap, currentTick, alpha);
      }

      if (!this.cursorManager.setCursor(this.carried) && this.carried != null) {
         this.bitmap.blit(this.carried.getInventorySprite(), xMouse, yMouse + 8);
      }

      if (this.carried != null) {
         int x = (this.width - Font.normal.width(this.carried.getName())) / 2;
         Font.normal.drawShadow(this.bitmap, this.carried.getName(), x, this.height - 9, 16777215);
      }
   }

   public BufferedImage getImage() {
      return this.image;
   }

   public void renderFps(int fps) {
      Font.normal.draw(this.bitmap, "Fps: " + fps, 0, 0, 16777215);
   }

   @Override
   public Component getComponentAt(int x, int y) {
      return x >= 0 && y >= this.height - this.bottomBg.height && x < this.width && y < this.height ? this : null;
   }

   public void openInventory(Hero hero) {
      if (this.inventoryOpen) {
         boolean isCurrent = this.inventoryPanel == this.inventoryPanelMap.get(hero);
         this.closeInventory();
         if (isCurrent) {
            return;
         }
      }

      this.inventoryOpen = true;
      this.dirty = true;

      for (int i = 0; i < 4; i++) {
         this.removeChild(this.heroPanels[i]);
      }

      this.inventoryPanel = this.inventoryPanelMap.get(hero);
      this.addChild(this.inventoryPanel);
   }

   public void closeInventory() {
      if (this.inventoryOpen) {
         this.inventoryOpen = false;
         this.dirty = true;
         this.removeChild(this.inventoryPanel);

         for (int i = 0; i < 4; i++) {
            this.addChild(this.heroPanels[i]);
         }
      }
   }

   public void performAction(int action) {
      if (action == 1) {
         this.openInventory(this.world.party.heroes[0]);
      } else if (action == 2) {
         this.openInventory(this.world.party.heroes[1]);
      } else if (action == 3) {
         this.openInventory(this.world.party.heroes[2]);
      } else if (action == 4) {
         this.openInventory(this.world.party.heroes[3]);
      } else if (action == 5) {
         this.closeInventory();
      }
   }
}
