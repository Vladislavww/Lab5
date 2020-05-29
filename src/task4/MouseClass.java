/*package task4;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.util.Stack;

public class MouseClass extends MouseAdapter{
	private boolean pressed = false;
	private Stack start_st = new Stack();
	private Stack finish_st = new Stack();
	private int stack_level = 0;
	private boolean lev_changed = false;
	public void mousePressed(MouseEvent e){
		pressed = true;
	}
	
	public void mouseReleased(MouseEvent e){
		pressed = false;
	}
	
	public void mouseClicked(MouseEvent e){
		if(stack_level > 0){
			start_st.pop();
			finish_st.pop();
			stack_level -= 1;
		}
	}
	public boolean getPressed(){
		return pressed;
	}
	public void add(double[] start, double[] finish){
		start_st.add(start);
		finish_st.add(finish);
		stack_level += 1;
		
	}
	public double[] get(int num){
		if(num == 0){
			return (double[]) start_st.peek();
		}
		else{
			return (double[]) finish_st.peek();
		}
	}
	public int get_level(){
		return stack_level;
	}
	public boolean level_changed(){
		boolean result = lev_changed;
		lev_changed = false;
		return result;
	}
}
*/