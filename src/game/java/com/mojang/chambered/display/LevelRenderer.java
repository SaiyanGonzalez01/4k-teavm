package com.mojang.chambered.display;

import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.level.Level;
import com.mojang.chambered.level.Tile;
import com.mojang.chambered.level.surface.Surface;
import java.util.ArrayList;
import java.util.List;

public class LevelRenderer {
   private Viewport viewport;
   private Camera camera;
   private Level level;
   private boolean pick = false;
   private int xPick;
   private int yPick;
   private List<Tile> visibleTiles = new ArrayList<>();

   public LevelRenderer(Level level) {
      this.level = level;
      Surface.defaultCeilingTexture.texture = BitmapCache.get("/floor/dirt.png");
      Surface.defaultFloorTexture.texture = BitmapCache.get("/floor/dirt.png");
      Surface.defaultWallTexture.texture = BitmapCache.get("/wall/rock.png");
   }

   private void renderBlock(int x, int z, int xOffs, int zOffs) {
      if (!this.viewport.isFilled()) {
         x += xOffs;
         z += zOffs;
         Tile tile = this.level.getTile(x, z);
         this.visibleTiles.add(tile);
         if (tile.isSolid()) {
            for (int rot = 0; rot < 4; rot++) {
               if ((rot != 0 || z < zOffs) && (rot != 1 || x < xOffs) && (rot != 2 || z > zOffs) && (rot != 3 || x > xOffs)) {
                  Surface surface = tile.getWall(rot);
                  if (surface != null) {
                     Wall wall = surface.clipWall.move(x, 0, z, rot);
                     wall.ceilingTexture = tile.ceilingTexture;
                     wall.floorTexture = tile.floorTexture;
                     this.viewport.clip(wall.move(this.camera).clip());

                     for (int face = 0; face < surface.walls.size(); face++) {
                        wall = surface.walls.get(face).move(x, 0, z, rot);
                        this.viewport.renderWall(wall.move(this.camera).clip(), tile);
                     }
                  }
               }
            }
         } else {
            this.viewport.unclip();

            for (int rotx = 0; rotx < 1; rotx++) {
               Surface surface = tile.getWall(rotx);
               if (surface != null) {
                  for (int face = 0; face < surface.walls.size(); face++) {
                     Wall wall = surface.walls.get(face).move(x, 0, z, rotx);
                     this.viewport.renderWall(wall.move(this.camera).clip(), tile);
                  }
               }
            }

            Surface surface = Surface.floorAndCeiling;

            for (int rotxx = 0; rotxx < 4; rotxx++) {
               for (int face = 0; face < surface.walls.size(); face++) {
                  Wall wall = surface.walls.get(face).move(x, 0, z, rotxx);
                  wall.ceilingTexture = tile.ceilingTexture;
                  wall.floorTexture = tile.floorTexture;
                  this.viewport.renderWall(wall.move(this.camera).clip(), tile);
               }
            }
         }
      }
   }

   private void renderWalls(int xOffs, int zOffs, int maxDepth) {
      this.renderBlock(0, 0, xOffs, zOffs);
      int d = 0;

      while (!this.viewport.isFilled() && d < maxDepth) {
         d++;

         for (int s = 0; s < d * 2; s++) {
            if (this.viewport.isFilled()) {
               return;
            }

            int flip = (s & 1) * 2 - 1;
            int step = (s + 1) / 2 * flip;
            this.renderBlock(step, -d, xOffs, zOffs);
            this.renderBlock(-step, d, xOffs, zOffs);
            this.renderBlock(-d, -step, xOffs, zOffs);
            this.renderBlock(d, step, xOffs, zOffs);
         }
      }
   }

   public void render(Viewport viewport, Camera camera, int tick, double alpha) {
      this.visibleTiles.clear();
      int maxDepth = 10;
      if (this.pick) {
         viewport.pick(this.xPick, this.yPick);
         maxDepth = 2;
      } else {
         viewport.clear(camera);
      }

      viewport.setCameraPos(camera);
      int xOffs = -((int)Math.floor((camera.x + this.level.wallWidth / 2.0) / this.level.wallWidth));
      int zOffs = -((int)Math.floor((camera.z + this.level.wallWidth / 2.0) / this.level.wallWidth));
      this.camera = camera;
      this.viewport = viewport;
      this.renderWalls(xOffs, zOffs, maxDepth);

      for (int i = 0; i < this.visibleTiles.size(); i++) {
         this.renderSprites(this.visibleTiles.get(i).getRenderables(), tick, alpha);
      }

      this.level.renderSprites(viewport, camera, tick, alpha);
   }

   private void renderSprites(List<Renderable> sprites, int currentTick, double alpha) {
      for (int i = 0; i < sprites.size(); i++) {
         sprites.get(i).render(this.viewport, this.camera, currentTick, alpha);
      }
   }

   public PickResult pick(int x, int y, Viewport viewport, Camera camera, int tick, double alpha) {
      if (x >= 0 && y >= 0 && x < viewport.width && y < viewport.height) {
         this.pick = true;
         this.xPick = x;
         this.yPick = y;
         this.render(viewport, camera, tick, alpha);
         this.pick = false;
         PickResult pickResult = viewport.pickResult;
         Pickable.pickResult = pickResult;
         return pickResult;
      } else {
         return null;
      }
   }
}
