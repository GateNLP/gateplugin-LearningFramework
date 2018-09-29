package gate.resources.img.svg;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class has been automatically generated using <a
 * href="http://englishjavadrinker.blogspot.com/search/label/SVGRoundTrip">SVGRoundTrip</a>.
 */
@SuppressWarnings("unused")
public class LF_TrainTopicModel_Mallet_ENIcon implements
		javax.swing.Icon {
		
	private static Color getColor(int red, int green, int blue, int alpha, boolean disabled) {
		
		if (!disabled) return new Color(red, green, blue, alpha);
		
		int gray = (int)(((0.30f * red) + (0.59f * green) + (0.11f * blue))/3f);
		
		gray = Math.min(255, Math.max(0, gray));
		
		//This brightens the image the same as GrayFilter
		int percent = 50;		
		gray = (255 - ((255 - gray) * (100 - percent) / 100));

		return new Color(gray, gray, gray, alpha);
	}
	
	/**
	 * Paints the transcoded SVG image on the specified graphics context. You
	 * can install a custom transformation on the graphics context to scale the
	 * image.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	public static void paint(Graphics2D g, boolean disabled) {
        Shape shape = null;
        Paint paint = null;
        Stroke stroke = null;
        Area clip = null;
         
        float origAlpha = 1.0f;
        Composite origComposite = g.getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = 
                (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }
        
	    Shape clip_ = g.getClip();
AffineTransform defaultTransform_ = g.getTransform();
//  is CompositeGraphicsNode
float alpha__0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0 = g.getClip();
AffineTransform defaultTransform__0 = g.getTransform();
g.transform(new AffineTransform(3.7809524536132812f, 0.0f, 0.0f, 3.7809524536132812f, -0.0f, 0.028557914192788303f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(0.0,-1.52587890625E-5,209.99999899069473,297.000015644232)));
g.setClip(clip);
// _0 is CompositeGraphicsNode
float alpha__0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0 = g.getClip();
AffineTransform defaultTransform__0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0 is CompositeGraphicsNode
float alpha__0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(67.58283233642578, 207.8485107421875, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(67.58283233642578, 207.8485107421875, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
float alpha__0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_1 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(112.55906677246094, 207.84848022460938, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(112.55906677246094, 207.84848022460938, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_1;
g.setTransform(defaultTransform__0_0_1);
g.setClip(clip__0_0_1);
float alpha__0_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_2 = g.getClip();
AffineTransform defaultTransform__0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_2 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(205.11888122558594, 207.21836853027344, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(205.11888122558594, 207.21836853027344, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_2;
g.setTransform(defaultTransform__0_0_2);
g.setClip(clip__0_0_2);
float alpha__0_0_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_3 = g.getClip();
AffineTransform defaultTransform__0_0_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_3 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(158.83895874023438, 209.1087646484375, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(158.83895874023438, 209.1087646484375, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_3;
g.setTransform(defaultTransform__0_0_3);
g.setClip(clip__0_0_3);
float alpha__0_0_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_4 = g.getClip();
AffineTransform defaultTransform__0_0_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_4 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(21.954753875732422, 122.78001403808594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(21.954753875732422, 122.78001403808594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_4;
g.setTransform(defaultTransform__0_0_4);
g.setClip(clip__0_0_4);
float alpha__0_0_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_5 = g.getClip();
AffineTransform defaultTransform__0_0_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_5 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(66.93099212646484, 122.78001403808594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(66.93099212646484, 122.78001403808594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_5;
g.setTransform(defaultTransform__0_0_5);
g.setClip(clip__0_0_5);
float alpha__0_0_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_6 = g.getClip();
AffineTransform defaultTransform__0_0_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_6 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(111.90725708007812, 122.77999877929688, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(111.90725708007812, 122.77999877929688, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_6;
g.setTransform(defaultTransform__0_0_6);
g.setClip(clip__0_0_6);
float alpha__0_0_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_7 = g.getClip();
AffineTransform defaultTransform__0_0_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_7 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(249.44329833984375, 123.41011047363281, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(249.44329833984375, 123.41011047363281, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_7;
g.setTransform(defaultTransform__0_0_7);
g.setClip(clip__0_0_7);
float alpha__0_0_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_8 = g.getClip();
AffineTransform defaultTransform__0_0_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_8 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(204.46707153320312, 122.14987182617188, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(204.46707153320312, 122.14987182617188, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_8;
g.setTransform(defaultTransform__0_0_8);
g.setClip(clip__0_0_8);
float alpha__0_0_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_9 = g.getClip();
AffineTransform defaultTransform__0_0_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_9 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(158.18714904785156, 124.04026794433594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(158.18714904785156, 124.04026794433594, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_9;
g.setTransform(defaultTransform__0_0_9);
g.setClip(clip__0_0_9);
float alpha__0_0_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_10 = g.getClip();
AffineTransform defaultTransform__0_0_10 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_10 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(111.90725708007812, 32.67039489746094, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(111.90725708007812, 32.67039489746094, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_10;
g.setTransform(defaultTransform__0_0_10);
g.setClip(clip__0_0_10);
float alpha__0_0_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_11 = g.getClip();
AffineTransform defaultTransform__0_0_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_11 is ShapeNode
paint = getColor(0, 0, 0, 47, disabled);
shape = new Ellipse2D.Double(158.18714904785156, 33.930667877197266, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.fill(shape);
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(7.882092f,0,0,4.0f,null,0.0f);
shape = new Ellipse2D.Double(158.18714904785156, 33.930667877197266, 33.8951416015625, 32.767127990722656);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_11;
g.setTransform(defaultTransform__0_0_11);
g.setClip(clip__0_0_11);
float alpha__0_0_12 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_12 = g.getClip();
AffineTransform defaultTransform__0_0_12 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_12 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(50.67075, 127.37459);
((GeneralPath)shape).lineTo(117.0864, 60.842937);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_12;
g.setTransform(defaultTransform__0_0_12);
g.setClip(clip__0_0_12);
float alpha__0_0_13 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_13 = g.getClip();
AffineTransform defaultTransform__0_0_13 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_13 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(161.08566, 59.476883);
((GeneralPath)shape).lineTo(52.95139, 130.00092);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_13;
g.setTransform(defaultTransform__0_0_13);
g.setClip(clip__0_0_13);
float alpha__0_0_14 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_14 = g.getClip();
AffineTransform defaultTransform__0_0_14 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_14 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(91.24339, 124.40819);
((GeneralPath)shape).lineTo(121.48999, 63.809338);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_14;
g.setTransform(defaultTransform__0_0_14);
g.setClip(clip__0_0_14);
float alpha__0_0_15 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_15 = g.getClip();
AffineTransform defaultTransform__0_0_15 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_15 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(95.8194, 127.53767);
((GeneralPath)shape).lineTo(163.19388, 61.940132);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_15;
g.setTransform(defaultTransform__0_0_15);
g.setClip(clip__0_0_15);
float alpha__0_0_16 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_16 = g.getClip();
AffineTransform defaultTransform__0_0_16 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_16 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(128.85481, 122.78);
((GeneralPath)shape).lineTo(128.85481, 65.43752);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_16;
g.setTransform(defaultTransform__0_0_16);
g.setClip(clip__0_0_16);
float alpha__0_0_17 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_17 = g.getClip();
AffineTransform defaultTransform__0_0_17 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_17 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(175.13472, 124.04026);
((GeneralPath)shape).lineTo(175.13472, 66.6978);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_17;
g.setTransform(defaultTransform__0_0_17);
g.setClip(clip__0_0_17);
float alpha__0_0_18 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_18 = g.getClip();
AffineTransform defaultTransform__0_0_18 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_18 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(136.47691, 124.53048);
((GeneralPath)shape).lineTo(167.51263, 64.94731);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_18;
g.setTransform(defaultTransform__0_0_18);
g.setClip(clip__0_0_18);
float alpha__0_0_19 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_19 = g.getClip();
AffineTransform defaultTransform__0_0_19 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_19 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(167.68181, 125.70965);
((GeneralPath)shape).lineTo(136.30772, 63.768143);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_19;
g.setTransform(defaultTransform__0_0_19);
g.setClip(clip__0_0_19);
float alpha__0_0_20 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_20 = g.getClip();
AffineTransform defaultTransform__0_0_20 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_20 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(209.4309, 126.9485);
((GeneralPath)shape).lineTo(140.83858, 60.63889);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_20;
g.setTransform(defaultTransform__0_0_20);
g.setClip(clip__0_0_20);
float alpha__0_0_21 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_21 = g.getClip();
AffineTransform defaultTransform__0_0_21 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_21 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(213.74931, 123.92172);
((GeneralPath)shape).lineTo(182.80003, 64.92595);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_21;
g.setTransform(defaultTransform__0_0_21);
g.setClip(clip__0_0_21);
float alpha__0_0_22 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_22 = g.getClip();
AffineTransform defaultTransform__0_0_22 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_22 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(252.3927, 130.55835);
((GeneralPath)shape).lineTo(142.853, 58.289284);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_22;
g.setTransform(defaultTransform__0_0_22);
g.setClip(clip__0_0_22);
float alpha__0_0_23 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_23 = g.getClip();
AffineTransform defaultTransform__0_0_23 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_23 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(254.49263, 128.12709);
((GeneralPath)shape).lineTo(187.03294, 61.980816);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_23;
g.setTransform(defaultTransform__0_0_23);
g.setClip(clip__0_0_23);
float alpha__0_0_24 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_24 = g.getClip();
AffineTransform defaultTransform__0_0_24 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_24 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(76.72916, 209.68752);
((GeneralPath)shape).lineTo(46.70357, 153.70815);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_24;
g.setTransform(defaultTransform__0_0_24);
g.setClip(clip__0_0_24);
float alpha__0_0_25 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_25 = g.getClip();
AffineTransform defaultTransform__0_0_25 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_25 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(84.40487, 207.84924);
((GeneralPath)shape).lineTo(84.0041, 155.54639);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_25;
g.setTransform(defaultTransform__0_0_25);
g.setClip(clip__0_0_25);
float alpha__0_0_26 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_26 = g.getClip();
AffineTransform defaultTransform__0_0_26 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_26 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(92.15439, 209.59993);
((GeneralPath)shape).lineTo(121.23084, 153.79568);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_26;
g.setTransform(defaultTransform__0_0_26);
g.setClip(clip__0_0_26);
float alpha__0_0_27 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_27 = g.getClip();
AffineTransform defaultTransform__0_0_27 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_27 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(96.77537, 212.90558);
((GeneralPath)shape).lineTo(162.88976, 151.7503);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_27;
g.setTransform(defaultTransform__0_0_27);
g.setClip(clip__0_0_27);
float alpha__0_0_28 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_28 = g.getClip();
AffineTransform defaultTransform__0_0_28 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_28 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(98.755295, 215.32634);
((GeneralPath)shape).lineTo(207.18974, 147.43915);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_28;
g.setTransform(defaultTransform__0_0_28);
g.setClip(clip__0_0_28);
float alpha__0_0_29 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_29 = g.getClip();
AffineTransform defaultTransform__0_0_29 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_29 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(99.80703, 217.13908);
((GeneralPath)shape).lineTo(251.11424, 146.88666);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_29;
g.setTransform(defaultTransform__0_0_29);
g.setClip(clip__0_0_29);
float alpha__0_0_30 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_30 = g.getClip();
AffineTransform defaultTransform__0_0_30 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_30 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(117.34938, 212.8176);
((GeneralPath)shape).lineTo(51.059574, 150.57803);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_30;
g.setTransform(defaultTransform__0_0_30);
g.setClip(clip__0_0_30);
float alpha__0_0_31 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_31 = g.getClip();
AffineTransform defaultTransform__0_0_31 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_31 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(121.70539, 209.68748);
((GeneralPath)shape).lineTo(91.67981, 153.70815);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_31;
g.setTransform(defaultTransform__0_0_31);
g.setClip(clip__0_0_31);
float alpha__0_0_32 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_32 = g.getClip();
AffineTransform defaultTransform__0_0_32 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_32 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(129.3811, 207.84921);
((GeneralPath)shape).lineTo(128.98035, 155.54637);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_32;
g.setTransform(defaultTransform__0_0_32);
g.setClip(clip__0_0_32);
float alpha__0_0_33 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_33 = g.getClip();
AffineTransform defaultTransform__0_0_33 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_33 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(137.39977, 209.7342);
((GeneralPath)shape).lineTo(167.2416, 154.92168);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_33;
g.setTransform(defaultTransform__0_0_33);
g.setClip(clip__0_0_33);
float alpha__0_0_34 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_34 = g.getClip();
AffineTransform defaultTransform__0_0_34 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_34 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(141.70448, 212.8583);
((GeneralPath)shape).lineTo(209.2168, 149.90717);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_34;
g.setTransform(defaultTransform__0_0_34);
g.setClip(clip__0_0_34);
float alpha__0_0_35 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_35 = g.getClip();
AffineTransform defaultTransform__0_0_35 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_35 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(143.79321, 215.41924);
((GeneralPath)shape).lineTo(252.1043, 148.60649);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_35;
g.setTransform(defaultTransform__0_0_35);
g.setClip(clip__0_0_35);
float alpha__0_0_36 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_36 = g.getClip();
AffineTransform defaultTransform__0_0_36 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_36 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(53.096344, 148.11533);
((GeneralPath)shape).lineTo(161.59251, 216.54057);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_36;
g.setTransform(defaultTransform__0_0_36);
g.setClip(clip__0_0_36);
float alpha__0_0_37 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_37 = g.getClip();
AffineTransform defaultTransform__0_0_37 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_37 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(96.03334, 150.5805);
((GeneralPath)shape).lineTo(163.63174, 214.07538);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_37;
g.setTransform(defaultTransform__0_0_37);
g.setClip(clip__0_0_37);
float alpha__0_0_38 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_38 = g.getClip();
AffineTransform defaultTransform__0_0_38 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_38 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(136.73892, 153.666);
((GeneralPath)shape).lineTo(167.90244, 210.98988);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_38;
g.setTransform(defaultTransform__0_0_38);
g.setClip(clip__0_0_38);
float alpha__0_0_39 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_39 = g.getClip();
AffineTransform defaultTransform__0_0_39 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_39 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(175.26025, 156.80666);
((GeneralPath)shape).lineTo(175.66101, 209.10951);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_39;
g.setTransform(defaultTransform__0_0_39);
g.setClip(clip__0_0_39);
float alpha__0_0_40 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_40 = g.getClip();
AffineTransform defaultTransform__0_0_40 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_40 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(213.74806, 153.14453);
((GeneralPath)shape).lineTo(183.4531, 210.88123);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_40;
g.setTransform(defaultTransform__0_0_40);
g.setClip(clip__0_0_40);
float alpha__0_0_41 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_41 = g.getClip();
AffineTransform defaultTransform__0_0_41 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_41 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(254.2772, 151.25146);
((GeneralPath)shape).lineTo(187.90018, 214.03455);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_41;
g.setTransform(defaultTransform__0_0_41);
g.setClip(clip__0_0_41);
float alpha__0_0_42 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_42 = g.getClip();
AffineTransform defaultTransform__0_0_42 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_42 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(54.199368, 146.21548);
((GeneralPath)shape).lineTo(206.76941, 216.55);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_42;
g.setTransform(defaultTransform__0_0_42);
g.setClip(clip__0_0_42);
float alpha__0_0_43 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_43 = g.getClip();
AffineTransform defaultTransform__0_0_43 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_43 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(98.20426, 147.91716);
((GeneralPath)shape).lineTo(207.74075, 214.84834);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_43;
g.setTransform(defaultTransform__0_0_43);
g.setClip(clip__0_0_43);
float alpha__0_0_44 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_44 = g.getClip();
AffineTransform defaultTransform__0_0_44 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_44 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(141.22119, 150.36598);
((GeneralPath)shape).lineTo(209.70009, 212.3995);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_44;
g.setTransform(defaultTransform__0_0_44);
g.setClip(clip__0_0_44);
float alpha__0_0_45 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_45 = g.getClip();
AffineTransform defaultTransform__0_0_45 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_45 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(183.24997, 154.80666);
((GeneralPath)shape).lineTo(213.9512, 209.2191);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_45;
g.setTransform(defaultTransform__0_0_45);
g.setClip(clip__0_0_45);
float alpha__0_0_46 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_46 = g.getClip();
AffineTransform defaultTransform__0_0_46 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_46 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(221.54018, 154.91624);
((GeneralPath)shape).lineTo(221.94093, 207.2191);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_46;
g.setTransform(defaultTransform__0_0_46);
g.setClip(clip__0_0_46);
float alpha__0_0_47 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_47 = g.getClip();
AffineTransform defaultTransform__0_0_47 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_47 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(3.3792465f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(258.676, 154.3809);
((GeneralPath)shape).lineTo(229.78133, 209.0147);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_0_47;
g.setTransform(defaultTransform__0_0_47);
g.setClip(clip__0_0_47);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
origAlpha = alpha__0;
g.setTransform(defaultTransform__0);
g.setClip(clip__0);
g.setTransform(defaultTransform_);
g.setClip(clip_);

	}
	
	public Image getImage() {
		BufferedImage image =
            new BufferedImage(getIconWidth(), getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = image.createGraphics();
    	paintIcon(null, g, 0, 0);
    	g.dispose();
    	return image;
	}

    /**
     * Returns the X of the bounding box of the original SVG image.
     * 
     * @return The X of the bounding box of the original SVG image.
     */
    public static int getOrigX() {
        return 69;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 109;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 794;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 1123;
	}

	/**
	 * The current width of this resizable icon.
	 */
	int width;

	/**
	 * The current height of this resizable icon.
	 */
	int height;
	
	/**
	 * Should this icon be drawn in a disabled state
	 */
	boolean disabled = false;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public LF_TrainTopicModel_Mallet_ENIcon() {
        this(getOrigWidth(),getOrigHeight(),false);
	}
	
	public LF_TrainTopicModel_Mallet_ENIcon(boolean disabled) {
        this(getOrigWidth(),getOrigHeight(),disabled);
	}
	
	/**
	 * Creates a new transcoded SVG image with the given dimensions.
	 *
	 * @param size the dimensions of the icon
	 */
	public LF_TrainTopicModel_Mallet_ENIcon(Dimension size) {
		this(size.width, size.height, false);
	}
	
	public LF_TrainTopicModel_Mallet_ENIcon(Dimension size, boolean disabled) {
		this(size.width, size.height, disabled);
	}

	public LF_TrainTopicModel_Mallet_ENIcon(int width, int height) {
		this(width, height, false);
	}
	
	public LF_TrainTopicModel_Mallet_ENIcon(int width, int height, boolean disabled) {
		this.width = width;
		this.height = height;
		this.disabled = disabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
    @Override
	public int getIconHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
    @Override
	public int getIconWidth() {
		return width;
	}

	public void setDimension(Dimension newDimension) {
		this.width = newDimension.width;
		this.height = newDimension.height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
    @Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(x, y);
						
		Area clip = new Area(new Rectangle(0, 0, this.width, this.height));		
		if (g2d.getClip() != null) clip.intersect(new Area(g2d.getClip()));		
		g2d.setClip(clip);

		double coef1 = (double) this.width / (double) getOrigWidth();
		double coef2 = (double) this.height / (double) getOrigHeight();
		double coef = Math.min(coef1, coef2);
		g2d.scale(coef, coef);
		paint(g2d, disabled);
		g2d.dispose();
	}
}

