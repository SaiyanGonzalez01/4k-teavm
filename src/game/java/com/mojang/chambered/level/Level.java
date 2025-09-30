package com.mojang.chambered.level;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.DynamicBitmap;
import com.mojang.chambered.display.bitmap.NullBitmap;
import com.mojang.chambered.display.bitmap.SignBitmap;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.entity.ItemEntity;
import com.mojang.chambered.entity.Mob;
import com.mojang.chambered.entity.Monster;
import com.mojang.chambered.entity.Tree;
import com.mojang.chambered.item.Item;
import com.mojang.chambered.item.ItemInstance;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import com.mojang.chambered.phys.CollisionSphere;
import java.util.ArrayList;
import java.util.List;

public class Level {
   private String map = "#####0##=6#######  o#$##  =#   ##         ## # =#  o####  ##   ###1#####  ## ######<   #      #####3 _    ###  ####>   #  ##9 #######-#######-###. . .   ###a .##:.:. .. 4## .a##..::.. .##7####5aa.::*::#  :###9aa..:.:.    &###..a.. . #  :##############8####";
   public Tile[] tiles;
   private int width = 16;
   private int height = 16;
   private Tile outOfMapTile;
   private SpriteImage[] sprite;
   public double ceilingHeight;
   public double wallWidth;
   private List<Mob> mobs;
   public World world;

   public Level(World world) {
      this.ceilingHeight = Surface.ceilingHeight;
      this.wallWidth = Surface.wallWidth;
      this.mobs = new ArrayList<>();
      this.world = world;
      this.outOfMapTile = new Tile(world, this, -1, -1, true);
      this.sprite = new SpriteImage[2];
      this.sprite[0] = new SpriteImage(BitmapCache.get("/monster.png"), -8.0);
      this.sprite[1] = new SpriteImage(BitmapCache.get("/monsterattack.png"), -8.0);
      String solidChars = "#=<>&1234567890";
      this.tiles = new Tile[this.width * this.height];

      for (int x = 0; x < this.width; x++) {
         for (int y = 0; y < this.height; y++) {
            char ch = this.map.charAt(x + y * 16);
            boolean solid = solidChars.indexOf(ch) >= 0;
            Tile tile = new Tile(world, this, x, y, solid);
            if (ch == '#') {
               tile.wallType = Surface.flat;
            }

            if (ch == '=') {
               tile.wallType = Surface.niche;
            }

            if (ch == '-') {
               tile.wallType = Surface.door;
            }

            if (ch == '_') {
               tile.wallType = Surface.pressurePlate;
            }

            if (ch == '*') {
               tile.wallType = Surface.pit;
            }

            if (ch == 'o') {
               tile.wallType = Surface.column;
            }

            if (ch == '<') {
               tile.wallType = Surface.stairsUp;
            }

            if (ch == '>') {
               tile.wallType = Surface.stairsDown;
            }

            if (ch == '&') {
               tile.wallType = Surface.flat.setWallTexture(new DynamicBitmap(128, 96));
            }

            if (ch == '1') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Welcome!"));
            }

            if (ch == '2') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Particles!"));
            }

            if (ch == '3') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Stairs!"));
            }

