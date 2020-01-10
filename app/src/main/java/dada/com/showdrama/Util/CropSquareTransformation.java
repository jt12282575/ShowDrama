package dada.com.showdrama.Util;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

 class CropSquareTransformation implements Transformation {


    @Override
    public Bitmap transform(Bitmap source) {

        int targetWidth = 200;

        int targetHeight = 280;

        if (source.getWidth() == 0 || source.getHeight() == 0) {
            return source;
        }

        if (source.getWidth() > source.getHeight()) {
            if (source.getHeight() < targetHeight && source.getWidth() <= 400) {
                return source;
            } else {

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int width = (int) (targetHeight * aspectRatio);
                if (width > 400) {
                    width = 400;
                    targetHeight = (int) (width / aspectRatio);
                }
                if (width != 0 && targetHeight != 0) {
                    Bitmap result = Bitmap.createScaledBitmap(source, width, targetHeight, false);
                    if (result != source) {

                        source.recycle();
                    }
                    return result;
                } else {
                    return source;
                }
            }
        } else {
            if (source.getWidth() < targetWidth && source.getHeight() <= 600) {
                return source;
            } else {

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int height = (int) (targetWidth * aspectRatio);
                if (height > 600) {
                    height = 600;
                    targetWidth = (int) (height / aspectRatio);
                }
                if (height != 0 && targetWidth != 0) {
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, height, false);
                    if (result != source) {

                        source.recycle();
                    }
                    return result;
                } else {
                    return source;
                }
            }
        }
    }

    @Override
    public String key() {
        return "desiredWidth" + " desiredHeight";
    }
}

