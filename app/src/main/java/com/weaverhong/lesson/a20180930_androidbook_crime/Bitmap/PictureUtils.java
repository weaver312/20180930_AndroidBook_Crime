package com.weaverhong.lesson.a20180930_androidbook_crime.Bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

// 这个处理图像的步骤比较多，以后还要扩展，所以单独拉到一个class里来
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // 读出图片的的大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 这里设为true后，这个options就被告知，只查出尺寸就行，不用返回bitmap之类的东西
        options.inJustDecodeBounds = true;
        // 让系统类BitmapFactory去找到URI下图片的尺寸
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // 计算出图片应该怎么调整
        int inSampleSize = 1;
        // 根据传入的限制大小调整size
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        /**
         * comment on method "inSampleSize":
         * If set to a value > 1, requests the decoder to subsample the original
         * image, returning a smaller image to save memory. The sample size is
         * the number of pixels in either dimension that correspond to a single
         * pixel in the decoded bitmap. For example, inSampleSize == 4 returns
         * an image that is 1/4 the width/height of the original, and 1/16 the
         * number of pixels. Any value <= 1 is treated the same as 1. Note: the
         * decoder uses a final value based on powers of 2, any other value will
         * be rounded down to the nearest power of 2.
         * 换句话说，这里的size指的是长宽上调整过程中除以size
         * 所以上面要用一个三元运算符，用比较大的那个数，这样能保证图片长的部分也能塞进限定框里
         * 总的来讲，这个方法比较保守，没有用到特别的手段
         */
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }
}
