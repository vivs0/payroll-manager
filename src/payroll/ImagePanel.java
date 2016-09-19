package payroll;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
	private Image image;
	private int w,h,x,y;
	public ImagePanel(Image i,int x, int y, int w, int h)
	{
		this.image=i;
		this.w=w;
		this.h=h;
		this.x=x;
		this.y=y;
	}
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, x, y, w,h, null);
	}
}
