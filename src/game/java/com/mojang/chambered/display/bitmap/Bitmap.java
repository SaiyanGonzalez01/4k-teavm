package com.mojang.chambered.display.bitmap;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Bitmap extends AbstractBitmap {
   public int[] pixels;
   public int width;
   public int height;
   public boolean arrayCopyCapable = true;

   public Bitmap(int width, int height) {
      this.pixels = new int[width * height];
      this.width = width;
      this.height = height;
   }

   public Bitmap(int[] pixels, int width, int height) {
      this.pixels = pixels;
      this.width = width;
      this.height = height;
      this.removeAlphaPixels();
   }

   public Bitmap(BufferedImage image) {
      this.arrayCopyCapable = false;
      this.pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
      this.width = image.getWidth();
      this.height = image.getHeight();
   }

   @Override
   public boolean isEmpty(int x, int y, int w, int h) {
      for (int xx = x; xx < x + w; xx++) {
         for (int yy = y; yy < y + h; yy++) {
            if (this.pixels[yy * this.width + xx] != 0) {
               return false;
            }
         }
      }

      return true;
   }

   private void removeAlphaPixels() {
      for (int i = 0; i < this.pixels.length; i++) {
         if ((this.pixels[i] >> 24 & 0xFF) != 255) {
            this.pixels[i] = 0;
         }
      }
   }

   @Override
   public int[] getPixels() {
      return this.pixels;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   public void blit(Bitmap source, int x, int y) {
      this.blit(source, x, y, 0, 0, source.width, source.height);
   }

   public void blit(Bitmap source, int x, int y, int x0, int y0, int w, int h) {
      int x1 = x0 + w;
      int y1 = y0 + h;
      int xOffs = x0;
      int yOffs = y0;
      if (x0 + (x - x0) < 0) {
         x0 -= x - x0;
      }

      if (y0 + (y - y0) < 0) {
         y0 -= y - y0;
      }

      if (x1 + (x - xOffs) > this.width) {
         x1 -= x1 + (x - xOffs) - this.width;
      }

      if (y1 + (y - yOffs) > this.height) {
         y1 -= y1 + (y - yOffs) - this.height;
      }

      if (x1 - xOffs >= 1 && y1 - yOffs >= 1 && x0 - xOffs < this.width && y0 - yOffs < this.height && x0 < x1 && y0 < y1) {
         if (this.arrayCopyCapable && source.arrayCopyCapable) {
            for (int yy = y0; yy < y1; yy++) {
               int tp = (yy + y - yOffs) * this.width + x + x0 - xOffs;
               int sp = yy * source.width + x0;
               System.arraycopy(source.pixels, sp, this.pixels, tp, x1 - x0);
            }
         } else {
            for (int yy = y0; yy < y1; yy++) {
               int tp = (yy + y - yOffs) * this.width + x + x0 - xOffs;
               int sp = yy * source.width + x0;

               for (int xx = x0; xx < x1; xx++) {
                  this.pixels[tp++] = source.pixels[sp++];
               }
            }
         }
      }
   }

   public void alphaBlit(Bitmap source, int x, int y) {
      this.alphaBlit(source, x, y, 0, 0, source.width, source.height);
   }

   public void alphaBlit(Bitmap source, int x, int y, int x0, int y0, int w, int h) {
      int x1 = x0 + w;
      int y1 = y0 + h;
      int xOffs = x0;
      int yOffs = y0;
      if (x0 + (x - x0) < 0) {
         x0 -= x0 + (x - x0);
      }

      if (y0 + (y - y0) < 0) {
         y0 -= y0 + (y - y0);
      }

      if (x1 + (x - xOffs) > this.width) {
         x1 -= x1 + (x - xOffs) - this.width;
      }

      if (y1 + (y - yOffs) > this.height) {
         y1 -= y1 + (y - yOffs) - this.height;
      }

      if (x1 - xOffs >= 1 && y1 - yOffs >= 1 && x0 - xOffs < this.width && y0 - yOffs < this.height && x0 < x1 && y0 < y1) {
         for (int yy = y0; yy < y1; yy++) {
            int tp = (yy + y - yOffs) * this.width + x + x0 - xOffs;
            int sp = yy * source.width + x0;

            for (int xx = x0; xx < x1; xx++) {
               int col = source.pixels[sp++];
               if (col != 0) {
                  this.pixels[tp] = col;
               }

               tp++;
            }
         }
      }
   }

   public void colorBlit(Bitmap source, int x, int y, int color) {
      this.colorBlit(source, x, y, color, 0, 0, source.width, source.height);
   }

   public void colorBlit(Bitmap source, int x, int y, int color, int x0, int y0, int w, int h) {
      int x1 = x0 + w;
      int y1 = y0 + h;
      int xOffs = x0;
      int yOffs = y0;
      if (x0 + (x - x0) < 0) {
         x0 -= x - x0;
      }

      if (y0 + (y - y0) < 0) {
         y0 -= y - y0;
      }

      if (x1 + (x - xOffs) > this.width) {
         x1 -= x1 + (x - xOffs) - this.width;
      }

      if (y1 + (y - yOffs) > this.height) {
         y1 -= y1 + (y - yOffs) - this.height;
      }

      if (x1 - xOffs >= 1 && y1 - yOffs >= 1 && x0 - xOffs < this.width && y0 - yOffs < this.height && x0 < x1 && y0 < y1) {
         int rc = color >> 16 & 0xFF;
         int gc = color >> 8 & 0xFF;
         int bc = color & 0xFF;

         for (int yy = y0; yy < y1; yy++) {
            int tp = (yy + y - yOffs) * this.width + x + x0 - xOffs;
            int sp = yy * source.width + x0;

            for (int xx = x0; xx < x1; xx++) {
               int col = source.pixels[sp++];
               if (col != 0) {
                  int r = (col >> 16 & 0xFF) * rc / 255;
                  int g = (col >> 8 & 0xFF) * gc / 255;
                  int b = (col & 0xFF) * bc / 255;
                  this.pixels[tp] = 0xFF000000 | r << 16 | g << 8 | b;
               }

               tp++;
            }
         }
      }
   }

   public void drawBox(int x0, int y0, int w, int h, int col, int side) {
      for (int x = 0; x < w; x++) {
         for (int y = 0; y < h; y++) {
            int color = 0;

            for (int i = 0; i < 3; i++) {
               int br = col >> i * 8 & 0xFF;
               if (x == 0 || y == 0) {
                  br += side;
               }

               if (x == w - 1 || y == h - 1) {
                  br -= side;
               }

               if (br < 0) {
                  br = 0;
               }

               if (br > 255) {
                  br = 255;
               }

               color |= br << i * 8;
            }

            this.pixels[x + x0 + (y + y0) * this.width] = -16777216 + color;
         }
      }
   }

   public void blit(SpriteImage sprite, int x, int y) {
      this.alphaBlit(sprite.texture, x - (int)sprite.xo, y - (int)sprite.yo, sprite.u0, sprite.v0, sprite.u1 - sprite.u0, sprite.v1 - sprite.v0);
   }
}
