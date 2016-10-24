package com.treefrogapps.tag_editor;

import com.sun.istack.internal.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtils {

    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 400;

    public static String getImageType(@NotNull File imageFile) {
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] buffer = new byte[8];
            fis.read(buffer, 0, buffer.length);
            fis.close();

            return getTypeFromBytes(buffer);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getTypeFromBytes(byte[] fileByteArray) {

        int[] headerValues = new int[fileByteArray.length];

        for (int i = 0; i < headerValues.length; i++) {
            headerValues[i] = fileByteArray[i] & 0xFF;
        }

        if (headerValues[0] == 0xFF && headerValues[1] == 0xD8 && headerValues[2] == 0xFF) {
            // image is a jpg (can be various types of jpg - denoted from 4th byte

            return "jpg";

        } else if (headerValues[0] == 0x89 && headerValues[1] == 0x50
                && headerValues[2] == 0x4E && headerValues[3] == 0x47) {
            // image is a PNG file
            return "png";

        } else {
            return "";
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static byte[] getScaledImage(@NotNull File imageFile) {

        String imageType = getImageType(imageFile);

        if (imageType.equals("jpg") || imageType.equals("png")) {

            try {
                FileInputStream fis = new FileInputStream(imageFile);
                BufferedInputStream bis = new BufferedInputStream(fis);

                BufferedImage bufferedImage = ImageIO.read(bis);
                BufferedImage scaledBufferedImage = scale(bufferedImage, IMAGE_WIDTH, IMAGE_HEIGHT);

                fis.close();
                bis.close();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(scaledBufferedImage, imageType, baos);
                byte[] imageByteArray = baos.toByteArray();
                baos.close();

                return imageByteArray;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * Scale Image to desired size
     * @param sourceImg sourceImg Buffered Image to scale
     * @param outWidth desired width
     * @param outHeight desired height
     * @return scaled Buffered Image
     */
    private static BufferedImage scale(BufferedImage sourceImg, int outWidth, int outHeight) {
        BufferedImage scaledImg = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int srcWidth = sourceImg.getWidth();
        int srcHeight = sourceImg.getHeight();
        for (x = 0; x < outWidth; x++) {
            for (y = 0; y < outHeight; y++) {
                int col = sourceImg.getRGB(x * srcWidth / outWidth, y * srcHeight / outHeight);
                scaledImg.setRGB(x, y, col);
            }
        }
        return scaledImg;
    }


}
