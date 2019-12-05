package task4;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;

public class MouseClass extends MouseAdapter{
	private boolean pressed = false;
	public void mousePressed(MouseEvent e){
		pressed = true;
	}
	
	public void mouseReleased(MouseEvent e){
		pressed = false;
	}
	
	public void mouseClicked(MouseEvent e){
		System.out.println(7);
	}
	public boolean getPressed(){
		return pressed;
	}
}
