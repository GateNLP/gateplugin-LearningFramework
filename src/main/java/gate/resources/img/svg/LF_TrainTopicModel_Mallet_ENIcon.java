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
g.transform(new AffineTransform(0.03779999911785126f, 0.0f, 0.0f, 0.03779999911785126f, -0.0f, -0.0f));
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
// _0_0_0 is CompositeGraphicsNode
float alpha__0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_0 is CompositeGraphicsNode
origAlpha = alpha__0_0_0_0;
g.setTransform(defaultTransform__0_0_0_0);
g.setClip(clip__0_0_0_0);
float alpha__0_0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0_0_1 is CompositeGraphicsNode
origAlpha = alpha__0_0_0_1;
g.setTransform(defaultTransform__0_0_0_1);
g.setClip(clip__0_0_0_1);
origAlpha = alpha__0_0_0;
g.setTransform(defaultTransform__0_0_0);
g.setClip(clip__0_0_0);
origAlpha = alpha__0_0;
g.setTransform(defaultTransform__0_0);
g.setClip(clip__0_0);
float alpha__0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1 = g.getClip();
AffineTransform defaultTransform__0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1 is CompositeGraphicsNode
float alpha__0_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0 = g.getClip();
AffineTransform defaultTransform__0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0 is CompositeGraphicsNode
float alpha__0_1_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
clip = new Area(g.getClip());
clip.intersect(new Area(new Rectangle2D.Double(0.0,0.0,100000.0,100000.0)));
g.setClip(clip);
// _0_1_0_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_0_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_0_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_0_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_0_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_0_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_0_0_0);
g.setClip(clip__0_1_0_0_0_0_0_0_0_0);
float alpha__0_1_0_0_0_0_0_0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_0_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31995.0, 80752.0);
((GeneralPath)shape).curveTo(31995.0, 81840.0, 31739.0, 82760.0, 31178.0, 83700.0);
((GeneralPath)shape).curveTo(30617.0, 84638.0, 29919.0, 85314.0, 28946.0, 85855.0);
((GeneralPath)shape).curveTo(27977.0, 86398.0, 27022.0, 86646.0, 25901.0, 86646.0);
((GeneralPath)shape).curveTo(24779.0, 86646.0, 23824.0, 86398.0, 22852.0, 85855.0);
((GeneralPath)shape).curveTo(21883.0, 85314.0, 21184.0, 84638.0, 20623.0, 83700.0);
((GeneralPath)shape).curveTo(20062.0, 82760.0, 19806.0, 81840.0, 19806.0, 80752.0);
((GeneralPath)shape).curveTo(19806.0, 79668.0, 20062.0, 78748.0, 20623.0, 77807.0);
((GeneralPath)shape).curveTo(21184.0, 76870.0, 21883.0, 76194.0, 22852.0, 75650.0);
((GeneralPath)shape).curveTo(23824.0, 75109.0, 24779.0, 74862.0, 25901.0, 74862.0);
((GeneralPath)shape).curveTo(27022.0, 74862.0, 27977.0, 75109.0, 28946.0, 75650.0);
((GeneralPath)shape).curveTo(29919.0, 76194.0, 30617.0, 76870.0, 31178.0, 77807.0);
((GeneralPath)shape).curveTo(31739.0, 78748.0, 31995.0, 79668.0, 31995.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31995.0, 80752.0);
((GeneralPath)shape).curveTo(31995.0, 81840.0, 31739.0, 82760.0, 31178.0, 83700.0);
((GeneralPath)shape).curveTo(30617.0, 84638.0, 29919.0, 85314.0, 28946.0, 85855.0);
((GeneralPath)shape).curveTo(27977.0, 86398.0, 27022.0, 86646.0, 25901.0, 86646.0);
((GeneralPath)shape).curveTo(24779.0, 86646.0, 23824.0, 86398.0, 22852.0, 85855.0);
((GeneralPath)shape).curveTo(21883.0, 85314.0, 21184.0, 84638.0, 20623.0, 83700.0);
((GeneralPath)shape).curveTo(20062.0, 82760.0, 19806.0, 81840.0, 19806.0, 80752.0);
((GeneralPath)shape).curveTo(19806.0, 79668.0, 20062.0, 78748.0, 20623.0, 77807.0);
((GeneralPath)shape).curveTo(21184.0, 76870.0, 21883.0, 76194.0, 22852.0, 75650.0);
((GeneralPath)shape).curveTo(23824.0, 75109.0, 24779.0, 74862.0, 25901.0, 74862.0);
((GeneralPath)shape).curveTo(27022.0, 74862.0, 27977.0, 75109.0, 28946.0, 75650.0);
((GeneralPath)shape).curveTo(29919.0, 76194.0, 30617.0, 76870.0, 31178.0, 77807.0);
((GeneralPath)shape).curveTo(31739.0, 78748.0, 31995.0, 79668.0, 31995.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_0_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_0_0_1);
g.setClip(clip__0_1_0_0_0_0_0_0_0_1);
float alpha__0_1_0_0_0_0_0_0_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_0_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_0_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31995.0, 80752.0);
((GeneralPath)shape).curveTo(31995.0, 81840.0, 31739.0, 82760.0, 31178.0, 83700.0);
((GeneralPath)shape).curveTo(30617.0, 84638.0, 29919.0, 85314.0, 28946.0, 85855.0);
((GeneralPath)shape).curveTo(27977.0, 86398.0, 27022.0, 86646.0, 25901.0, 86646.0);
((GeneralPath)shape).curveTo(24779.0, 86646.0, 23824.0, 86398.0, 22852.0, 85855.0);
((GeneralPath)shape).curveTo(21883.0, 85314.0, 21184.0, 84638.0, 20623.0, 83700.0);
((GeneralPath)shape).curveTo(20062.0, 82760.0, 19806.0, 81840.0, 19806.0, 80752.0);
((GeneralPath)shape).curveTo(19806.0, 79668.0, 20062.0, 78748.0, 20623.0, 77807.0);
((GeneralPath)shape).curveTo(21184.0, 76870.0, 21883.0, 76194.0, 22852.0, 75650.0);
((GeneralPath)shape).curveTo(23824.0, 75109.0, 24779.0, 74862.0, 25901.0, 74862.0);
((GeneralPath)shape).curveTo(27022.0, 74862.0, 27977.0, 75109.0, 28946.0, 75650.0);
((GeneralPath)shape).curveTo(29919.0, 76194.0, 30617.0, 76870.0, 31178.0, 77807.0);
((GeneralPath)shape).curveTo(31739.0, 78748.0, 31995.0, 79668.0, 31995.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_0_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_0_0_2);
g.setClip(clip__0_1_0_0_0_0_0_0_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_0_0);
g.setClip(clip__0_1_0_0_0_0_0_0_0);
origAlpha = alpha__0_1_0_0_0_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_0);
g.setClip(clip__0_1_0_0_0_0_0_0);
float alpha__0_1_0_0_0_0_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_1 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_1_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_1_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_1_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_1_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_1_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_1_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_1_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_1_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_1_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_1_0_0);
g.setClip(clip__0_1_0_0_0_0_0_1_0_0);
float alpha__0_1_0_0_0_0_0_1_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_1_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_1_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_1_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(48164.0, 80752.0);
((GeneralPath)shape).curveTo(48164.0, 81840.0, 47908.0, 82760.0, 47347.0, 83700.0);
((GeneralPath)shape).curveTo(46786.0, 84638.0, 46088.0, 85314.0, 45115.0, 85855.0);
((GeneralPath)shape).curveTo(44146.0, 86398.0, 43191.0, 86646.0, 42070.0, 86646.0);
((GeneralPath)shape).curveTo(40948.0, 86646.0, 39993.0, 86398.0, 39021.0, 85855.0);
((GeneralPath)shape).curveTo(38052.0, 85314.0, 37353.0, 84638.0, 36792.0, 83700.0);
((GeneralPath)shape).curveTo(36231.0, 82760.0, 35975.0, 81840.0, 35975.0, 80752.0);
((GeneralPath)shape).curveTo(35975.0, 79668.0, 36231.0, 78748.0, 36792.0, 77807.0);
((GeneralPath)shape).curveTo(37353.0, 76870.0, 38052.0, 76194.0, 39021.0, 75650.0);
((GeneralPath)shape).curveTo(39993.0, 75109.0, 40948.0, 74862.0, 42070.0, 74862.0);
((GeneralPath)shape).curveTo(43191.0, 74862.0, 44146.0, 75109.0, 45115.0, 75650.0);
((GeneralPath)shape).curveTo(46088.0, 76194.0, 46786.0, 76870.0, 47347.0, 77807.0);
((GeneralPath)shape).curveTo(47908.0, 78748.0, 48164.0, 79668.0, 48164.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(48164.0, 80752.0);
((GeneralPath)shape).curveTo(48164.0, 81840.0, 47908.0, 82760.0, 47347.0, 83700.0);
((GeneralPath)shape).curveTo(46786.0, 84638.0, 46088.0, 85314.0, 45115.0, 85855.0);
((GeneralPath)shape).curveTo(44146.0, 86398.0, 43191.0, 86646.0, 42070.0, 86646.0);
((GeneralPath)shape).curveTo(40948.0, 86646.0, 39993.0, 86398.0, 39021.0, 85855.0);
((GeneralPath)shape).curveTo(38052.0, 85314.0, 37353.0, 84638.0, 36792.0, 83700.0);
((GeneralPath)shape).curveTo(36231.0, 82760.0, 35975.0, 81840.0, 35975.0, 80752.0);
((GeneralPath)shape).curveTo(35975.0, 79668.0, 36231.0, 78748.0, 36792.0, 77807.0);
((GeneralPath)shape).curveTo(37353.0, 76870.0, 38052.0, 76194.0, 39021.0, 75650.0);
((GeneralPath)shape).curveTo(39993.0, 75109.0, 40948.0, 74862.0, 42070.0, 74862.0);
((GeneralPath)shape).curveTo(43191.0, 74862.0, 44146.0, 75109.0, 45115.0, 75650.0);
((GeneralPath)shape).curveTo(46088.0, 76194.0, 46786.0, 76870.0, 47347.0, 77807.0);
((GeneralPath)shape).curveTo(47908.0, 78748.0, 48164.0, 79668.0, 48164.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_1_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_1_0_1);
g.setClip(clip__0_1_0_0_0_0_0_1_0_1);
float alpha__0_1_0_0_0_0_0_1_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_1_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_1_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_1_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(48164.0, 80752.0);
((GeneralPath)shape).curveTo(48164.0, 81840.0, 47908.0, 82760.0, 47347.0, 83700.0);
((GeneralPath)shape).curveTo(46786.0, 84638.0, 46088.0, 85314.0, 45115.0, 85855.0);
((GeneralPath)shape).curveTo(44146.0, 86398.0, 43191.0, 86646.0, 42070.0, 86646.0);
((GeneralPath)shape).curveTo(40948.0, 86646.0, 39993.0, 86398.0, 39021.0, 85855.0);
((GeneralPath)shape).curveTo(38052.0, 85314.0, 37353.0, 84638.0, 36792.0, 83700.0);
((GeneralPath)shape).curveTo(36231.0, 82760.0, 35975.0, 81840.0, 35975.0, 80752.0);
((GeneralPath)shape).curveTo(35975.0, 79668.0, 36231.0, 78748.0, 36792.0, 77807.0);
((GeneralPath)shape).curveTo(37353.0, 76870.0, 38052.0, 76194.0, 39021.0, 75650.0);
((GeneralPath)shape).curveTo(39993.0, 75109.0, 40948.0, 74862.0, 42070.0, 74862.0);
((GeneralPath)shape).curveTo(43191.0, 74862.0, 44146.0, 75109.0, 45115.0, 75650.0);
((GeneralPath)shape).curveTo(46088.0, 76194.0, 46786.0, 76870.0, 47347.0, 77807.0);
((GeneralPath)shape).curveTo(47908.0, 78748.0, 48164.0, 79668.0, 48164.0, 80752.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_1_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_1_0_2);
g.setClip(clip__0_1_0_0_0_0_0_1_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_1_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_1_0);
g.setClip(clip__0_1_0_0_0_0_0_1_0);
origAlpha = alpha__0_1_0_0_0_0_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_1);
g.setClip(clip__0_1_0_0_0_0_0_1);
float alpha__0_1_0_0_0_0_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_2 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_2_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_2_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_2_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_2_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_2_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_2_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_2_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_2_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_2_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_2_0_0);
g.setClip(clip__0_1_0_0_0_0_0_2_0_0);
float alpha__0_1_0_0_0_0_0_2_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_2_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_2_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_2_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81441.0, 80525.0);
((GeneralPath)shape).curveTo(81441.0, 81613.0, 81185.0, 82533.0, 80624.0, 83473.0);
((GeneralPath)shape).curveTo(80063.0, 84411.0, 79365.0, 85087.0, 78392.0, 85628.0);
((GeneralPath)shape).curveTo(77423.0, 86171.0, 76468.0, 86419.0, 75347.0, 86419.0);
((GeneralPath)shape).curveTo(74225.0, 86419.0, 73270.0, 86171.0, 72298.0, 85628.0);
((GeneralPath)shape).curveTo(71329.0, 85087.0, 70630.0, 84411.0, 70069.0, 83473.0);
((GeneralPath)shape).curveTo(69508.0, 82533.0, 69252.0, 81613.0, 69252.0, 80525.0);
((GeneralPath)shape).curveTo(69252.0, 79441.0, 69508.0, 78521.0, 70069.0, 77580.0);
((GeneralPath)shape).curveTo(70630.0, 76643.0, 71329.0, 75967.0, 72298.0, 75423.0);
((GeneralPath)shape).curveTo(73270.0, 74882.0, 74225.0, 74635.0, 75347.0, 74635.0);
((GeneralPath)shape).curveTo(76468.0, 74635.0, 77423.0, 74882.0, 78392.0, 75423.0);
((GeneralPath)shape).curveTo(79365.0, 75967.0, 80063.0, 76643.0, 80624.0, 77580.0);
((GeneralPath)shape).curveTo(81185.0, 78521.0, 81441.0, 79441.0, 81441.0, 80525.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81441.0, 80525.0);
((GeneralPath)shape).curveTo(81441.0, 81613.0, 81185.0, 82533.0, 80624.0, 83473.0);
((GeneralPath)shape).curveTo(80063.0, 84411.0, 79365.0, 85087.0, 78392.0, 85628.0);
((GeneralPath)shape).curveTo(77423.0, 86171.0, 76468.0, 86419.0, 75347.0, 86419.0);
((GeneralPath)shape).curveTo(74225.0, 86419.0, 73270.0, 86171.0, 72298.0, 85628.0);
((GeneralPath)shape).curveTo(71329.0, 85087.0, 70630.0, 84411.0, 70069.0, 83473.0);
((GeneralPath)shape).curveTo(69508.0, 82533.0, 69252.0, 81613.0, 69252.0, 80525.0);
((GeneralPath)shape).curveTo(69252.0, 79441.0, 69508.0, 78521.0, 70069.0, 77580.0);
((GeneralPath)shape).curveTo(70630.0, 76643.0, 71329.0, 75967.0, 72298.0, 75423.0);
((GeneralPath)shape).curveTo(73270.0, 74882.0, 74225.0, 74635.0, 75347.0, 74635.0);
((GeneralPath)shape).curveTo(76468.0, 74635.0, 77423.0, 74882.0, 78392.0, 75423.0);
((GeneralPath)shape).curveTo(79365.0, 75967.0, 80063.0, 76643.0, 80624.0, 77580.0);
((GeneralPath)shape).curveTo(81185.0, 78521.0, 81441.0, 79441.0, 81441.0, 80525.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_2_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_2_0_1);
g.setClip(clip__0_1_0_0_0_0_0_2_0_1);
float alpha__0_1_0_0_0_0_0_2_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_2_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_2_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_2_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81441.0, 80525.0);
((GeneralPath)shape).curveTo(81441.0, 81613.0, 81185.0, 82533.0, 80624.0, 83473.0);
((GeneralPath)shape).curveTo(80063.0, 84411.0, 79365.0, 85087.0, 78392.0, 85628.0);
((GeneralPath)shape).curveTo(77423.0, 86171.0, 76468.0, 86419.0, 75347.0, 86419.0);
((GeneralPath)shape).curveTo(74225.0, 86419.0, 73270.0, 86171.0, 72298.0, 85628.0);
((GeneralPath)shape).curveTo(71329.0, 85087.0, 70630.0, 84411.0, 70069.0, 83473.0);
((GeneralPath)shape).curveTo(69508.0, 82533.0, 69252.0, 81613.0, 69252.0, 80525.0);
((GeneralPath)shape).curveTo(69252.0, 79441.0, 69508.0, 78521.0, 70069.0, 77580.0);
((GeneralPath)shape).curveTo(70630.0, 76643.0, 71329.0, 75967.0, 72298.0, 75423.0);
((GeneralPath)shape).curveTo(73270.0, 74882.0, 74225.0, 74635.0, 75347.0, 74635.0);
((GeneralPath)shape).curveTo(76468.0, 74635.0, 77423.0, 74882.0, 78392.0, 75423.0);
((GeneralPath)shape).curveTo(79365.0, 75967.0, 80063.0, 76643.0, 80624.0, 77580.0);
((GeneralPath)shape).curveTo(81185.0, 78521.0, 81441.0, 79441.0, 81441.0, 80525.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_2_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_2_0_2);
g.setClip(clip__0_1_0_0_0_0_0_2_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_2_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_2_0);
g.setClip(clip__0_1_0_0_0_0_0_2_0);
origAlpha = alpha__0_1_0_0_0_0_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_2);
g.setClip(clip__0_1_0_0_0_0_0_2);
float alpha__0_1_0_0_0_0_0_3 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_3 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_3 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_3 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_3_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_3_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_3_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_3_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_3_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_3_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_3_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_3_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_3_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_3_0_0);
g.setClip(clip__0_1_0_0_0_0_0_3_0_0);
float alpha__0_1_0_0_0_0_0_3_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_3_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_3_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_3_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64803.0, 81206.0);
((GeneralPath)shape).curveTo(64803.0, 82294.0, 64547.0, 83214.0, 63986.0, 84154.0);
((GeneralPath)shape).curveTo(63425.0, 85092.0, 62727.0, 85768.0, 61754.0, 86309.0);
((GeneralPath)shape).curveTo(60785.0, 86852.0, 59830.0, 87100.0, 58709.0, 87100.0);
((GeneralPath)shape).curveTo(57587.0, 87100.0, 56632.0, 86852.0, 55660.0, 86309.0);
((GeneralPath)shape).curveTo(54691.0, 85768.0, 53992.0, 85092.0, 53431.0, 84154.0);
((GeneralPath)shape).curveTo(52870.0, 83214.0, 52614.0, 82294.0, 52614.0, 81206.0);
((GeneralPath)shape).curveTo(52614.0, 80122.0, 52870.0, 79202.0, 53431.0, 78261.0);
((GeneralPath)shape).curveTo(53992.0, 77324.0, 54691.0, 76648.0, 55660.0, 76104.0);
((GeneralPath)shape).curveTo(56632.0, 75563.0, 57587.0, 75316.0, 58709.0, 75316.0);
((GeneralPath)shape).curveTo(59830.0, 75316.0, 60785.0, 75563.0, 61754.0, 76104.0);
((GeneralPath)shape).curveTo(62727.0, 76648.0, 63425.0, 77324.0, 63986.0, 78261.0);
((GeneralPath)shape).curveTo(64547.0, 79202.0, 64803.0, 80122.0, 64803.0, 81206.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64803.0, 81206.0);
((GeneralPath)shape).curveTo(64803.0, 82294.0, 64547.0, 83214.0, 63986.0, 84154.0);
((GeneralPath)shape).curveTo(63425.0, 85092.0, 62727.0, 85768.0, 61754.0, 86309.0);
((GeneralPath)shape).curveTo(60785.0, 86852.0, 59830.0, 87100.0, 58709.0, 87100.0);
((GeneralPath)shape).curveTo(57587.0, 87100.0, 56632.0, 86852.0, 55660.0, 86309.0);
((GeneralPath)shape).curveTo(54691.0, 85768.0, 53992.0, 85092.0, 53431.0, 84154.0);
((GeneralPath)shape).curveTo(52870.0, 83214.0, 52614.0, 82294.0, 52614.0, 81206.0);
((GeneralPath)shape).curveTo(52614.0, 80122.0, 52870.0, 79202.0, 53431.0, 78261.0);
((GeneralPath)shape).curveTo(53992.0, 77324.0, 54691.0, 76648.0, 55660.0, 76104.0);
((GeneralPath)shape).curveTo(56632.0, 75563.0, 57587.0, 75316.0, 58709.0, 75316.0);
((GeneralPath)shape).curveTo(59830.0, 75316.0, 60785.0, 75563.0, 61754.0, 76104.0);
((GeneralPath)shape).curveTo(62727.0, 76648.0, 63425.0, 77324.0, 63986.0, 78261.0);
((GeneralPath)shape).curveTo(64547.0, 79202.0, 64803.0, 80122.0, 64803.0, 81206.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_3_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_3_0_1);
g.setClip(clip__0_1_0_0_0_0_0_3_0_1);
float alpha__0_1_0_0_0_0_0_3_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_3_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_3_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_3_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64803.0, 81206.0);
((GeneralPath)shape).curveTo(64803.0, 82294.0, 64547.0, 83214.0, 63986.0, 84154.0);
((GeneralPath)shape).curveTo(63425.0, 85092.0, 62727.0, 85768.0, 61754.0, 86309.0);
((GeneralPath)shape).curveTo(60785.0, 86852.0, 59830.0, 87100.0, 58709.0, 87100.0);
((GeneralPath)shape).curveTo(57587.0, 87100.0, 56632.0, 86852.0, 55660.0, 86309.0);
((GeneralPath)shape).curveTo(54691.0, 85768.0, 53992.0, 85092.0, 53431.0, 84154.0);
((GeneralPath)shape).curveTo(52870.0, 83214.0, 52614.0, 82294.0, 52614.0, 81206.0);
((GeneralPath)shape).curveTo(52614.0, 80122.0, 52870.0, 79202.0, 53431.0, 78261.0);
((GeneralPath)shape).curveTo(53992.0, 77324.0, 54691.0, 76648.0, 55660.0, 76104.0);
((GeneralPath)shape).curveTo(56632.0, 75563.0, 57587.0, 75316.0, 58709.0, 75316.0);
((GeneralPath)shape).curveTo(59830.0, 75316.0, 60785.0, 75563.0, 61754.0, 76104.0);
((GeneralPath)shape).curveTo(62727.0, 76648.0, 63425.0, 77324.0, 63986.0, 78261.0);
((GeneralPath)shape).curveTo(64547.0, 79202.0, 64803.0, 80122.0, 64803.0, 81206.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_3_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_3_0_2);
g.setClip(clip__0_1_0_0_0_0_0_3_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_3_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_3_0);
g.setClip(clip__0_1_0_0_0_0_0_3_0);
origAlpha = alpha__0_1_0_0_0_0_0_3;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_3);
g.setClip(clip__0_1_0_0_0_0_0_3);
float alpha__0_1_0_0_0_0_0_4 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_4 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_4 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_4 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_4_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_4_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_4_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_4_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_4_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_4_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_4_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_4_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_4_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_4_0_0);
g.setClip(clip__0_1_0_0_0_0_0_4_0_0);
float alpha__0_1_0_0_0_0_0_4_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_4_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_4_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_4_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15589.0, 50170.0);
((GeneralPath)shape).curveTo(15589.0, 51258.0, 15333.0, 52178.0, 14772.0, 53118.0);
((GeneralPath)shape).curveTo(14211.0, 54056.0, 13513.0, 54732.0, 12540.0, 55273.0);
((GeneralPath)shape).curveTo(11571.0, 55816.0, 10616.0, 56064.0, 9495.0, 56064.0);
((GeneralPath)shape).curveTo(8373.0, 56064.0, 7418.0, 55816.0, 6446.0, 55273.0);
((GeneralPath)shape).curveTo(5477.0, 54732.0, 4778.0, 54056.0, 4217.0, 53118.0);
((GeneralPath)shape).curveTo(3656.0, 52178.0, 3400.0, 51258.0, 3400.0, 50170.0);
((GeneralPath)shape).curveTo(3400.0, 49086.0, 3656.0, 48166.0, 4217.0, 47225.0);
((GeneralPath)shape).curveTo(4778.0, 46288.0, 5477.0, 45612.0, 6446.0, 45068.0);
((GeneralPath)shape).curveTo(7418.0, 44527.0, 8373.0, 44280.0, 9495.0, 44280.0);
((GeneralPath)shape).curveTo(10616.0, 44280.0, 11571.0, 44527.0, 12540.0, 45068.0);
((GeneralPath)shape).curveTo(13513.0, 45612.0, 14211.0, 46288.0, 14772.0, 47225.0);
((GeneralPath)shape).curveTo(15333.0, 48166.0, 15589.0, 49086.0, 15589.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15589.0, 50170.0);
((GeneralPath)shape).curveTo(15589.0, 51258.0, 15333.0, 52178.0, 14772.0, 53118.0);
((GeneralPath)shape).curveTo(14211.0, 54056.0, 13513.0, 54732.0, 12540.0, 55273.0);
((GeneralPath)shape).curveTo(11571.0, 55816.0, 10616.0, 56064.0, 9495.0, 56064.0);
((GeneralPath)shape).curveTo(8373.0, 56064.0, 7418.0, 55816.0, 6446.0, 55273.0);
((GeneralPath)shape).curveTo(5477.0, 54732.0, 4778.0, 54056.0, 4217.0, 53118.0);
((GeneralPath)shape).curveTo(3656.0, 52178.0, 3400.0, 51258.0, 3400.0, 50170.0);
((GeneralPath)shape).curveTo(3400.0, 49086.0, 3656.0, 48166.0, 4217.0, 47225.0);
((GeneralPath)shape).curveTo(4778.0, 46288.0, 5477.0, 45612.0, 6446.0, 45068.0);
((GeneralPath)shape).curveTo(7418.0, 44527.0, 8373.0, 44280.0, 9495.0, 44280.0);
((GeneralPath)shape).curveTo(10616.0, 44280.0, 11571.0, 44527.0, 12540.0, 45068.0);
((GeneralPath)shape).curveTo(13513.0, 45612.0, 14211.0, 46288.0, 14772.0, 47225.0);
((GeneralPath)shape).curveTo(15333.0, 48166.0, 15589.0, 49086.0, 15589.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_4_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_4_0_1);
g.setClip(clip__0_1_0_0_0_0_0_4_0_1);
float alpha__0_1_0_0_0_0_0_4_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_4_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_4_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_4_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(15589.0, 50170.0);
((GeneralPath)shape).curveTo(15589.0, 51258.0, 15333.0, 52178.0, 14772.0, 53118.0);
((GeneralPath)shape).curveTo(14211.0, 54056.0, 13513.0, 54732.0, 12540.0, 55273.0);
((GeneralPath)shape).curveTo(11571.0, 55816.0, 10616.0, 56064.0, 9495.0, 56064.0);
((GeneralPath)shape).curveTo(8373.0, 56064.0, 7418.0, 55816.0, 6446.0, 55273.0);
((GeneralPath)shape).curveTo(5477.0, 54732.0, 4778.0, 54056.0, 4217.0, 53118.0);
((GeneralPath)shape).curveTo(3656.0, 52178.0, 3400.0, 51258.0, 3400.0, 50170.0);
((GeneralPath)shape).curveTo(3400.0, 49086.0, 3656.0, 48166.0, 4217.0, 47225.0);
((GeneralPath)shape).curveTo(4778.0, 46288.0, 5477.0, 45612.0, 6446.0, 45068.0);
((GeneralPath)shape).curveTo(7418.0, 44527.0, 8373.0, 44280.0, 9495.0, 44280.0);
((GeneralPath)shape).curveTo(10616.0, 44280.0, 11571.0, 44527.0, 12540.0, 45068.0);
((GeneralPath)shape).curveTo(13513.0, 45612.0, 14211.0, 46288.0, 14772.0, 47225.0);
((GeneralPath)shape).curveTo(15333.0, 48166.0, 15589.0, 49086.0, 15589.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_4_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_4_0_2);
g.setClip(clip__0_1_0_0_0_0_0_4_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_4_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_4_0);
g.setClip(clip__0_1_0_0_0_0_0_4_0);
origAlpha = alpha__0_1_0_0_0_0_0_4;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_4);
g.setClip(clip__0_1_0_0_0_0_0_4);
float alpha__0_1_0_0_0_0_0_5 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_5 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_5 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_5 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_5_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_5_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_5_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_5_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_5_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_5_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_5_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_5_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_5_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_5_0_0);
g.setClip(clip__0_1_0_0_0_0_0_5_0_0);
float alpha__0_1_0_0_0_0_0_5_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_5_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_5_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_5_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31759.0, 50170.0);
((GeneralPath)shape).curveTo(31759.0, 51258.0, 31503.0, 52178.0, 30942.0, 53118.0);
((GeneralPath)shape).curveTo(30381.0, 54056.0, 29683.0, 54732.0, 28710.0, 55273.0);
((GeneralPath)shape).curveTo(27741.0, 55816.0, 26786.0, 56064.0, 25665.0, 56064.0);
((GeneralPath)shape).curveTo(24543.0, 56064.0, 23588.0, 55816.0, 22616.0, 55273.0);
((GeneralPath)shape).curveTo(21647.0, 54732.0, 20948.0, 54056.0, 20387.0, 53118.0);
((GeneralPath)shape).curveTo(19826.0, 52178.0, 19570.0, 51258.0, 19570.0, 50170.0);
((GeneralPath)shape).curveTo(19570.0, 49086.0, 19826.0, 48166.0, 20387.0, 47225.0);
((GeneralPath)shape).curveTo(20948.0, 46288.0, 21647.0, 45612.0, 22616.0, 45068.0);
((GeneralPath)shape).curveTo(23588.0, 44527.0, 24543.0, 44280.0, 25665.0, 44280.0);
((GeneralPath)shape).curveTo(26786.0, 44280.0, 27741.0, 44527.0, 28710.0, 45068.0);
((GeneralPath)shape).curveTo(29683.0, 45612.0, 30381.0, 46288.0, 30942.0, 47225.0);
((GeneralPath)shape).curveTo(31503.0, 48166.0, 31759.0, 49086.0, 31759.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31759.0, 50170.0);
((GeneralPath)shape).curveTo(31759.0, 51258.0, 31503.0, 52178.0, 30942.0, 53118.0);
((GeneralPath)shape).curveTo(30381.0, 54056.0, 29683.0, 54732.0, 28710.0, 55273.0);
((GeneralPath)shape).curveTo(27741.0, 55816.0, 26786.0, 56064.0, 25665.0, 56064.0);
((GeneralPath)shape).curveTo(24543.0, 56064.0, 23588.0, 55816.0, 22616.0, 55273.0);
((GeneralPath)shape).curveTo(21647.0, 54732.0, 20948.0, 54056.0, 20387.0, 53118.0);
((GeneralPath)shape).curveTo(19826.0, 52178.0, 19570.0, 51258.0, 19570.0, 50170.0);
((GeneralPath)shape).curveTo(19570.0, 49086.0, 19826.0, 48166.0, 20387.0, 47225.0);
((GeneralPath)shape).curveTo(20948.0, 46288.0, 21647.0, 45612.0, 22616.0, 45068.0);
((GeneralPath)shape).curveTo(23588.0, 44527.0, 24543.0, 44280.0, 25665.0, 44280.0);
((GeneralPath)shape).curveTo(26786.0, 44280.0, 27741.0, 44527.0, 28710.0, 45068.0);
((GeneralPath)shape).curveTo(29683.0, 45612.0, 30381.0, 46288.0, 30942.0, 47225.0);
((GeneralPath)shape).curveTo(31503.0, 48166.0, 31759.0, 49086.0, 31759.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_5_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_5_0_1);
g.setClip(clip__0_1_0_0_0_0_0_5_0_1);
float alpha__0_1_0_0_0_0_0_5_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_5_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_5_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_5_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31759.0, 50170.0);
((GeneralPath)shape).curveTo(31759.0, 51258.0, 31503.0, 52178.0, 30942.0, 53118.0);
((GeneralPath)shape).curveTo(30381.0, 54056.0, 29683.0, 54732.0, 28710.0, 55273.0);
((GeneralPath)shape).curveTo(27741.0, 55816.0, 26786.0, 56064.0, 25665.0, 56064.0);
((GeneralPath)shape).curveTo(24543.0, 56064.0, 23588.0, 55816.0, 22616.0, 55273.0);
((GeneralPath)shape).curveTo(21647.0, 54732.0, 20948.0, 54056.0, 20387.0, 53118.0);
((GeneralPath)shape).curveTo(19826.0, 52178.0, 19570.0, 51258.0, 19570.0, 50170.0);
((GeneralPath)shape).curveTo(19570.0, 49086.0, 19826.0, 48166.0, 20387.0, 47225.0);
((GeneralPath)shape).curveTo(20948.0, 46288.0, 21647.0, 45612.0, 22616.0, 45068.0);
((GeneralPath)shape).curveTo(23588.0, 44527.0, 24543.0, 44280.0, 25665.0, 44280.0);
((GeneralPath)shape).curveTo(26786.0, 44280.0, 27741.0, 44527.0, 28710.0, 45068.0);
((GeneralPath)shape).curveTo(29683.0, 45612.0, 30381.0, 46288.0, 30942.0, 47225.0);
((GeneralPath)shape).curveTo(31503.0, 48166.0, 31759.0, 49086.0, 31759.0, 50170.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_5_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_5_0_2);
g.setClip(clip__0_1_0_0_0_0_0_5_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_5_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_5_0);
g.setClip(clip__0_1_0_0_0_0_0_5_0);
origAlpha = alpha__0_1_0_0_0_0_0_5;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_5);
g.setClip(clip__0_1_0_0_0_0_0_5);
float alpha__0_1_0_0_0_0_0_6 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_6 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_6 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_6 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_6_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_6_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_6_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_6_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_6_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_6_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_6_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_6_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_6_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_6_0_0);
g.setClip(clip__0_1_0_0_0_0_0_6_0_0);
float alpha__0_1_0_0_0_0_0_6_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_6_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_6_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_6_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 50167.0);
((GeneralPath)shape).curveTo(47928.0, 51255.0, 47672.0, 52175.0, 47111.0, 53115.0);
((GeneralPath)shape).curveTo(46550.0, 54053.0, 45852.0, 54729.0, 44879.0, 55270.0);
((GeneralPath)shape).curveTo(43910.0, 55813.0, 42955.0, 56061.0, 41834.0, 56061.0);
((GeneralPath)shape).curveTo(40712.0, 56061.0, 39757.0, 55813.0, 38785.0, 55270.0);
((GeneralPath)shape).curveTo(37816.0, 54729.0, 37117.0, 54053.0, 36556.0, 53115.0);
((GeneralPath)shape).curveTo(35995.0, 52175.0, 35739.0, 51255.0, 35739.0, 50167.0);
((GeneralPath)shape).curveTo(35739.0, 49083.0, 35995.0, 48163.0, 36556.0, 47222.0);
((GeneralPath)shape).curveTo(37117.0, 46285.0, 37816.0, 45609.0, 38785.0, 45065.0);
((GeneralPath)shape).curveTo(39757.0, 44524.0, 40712.0, 44277.0, 41834.0, 44277.0);
((GeneralPath)shape).curveTo(42955.0, 44277.0, 43910.0, 44524.0, 44879.0, 45065.0);
((GeneralPath)shape).curveTo(45852.0, 45609.0, 46550.0, 46285.0, 47111.0, 47222.0);
((GeneralPath)shape).curveTo(47672.0, 48163.0, 47928.0, 49083.0, 47928.0, 50167.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 50167.0);
((GeneralPath)shape).curveTo(47928.0, 51255.0, 47672.0, 52175.0, 47111.0, 53115.0);
((GeneralPath)shape).curveTo(46550.0, 54053.0, 45852.0, 54729.0, 44879.0, 55270.0);
((GeneralPath)shape).curveTo(43910.0, 55813.0, 42955.0, 56061.0, 41834.0, 56061.0);
((GeneralPath)shape).curveTo(40712.0, 56061.0, 39757.0, 55813.0, 38785.0, 55270.0);
((GeneralPath)shape).curveTo(37816.0, 54729.0, 37117.0, 54053.0, 36556.0, 53115.0);
((GeneralPath)shape).curveTo(35995.0, 52175.0, 35739.0, 51255.0, 35739.0, 50167.0);
((GeneralPath)shape).curveTo(35739.0, 49083.0, 35995.0, 48163.0, 36556.0, 47222.0);
((GeneralPath)shape).curveTo(37117.0, 46285.0, 37816.0, 45609.0, 38785.0, 45065.0);
((GeneralPath)shape).curveTo(39757.0, 44524.0, 40712.0, 44277.0, 41834.0, 44277.0);
((GeneralPath)shape).curveTo(42955.0, 44277.0, 43910.0, 44524.0, 44879.0, 45065.0);
((GeneralPath)shape).curveTo(45852.0, 45609.0, 46550.0, 46285.0, 47111.0, 47222.0);
((GeneralPath)shape).curveTo(47672.0, 48163.0, 47928.0, 49083.0, 47928.0, 50167.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_6_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_6_0_1);
g.setClip(clip__0_1_0_0_0_0_0_6_0_1);
float alpha__0_1_0_0_0_0_0_6_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_6_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_6_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_6_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 50167.0);
((GeneralPath)shape).curveTo(47928.0, 51255.0, 47672.0, 52175.0, 47111.0, 53115.0);
((GeneralPath)shape).curveTo(46550.0, 54053.0, 45852.0, 54729.0, 44879.0, 55270.0);
((GeneralPath)shape).curveTo(43910.0, 55813.0, 42955.0, 56061.0, 41834.0, 56061.0);
((GeneralPath)shape).curveTo(40712.0, 56061.0, 39757.0, 55813.0, 38785.0, 55270.0);
((GeneralPath)shape).curveTo(37816.0, 54729.0, 37117.0, 54053.0, 36556.0, 53115.0);
((GeneralPath)shape).curveTo(35995.0, 52175.0, 35739.0, 51255.0, 35739.0, 50167.0);
((GeneralPath)shape).curveTo(35739.0, 49083.0, 35995.0, 48163.0, 36556.0, 47222.0);
((GeneralPath)shape).curveTo(37117.0, 46285.0, 37816.0, 45609.0, 38785.0, 45065.0);
((GeneralPath)shape).curveTo(39757.0, 44524.0, 40712.0, 44277.0, 41834.0, 44277.0);
((GeneralPath)shape).curveTo(42955.0, 44277.0, 43910.0, 44524.0, 44879.0, 45065.0);
((GeneralPath)shape).curveTo(45852.0, 45609.0, 46550.0, 46285.0, 47111.0, 47222.0);
((GeneralPath)shape).curveTo(47672.0, 48163.0, 47928.0, 49083.0, 47928.0, 50167.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_6_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_6_0_2);
g.setClip(clip__0_1_0_0_0_0_0_6_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_6_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_6_0);
g.setClip(clip__0_1_0_0_0_0_0_6_0);
origAlpha = alpha__0_1_0_0_0_0_0_6;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_6);
g.setClip(clip__0_1_0_0_0_0_0_6);
float alpha__0_1_0_0_0_0_0_7 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_7 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_7 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_7 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_7_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_7_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_7_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_7_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_7_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_7_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_7_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_7_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_7_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_7_0_0);
g.setClip(clip__0_1_0_0_0_0_0_7_0_0);
float alpha__0_1_0_0_0_0_0_7_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_7_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_7_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_7_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(97375.0, 50394.0);
((GeneralPath)shape).curveTo(97375.0, 51482.0, 97119.0, 52402.0, 96558.0, 53342.0);
((GeneralPath)shape).curveTo(95997.0, 54280.0, 95299.0, 54956.0, 94326.0, 55497.0);
((GeneralPath)shape).curveTo(93357.0, 56040.0, 92402.0, 56288.0, 91281.0, 56288.0);
((GeneralPath)shape).curveTo(90159.0, 56288.0, 89204.0, 56040.0, 88232.0, 55497.0);
((GeneralPath)shape).curveTo(87263.0, 54956.0, 86564.0, 54280.0, 86003.0, 53342.0);
((GeneralPath)shape).curveTo(85442.0, 52402.0, 85186.0, 51482.0, 85186.0, 50394.0);
((GeneralPath)shape).curveTo(85186.0, 49310.0, 85442.0, 48390.0, 86003.0, 47449.0);
((GeneralPath)shape).curveTo(86564.0, 46512.0, 87263.0, 45836.0, 88232.0, 45292.0);
((GeneralPath)shape).curveTo(89204.0, 44751.0, 90159.0, 44504.0, 91281.0, 44504.0);
((GeneralPath)shape).curveTo(92402.0, 44504.0, 93357.0, 44751.0, 94326.0, 45292.0);
((GeneralPath)shape).curveTo(95299.0, 45836.0, 95997.0, 46512.0, 96558.0, 47449.0);
((GeneralPath)shape).curveTo(97119.0, 48390.0, 97375.0, 49310.0, 97375.0, 50394.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(97375.0, 50394.0);
((GeneralPath)shape).curveTo(97375.0, 51482.0, 97119.0, 52402.0, 96558.0, 53342.0);
((GeneralPath)shape).curveTo(95997.0, 54280.0, 95299.0, 54956.0, 94326.0, 55497.0);
((GeneralPath)shape).curveTo(93357.0, 56040.0, 92402.0, 56288.0, 91281.0, 56288.0);
((GeneralPath)shape).curveTo(90159.0, 56288.0, 89204.0, 56040.0, 88232.0, 55497.0);
((GeneralPath)shape).curveTo(87263.0, 54956.0, 86564.0, 54280.0, 86003.0, 53342.0);
((GeneralPath)shape).curveTo(85442.0, 52402.0, 85186.0, 51482.0, 85186.0, 50394.0);
((GeneralPath)shape).curveTo(85186.0, 49310.0, 85442.0, 48390.0, 86003.0, 47449.0);
((GeneralPath)shape).curveTo(86564.0, 46512.0, 87263.0, 45836.0, 88232.0, 45292.0);
((GeneralPath)shape).curveTo(89204.0, 44751.0, 90159.0, 44504.0, 91281.0, 44504.0);
((GeneralPath)shape).curveTo(92402.0, 44504.0, 93357.0, 44751.0, 94326.0, 45292.0);
((GeneralPath)shape).curveTo(95299.0, 45836.0, 95997.0, 46512.0, 96558.0, 47449.0);
((GeneralPath)shape).curveTo(97119.0, 48390.0, 97375.0, 49310.0, 97375.0, 50394.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_7_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_7_0_1);
g.setClip(clip__0_1_0_0_0_0_0_7_0_1);
float alpha__0_1_0_0_0_0_0_7_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_7_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_7_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_7_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(97375.0, 50394.0);
((GeneralPath)shape).curveTo(97375.0, 51482.0, 97119.0, 52402.0, 96558.0, 53342.0);
((GeneralPath)shape).curveTo(95997.0, 54280.0, 95299.0, 54956.0, 94326.0, 55497.0);
((GeneralPath)shape).curveTo(93357.0, 56040.0, 92402.0, 56288.0, 91281.0, 56288.0);
((GeneralPath)shape).curveTo(90159.0, 56288.0, 89204.0, 56040.0, 88232.0, 55497.0);
((GeneralPath)shape).curveTo(87263.0, 54956.0, 86564.0, 54280.0, 86003.0, 53342.0);
((GeneralPath)shape).curveTo(85442.0, 52402.0, 85186.0, 51482.0, 85186.0, 50394.0);
((GeneralPath)shape).curveTo(85186.0, 49310.0, 85442.0, 48390.0, 86003.0, 47449.0);
((GeneralPath)shape).curveTo(86564.0, 46512.0, 87263.0, 45836.0, 88232.0, 45292.0);
((GeneralPath)shape).curveTo(89204.0, 44751.0, 90159.0, 44504.0, 91281.0, 44504.0);
((GeneralPath)shape).curveTo(92402.0, 44504.0, 93357.0, 44751.0, 94326.0, 45292.0);
((GeneralPath)shape).curveTo(95299.0, 45836.0, 95997.0, 46512.0, 96558.0, 47449.0);
((GeneralPath)shape).curveTo(97119.0, 48390.0, 97375.0, 49310.0, 97375.0, 50394.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_7_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_7_0_2);
g.setClip(clip__0_1_0_0_0_0_0_7_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_7_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_7_0);
g.setClip(clip__0_1_0_0_0_0_0_7_0);
origAlpha = alpha__0_1_0_0_0_0_0_7;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_7);
g.setClip(clip__0_1_0_0_0_0_0_7);
float alpha__0_1_0_0_0_0_0_8 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_8 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_8 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_8 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_8_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_8_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_8_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_8_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_8_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_8_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_8_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_8_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_8_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_8_0_0);
g.setClip(clip__0_1_0_0_0_0_0_8_0_0);
float alpha__0_1_0_0_0_0_0_8_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_8_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_8_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_8_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81205.0, 49943.0);
((GeneralPath)shape).curveTo(81205.0, 51031.0, 80949.0, 51951.0, 80388.0, 52891.0);
((GeneralPath)shape).curveTo(79827.0, 53829.0, 79129.0, 54505.0, 78156.0, 55046.0);
((GeneralPath)shape).curveTo(77187.0, 55589.0, 76232.0, 55837.0, 75111.0, 55837.0);
((GeneralPath)shape).curveTo(73989.0, 55837.0, 73034.0, 55589.0, 72062.0, 55046.0);
((GeneralPath)shape).curveTo(71093.0, 54505.0, 70394.0, 53829.0, 69833.0, 52891.0);
((GeneralPath)shape).curveTo(69272.0, 51951.0, 69016.0, 51031.0, 69016.0, 49943.0);
((GeneralPath)shape).curveTo(69016.0, 48859.0, 69272.0, 47939.0, 69833.0, 46998.0);
((GeneralPath)shape).curveTo(70394.0, 46061.0, 71093.0, 45385.0, 72062.0, 44841.0);
((GeneralPath)shape).curveTo(73034.0, 44300.0, 73989.0, 44053.0, 75111.0, 44053.0);
((GeneralPath)shape).curveTo(76232.0, 44053.0, 77187.0, 44300.0, 78156.0, 44841.0);
((GeneralPath)shape).curveTo(79129.0, 45385.0, 79827.0, 46061.0, 80388.0, 46998.0);
((GeneralPath)shape).curveTo(80949.0, 47939.0, 81205.0, 48859.0, 81205.0, 49943.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81205.0, 49943.0);
((GeneralPath)shape).curveTo(81205.0, 51031.0, 80949.0, 51951.0, 80388.0, 52891.0);
((GeneralPath)shape).curveTo(79827.0, 53829.0, 79129.0, 54505.0, 78156.0, 55046.0);
((GeneralPath)shape).curveTo(77187.0, 55589.0, 76232.0, 55837.0, 75111.0, 55837.0);
((GeneralPath)shape).curveTo(73989.0, 55837.0, 73034.0, 55589.0, 72062.0, 55046.0);
((GeneralPath)shape).curveTo(71093.0, 54505.0, 70394.0, 53829.0, 69833.0, 52891.0);
((GeneralPath)shape).curveTo(69272.0, 51951.0, 69016.0, 51031.0, 69016.0, 49943.0);
((GeneralPath)shape).curveTo(69016.0, 48859.0, 69272.0, 47939.0, 69833.0, 46998.0);
((GeneralPath)shape).curveTo(70394.0, 46061.0, 71093.0, 45385.0, 72062.0, 44841.0);
((GeneralPath)shape).curveTo(73034.0, 44300.0, 73989.0, 44053.0, 75111.0, 44053.0);
((GeneralPath)shape).curveTo(76232.0, 44053.0, 77187.0, 44300.0, 78156.0, 44841.0);
((GeneralPath)shape).curveTo(79129.0, 45385.0, 79827.0, 46061.0, 80388.0, 46998.0);
((GeneralPath)shape).curveTo(80949.0, 47939.0, 81205.0, 48859.0, 81205.0, 49943.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_8_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_8_0_1);
g.setClip(clip__0_1_0_0_0_0_0_8_0_1);
float alpha__0_1_0_0_0_0_0_8_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_8_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_8_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_8_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(81205.0, 49943.0);
((GeneralPath)shape).curveTo(81205.0, 51031.0, 80949.0, 51951.0, 80388.0, 52891.0);
((GeneralPath)shape).curveTo(79827.0, 53829.0, 79129.0, 54505.0, 78156.0, 55046.0);
((GeneralPath)shape).curveTo(77187.0, 55589.0, 76232.0, 55837.0, 75111.0, 55837.0);
((GeneralPath)shape).curveTo(73989.0, 55837.0, 73034.0, 55589.0, 72062.0, 55046.0);
((GeneralPath)shape).curveTo(71093.0, 54505.0, 70394.0, 53829.0, 69833.0, 52891.0);
((GeneralPath)shape).curveTo(69272.0, 51951.0, 69016.0, 51031.0, 69016.0, 49943.0);
((GeneralPath)shape).curveTo(69016.0, 48859.0, 69272.0, 47939.0, 69833.0, 46998.0);
((GeneralPath)shape).curveTo(70394.0, 46061.0, 71093.0, 45385.0, 72062.0, 44841.0);
((GeneralPath)shape).curveTo(73034.0, 44300.0, 73989.0, 44053.0, 75111.0, 44053.0);
((GeneralPath)shape).curveTo(76232.0, 44053.0, 77187.0, 44300.0, 78156.0, 44841.0);
((GeneralPath)shape).curveTo(79129.0, 45385.0, 79827.0, 46061.0, 80388.0, 46998.0);
((GeneralPath)shape).curveTo(80949.0, 47939.0, 81205.0, 48859.0, 81205.0, 49943.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_8_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_8_0_2);
g.setClip(clip__0_1_0_0_0_0_0_8_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_8_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_8_0);
g.setClip(clip__0_1_0_0_0_0_0_8_0);
origAlpha = alpha__0_1_0_0_0_0_0_8;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_8);
g.setClip(clip__0_1_0_0_0_0_0_8);
float alpha__0_1_0_0_0_0_0_9 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_9 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_9 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_9 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_9_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_9_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_9_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_9_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_9_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_9_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_9_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_9_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_9_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_9_0_0);
g.setClip(clip__0_1_0_0_0_0_0_9_0_0);
float alpha__0_1_0_0_0_0_0_9_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_9_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_9_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_9_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 50621.0);
((GeneralPath)shape).curveTo(64567.0, 51709.0, 64311.0, 52629.0, 63750.0, 53569.0);
((GeneralPath)shape).curveTo(63189.0, 54507.0, 62491.0, 55183.0, 61518.0, 55724.0);
((GeneralPath)shape).curveTo(60549.0, 56267.0, 59594.0, 56515.0, 58473.0, 56515.0);
((GeneralPath)shape).curveTo(57351.0, 56515.0, 56396.0, 56267.0, 55424.0, 55724.0);
((GeneralPath)shape).curveTo(54455.0, 55183.0, 53756.0, 54507.0, 53195.0, 53569.0);
((GeneralPath)shape).curveTo(52634.0, 52629.0, 52378.0, 51709.0, 52378.0, 50621.0);
((GeneralPath)shape).curveTo(52378.0, 49537.0, 52634.0, 48617.0, 53195.0, 47676.0);
((GeneralPath)shape).curveTo(53756.0, 46739.0, 54455.0, 46063.0, 55424.0, 45519.0);
((GeneralPath)shape).curveTo(56396.0, 44978.0, 57351.0, 44731.0, 58473.0, 44731.0);
((GeneralPath)shape).curveTo(59594.0, 44731.0, 60549.0, 44978.0, 61518.0, 45519.0);
((GeneralPath)shape).curveTo(62491.0, 46063.0, 63189.0, 46739.0, 63750.0, 47676.0);
((GeneralPath)shape).curveTo(64311.0, 48617.0, 64567.0, 49537.0, 64567.0, 50621.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 50621.0);
((GeneralPath)shape).curveTo(64567.0, 51709.0, 64311.0, 52629.0, 63750.0, 53569.0);
((GeneralPath)shape).curveTo(63189.0, 54507.0, 62491.0, 55183.0, 61518.0, 55724.0);
((GeneralPath)shape).curveTo(60549.0, 56267.0, 59594.0, 56515.0, 58473.0, 56515.0);
((GeneralPath)shape).curveTo(57351.0, 56515.0, 56396.0, 56267.0, 55424.0, 55724.0);
((GeneralPath)shape).curveTo(54455.0, 55183.0, 53756.0, 54507.0, 53195.0, 53569.0);
((GeneralPath)shape).curveTo(52634.0, 52629.0, 52378.0, 51709.0, 52378.0, 50621.0);
((GeneralPath)shape).curveTo(52378.0, 49537.0, 52634.0, 48617.0, 53195.0, 47676.0);
((GeneralPath)shape).curveTo(53756.0, 46739.0, 54455.0, 46063.0, 55424.0, 45519.0);
((GeneralPath)shape).curveTo(56396.0, 44978.0, 57351.0, 44731.0, 58473.0, 44731.0);
((GeneralPath)shape).curveTo(59594.0, 44731.0, 60549.0, 44978.0, 61518.0, 45519.0);
((GeneralPath)shape).curveTo(62491.0, 46063.0, 63189.0, 46739.0, 63750.0, 47676.0);
((GeneralPath)shape).curveTo(64311.0, 48617.0, 64567.0, 49537.0, 64567.0, 50621.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_9_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_9_0_1);
g.setClip(clip__0_1_0_0_0_0_0_9_0_1);
float alpha__0_1_0_0_0_0_0_9_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_9_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_9_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_9_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 50621.0);
((GeneralPath)shape).curveTo(64567.0, 51709.0, 64311.0, 52629.0, 63750.0, 53569.0);
((GeneralPath)shape).curveTo(63189.0, 54507.0, 62491.0, 55183.0, 61518.0, 55724.0);
((GeneralPath)shape).curveTo(60549.0, 56267.0, 59594.0, 56515.0, 58473.0, 56515.0);
((GeneralPath)shape).curveTo(57351.0, 56515.0, 56396.0, 56267.0, 55424.0, 55724.0);
((GeneralPath)shape).curveTo(54455.0, 55183.0, 53756.0, 54507.0, 53195.0, 53569.0);
((GeneralPath)shape).curveTo(52634.0, 52629.0, 52378.0, 51709.0, 52378.0, 50621.0);
((GeneralPath)shape).curveTo(52378.0, 49537.0, 52634.0, 48617.0, 53195.0, 47676.0);
((GeneralPath)shape).curveTo(53756.0, 46739.0, 54455.0, 46063.0, 55424.0, 45519.0);
((GeneralPath)shape).curveTo(56396.0, 44978.0, 57351.0, 44731.0, 58473.0, 44731.0);
((GeneralPath)shape).curveTo(59594.0, 44731.0, 60549.0, 44978.0, 61518.0, 45519.0);
((GeneralPath)shape).curveTo(62491.0, 46063.0, 63189.0, 46739.0, 63750.0, 47676.0);
((GeneralPath)shape).curveTo(64311.0, 48617.0, 64567.0, 49537.0, 64567.0, 50621.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_9_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_9_0_2);
g.setClip(clip__0_1_0_0_0_0_0_9_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_9_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_9_0);
g.setClip(clip__0_1_0_0_0_0_0_9_0);
origAlpha = alpha__0_1_0_0_0_0_0_9;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_9);
g.setClip(clip__0_1_0_0_0_0_0_9);
float alpha__0_1_0_0_0_0_0_10 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_10 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_10 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_10 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_10_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_10_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_10_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_10_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_10_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_10_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_10_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_10_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_10_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_10_0_0);
g.setClip(clip__0_1_0_0_0_0_0_10_0_0);
float alpha__0_1_0_0_0_0_0_10_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_10_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_10_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_10_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 17773.0);
((GeneralPath)shape).curveTo(47928.0, 18861.0, 47672.0, 19781.0, 47111.0, 20721.0);
((GeneralPath)shape).curveTo(46550.0, 21659.0, 45852.0, 22335.0, 44879.0, 22876.0);
((GeneralPath)shape).curveTo(43910.0, 23419.0, 42955.0, 23667.0, 41834.0, 23667.0);
((GeneralPath)shape).curveTo(40712.0, 23667.0, 39757.0, 23419.0, 38785.0, 22876.0);
((GeneralPath)shape).curveTo(37816.0, 22335.0, 37117.0, 21659.0, 36556.0, 20721.0);
((GeneralPath)shape).curveTo(35995.0, 19781.0, 35739.0, 18861.0, 35739.0, 17773.0);
((GeneralPath)shape).curveTo(35739.0, 16689.0, 35995.0, 15769.0, 36556.0, 14828.0);
((GeneralPath)shape).curveTo(37117.0, 13891.0, 37816.0, 13215.0, 38785.0, 12671.0);
((GeneralPath)shape).curveTo(39757.0, 12130.0, 40712.0, 11883.0, 41834.0, 11883.0);
((GeneralPath)shape).curveTo(42955.0, 11883.0, 43910.0, 12130.0, 44879.0, 12671.0);
((GeneralPath)shape).curveTo(45852.0, 13215.0, 46550.0, 13891.0, 47111.0, 14828.0);
((GeneralPath)shape).curveTo(47672.0, 15769.0, 47928.0, 16689.0, 47928.0, 17773.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 17773.0);
((GeneralPath)shape).curveTo(47928.0, 18861.0, 47672.0, 19781.0, 47111.0, 20721.0);
((GeneralPath)shape).curveTo(46550.0, 21659.0, 45852.0, 22335.0, 44879.0, 22876.0);
((GeneralPath)shape).curveTo(43910.0, 23419.0, 42955.0, 23667.0, 41834.0, 23667.0);
((GeneralPath)shape).curveTo(40712.0, 23667.0, 39757.0, 23419.0, 38785.0, 22876.0);
((GeneralPath)shape).curveTo(37816.0, 22335.0, 37117.0, 21659.0, 36556.0, 20721.0);
((GeneralPath)shape).curveTo(35995.0, 19781.0, 35739.0, 18861.0, 35739.0, 17773.0);
((GeneralPath)shape).curveTo(35739.0, 16689.0, 35995.0, 15769.0, 36556.0, 14828.0);
((GeneralPath)shape).curveTo(37117.0, 13891.0, 37816.0, 13215.0, 38785.0, 12671.0);
((GeneralPath)shape).curveTo(39757.0, 12130.0, 40712.0, 11883.0, 41834.0, 11883.0);
((GeneralPath)shape).curveTo(42955.0, 11883.0, 43910.0, 12130.0, 44879.0, 12671.0);
((GeneralPath)shape).curveTo(45852.0, 13215.0, 46550.0, 13891.0, 47111.0, 14828.0);
((GeneralPath)shape).curveTo(47672.0, 15769.0, 47928.0, 16689.0, 47928.0, 17773.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_10_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_10_0_1);
g.setClip(clip__0_1_0_0_0_0_0_10_0_1);
float alpha__0_1_0_0_0_0_0_10_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_10_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_10_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_10_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47928.0, 17773.0);
((GeneralPath)shape).curveTo(47928.0, 18861.0, 47672.0, 19781.0, 47111.0, 20721.0);
((GeneralPath)shape).curveTo(46550.0, 21659.0, 45852.0, 22335.0, 44879.0, 22876.0);
((GeneralPath)shape).curveTo(43910.0, 23419.0, 42955.0, 23667.0, 41834.0, 23667.0);
((GeneralPath)shape).curveTo(40712.0, 23667.0, 39757.0, 23419.0, 38785.0, 22876.0);
((GeneralPath)shape).curveTo(37816.0, 22335.0, 37117.0, 21659.0, 36556.0, 20721.0);
((GeneralPath)shape).curveTo(35995.0, 19781.0, 35739.0, 18861.0, 35739.0, 17773.0);
((GeneralPath)shape).curveTo(35739.0, 16689.0, 35995.0, 15769.0, 36556.0, 14828.0);
((GeneralPath)shape).curveTo(37117.0, 13891.0, 37816.0, 13215.0, 38785.0, 12671.0);
((GeneralPath)shape).curveTo(39757.0, 12130.0, 40712.0, 11883.0, 41834.0, 11883.0);
((GeneralPath)shape).curveTo(42955.0, 11883.0, 43910.0, 12130.0, 44879.0, 12671.0);
((GeneralPath)shape).curveTo(45852.0, 13215.0, 46550.0, 13891.0, 47111.0, 14828.0);
((GeneralPath)shape).curveTo(47672.0, 15769.0, 47928.0, 16689.0, 47928.0, 17773.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_10_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_10_0_2);
g.setClip(clip__0_1_0_0_0_0_0_10_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_10_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_10_0);
g.setClip(clip__0_1_0_0_0_0_0_10_0);
origAlpha = alpha__0_1_0_0_0_0_0_10;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_10);
g.setClip(clip__0_1_0_0_0_0_0_10);
float alpha__0_1_0_0_0_0_0_11 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_11 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_11 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_11 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_11_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_11_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_11_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_11_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_11_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_11_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_11_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_11_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_11_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_11_0_0);
g.setClip(clip__0_1_0_0_0_0_0_11_0_0);
float alpha__0_1_0_0_0_0_0_11_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_11_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_11_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_11_0_1 is ShapeNode
paint = getColor(0, 0, 0, 48, disabled);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 18225.0);
((GeneralPath)shape).curveTo(64567.0, 19313.0, 64311.0, 20233.0, 63750.0, 21173.0);
((GeneralPath)shape).curveTo(63189.0, 22111.0, 62491.0, 22787.0, 61518.0, 23328.0);
((GeneralPath)shape).curveTo(60549.0, 23871.0, 59594.0, 24119.0, 58473.0, 24119.0);
((GeneralPath)shape).curveTo(57351.0, 24119.0, 56396.0, 23871.0, 55424.0, 23328.0);
((GeneralPath)shape).curveTo(54455.0, 22787.0, 53756.0, 22111.0, 53195.0, 21173.0);
((GeneralPath)shape).curveTo(52634.0, 20233.0, 52378.0, 19313.0, 52378.0, 18225.0);
((GeneralPath)shape).curveTo(52378.0, 17141.0, 52634.0, 16221.0, 53195.0, 15280.0);
((GeneralPath)shape).curveTo(53756.0, 14343.0, 54455.0, 13667.0, 55424.0, 13123.0);
((GeneralPath)shape).curveTo(56396.0, 12582.0, 57351.0, 12335.0, 58473.0, 12335.0);
((GeneralPath)shape).curveTo(59594.0, 12335.0, 60549.0, 12582.0, 61518.0, 13123.0);
((GeneralPath)shape).curveTo(62491.0, 13667.0, 63189.0, 14343.0, 63750.0, 15280.0);
((GeneralPath)shape).curveTo(64311.0, 16221.0, 64567.0, 17141.0, 64567.0, 18225.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.fill(shape);
paint = getColor(255, 255, 255, 48, disabled);
stroke = new BasicStroke(28.222f,0,1,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 18225.0);
((GeneralPath)shape).curveTo(64567.0, 19313.0, 64311.0, 20233.0, 63750.0, 21173.0);
((GeneralPath)shape).curveTo(63189.0, 22111.0, 62491.0, 22787.0, 61518.0, 23328.0);
((GeneralPath)shape).curveTo(60549.0, 23871.0, 59594.0, 24119.0, 58473.0, 24119.0);
((GeneralPath)shape).curveTo(57351.0, 24119.0, 56396.0, 23871.0, 55424.0, 23328.0);
((GeneralPath)shape).curveTo(54455.0, 22787.0, 53756.0, 22111.0, 53195.0, 21173.0);
((GeneralPath)shape).curveTo(52634.0, 20233.0, 52378.0, 19313.0, 52378.0, 18225.0);
((GeneralPath)shape).curveTo(52378.0, 17141.0, 52634.0, 16221.0, 53195.0, 15280.0);
((GeneralPath)shape).curveTo(53756.0, 14343.0, 54455.0, 13667.0, 55424.0, 13123.0);
((GeneralPath)shape).curveTo(56396.0, 12582.0, 57351.0, 12335.0, 58473.0, 12335.0);
((GeneralPath)shape).curveTo(59594.0, 12335.0, 60549.0, 12582.0, 61518.0, 13123.0);
((GeneralPath)shape).curveTo(62491.0, 13667.0, 63189.0, 14343.0, 63750.0, 15280.0);
((GeneralPath)shape).curveTo(64311.0, 16221.0, 64567.0, 17141.0, 64567.0, 18225.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_11_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_11_0_1);
g.setClip(clip__0_1_0_0_0_0_0_11_0_1);
float alpha__0_1_0_0_0_0_0_11_0_2 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_11_0_2 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_11_0_2 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_11_0_2 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(985.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(64567.0, 18225.0);
((GeneralPath)shape).curveTo(64567.0, 19313.0, 64311.0, 20233.0, 63750.0, 21173.0);
((GeneralPath)shape).curveTo(63189.0, 22111.0, 62491.0, 22787.0, 61518.0, 23328.0);
((GeneralPath)shape).curveTo(60549.0, 23871.0, 59594.0, 24119.0, 58473.0, 24119.0);
((GeneralPath)shape).curveTo(57351.0, 24119.0, 56396.0, 23871.0, 55424.0, 23328.0);
((GeneralPath)shape).curveTo(54455.0, 22787.0, 53756.0, 22111.0, 53195.0, 21173.0);
((GeneralPath)shape).curveTo(52634.0, 20233.0, 52378.0, 19313.0, 52378.0, 18225.0);
((GeneralPath)shape).curveTo(52378.0, 17141.0, 52634.0, 16221.0, 53195.0, 15280.0);
((GeneralPath)shape).curveTo(53756.0, 14343.0, 54455.0, 13667.0, 55424.0, 13123.0);
((GeneralPath)shape).curveTo(56396.0, 12582.0, 57351.0, 12335.0, 58473.0, 12335.0);
((GeneralPath)shape).curveTo(59594.0, 12335.0, 60549.0, 12582.0, 61518.0, 13123.0);
((GeneralPath)shape).curveTo(62491.0, 13667.0, 63189.0, 14343.0, 63750.0, 15280.0);
((GeneralPath)shape).curveTo(64311.0, 16221.0, 64567.0, 17141.0, 64567.0, 18225.0);
((GeneralPath)shape).closePath();
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_11_0_2;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_11_0_2);
g.setClip(clip__0_1_0_0_0_0_0_11_0_2);
origAlpha = alpha__0_1_0_0_0_0_0_11_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_11_0);
g.setClip(clip__0_1_0_0_0_0_0_11_0);
origAlpha = alpha__0_1_0_0_0_0_0_11;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_11);
g.setClip(clip__0_1_0_0_0_0_0_11);
float alpha__0_1_0_0_0_0_0_12 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_12 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_12 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_12 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_12_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_12_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_12_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_12_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_12_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_12_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_12_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_12_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_12_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_12_0_0);
g.setClip(clip__0_1_0_0_0_0_0_12_0_0);
float alpha__0_1_0_0_0_0_0_12_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_12_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_12_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_12_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(13725.0, 45931.0);
((GeneralPath)shape).lineTo(37606.0, 22010.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_12_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_12_0_1);
g.setClip(clip__0_1_0_0_0_0_0_12_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_12_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_12_0);
g.setClip(clip__0_1_0_0_0_0_0_12_0);
origAlpha = alpha__0_1_0_0_0_0_0_12;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_12);
g.setClip(clip__0_1_0_0_0_0_0_12);
float alpha__0_1_0_0_0_0_0_13 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_13 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_13 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_13 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_13_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_13_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_13_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_13_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_13_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_13_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_13_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_13_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_13_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_13_0_0);
g.setClip(clip__0_1_0_0_0_0_0_13_0_0);
float alpha__0_1_0_0_0_0_0_13_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_13_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_13_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_13_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(53425.0, 21521.0);
((GeneralPath)shape).lineTo(14545.0, 46880.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_13_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_13_0_1);
g.setClip(clip__0_1_0_0_0_0_0_13_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_13_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_13_0);
g.setClip(clip__0_1_0_0_0_0_0_13_0);
origAlpha = alpha__0_1_0_0_0_0_0_13;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_13);
g.setClip(clip__0_1_0_0_0_0_0_13);
float alpha__0_1_0_0_0_0_0_14 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_14 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_14 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_14 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_14_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_14_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_14_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_14_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_14_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_14_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_14_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_14_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_14_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_14_0_0);
g.setClip(clip__0_1_0_0_0_0_0_14_0_0);
float alpha__0_1_0_0_0_0_0_14_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_14_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_14_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_14_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28310.0, 44867.0);
((GeneralPath)shape).lineTo(39188.0, 23077.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_14_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_14_0_1);
g.setClip(clip__0_1_0_0_0_0_0_14_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_14_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_14_0);
g.setClip(clip__0_1_0_0_0_0_0_14_0);
origAlpha = alpha__0_1_0_0_0_0_0_14;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_14);
g.setClip(clip__0_1_0_0_0_0_0_14);
float alpha__0_1_0_0_0_0_0_15 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_15 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_15 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_15 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_15_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_15_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_15_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_15_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_15_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_15_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_15_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_15_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_15_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_15_0_0);
g.setClip(clip__0_1_0_0_0_0_0_15_0_0);
float alpha__0_1_0_0_0_0_0_15_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_15_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_15_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_15_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(29955.0, 45994.0);
((GeneralPath)shape).lineTo(54181.0, 22407.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_15_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_15_0_1);
g.setClip(clip__0_1_0_0_0_0_0_15_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_15_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_15_0);
g.setClip(clip__0_1_0_0_0_0_0_15_0);
origAlpha = alpha__0_1_0_0_0_0_0_15;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_15);
g.setClip(clip__0_1_0_0_0_0_0_15);
float alpha__0_1_0_0_0_0_0_16 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_16 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_16 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_16 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_16_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_16_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_16_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_16_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_16_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_16_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_16_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_16_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_16_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_16_0_0);
g.setClip(clip__0_1_0_0_0_0_0_16_0_0);
float alpha__0_1_0_0_0_0_0_16_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_16_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_16_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_16_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(41834.0, 44280.0);
((GeneralPath)shape).lineTo(41834.0, 23664.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_16_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_16_0_1);
g.setClip(clip__0_1_0_0_0_0_0_16_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_16_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_16_0);
g.setClip(clip__0_1_0_0_0_0_0_16_0);
origAlpha = alpha__0_1_0_0_0_0_0_16;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_16);
g.setClip(clip__0_1_0_0_0_0_0_16);
float alpha__0_1_0_0_0_0_0_17 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_17 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_17 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_17 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_17_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_17_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_17_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_17_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_17_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_17_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_17_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_17_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_17_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_17_0_0);
g.setClip(clip__0_1_0_0_0_0_0_17_0_0);
float alpha__0_1_0_0_0_0_0_17_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_17_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_17_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_17_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(58472.0, 44731.0);
((GeneralPath)shape).lineTo(58472.0, 24115.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_17_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_17_0_1);
g.setClip(clip__0_1_0_0_0_0_0_17_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_17_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_17_0);
g.setClip(clip__0_1_0_0_0_0_0_17_0);
origAlpha = alpha__0_1_0_0_0_0_0_17;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_17);
g.setClip(clip__0_1_0_0_0_0_0_17);
float alpha__0_1_0_0_0_0_0_18 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_18 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_18 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_18 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_18_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_18_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_18_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_18_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_18_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_18_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_18_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_18_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_18_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_18_0_0);
g.setClip(clip__0_1_0_0_0_0_0_18_0_0);
float alpha__0_1_0_0_0_0_0_18_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_18_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_18_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_18_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(44575.0, 44909.0);
((GeneralPath)shape).lineTo(55734.0, 23485.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_18_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_18_0_1);
g.setClip(clip__0_1_0_0_0_0_0_18_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_18_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_18_0);
g.setClip(clip__0_1_0_0_0_0_0_18_0);
origAlpha = alpha__0_1_0_0_0_0_0_18;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_18);
g.setClip(clip__0_1_0_0_0_0_0_18);
float alpha__0_1_0_0_0_0_0_19 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_19 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_19 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_19 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_19_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_19_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_19_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_19_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_19_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_19_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_19_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_19_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_19_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_19_0_0);
g.setClip(clip__0_1_0_0_0_0_0_19_0_0);
float alpha__0_1_0_0_0_0_0_19_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_19_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_19_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_19_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(55795.0, 45336.0);
((GeneralPath)shape).lineTo(44512.0, 23063.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_19_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_19_0_1);
g.setClip(clip__0_1_0_0_0_0_0_19_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_19_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_19_0);
g.setClip(clip__0_1_0_0_0_0_0_19_0);
origAlpha = alpha__0_1_0_0_0_0_0_19;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_19);
g.setClip(clip__0_1_0_0_0_0_0_19);
float alpha__0_1_0_0_0_0_0_20 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_20 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_20 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_20 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_20_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_20_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_20_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_20_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_20_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_20_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_20_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_20_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_20_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_20_0_0);
g.setClip(clip__0_1_0_0_0_0_0_20_0_0);
float alpha__0_1_0_0_0_0_0_20_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_20_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_20_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_20_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(70805.0, 45781.0);
((GeneralPath)shape).lineTo(46142.0, 21938.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_20_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_20_0_1);
g.setClip(clip__0_1_0_0_0_0_0_20_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_20_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_20_0);
g.setClip(clip__0_1_0_0_0_0_0_20_0);
origAlpha = alpha__0_1_0_0_0_0_0_20;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_20);
g.setClip(clip__0_1_0_0_0_0_0_20);
float alpha__0_1_0_0_0_0_0_21 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_21 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_21 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_21 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_21_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_21_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_21_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_21_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_21_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_21_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_21_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_21_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_21_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_21_0_0);
g.setClip(clip__0_1_0_0_0_0_0_21_0_0);
float alpha__0_1_0_0_0_0_0_21_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_21_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_21_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_21_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(72359.0, 44692.0);
((GeneralPath)shape).lineTo(61228.0, 23480.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_21_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_21_0_1);
g.setClip(clip__0_1_0_0_0_0_0_21_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_21_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_21_0);
g.setClip(clip__0_1_0_0_0_0_0_21_0);
origAlpha = alpha__0_1_0_0_0_0_0_21;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_21);
g.setClip(clip__0_1_0_0_0_0_0_21);
float alpha__0_1_0_0_0_0_0_22 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_22 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_22 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_22 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_22_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_22_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_22_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_22_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_22_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_22_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_22_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_22_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_22_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_22_0_0);
g.setClip(clip__0_1_0_0_0_0_0_22_0_0);
float alpha__0_1_0_0_0_0_0_22_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_22_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_22_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_22_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(86250.0, 47078.0);
((GeneralPath)shape).lineTo(46867.0, 21092.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_22_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_22_0_1);
g.setClip(clip__0_1_0_0_0_0_0_22_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_22_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_22_0);
g.setClip(clip__0_1_0_0_0_0_0_22_0);
origAlpha = alpha__0_1_0_0_0_0_0_22;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_22);
g.setClip(clip__0_1_0_0_0_0_0_22);
float alpha__0_1_0_0_0_0_0_23 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_23 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_23 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_23 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_23_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_23_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_23_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_23_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_23_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_23_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_23_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_23_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_23_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_23_0_0);
g.setClip(clip__0_1_0_0_0_0_0_23_0_0);
float alpha__0_1_0_0_0_0_0_23_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_23_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_23_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_23_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(87004.0, 46204.0);
((GeneralPath)shape).lineTo(62749.0, 22421.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_23_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_23_0_1);
g.setClip(clip__0_1_0_0_0_0_0_23_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_23_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_23_0);
g.setClip(clip__0_1_0_0_0_0_0_23_0);
origAlpha = alpha__0_1_0_0_0_0_0_23;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_23);
g.setClip(clip__0_1_0_0_0_0_0_23);
float alpha__0_1_0_0_0_0_0_24 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_24 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_24 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_24 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_24_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_24_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_24_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_24_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_24_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_24_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_24_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_24_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_24_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_24_0_0);
g.setClip(clip__0_1_0_0_0_0_0_24_0_0);
float alpha__0_1_0_0_0_0_0_24_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_24_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_24_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_24_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(23096.0, 75526.0);
((GeneralPath)shape).lineTo(12299.0, 55399.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_24_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_24_0_1);
g.setClip(clip__0_1_0_0_0_0_0_24_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_24_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_24_0);
g.setClip(clip__0_1_0_0_0_0_0_24_0);
origAlpha = alpha__0_1_0_0_0_0_0_24;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_24);
g.setClip(clip__0_1_0_0_0_0_0_24);
float alpha__0_1_0_0_0_0_0_25 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_25 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_25 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_25 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_25_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_25_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_25_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_25_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_25_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_25_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_25_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_25_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_25_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_25_0_0);
g.setClip(clip__0_1_0_0_0_0_0_25_0_0);
float alpha__0_1_0_0_0_0_0_25_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_25_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_25_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_25_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(25857.0, 74865.0);
((GeneralPath)shape).lineTo(25710.0, 56058.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_25_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_25_0_1);
g.setClip(clip__0_1_0_0_0_0_0_25_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_25_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_25_0);
g.setClip(clip__0_1_0_0_0_0_0_25_0);
origAlpha = alpha__0_1_0_0_0_0_0_25;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_25);
g.setClip(clip__0_1_0_0_0_0_0_25);
float alpha__0_1_0_0_0_0_0_26 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_26 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_26 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_26 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_26_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_26_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_26_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_26_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_26_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_26_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_26_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_26_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_26_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_26_0_0);
g.setClip(clip__0_1_0_0_0_0_0_26_0_0);
float alpha__0_1_0_0_0_0_0_26_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_26_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_26_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_26_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(28638.0, 75495.0);
((GeneralPath)shape).lineTo(39096.0, 55428.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_26_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_26_0_1);
g.setClip(clip__0_1_0_0_0_0_0_26_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_26_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_26_0);
g.setClip(clip__0_1_0_0_0_0_0_26_0);
origAlpha = alpha__0_1_0_0_0_0_0_26;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_26);
g.setClip(clip__0_1_0_0_0_0_0_26);
float alpha__0_1_0_0_0_0_0_27 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_27 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_27 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_27 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_27_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_27_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_27_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_27_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_27_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_27_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_27_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_27_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_27_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_27_0_0);
g.setClip(clip__0_1_0_0_0_0_0_27_0_0);
float alpha__0_1_0_0_0_0_0_27_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_27_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_27_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_27_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30301.0, 76682.0);
((GeneralPath)shape).lineTo(54072.0, 54694.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_27_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_27_0_1);
g.setClip(clip__0_1_0_0_0_0_0_27_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_27_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_27_0);
g.setClip(clip__0_1_0_0_0_0_0_27_0);
origAlpha = alpha__0_1_0_0_0_0_0_27;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_27);
g.setClip(clip__0_1_0_0_0_0_0_27);
float alpha__0_1_0_0_0_0_0_28 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_28 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_28 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_28 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_28_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_28_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_28_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_28_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_28_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_28_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_28_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_28_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_28_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_28_0_0);
g.setClip(clip__0_1_0_0_0_0_0_28_0_0);
float alpha__0_1_0_0_0_0_0_28_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_28_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_28_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_28_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31011.0, 77554.0);
((GeneralPath)shape).lineTo(69997.0, 53144.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_28_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_28_0_1);
g.setClip(clip__0_1_0_0_0_0_0_28_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_28_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_28_0);
g.setClip(clip__0_1_0_0_0_0_0_28_0);
origAlpha = alpha__0_1_0_0_0_0_0_28;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_28);
g.setClip(clip__0_1_0_0_0_0_0_28);
float alpha__0_1_0_0_0_0_0_29 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_29 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_29 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_29 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_29_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_29_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_29_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_29_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_29_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_29_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_29_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_29_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_29_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_29_0_0);
g.setClip(clip__0_1_0_0_0_0_0_29_0_0);
float alpha__0_1_0_0_0_0_0_29_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_29_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_29_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_29_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(31391.0, 78207.0);
((GeneralPath)shape).lineTo(85790.0, 52946.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_29_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_29_0_1);
g.setClip(clip__0_1_0_0_0_0_0_29_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_29_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_29_0);
g.setClip(clip__0_1_0_0_0_0_0_29_0);
origAlpha = alpha__0_1_0_0_0_0_0_29;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_29);
g.setClip(clip__0_1_0_0_0_0_0_29);
float alpha__0_1_0_0_0_0_0_30 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_30 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_30 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_30 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_30_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_30_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_30_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_30_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_30_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_30_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_30_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_30_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_30_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_30_0_0);
g.setClip(clip__0_1_0_0_0_0_0_30_0_0);
float alpha__0_1_0_0_0_0_0_30_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_30_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_30_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_30_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(37698.0, 76651.0);
((GeneralPath)shape).lineTo(13863.0, 54272.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_30_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_30_0_1);
g.setClip(clip__0_1_0_0_0_0_0_30_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_30_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_30_0);
g.setClip(clip__0_1_0_0_0_0_0_30_0);
origAlpha = alpha__0_1_0_0_0_0_0_30;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_30);
g.setClip(clip__0_1_0_0_0_0_0_30);
float alpha__0_1_0_0_0_0_0_31 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_31 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_31 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_31 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_31_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_31_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_31_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_31_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_31_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_31_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_31_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_31_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_31_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_31_0_0);
g.setClip(clip__0_1_0_0_0_0_0_31_0_0);
float alpha__0_1_0_0_0_0_0_31_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_31_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_31_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_31_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(39265.0, 75526.0);
((GeneralPath)shape).lineTo(28468.0, 55399.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_31_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_31_0_1);
g.setClip(clip__0_1_0_0_0_0_0_31_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_31_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_31_0);
g.setClip(clip__0_1_0_0_0_0_0_31_0);
origAlpha = alpha__0_1_0_0_0_0_0_31;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_31);
g.setClip(clip__0_1_0_0_0_0_0_31);
float alpha__0_1_0_0_0_0_0_32 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_32 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_32 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_32 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_32_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_32_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_32_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_32_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_32_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_32_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_32_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_32_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_32_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_32_0_0);
g.setClip(clip__0_1_0_0_0_0_0_32_0_0);
float alpha__0_1_0_0_0_0_0_32_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_32_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_32_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_32_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(42027.0, 74865.0);
((GeneralPath)shape).lineTo(41880.0, 56058.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_32_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_32_0_1);
g.setClip(clip__0_1_0_0_0_0_0_32_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_32_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_32_0);
g.setClip(clip__0_1_0_0_0_0_0_32_0);
origAlpha = alpha__0_1_0_0_0_0_0_32;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_32);
g.setClip(clip__0_1_0_0_0_0_0_32);
float alpha__0_1_0_0_0_0_0_33 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_33 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_33 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_33 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_33_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_33_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_33_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_33_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_33_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_33_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_33_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_33_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_33_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_33_0_0);
g.setClip(clip__0_1_0_0_0_0_0_33_0_0);
float alpha__0_1_0_0_0_0_0_33_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_33_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_33_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_33_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(44906.0, 75543.0);
((GeneralPath)shape).lineTo(55637.0, 55833.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_33_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_33_0_1);
g.setClip(clip__0_1_0_0_0_0_0_33_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_33_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_33_0);
g.setClip(clip__0_1_0_0_0_0_0_33_0);
origAlpha = alpha__0_1_0_0_0_0_0_33;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_33);
g.setClip(clip__0_1_0_0_0_0_0_33);
float alpha__0_1_0_0_0_0_0_34 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_34 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_34 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_34 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_34_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_34_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_34_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_34_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_34_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_34_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_34_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_34_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_34_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_34_0_0);
g.setClip(clip__0_1_0_0_0_0_0_34_0_0);
float alpha__0_1_0_0_0_0_0_34_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_34_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_34_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_34_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(46453.0, 76665.0);
((GeneralPath)shape).lineTo(70728.0, 54030.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_34_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_34_0_1);
g.setClip(clip__0_1_0_0_0_0_0_34_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_34_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_34_0);
g.setClip(clip__0_1_0_0_0_0_0_34_0);
origAlpha = alpha__0_1_0_0_0_0_0_34;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_34);
g.setClip(clip__0_1_0_0_0_0_0_34);
float alpha__0_1_0_0_0_0_0_35 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_35 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_35 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_35 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_35_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_35_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_35_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_35_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_35_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_35_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_35_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_35_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_35_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_35_0_0);
g.setClip(clip__0_1_0_0_0_0_0_35_0_0);
float alpha__0_1_0_0_0_0_0_35_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_35_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_35_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_35_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(47204.0, 77588.0);
((GeneralPath)shape).lineTo(86147.0, 53564.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_35_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_35_0_1);
g.setClip(clip__0_1_0_0_0_0_0_35_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_35_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_35_0);
g.setClip(clip__0_1_0_0_0_0_0_35_0);
origAlpha = alpha__0_1_0_0_0_0_0_35;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_35);
g.setClip(clip__0_1_0_0_0_0_0_35);
float alpha__0_1_0_0_0_0_0_36 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_36 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_36 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_36 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_36_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_36_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_36_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_36_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_36_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_36_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_36_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_36_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_36_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_36_0_0);
g.setClip(clip__0_1_0_0_0_0_0_36_0_0);
float alpha__0_1_0_0_0_0_0_36_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_36_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_36_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_36_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(14597.0, 53386.0);
((GeneralPath)shape).lineTo(53606.0, 77989.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_36_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_36_0_1);
g.setClip(clip__0_1_0_0_0_0_0_36_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_36_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_36_0);
g.setClip(clip__0_1_0_0_0_0_0_36_0);
origAlpha = alpha__0_1_0_0_0_0_0_36;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_36);
g.setClip(clip__0_1_0_0_0_0_0_36);
float alpha__0_1_0_0_0_0_0_37 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_37 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_37 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_37 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_37_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_37_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_37_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_37_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_37_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_37_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_37_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_37_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_37_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_37_0_0);
g.setClip(clip__0_1_0_0_0_0_0_37_0_0);
float alpha__0_1_0_0_0_0_0_37_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_37_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_37_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_37_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30033.0, 54275.0);
((GeneralPath)shape).lineTo(54339.0, 77106.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_37_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_37_0_1);
g.setClip(clip__0_1_0_0_0_0_0_37_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_37_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_37_0);
g.setClip(clip__0_1_0_0_0_0_0_37_0);
origAlpha = alpha__0_1_0_0_0_0_0_37;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_37);
g.setClip(clip__0_1_0_0_0_0_0_37);
float alpha__0_1_0_0_0_0_0_38 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_38 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_38 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_38 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_38_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_38_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_38_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_38_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_38_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_38_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_38_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_38_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_38_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_38_0_0);
g.setClip(clip__0_1_0_0_0_0_0_38_0_0);
float alpha__0_1_0_0_0_0_0_38_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_38_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_38_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_38_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(44667.0, 55382.0);
((GeneralPath)shape).lineTo(55872.0, 75992.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_38_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_38_0_1);
g.setClip(clip__0_1_0_0_0_0_0_38_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_38_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_38_0);
g.setClip(clip__0_1_0_0_0_0_0_38_0);
origAlpha = alpha__0_1_0_0_0_0_0_38;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_38);
g.setClip(clip__0_1_0_0_0_0_0_38);
float alpha__0_1_0_0_0_0_0_39 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_39 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_39 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_39 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_39_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_39_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_39_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_39_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_39_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_39_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_39_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_39_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_39_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_39_0_0);
g.setClip(clip__0_1_0_0_0_0_0_39_0_0);
float alpha__0_1_0_0_0_0_0_39_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_39_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_39_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_39_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(58518.0, 56512.0);
((GeneralPath)shape).lineTo(58665.0, 75319.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_39_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_39_0_1);
g.setClip(clip__0_1_0_0_0_0_0_39_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_39_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_39_0);
g.setClip(clip__0_1_0_0_0_0_0_39_0);
origAlpha = alpha__0_1_0_0_0_0_0_39;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_39);
g.setClip(clip__0_1_0_0_0_0_0_39);
float alpha__0_1_0_0_0_0_0_40 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_40 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_40 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_40 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_40_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_40_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_40_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_40_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_40_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_40_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_40_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_40_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_40_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_40_0_0);
g.setClip(clip__0_1_0_0_0_0_0_40_0_0);
float alpha__0_1_0_0_0_0_0_40_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_40_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_40_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_40_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(72359.0, 55195.0);
((GeneralPath)shape).lineTo(61464.0, 75955.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_40_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_40_0_1);
g.setClip(clip__0_1_0_0_0_0_0_40_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_40_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_40_0);
g.setClip(clip__0_1_0_0_0_0_0_40_0);
origAlpha = alpha__0_1_0_0_0_0_0_40;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_40);
g.setClip(clip__0_1_0_0_0_0_0_40);
float alpha__0_1_0_0_0_0_0_41 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_41 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_41 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_41 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_41_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_41_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_41_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_41_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_41_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_41_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_41_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_41_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_41_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_41_0_0);
g.setClip(clip__0_1_0_0_0_0_0_41_0_0);
float alpha__0_1_0_0_0_0_0_41_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_41_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_41_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_41_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(86929.0, 54513.0);
((GeneralPath)shape).lineTo(63063.0, 77088.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_41_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_41_0_1);
g.setClip(clip__0_1_0_0_0_0_0_41_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_41_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_41_0);
g.setClip(clip__0_1_0_0_0_0_0_41_0);
origAlpha = alpha__0_1_0_0_0_0_0_41;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_41);
g.setClip(clip__0_1_0_0_0_0_0_41);
float alpha__0_1_0_0_0_0_0_42 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_42 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_42 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_42 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_42_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_42_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_42_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_42_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_42_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_42_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_42_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_42_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_42_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_42_0_0);
g.setClip(clip__0_1_0_0_0_0_0_42_0_0);
float alpha__0_1_0_0_0_0_0_42_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_42_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_42_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_42_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(14994.0, 52704.0);
((GeneralPath)shape).lineTo(69848.0, 77994.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_42_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_42_0_1);
g.setClip(clip__0_1_0_0_0_0_0_42_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_42_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_42_0);
g.setClip(clip__0_1_0_0_0_0_0_42_0);
origAlpha = alpha__0_1_0_0_0_0_0_42;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_42);
g.setClip(clip__0_1_0_0_0_0_0_42);
float alpha__0_1_0_0_0_0_0_43 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_43 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_43 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_43 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_43_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_43_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_43_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_43_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_43_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_43_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_43_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_43_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_43_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_43_0_0);
g.setClip(clip__0_1_0_0_0_0_0_43_0_0);
float alpha__0_1_0_0_0_0_0_43_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_43_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_43_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_43_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(30815.0, 53317.0);
((GeneralPath)shape).lineTo(70198.0, 77382.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_43_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_43_0_1);
g.setClip(clip__0_1_0_0_0_0_0_43_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_43_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_43_0);
g.setClip(clip__0_1_0_0_0_0_0_43_0);
origAlpha = alpha__0_1_0_0_0_0_0_43;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_43);
g.setClip(clip__0_1_0_0_0_0_0_43);
float alpha__0_1_0_0_0_0_0_44 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_44 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_44 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_44 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_44_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_44_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_44_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_44_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_44_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_44_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_44_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_44_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_44_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_44_0_0);
g.setClip(clip__0_1_0_0_0_0_0_44_0_0);
float alpha__0_1_0_0_0_0_0_44_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_44_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_44_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_44_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(46280.0, 54197.0);
((GeneralPath)shape).lineTo(70903.0, 76502.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_44_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_44_0_1);
g.setClip(clip__0_1_0_0_0_0_0_44_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_44_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_44_0);
g.setClip(clip__0_1_0_0_0_0_0_44_0);
origAlpha = alpha__0_1_0_0_0_0_0_44;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_44);
g.setClip(clip__0_1_0_0_0_0_0_44);
float alpha__0_1_0_0_0_0_0_45 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_45 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_45 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_45 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_45_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_45_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_45_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_45_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_45_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_45_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_45_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_45_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_45_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_45_0_0);
g.setClip(clip__0_1_0_0_0_0_0_45_0_0);
float alpha__0_1_0_0_0_0_0_45_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_45_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_45_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_45_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(61389.0, 55793.0);
((GeneralPath)shape).lineTo(72431.0, 75359.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_45_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_45_0_1);
g.setClip(clip__0_1_0_0_0_0_0_45_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_45_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_45_0);
g.setClip(clip__0_1_0_0_0_0_0_45_0);
origAlpha = alpha__0_1_0_0_0_0_0_45;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_45);
g.setClip(clip__0_1_0_0_0_0_0_45);
float alpha__0_1_0_0_0_0_0_46 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_46 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_46 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_46 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_46_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_46_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_46_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_46_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_46_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_46_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_46_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_46_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_46_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_46_0_0);
g.setClip(clip__0_1_0_0_0_0_0_46_0_0);
float alpha__0_1_0_0_0_0_0_46_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_46_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_46_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_46_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(75157.0, 55833.0);
((GeneralPath)shape).lineTo(75304.0, 74640.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_46_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_46_0_1);
g.setClip(clip__0_1_0_0_0_0_0_46_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_46_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_46_0);
g.setClip(clip__0_1_0_0_0_0_0_46_0);
origAlpha = alpha__0_1_0_0_0_0_0_46;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_46);
g.setClip(clip__0_1_0_0_0_0_0_46);
float alpha__0_1_0_0_0_0_0_47 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_47 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_47 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_47 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_47_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_47_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_47_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_47_0 is CompositeGraphicsNode
float alpha__0_1_0_0_0_0_0_47_0_0 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_47_0_0 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_47_0_0 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_47_0_0 is ShapeNode
origAlpha = alpha__0_1_0_0_0_0_0_47_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_47_0_0);
g.setClip(clip__0_1_0_0_0_0_0_47_0_0);
float alpha__0_1_0_0_0_0_0_47_0_1 = origAlpha;
origAlpha = origAlpha * 1.0f;
g.setComposite(AlphaComposite.getInstance(3, origAlpha));
Shape clip__0_1_0_0_0_0_0_47_0_1 = g.getClip();
AffineTransform defaultTransform__0_1_0_0_0_0_0_47_0_1 = g.getTransform();
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1_0_0_0_0_0_47_0_1 is ShapeNode
paint = getColor(0, 0, 0, 255, disabled);
stroke = new BasicStroke(422.0f,0,0,4.0f,null,0.0f);
shape = new GeneralPath();
((GeneralPath)shape).moveTo(88511.0, 55641.0);
((GeneralPath)shape).lineTo(78119.0, 75285.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
origAlpha = alpha__0_1_0_0_0_0_0_47_0_1;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_47_0_1);
g.setClip(clip__0_1_0_0_0_0_0_47_0_1);
origAlpha = alpha__0_1_0_0_0_0_0_47_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_47_0);
g.setClip(clip__0_1_0_0_0_0_0_47_0);
origAlpha = alpha__0_1_0_0_0_0_0_47;
g.setTransform(defaultTransform__0_1_0_0_0_0_0_47);
g.setClip(clip__0_1_0_0_0_0_0_47);
origAlpha = alpha__0_1_0_0_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0_0);
g.setClip(clip__0_1_0_0_0_0_0);
origAlpha = alpha__0_1_0_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0_0);
g.setClip(clip__0_1_0_0_0_0);
origAlpha = alpha__0_1_0_0_0;
g.setTransform(defaultTransform__0_1_0_0_0);
g.setClip(clip__0_1_0_0_0);
origAlpha = alpha__0_1_0_0;
g.setTransform(defaultTransform__0_1_0_0);
g.setClip(clip__0_1_0_0);
origAlpha = alpha__0_1_0;
g.setTransform(defaultTransform__0_1_0);
g.setClip(clip__0_1_0);
origAlpha = alpha__0_1;
g.setTransform(defaultTransform__0_1);
g.setClip(clip__0_1);
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
        return 110;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 431;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static int getOrigWidth() {
		return 3780;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static int getOrigHeight() {
		return 3780;
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

