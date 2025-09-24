package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Font;
import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.gui.particle.GuiParticle;
import com.mojang.chambered.hero.Hero;
import com.mojang.chambered.hero.StatListener;
import java.util.ArrayList;
import java.util.List;

public class HeroPanel extends Component implements StatListener {
   private static Bitmap charBitmap = BitmapCache.get("/gui/portrait.png");
   private static Bitmap hudBitmap = BitmapCache.get("/gui/hud.png");
   private static final double FOOD_WARN_LEVEL = 0.2;
   private Bitmap bg;
   private boolean large;
   protected Hero hero;

   public HeroPanel(World world, int x, int y, Hero hero) {
      this(world, x, y, hero, false);
   }

   public HeroPanel(World world, int x, int y, Hero hero, boolean large) {
      super(world, x, y, large ? 307 : 60, 58 + (large ? 10 : 0));
      this.large = large;
      this.hero = hero;
      hero.addStatListener(this);
      List<Panel> panels = new ArrayList<>();
      panels.add(new Panel(0, 0, 56 + (large ? 49 : 0), 54 + (large ? 10 : 0)));
      if (large) {
         panels.add(new Panel(105, 6, 98, 48));
         panels.add(new Panel(203, 0, 104, 64, true));
      }

      int col = 6254195;
      int br = 50;
      int xo = 2;
      int yo = 2;
      int w = 56;
      int w3 = large ? 307 : w;
      int h = 54 + (large ? 10 : 0);
      this.bg = new Bitmap(w3 + 4, h + 4);
      this.addChild(new HandButton(world, hero, 5, x + xo + w - 24, y + yo + 11, 24, 16));
      this.addChild(new HandButton(world, hero, 3, x + xo + w - 24, y + yo + 11 + 16, 24, 16));

      for (int i = 0; i < panels.size(); i++) {
         Panel panel = panels.get(i);
         this.bg.drawBox(xo - 2 + panel.x, yo - 2 + panel.y, panel.w + 4, panel.h + 4, this.darken(6770991, 220), -50);
      }

      for (int i = 0; i < panels.size(); i++) {
         Panel panel = panels.get(i);
         this.bg.drawBox(xo - 1 + panel.x, yo - 1 + panel.y, panel.w + 2, panel.h + 2, this.darken(col, 6), 0);
      }

      for (int i = 0; i < panels.size(); i++) {
         Panel panel = panels.get(i);
         if (panel.solid) {
            this.bg.drawBox(xo + panel.x, yo + panel.y, panel.w, panel.h, col, br);
         }
      }

      this.bg.drawBox(xo, yo, w, 11, col, br);
      this.bg.drawBox(xo + 6, yo + 12 + 32, w - 6, 5, col, -br);
      this.bg.drawBox(xo + 6, yo + 12 + 32 + 5, w - 6, 5, col, -br);
      this.bg.alphaBlit(hudBitmap, xo, yo + 12 + 32 + 0, 0, 64, 5, 10);
      if (large) {
         this.bg.drawBox(xo + 6, yo + 12 + 32 + 10, w - 6, 5, col, -br);
         this.bg.drawBox(xo + 6, yo + 12 + 32 + 15, w - 6, 5, col, -br);
         this.bg.alphaBlit(hudBitmap, xo, yo + 12 + 32 + 10, 0, 74, 5, 10);
      }
   }

   @Override
   public void tick(int currentTick) {
   }

