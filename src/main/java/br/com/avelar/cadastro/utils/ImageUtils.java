package br.com.avelar.cadastro.utils;

import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.xmlbeans.impl.util.Base64;

public class ImageUtils {

    private static final Float COMPRESSION_LEVEL = 0.9f;

    private static final Float RESIZING_PERCENTAGE = 0.7f;

    private ImageUtils() {

    }

    public static String guessImageFormat(String base64) throws IOException {
        return guessImageFormat(transformBase64ToBytes(base64));
    }

    public static String guessImageFormat(byte[] bytes) throws IOException {
        InputStream is = new ByteArrayInputStream(bytes);
        String mimeType = URLConnection.guessContentTypeFromStream(is);

        if(mimeType == null) {
            return "application/octet-stream";
        }

        String[] tokens = mimeType.split("[/]");
        return tokens[1];
    }

    public static byte[] compressImage(byte[] bytes) throws IOException {
        File originalImageFile = transformBytesToFile(bytes);

        String outputPath = UUID.randomUUID().toString();
        convertImageToJpg(originalImageFile, outputPath);

        File compressedImageFile = new File(outputPath);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(compressedImageFile));
        BufferedImage image = ImageIO.read(bis);

        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(COMPRESSION_LEVEL);

        writer.write(null, new IIOImage(image, null, null), param);

        byte[] compressedImageBytes = {};

        ImageResolution resolution = getImageResolution(compressedImageFile);

        if(resolution.getWidth() > 800) {
            File resizedImage = new File(UUID.randomUUID().toString() + "-resized.jpg");
            resize(compressedImageFile, resizedImage, RESIZING_PERCENTAGE);
            compressedImageBytes = FileUtils.readFileToByteArray(resizedImage);
            resizedImage.delete();
        } else {
            compressedImageBytes = FileUtils.readFileToByteArray(compressedImageFile);
        }

        os.close();
        ios.close();
        writer.dispose();

        originalImageFile.delete();
        compressedImageFile.delete();

        return compressedImageBytes;
    }

    public static String compressImageSocialMediaB64(String base64) throws IOException {
        File originalImageFile = transformBase64ToFile(base64);

        String outputPath = UUID.randomUUID().toString();
        convertImageToJpg(originalImageFile, outputPath);

        File compressedImageFile = new File(outputPath);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(compressedImageFile));
        BufferedImage image = ImageIO.read(bis);

        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(COMPRESSION_LEVEL);

        writer.write(null, new IIOImage(image, null, null), param);

        byte[] compressedImageBytes = {};

        ImageResolution resolution = getImageResolution(compressedImageFile);

        if(resolution.getWidth() > 800) {
            File resizedImage = new File(UUID.randomUUID().toString() + "-resized.jpg");
            double percentual = ((800*100)/Double.parseDouble(resolution.getWidth()+""))/100;

            resizeSocial(compressedImageFile, resizedImage, percentual);
            compressedImageBytes = FileUtils.readFileToByteArray(resizedImage);
            resizedImage.delete();
        } else {
            compressedImageBytes = FileUtils.readFileToByteArray(compressedImageFile);
        }

        os.close();
        ios.close();
        writer.dispose();

        originalImageFile.delete();
        compressedImageFile.delete();

        return new String(Base64.encode(compressedImageBytes));

    }

    public static String compressBase64Image(String base64) throws IOException {
        File originalImageFile = transformBase64ToFile(base64);
        byte[] compressedImage = compressImage(FileUtils.readFileToByteArray(originalImageFile));
        return new String(Base64.encode(compressedImage));
    }

    public static String compressImage(String base64) throws IOException {
        byte[] imageBytes = transformBase64ToBytes(base64);
        imageBytes = compressImage(imageBytes);
        return new String(Base64.encode(imageBytes));
    }

    public static void resize(File inputFile, File outputFile, int scaledWidth, int scaledHeight) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputFile);

        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        String outputImagePath = outputFile.getPath();

        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

        ImageIO.write(outputImage, formatName, outputFile);
    }

    public static void resize(File inputFile, File outputFile, double percent) throws IOException {
        ImageResolution resolution = getImageResolution(inputFile).scale(RESIZING_PERCENTAGE);
        resize(inputFile, outputFile, resolution.getWidth(), resolution.getHeight());
    }

    public static void resizeSocial(File inputFile, File outputFile, double percent) throws IOException {
        ImageResolution resolution = getImageResolution(inputFile).scale(percent);
        resize(inputFile, outputFile, resolution.getWidth(), resolution.getHeight());
    }

    public static ImageResolution getImageResolution(File inputFile) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputFile);
        return new ImageResolution(inputImage.getWidth(), inputImage.getHeight());
    }

    public static String convertImageToJpg(String base64) throws IOException {
        File originalImageFile = transformBase64ToFile(base64);
        String outputPath = UUID.randomUUID().toString();
        convertImageToJpg(originalImageFile, outputPath);
        byte[] convertedImageBytes = FileUtils.readFileToByteArray(originalImageFile);
        originalImageFile.delete();
        return new String(Base64.encode(convertedImageBytes));
    }

    public static File convertImageToJpg(File inputFile, String outputPath) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputFile);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

        File output = new File(outputPath);
        ImageIO.write(newBufferedImage, "jpg", output);

        return output;
    }

    private static File transformBase64ToFile(String base64) throws IOException {
        File file = new File(UUID.randomUUID().toString());
        byte[] bytes = Base64.decode(base64.getBytes());
        return transformBytesToFile(bytes);
    }

    private static byte[] transformBase64ToBytes(String base64) {
        return Base64.decode(base64.getBytes());
    }

    private static File transformBytesToFile(byte[] bytes) throws IOException {
        File file = new File(UUID.randomUUID().toString());
        OutputStream os = new FileOutputStream(file);
        os.write(bytes);
        os.close();
        return file;
    }

}
