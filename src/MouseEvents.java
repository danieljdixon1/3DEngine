import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

public class MouseEvents implements MouseListener {
	public static Robot rob;
	MouseEvents(){
		try {
			rob = new Robot();
		}catch (AWTException  e){
			System.out.println();
		}
	}
	public static int get_mouseposx(){
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		SwingUtilities.convertPointFromScreen(b, Main.displayobj);
		return ((int)b.getX());
	}
	public static int get_mouseposy(){
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		SwingUtilities.convertPointFromScreen(b, Main.displayobj);
		return ((int)b.getY());
	}
	public static void set_mouseposxy(int x, int y){
		Point po=new Point(x, y);
		SwingUtilities.convertPointToScreen(po, Main.displayobj);
		try{
			rob.mouseMove((int)po.getX(), (int)po.getY());
		}catch (NullPointerException e){
			System.out.println("error here");
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}


}