   @Override
   public void render(Bitmap dest) {
      dest.alphaBlit(this.bg, this.x, this.y);
      int xo = 2 + this.x;
      int yo = 2 + this.y;
      int w = 56;
      double hp = (double)this.hero.hp / this.hero.hpMax;
      double stamina = (double)this.hero.power / this.hero.powerMax;
      double food = this.hero.food / 100.0;
      double water = this.hero.water / 100.0;
      int hpColor = 15346725;
      int staminaColor = 2021468;
      int foodColor = 13399050;
      int waterColor = 34815;
      if (hp > 0.0) {
         this.drawGauge(dest, xo + 7, yo + 12 + 32 + 1, w - 2 - 6, 3, hp, hpColor);
         this.drawGauge(dest, xo + 7, yo + 12 + 32 + 1 + 5, w - 2 - 6, 3, stamina, staminaColor);
      } else {
         dest.drawBox(xo, yo + 12 + 32, w, 10, 0, 0);
         String status = "*dead*";
         Font.normal.drawShadow(dest, status, xo + (w - Font.normal.width(status)) / 2, yo + 12 + 32 + 1, 8388608);
      }

      if (this.large) {
         this.drawGauge(dest, xo + 7, yo + 12 + 32 + 1 + 10, w - 2 - 6, 3, food, foodColor);
         this.drawGauge(dest, xo + 7, yo + 12 + 32 + 1 + 15, w - 2 - 6, 3, water, waterColor);
      }

      dest.blit(charBitmap, xo, yo + 11, 0, 32, 32, 32);
      if (this.hero.isDead()) {
         dest.alphaBlit(charBitmap, xo, yo + 11, (this.hero.portraitId + 4) * 32, 0, 32, 32);
      } else {
         dest.alphaBlit(charBitmap, xo, yo + 11, this.hero.portraitId * 32, 0, 32, 32);
         int yStatusOffs = 0;
         if (food < 0.2) {
            dest.alphaBlit(hudBitmap, xo + 1, yo + 12 + yStatusOffs, 0, 74, 5, 5);
            yStatusOffs += 6;
         }

         if (water < 0.2) {
            dest.alphaBlit(hudBitmap, xo + 1, yo + 12 + yStatusOffs, 0, 79, 5, 5);
            yStatusOffs += 6;
         }
      }

      int nameWidth = Font.normal.width(this.hero.name);
      if (this.hero.isLocked()) {
         Font.normal.draw(dest, this.hero.name, xo + (w - nameWidth) / 2, yo + 2, 10526880);
      } else {
         Font.normal.drawShadow(dest, this.hero.name, xo + (w - nameWidth) / 2, yo + 1, 16777215);
      }

      if (this.large) {
         int xText = xo + 32 + 24 + 48 + 1 + 96 + 3 + 1 + 8;
         int yText = yo + 1;
         Font.normal.drawShadow(dest, "§7Def §f" + this.hero.defense, xText, yText + 54, 16777215);
         Font.normal.drawShadow(dest, "§7Att §f" + this.hero.attack, xText, yText + 45, 16777215);
         Font.normal.drawShadow(dest, "§7Pow §f" + this.hero.power + "§7/§f" + this.hero.powerMax, xText, yText + 36, 16777215);
         Font.normal.drawShadow(dest, "§7HP §f" + this.hero.hp + "§7/§f" + this.hero.hpMax, xText, yText + 27, 16777215);
         Font.normal.drawShadow(dest, "§7Next §f" + this.hero.xpLeft, xText, yText + 18, 16777215);
         Font.normal.drawShadow(dest, "§7Level §f" + this.hero.level, xText, yText + 9, 16777215);
         Font.normal.drawShadow(dest, "§f" + this.hero.description, xText, yText + 0, 16777215);
      }

      this.renderChildren(dest);
   }

   public void drawGauge(Bitmap dest, int x, int y, int w, int h, double value, int color) {
      int darkColor = this.darken(color, 32);
      int mediumColor = this.darken(color, 64);
      int div = (int)(value * w);
      if (div < 0) {
         div = 0;
      }

      if (div < w) {
         dest.drawBox(x + div, y, w - div, 1, darkColor, 0);
         dest.drawBox(x + div, y, 1, h, darkColor, 0);
         if (div < w - 1) {
            dest.drawBox(x + div + 1, y + 1, w - div - 1, h - 1, mediumColor, 0);
         }
      }

      if (div > 0) {
         dest.drawBox(x, y, div, h, color, 0);
      }
   }

   private int darken(int color, int i) {
      int r = (color >> 16 & 0xFF) * i / 256;
      int g = (color >> 8 & 0xFF) * i / 256;
      int b = (color & 0xFF) * i / 256;
      return r << 16 | g << 8 | b;
   }

   @Override
   public boolean mouseDown(int xMouse, int yMouse, int button) {
      if (xMouse > this.x && yMouse > this.y && xMouse < this.x + 32 + 24 && button == 1 && (yMouse < this.x + 12 || yMouse < this.y + 32 + 11)) {
         this.toggleInventory();
      }

      if (xMouse > this.x
         && yMouse > this.y + 11
         && xMouse < this.x + 32 + 24
         && yMouse < this.y + 32 + 11
         && button == 3
         && this.world.gui.carried != null
         && this.world.gui.carried.consume(this.hero)) {
         this.world.gui.carried = null;
      }

      return true;
   }

   public void toggleInventory() {
      this.world.gui.openInventory(this.hero);
   }

   @Override
   public void statsChanged(Hero hero) {
      this.setDirty();
   }

   public void addParticleOnPortrait(GuiParticle particle, int xo, int yo) {
      particle.x = this.x + xo + 16;
      particle.y = this.y + yo + 11 + 16;
   }
}