            if (ch == '4') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Woods!"));
            }

            if (ch == '5') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Monsters!"));
            }

            if (ch == '6') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Niches!"));
            }

            if (ch == '7') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Win!"));
            }

            if (ch == '8') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Yay!"));
            }

            if (ch == '9') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "(dancing)"));
            }

            if (ch == '0') {
               tile.wallType = Surface.flat.setWallTexture(new SignBitmap(128, 96, "Loot!"));
            }

            this.tiles[x + y * 16] = tile;
            if (y > 8) {
               tile.ceilingTexture = new NullBitmap(128, 128);
            }

            if (ch == '$') {
               for (int j = 0; j < 4; j++) {
                  for (int i = 0; i < 10; i++) {
                     double xOffs = (Math.random() - 0.5) * 0.75 * this.wallWidth;
                     double yOffs = (Math.random() - 0.5) * 0.75 * this.wallWidth;
                     this.addItem(new ItemInstance(world, Item.items[i]), x * this.wallWidth + xOffs, 0.0, y * this.wallWidth + yOffs);
                  }
               }
            }

            if (ch == '.') {
               int i = (int)(Math.random() * 4.0);
               double xOffs = i % 2 * this.wallWidth / 2.0 - this.wallWidth / 4.0;
               double yOffs = i / 2 * this.wallWidth / 2.0 - this.wallWidth / 4.0;
               Tree tree = new Tree(x * this.wallWidth + xOffs, 0.0, y * this.wallWidth + yOffs);
               this.addApples(world, tree);
               tile.addEntity(tree);
            }

            if (ch == ':') {
               for (int i = 0; i < 4; i++) {
                  double xOffs = i % 2 * this.wallWidth / 2.0 - this.wallWidth / 4.0;
                  double yOffs = i / 2 * this.wallWidth / 2.0 - this.wallWidth / 4.0;
                  Tree tree = new Tree(x * this.wallWidth + xOffs, 0.0, y * this.wallWidth + yOffs);
                  this.addApples(world, tree);
                  tile.addEntity(tree);
               }
            }

            if (ch == 'a') {
               this.addMob(new Monster(world, this.sprite[0], this.sprite[1], x * this.wallWidth, 0.0, y * this.wallWidth));
            }
         }
      }
   }

   private void addApples(World world, Tree tree) {
      int count = (int)(Math.random() * 6.0 - 2.0);

      for (int i = 0; i < count; i++) {
         double dir = Math.random() * Math.PI * 2.0;
         double dist = Math.random() * 16.0 + 16.0;
         double xo = Math.sin(dir) * dist;
         double zo = Math.cos(dir) * dist;
         this.addItem(new ItemInstance(world, Item.apple), tree.x + xo, 0.0, tree.z + zo);
      }

      count = (int)(Math.random() * 6.0 - 2.0);

      for (int i = 0; i < count; i++) {
         ItemEntity apple = new ItemEntity(new ItemInstance(world, Item.apple), tree.x, tree.y, tree.z);
         tree.addItemEntity(apple);
      }
   }

   public void addItem(ItemInstance itemInstance, double x, double y, double z) {
      this.getTileAt(x, z).addEntity(new ItemEntity(itemInstance, x, y, z));
   }

   public void addMob(Mob mob) {
      this.mobs.add(mob);
   }

   public Tile getTile(int x, int y) {
      return x >= 0 && y >= 0 && x < 16 && y < 16 ? this.tiles[x + y * 16] : this.outOfMapTile;
   }

   public Tile getTileAt(double x, double y) {
      int xTile = (int)(x / this.wallWidth + 0.5);
      int yTile = (int)(y / this.wallWidth + 0.5);
      return this.getTile(xTile, yTile);
   }

   public List<Collideable> getCollideables(double x, double y, long mask) {
      List<Collideable> collideables = new ArrayList<>();
      int xTile = (int)(x / this.wallWidth);
      int yTile = (int)(y / this.wallWidth);

      for (int xt = xTile; xt <= xTile + 1; xt++) {
         for (int yt = yTile; yt <= yTile + 1; yt++) {
            this.getTile(xt, yt).getCollideables(collideables, mask);
         }
      }

      return collideables;
   }

   public boolean isFree(CollisionSphere s, long mask) {
      List<Collideable> collideables = this.getCollideables(s.pos.x, s.pos.y, mask);

      for (int i = 0; i < collideables.size(); i++) {
         if (collideables.get(i).isInside(s)) {
            return false;
         }
      }

      return true;
   }

   public void tick(int currentTick) {
      for (int i = 0; i < this.mobs.size(); i++) {
         Mob mob = this.mobs.get(i);
         boolean alive = mob.tick();
         if (!alive) {
            this.mobs.remove(i--);
            mob.removed();
         }
      }
   }

   public void renderSprites(Viewport viewport, Camera camera, int currentTick, double alpha) {
      for (int i = 0; i < this.mobs.size(); i++) {
         this.mobs.get(i).render(viewport, camera, currentTick, alpha);
      }
   }

   public void hurtMonsters(double x, double z, Camera camera) {
      for (int i = 0; i < this.mobs.size(); i++) {
         Mob mob = this.mobs.get(i);
         if (mob instanceof Monster) {
            Monster monster = (Monster)mob;
            double xd = monster.x - x;
            double zd = monster.z - z;
            double d = xd * xd + zd * zd;
            if (d < 16384.0 && monster.isInFront(camera, 0.5)) {
               monster.hurt();
            }
         }
      }
   }
}
