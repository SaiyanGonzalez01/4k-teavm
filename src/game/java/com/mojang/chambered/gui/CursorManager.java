package com.mojang.chambered.gui;

import com.mojang.chambered.Chambered;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.item.ItemInstance;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class CursorManager {
   private Chambered chambered;
   private boolean ok = true;
   private ItemInstance lastItem;
   private static SpriteImage pointerSprite = new SpriteImage(BitmapCache.get("/gui/mousepointers.png"), 0, 0, 16, 16);
   private Cursor defaultCursor;

   public CursorManager(Chambered chambered) {
      this.chambered = chambered;

      try {
         BufferedImage image = this.getImage(pointerSprite);
         this.defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "Normal pointer");
         chambered.setCursor(this.defaultCursor);
      } catch (Exception var3) {
         var3.printStackTrace();
         this.ok = false;
      }
   }

   public boolean setCursor(ItemInstance itemInstance) {
      if (!this.ok) {
         return false;
      } else if (itemInstance == this.lastItem) {
         return true;
      } else {
         try {
            if (itemInstance == null) {
               this.chambered.setCursor(this.defaultCursor);
            } else {
               this.chambered.setCursor(this.getCursor(itemInstance));
            }

            this.lastItem = itemInstance;
            return true;
         } catch (Exception var3) {
            var3.printStackTrace();
            this.ok = false;
            return false;
         }
      }
   }

   private Cursor getCursor(ItemInstance itemInstance) {
      SpriteImage sprite = itemInstance.getInventorySprite();
      BufferedImage image = this.getImage(sprite);
      return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point((int)(sprite.xo * 2.0), (int)(sprite.yo * 2.0 - 16.0)), itemInstance.getName());
   }

   private BufferedImage getImage(SpriteImage sprite) {
      int w = sprite.u1 - sprite.u0;
      int h = sprite.v1 - sprite.v0;
      int u0 = sprite.u0;
      int v0 = sprite.v0;
      int[] out = new int[1024];
      int[] in = sprite.texture.pixels;

      for (int y = 0; y < h; y++) {
         int ip = (v0 + y) * sprite.texture.width + u0;
         int op = y * 2 * 32;

         for (int x = 0; x < w; x++) {
            int col = in[ip++];
            out[op + 0] = col;
            out[op + 1] = col;
            out[op + 0 + 32] = col;
            out[op + 1 + 32] = col;
            op += 2;
         }
      }

      BufferedImage image = new BufferedImage(32, 32, 2);
      image.setRGB(0, 0, 32, 32, out, 0, 32);
      return image;
   }
}
