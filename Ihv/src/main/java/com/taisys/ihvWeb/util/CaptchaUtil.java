package com.taisys.ihvWeb.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CaptchaUtil {

	public static final Logger logger = Logger.getLogger(CaptchaUtil.class);
	
	public String generateCaptchaText1() {
		Random rdm = new Random();
		// Random numbers are generated.
		int rl = rdm.nextInt();
		// Random numbers are converted to Hexa Decimal.
		String hash1 = Integer.toHexString(rl);
		return hash1;
	}

	public String generateCaptchaText(int captchaLength) {
		String saltChars = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890";
		StringBuffer captchaStrBuffer = new StringBuffer();
		java.util.Random rnd = new java.util.Random();
		// build a random captchaLength chars salt
		while (captchaStrBuffer.length() < captchaLength) {
			int index = (int) (rnd.nextFloat() * saltChars.length());
			captchaStrBuffer.append(saltChars.substring(index, index + 1));
		}
		return captchaStrBuffer.toString();
	}

	public BufferedImage generateCaptchaImage(String captchaStr) {
		try {
			int width = 100;
			int height = 40;
			Color bg = new Color(36, 100, 163);
			Color fg = new Color(255, 255, 255);
			// Font font = new Font("Serif", Font.PLAIN, 24);
			Font newFont = new Font("Arial", Font.BOLD, 22);
			Map attributes = newFont.getAttributes();
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			Font font = new Font(attributes);
			BufferedImage cpimg = new BufferedImage(width, height, BufferedImage.OPAQUE);
			/*
			 * Graphics2D g = cpimg.createGraphics(); g.setFont(font);
			 * g.setColor(bg); g.fillRect(0, 0, width, height); g.setColor(fg);
			 * g.drawString(captchaStr, 10, 25);
			 */

			/*
			 * BasicStroke bs = new BasicStroke(2); g.setStroke(bs);
			 * g.setColor(Color.GRAY); for(int i=1;i<21;i++){
			 * g.drawLine((width+10)/20*i, 0, (width+10)/20*i,height-1);
			 * //g.drawLine(0, (height+2)/20*i, width-1, (height+2)/20*i); }
			 */
			cpimg = createBlurredText(width, height, font, bg, fg, captchaStr, 4);
			return cpimg;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public BufferedImage createBlurredText(int width, int height, Font f, Color backCol, Color fontCol, String str,
			int blurIntensity) {

		// Create a graphics object so we can get at the details in the font
		BufferedImage bTemp = new BufferedImage(width, height, BufferedImage.OPAQUE);
		Graphics gTemp = bTemp.createGraphics();
		gTemp.setFont(f);
		FontMetrics fm = gTemp.getFontMetrics();
		// How much extra space is needed to accomidate the blurred String
		int spread = 10;
		BufferedImage bCBT = new BufferedImage(fm.stringWidth(str) + spread, fm.getFont().getSize() + spread,
				BufferedImage.OPAQUE);
		Graphics2D gCBT = (Graphics2D) bCBT.createGraphics();
		
		gCBT.setColor(backCol);
		gCBT.fillRect(0, 0, width, height);

		/*
		 * gCBT.setColor(Color.BLACK); for(double x=-450; x<=450; x=x+0.5) {
		 * double y = 50 * Math.sin(x*(3.1415926/180)); int Y = (int)y; int X =
		 * (int)x; gCBT.drawLine(450+X, 350-Y, 450+X, 350-Y); }
		 */

		/* Draws the string as centered as possible inside the buffer */
		gCBT.setColor(fontCol);
		gCBT.setFont(f);
		gCBT.drawString(str, spread / 2, spread / 2 + fm.getFont().getSize());
		for (int i = 0; i < 5; i++) {
			gCBT.drawLine(Double.valueOf(Math.random() * width).intValue(),
					Double.valueOf(Math.random() * height).intValue(), Double.valueOf(Math.random() * width).intValue(),
					Double.valueOf(Math.random() * height).intValue());
			gCBT.drawLine(Double.valueOf(Math.random() * width).intValue(),
					Double.valueOf(Math.random() * height).intValue(), Double.valueOf(Math.random() * width).intValue(),
					Double.valueOf(Math.random() * height).intValue());
		}


		int blur = 30 - blurIntensity;

		float d = (((.10f / .09f) * (blur + 132) / 160) - 1.0f);

		float[] blurKernel = { 1 / 9f - d, 1 / 9f - d, 1 / 9f - d, // low-pass
																	// filter
																	// kernel
				1 / 9f - d, 1 / 9f + 8 * d, 1 / 9f - d, 1 / 9f - d, 1 / 9f - d, 1 / 9f - d };

		ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null);
		// Layer the blur effects
		for (int x = 0; x < 5; x++)
			gCBT.drawImage(bCBT, cop, 0, 0);

		gTemp.dispose();// Get rid of any data in the 1x1 BF
		return bCBT;

	}

}
