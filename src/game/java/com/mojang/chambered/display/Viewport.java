package com.mojang.chambered.display;

import com.mojang.chambered.display.bitmap.AbstractBitmap;
import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Viewport {
   public int width;
   public int height;
   private int xCenter;
   public int yCenter;
   private double fovScale;
   public Bitmap bitmap;
   private int[] color;
   private int[] zBuffer;
   private int[] brightness;
   private int[] yMins;
   private int[] yMaxs;
   private int xMin;
   private int xMax;
   private BufferedImage image;
   private AbstractBitmap skyTexture;
   private static final int FULL_BRIGHT = 268435456;
   private static final int FAR_DISTANCE = 268435455;
   public static final int MAX_PICK_DISTANCE = 180;
   private Camera camera;
   private int xClip0;
   private int xClip1;
   public boolean pick;
   public PickResult pickResult;
   private double[] xl;
   private double[] zl;
   private int xPick;
   private int yPick;

   public Viewport(int width, int height) {
      this.width = width;
      this.height = height;
      this.skyTexture = BitmapCache.get("/sky.png");
      this.xCenter = (int)(width * 0.5);
      this.yCenter = (int)(height * 0.333);
      this.fovScale = height;
      this.yMins = new int[width];
      this.yMaxs = new int[width];
      this.image = new BufferedImage(width, height, 1);
      this.bitmap = new Bitmap(this.image);
      this.color = new int[width * height];
      this.zBuffer = new int[width * height];
      this.brightness = new int[width * height];
      this.xl = new double[width];
      this.zl = new double[width];

      for (int x = 0; x < width; x++) {
         double xl = x - this.xCenter;
         double zl = 1.0 * this.fovScale;
         double zzd = Math.sqrt(xl * xl + zl * zl);
         xl /= zzd;
         zl /= zzd;
         this.xl[x] = -xl;
         this.zl[x] = -zl;
      }
   }

   public void clear(Camera camera) {
      this.xMin = 0;
      this.xMax = this.width;
      Arrays.fill(this.yMins, 0);
      Arrays.fill(this.yMaxs, this.height);
      Arrays.fill(this.zBuffer, 268435455);
      int xOffs = (int)(camera.rot * 320.0 * 4.0 / (Math.PI * 2)) % 320;
      if (xOffs < 0) {
         xOffs += 320;
      }

      int[] skyPixels = this.skyTexture.getPixels();
      Arrays.fill(this.zBuffer, 0, 17600, 268435456);
      Arrays.fill(this.brightness, 0, 17600, 255);

      for (int y = 0; y < 55; y++) {
         System.arraycopy(skyPixels, y * 320 + xOffs, this.color, y * 320, 320 - xOffs);
         System.arraycopy(skyPixels, y * 320, this.color, y * 320 + 320 - xOffs, xOffs);
      }

      this.pick = false;
   }

   public void pick(int x, int y) {
      this.xPick = x;
      this.yPick = y;
      int margin = 0;
      this.xMin = x - margin;
      this.xMax = x + 1 + margin;
      int yMin = y - margin;
      int yMax = y + 1 + margin;
      if (this.xMin < 0) {
         this.xMin = 0;
      }

      if (yMin < 0) {
         yMin = 0;
      }

      if (this.xMax >= this.width) {
         this.xMax = this.width - 1;
      }

      if (yMax >= this.height) {
         yMax = this.height - 1;
      }

      for (int xx = this.xMin; xx < this.xMax; xx++) {
         this.yMins[xx] = yMin;
         this.yMaxs[xx] = yMax;

         for (int yy = yMin; yy < yMax; yy++) {
            this.zBuffer[xx + yy * this.width] = 268435455;
         }
      }

      this.pick = true;
      this.pickResult = null;
   }

   public void unclip() {
      this.xClip0 = 0;
      this.xClip1 = this.width;
   }

   public void clip(Wall wall) {
      this.xClip0 = this.width;
      this.xClip1 = 0;
      if (this.xMin < this.xMax) {
         double xLeft = wall.x0 / wall.z0 * this.fovScale + this.xCenter;
         double xRight = wall.x1 / wall.z1 * this.fovScale + this.xCenter;
         int x0 = (int)xLeft;
         int x1 = (int)xRight;
         if (x1 > x0 && x1 > this.xMin && x0 < this.xMax) {
            this.xClip0 = x0;
            this.xClip1 = x1;
         }
      }
   }

   public void renderSprite(SpriteImage sprite, Pickable pickable) {
      if (!(sprite.z < 0.1)) {
         double scale = this.fovScale / sprite.z;
         int left = (int)((sprite.x - sprite.xo) * scale + this.xCenter);
         int right = (int)((sprite.x - sprite.xo + sprite.w) * scale + this.xCenter);
         int x0 = left;
         int x1 = right;
         if (left < 0) {
            x0 = 0;
         }

         if (right >= this.width) {
            x1 = this.width;
         }

         if (x1 >= 0 && x0 <= this.width) {
            int top = (int)((sprite.y - sprite.yo) * scale + this.yCenter);
            int bottom = (int)((sprite.y - sprite.yo + sprite.h) * scale + this.yCenter);
            int y0 = top;
            int y1 = bottom;
            if (top < 0) {
               y0 = 0;
            }

            if (bottom >= this.height) {
               y1 = this.height;
            }

            if (y1 >= 0 && y0 <= this.height) {
               if (this.pick) {
                  int margin = 0;
                  if (x0 < this.xPick - margin) {
                     x0 = this.xPick - margin;
                  }

                  if (x1 > this.xPick + margin) {
                     x1 = this.xPick + 1 + margin;
                  }

                  if (y0 < this.yPick - margin) {
                     y0 = this.yPick - margin;
                  }

                  if (y1 > this.yPick + margin) {
                     y1 = this.yPick + 1 + margin;
                  }
               }

               int z = (int)sprite.z;
               int[] tex = sprite.texture.getPixels();
               int texWidth = sprite.texture.getWidth();
               int texHeight = sprite.texture.getHeight();
               int xd = right - left;
               int xo = xd / 2;
               int ud = sprite.u1 - sprite.u0;
               int uo = sprite.u0;
               if (sprite.mirror) {
                  ud = sprite.u0 - sprite.u1;
                  uo = sprite.u1 - 1;
               }

               int yd = bottom - top;
               int yo = yd / 2;
               int vd = sprite.v1 - sprite.v0;
               int vo = sprite.v0;

               for (int y = y0; y < y1; y++) {
                  int v = vo + (vd * (y - top) + yo) / yd;
                  if (v >= 0 && v < texHeight) {
                     int pixel = y * this.width + x0;
                     if (this.pick) {
                        for (int x = x0; x < x1; x++) {
                           if (this.zBuffer[pixel] > z) {
                              int u = uo + (ud * (x - left) + xo) / xd;
                              if (u < 0 || u >= texWidth) {
                                 continue;
                              }

                              int col = tex[v * texWidth + u];
                              if (col != 0) {
                                 this.zBuffer[pixel] = z;
                                 if (z < 180) {
                                    this.pickResult = new PickResult(pickable, u, v).setSprite();
                                 }

                                 return;
                              }
                           }

                           pixel++;
                        }
                     } else if (sprite.solidColor != 0) {
                        for (int x = x0; x < x1; x++) {
                           if (this.zBuffer[pixel] > z) {
                              int ux = uo + (ud * (x - left) + xo) / xd;
                              if (ux < 0 || ux >= texWidth) {
                                 continue;
                              }

                              int col = tex[v * texWidth + ux];
                              if (col != 0) {
                                 this.color[pixel] = sprite.solidColor;
                                 this.zBuffer[pixel] = z;
                                 this.brightness[pixel] = sprite.brightness;
                              }
                           }

                           pixel++;
                        }
                     } else {
                        for (int x = x0; x < x1; x++) {
                           if (this.zBuffer[pixel] > z) {
                              int uxx = uo + (ud * (x - left) + xo) / xd;
                              if (uxx < 0 || uxx >= texWidth) {
                                 continue;
                              }

                              int col = tex[v * texWidth + uxx];
                              if (col != 0) {
                                 this.color[pixel] = col;
                                 this.zBuffer[pixel] = z;
                                 this.brightness[pixel] = sprite.brightness;
                              }
                           }

                           pixel++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void renderWall(Wall wall, Pickable pickable) {
      if (!this.pick || this.pickResult == null) {
         if (wall.visible && this.xMin < this.xMax && this.xClip0 <= this.xClip1) {
            boolean hasFloor = wall.hasFloor;
            boolean hasCeiling = wall.hasCeiling;
            boolean hasWall = wall.hasWall;
            if (hasWall || hasFloor || hasCeiling) {
               double xLeft = wall.x0 / wall.z0 * this.fovScale + this.xCenter;
               double xRight = wall.x1 / wall.z1 * this.fovScale + this.xCenter;
               int x0 = (int)xLeft;
               int x1 = (int)xRight;
               if (this.pick) {
                  if (x0 < this.xPick) {
                     x0 = this.xPick;
                  }

                  if (x1 > this.xPick) {
                     x1 = this.xPick + 1;
                  }
               }

               if (x1 > x0 && x1 > this.xMin && x0 < this.xMax) {
                  double yTopLeft = wall.y0 / wall.z0 * this.fovScale + this.yCenter;
                  double yTopRight = wall.y0 / wall.z1 * this.fovScale + this.yCenter;
                  double yTopDelta = yTopRight - yTopLeft;
                  double yBottomLeft = wall.y1 / wall.z0 * this.fovScale + this.yCenter;
                  double yBottomRight = wall.y1 / wall.z1 * this.fovScale + this.yCenter;
                  double yBottomDelta = yBottomRight - yBottomLeft;
                  if (hasCeiling || hasFloor || !(wall.y0 >= wall.y1)) {
                     if (hasFloor || !(yBottomLeft <= 0.0) || !(yBottomRight <= 0.0)) {
                        if (hasCeiling || !(yTopLeft >= this.height) || !(yTopRight >= this.height)) {
                           if (hasWall || !(yTopLeft <= 0.0) || !(yTopRight <= 0.0) || !(yBottomLeft >= this.height) || !(yBottomRight >= this.height)) {
                              double xDelta = xRight - xLeft;
                              int leftClip = this.xMin;
                              int rightClip = this.xMax;
                              if (leftClip < this.xClip0) {
                                 leftClip = this.xClip0;
                              }

                              if (rightClip > this.xClip1) {
                                 rightClip = this.xClip1;
                              }

                              if (x0 < leftClip) {
                                 x0 = leftClip;
                              }

                              if (x1 > rightClip) {
                                 x1 = rightClip;
                              }

                              double iz0 = 1.0 / wall.z0;
                              double izd = 1.0 / wall.z1 - iz0;
                              wall.calcNormal();
                              int[] ceilingTex = hasCeiling ? wall.ceilingTexture.getPixels() : null;
                              int[] floorTex = hasFloor ? wall.floorTexture.getPixels() : null;
                              int[] wallTex = hasWall ? wall.wallTexture.getPixels() : null;
                              int ceilingTexWidth = hasCeiling ? wall.ceilingTexture.getWidth() : 0;
                              int floorTexWidth = hasFloor ? wall.floorTexture.getWidth() : 0;
                              int wallTexWidth = hasWall ? wall.wallTexture.getWidth() : 0;
                              int ceilingTexHeight = hasCeiling ? wall.ceilingTexture.getHeight() : 0;
                              int floorTexHeight = hasFloor ? wall.floorTexture.getHeight() : 0;
                              int wallTexHeight = hasWall ? wall.wallTexture.getHeight() : 0;
                              if ((!hasCeiling || ceilingTexWidth == 128 && ceilingTexHeight == 128)
                                 && (!hasFloor || floorTexWidth == 128 && floorTexHeight == 128)) {
                                 double iu0 = wall.u0 * wallTexWidth / wall.z0;
                                 double iud = wall.u1 * wallTexWidth / wall.z1 - iu0;

                                 for (int x = x0; x < x1; x++) {
                                    int yMin = this.yMins[x];
                                    int yMax = this.yMaxs[x];
                                    if (yMax > yMin) {
                                       double a = (x - xLeft + 1.0) / xDelta;
                                       double _y0 = yTopLeft + yTopDelta * a;
                                       double _y1 = yBottomLeft + yBottomDelta * a;
                                       int y0 = (int)_y0;
                                       int y1 = (int)_y1;
                                       if (this.pick) {
                                          if (y0 < this.yPick) {
                                             y0 = this.yPick;
                                          }

                                          if (y1 > this.yPick) {
                                             y1 = this.yPick + 1;
                                          }
                                       }

                                       if ((hasCeiling || hasFloor || y1 > y0)
                                          && (hasFloor || y1 > yMin)
                                          && (hasCeiling || y0 < yMax)
                                          && (hasWall || y0 > yMin || y1 < yMax)) {
                                          if (y0 < yMin) {
                                             y0 = yMin;
                                          }

                                          if (y1 > yMax) {
                                             y1 = yMax;
                                          }

                                          int pixel = yMin * this.width + x;
                                          if (y0 < this.yCenter && y0 > yMin && hasCeiling) {
                                             double _uFloor = (x - this.xCenter) * wall.y0;
                                             double _vFloor = 1.0 * this.fovScale * wall.y0;
                                             double uFloor = _uFloor * this.camera.cos + _vFloor * this.camera.sin;
                                             double vFloor = -_vFloor * this.camera.cos + _uFloor * this.camera.sin;
                                             if (y0 > yMax) {
                                                y0 = yMax;
                                             }

                                             if (this.pick) {
                                                double z = yMin - this.yCenter + 1;
                                                int fu = (int)(-uFloor / z + this.camera.x);
                                                int fv = (int)(vFloor / z - this.camera.z);
                                                this.zBuffer[pixel] = (int)(_vFloor / z);
                                                if ((int)(_vFloor / z) < 180) {
                                                   this.pickResult = new PickResult(pickable, -fu, fv).setCeiling();
                                                }

                                                return;
                                             }

                                             if (ceilingTex != null) {
                                                for (int y = yMin; y < y0; y++) {
                                                   double z = y - this.yCenter + 1;
                                                   int fu = (int)(-uFloor / z + this.camera.x + 64.0) & 127;
                                                   int fv = (int)(vFloor / z - this.camera.z + 64.0) & 127;
                                                   this.color[pixel] = ceilingTex[fv << 7 | fu];
                                                   this.zBuffer[pixel] = (int)(_vFloor / z);
                                                   this.brightness[pixel] = 200;
                                                   pixel += this.width;
                                                }
                                             } else {
                                                pixel += this.width * (y0 - yMin);
                                             }

                                             if (y0 > yMin) {
                                                yMin = y0;
                                                this.yMins[x] = y0;
                                             }
                                          } else {
                                             pixel += this.width * (y0 - yMin);
                                          }

                                          if (y1 > y0 && y1 > yMin && y0 < yMax && hasWall) {
                                             double iz = iz0 + izd * a;
                                             double iu = iu0 + iud * a;
                                             int u = (int)(iu / iz - 0.5) % wallTexWidth;
                                             double v0 = wall.v0 * wallTexHeight;
                                             double vd = wall.v1 * wallTexHeight - v0;
                                             double yd = _y1 - _y0;
                                             double va = vd * 1.0 / yd;
                                             double vv = v0 + (y0 - _y0) * va;
                                             int z = (int)(1.0 / iz);
                                             if (this.pick) {
                                                int v = (int)vv;
                                                this.zBuffer[pixel] = z;
                                                if (z < 180) {
                                                   this.pickResult = new PickResult(pickable, u, v).setWall();
                                                }

                                                return;
                                             }

                                             double brr = (wall.xn * this.xl[x] + wall.zn * this.zl[x]) * 0.9 + 0.1;
                                             int br = (int)(wall.brightness * brr);

                                             for (int y = y0; y < y1; y++) {
                                                int v = (int)(vv + wallTexHeight) % wallTexHeight;
                                                this.color[pixel] = wallTex[v * wallTexWidth + u];
                                                this.zBuffer[pixel] = z;
                                                this.brightness[pixel] = br;
                                                vv += va;
                                                pixel += this.width;
                                             }
                                          } else {
                                             pixel += this.width * (y1 - y0);
                                          }

                                          if (y1 >= this.yCenter && y1 < yMax && hasFloor) {
                                             double _uFloorx = (x - this.xCenter) * wall.y1;
                                             double _vFloorx = 1.0 * this.fovScale * wall.y1;
                                             double uFloorx = _uFloorx * this.camera.cos + _vFloorx * this.camera.sin;
                                             double vFloorx = -_vFloorx * this.camera.cos + _uFloorx * this.camera.sin;
                                             if (y1 < yMin) {
                                                pixel += (yMin - y1) * this.width;
                                                y1 = yMin;
                                             }

                                             if (this.pick) {
                                                double z = y1 - this.yCenter + 1;
                                                int fu = (int)(-uFloorx / z + this.camera.x);
                                                int fv = (int)(vFloorx / z - this.camera.z);
                                                this.zBuffer[pixel] = (int)(_vFloorx / z);
                                                if ((int)(_vFloorx / z) < 180) {
                                                   this.pickResult = new PickResult(pickable, -fu, fv).setFloor();
                                                }

                                                return;
                                             }

                                             for (int y = y1; y < yMax; y++) {
                                                double z = y - this.yCenter + 1;
                                                int fu = (int)(-uFloorx / z + this.camera.x + 128.0) / 2 & 127;
                                                int fv = (int)(vFloorx / z - this.camera.z + 128.0) / 2 & 127;
                                                this.color[pixel] = floorTex[fv << 7 | fu];
                                                this.zBuffer[pixel] = (int)(_vFloorx / z);
                                                this.brightness[pixel] = 200;
                                                pixel += this.width;
                                             }

                                             if (y1 < yMax) {
                                                yMax = this.yMaxs[x] = y1;
                                             }
                                          }

                                          if (hasWall) {
                                             if ((y0 <= yMin || hasCeiling) && y1 > yMin) {
                                                yMin = this.yMins[x] = y1;
                                             }

                                             if ((y1 >= yMax || hasFloor) && y0 < yMax) {
                                                yMax = this.yMaxs[x] = y0;
                                             }

                                             if (y0 <= yMin && y1 > yMin) {
                                                yMin = this.yMins[x] = y1;
                                             }

                                             if (y1 >= yMax && y0 < yMax) {
                                                yMax = this.yMaxs[x] = y0;
                                             }
                                          }
                                       }
                                    }
                                 }

                                 if (hasFloor && hasWall && hasCeiling && x0 <= this.xMin) {
                                    this.xMin = x1;
                                 }

                                 if (hasFloor && hasWall && hasCeiling && x1 >= this.xMax) {
                                    this.xMax = x0;
                                 }

                                 while (this.xMin < this.width && this.xMin < this.xMax && this.yMins[this.xMin] >= this.yMaxs[this.xMin]) {
                                    this.xMin++;
                                 }

                                 while (this.xMax > 0 && this.xMin < this.xMax && this.yMins[this.xMax - 1] >= this.yMaxs[this.xMax - 1]) {
                                    this.xMax--;
                                 }
                              } else {
                                 throw new IllegalArgumentException("Floor and ceiling textures have to be 128x128");
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void setCameraPos(Camera camera) {
      this.camera = camera;
   }

   public boolean isFilled() {
      return this.xMin >= this.xMax;
   }

   public BufferedImage render() {
      int[] pixels = this.bitmap.pixels;
      int p = 0;

      for (int y = 0; y < this.height; y++) {
         double xaa = 1.0 / this.fovScale;
         double xa = -this.xCenter * xaa;
         double ya = (y - this.yCenter + 1) / this.fovScale;

         for (int x = 0; x < this.width; x++) {
            double zz = this.zBuffer[p] & 268435455;
            double xx = xa * zz * 2.0;
            double yy = ya * zz * 2.0;
            double d = xx * xx + yy * yy + zz * zz + 10000.0;
            int br = (int)(this.brightness[p] * 30000 / d);
            if (br > 256) {
               br = 256;
            }

            int col = this.color[p];
            int r = (col >> 16 & 0xFF) * br >> 8;
            int g = (col >> 8 & 0xFF) * br >> 8;
            int b = (col & 0xFF) * br >> 8;
            pixels[p++] = r << 16 | g << 8 | b;
            xa += xaa;
         }
      }

      return this.image;
   }

   public double getAngle(int x) {
      double xaa = 1.0 / this.fovScale;
      double xa = (x - this.xCenter) * xaa;
      double ya = 1.0;
      return Math.atan2(xa, ya);
   }
}
