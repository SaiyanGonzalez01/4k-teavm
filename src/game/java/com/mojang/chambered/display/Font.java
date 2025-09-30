package com.mojang.chambered.display;

import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;

public class Font {
   public static Font normal = new Font("/font/default.gif");
   private Bitmap texture;
   private int[] charWidths = new int[256];

   private Font(String name) {
      this.texture = BitmapCache.get(name);

      for (int i = 0; i < 128; i++) {
         int xt = i % 16;
         int yt = i / 16;
         int x = 0;

         for (boolean emptyColumn = false; x < 8 && !emptyColumn; x++) {
            int xPixel = xt * 8 + x;
            emptyColumn = true;

            for (int y = 0; y < 8 && emptyColumn; y++) {
               int yPixel = (yt * 8 + y) * this.texture.width;
               int pixel = this.texture.pixels[xPixel + yPixel] & 0xFF;
               if (pixel > 128) {
                  emptyColumn = false;
               }
            }
         }

         if (i == 32) {
            x = 4;
         }

         this.charWidths[i] = x;
      }
   }

   public void drawShadow(Bitmap target, String str, int x, int y, int color) {
      this.draw(target, str, x + 1, y + 1, color, true);
      this.draw(target, str, x, y, color);
   }

   public void draw(Bitmap target, String str, int x, int y, int color) {
      this.draw(target, str, x, y, color, false);
   }

   public void draw(Bitmap target, String str, int x, int y, int color, boolean darken) {
      char[] chars = str.toCharArray();
      if (darken) {
         color = (color & 16579836) >> 2;
      }

      int xo = 0;

      for (int i = 0; i < chars.length; i++) {
         if (chars[i] == 167) {
            int cc = "0123456789abcdef".indexOf(chars[i + 1]);
            int br = (cc & 8) * 8;
            int b = (cc & 1) * 191 + br;
            int g = ((cc & 2) >> 1) * 191 + br;
            int r = ((cc & 4) >> 2) * 191 + br;
            color = r << 16 | g << 8 | b;
            i += 2;
            if (darken) {
               color = (color & 16579836) >> 2;
            }
         }

         int ix = chars[i] % 16 * 8;
         int iy = chars[i] / 16 * 8;
         target.colorBlit(this.texture, x + xo, y, color, ix, iy, this.charWidths[chars[i]], 8);
         xo += this.charWidths[chars[i]];
      }
   }

   public int width(String str) {
      char[] chars = str.toCharArray();
      int len = 0;

      for (int i = 0; i < chars.length; i++) {
         if (chars[i] == 167) {
            i++;
         }

         len += this.charWidths[chars[i]];
      }

      return len;
   }
}
