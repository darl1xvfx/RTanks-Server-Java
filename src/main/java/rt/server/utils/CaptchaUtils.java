package rt.server.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class CaptchaUtils {
    
    public static String bytesToString(byte ... data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(b).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static byte[] generateCaptcha(String answer) throws IOException {
        int width = 281;
        int height = 50;
        Color color = Color.decode("#6404f5f");
        String captchaText = answer;
        BufferedImage image = new BufferedImage(width, height, 2);

        try {
            image.createGraphics();
            Graphics2D g2d = image.createGraphics();

            g2d.setPaint(color);
            g2d.fillRect(0, 0, width, height);

            double noiseDensity = 0.1;
            int noiseAmount = (int) (width * height * noiseDensity);
            for (int i = 0; i < noiseAmount; i++) {
                g2d.setColor(getRandomColor());
                int xPosNoise = (int) (Math.random() * width);
                int yPosNoise = (int) (Math.random() * height);
                g2d.fillRect(xPosNoise, yPosNoise, 1, 1);
            }

            g2d.setPaint(Color.BLACK);
            g2d.setFont(new Font("Intro", 1, 35));
            g2d.drawString(captchaText, 70, 35);

            int lineAmount = 2;
            for (int i = 0; i < lineAmount; i++) {
                g2d.setColor(Color.BLACK);
                int x1 = (int) (Math.random() * width);
                int y1 = (int) (Math.random() * height);
                int x2 = (int) (Math.random() * width);
                int y2 = (int) (Math.random() * height);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x1, y1, x2, y2);
            }

            g2d.dispose();
        } catch (Exception var4) {
            System.out.println(var4.getMessage());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public static String getRandomCaptchaText(int length) {
        String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            int number = new Random().nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    private static Color getRandomColor() {
        String letters = "0123456789ABCDEF";
        StringBuilder color = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            color.append(letters.charAt((int) (Math.random() * 10)));
        }
        return Color.decode(color.toString());
    }
}