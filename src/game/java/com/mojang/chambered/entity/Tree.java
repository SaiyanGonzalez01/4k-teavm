package com.mojang.chambered.entity;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.display.sprite.BobbingSprite;
import com.mojang.chambered.display.sprite.Sprite;
import com.mojang.chambered.phys.CollisionSphere;
import java.util.ArrayList;
import java.util.List;

public class Tree extends Entity {
   private static SpriteImage trunk = new SpriteImage(BitmapCache.get("/tree/trunk.png"));
   private static SpriteImage leaves = new SpriteImage(BitmapCache.get("/tree/leaves.png"));
   private List<Renderable> renderables = new ArrayList<>();
   private List<ItemEntity> itemEntities = new ArrayList<>();

   public Tree(double x, double y, double z) {
      this.setPos(x, y, z);
      this.collideType = 32L;
      this.addCollideable(new CollisionSphere(x, z, 10.0));
      Sprite trunkSprite = new Sprite(trunk, x, y, z);
      trunkSprite.mirrored = Math.random() < 0.5;
      trunkSprite.pickable = this;
      this.renderables.add(trunkSprite);
      int layers = 4;

      for (int j = 0; j < layers; j++) {
         double dir2 = j * Math.PI / 2.0 / (layers - 1);
         int particleCount = 1 + j * 2;

         for (int i = 0; i < particleCount; i++) {
            double dir = i * Math.PI * 2.0 / particleCount;
            double dist = Math.random() * 8.0 + 16.0;
            double xo = Math.sin(dir) * dist * Math.sin(dir2);
            double yo = -40.0 - 32.0 * Math.cos(dir2) + Math.random() * 4.0;
            double zo = Math.cos(dir) * dist * Math.sin(dir2);
            Sprite leavesSprite = new BobbingSprite(leaves, x + xo, y + yo, z + zo);
            leavesSprite.mirrored = Math.random() < 0.5;
            leavesSprite.pickable = this;
            this.renderables.add(leavesSprite);
         }
      }
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      for (int i = 0; i < this.renderables.size(); i++) {
         this.renderables.get(i).render(viewport, camera, currentTick, a);
      }
   }

   public void addItemEntity(ItemEntity itemEntity) {
      double dir2 = Math.random();
      dir2 *= dir2;
      dir2 = (1.0 - dir2) * Math.PI / 2.0;
      double dir = Math.random() * Math.PI * 2.0;
      double dist = Math.random() * 8.0 + 24.0;
      double xo = Math.sin(dir) * dist * Math.sin(dir2);
      double yo = -48.0 - 32.0 * Math.cos(dir2) + Math.random() * 4.0;
      double zo = Math.cos(dir) * dist * Math.sin(dir2);
      itemEntity.x = this.x + xo;
      itemEntity.y = this.y + yo;
      itemEntity.z = this.z + zo;
      this.itemEntities.add(itemEntity);
      this.renderables.add(itemEntity);
   }
}
