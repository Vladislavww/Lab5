package task4;

import java.awt.BasicStroke; 
import java.awt.Color; 
import java.awt.Font;
import java.awt.Graphics; 
import java.awt.Graphics2D; 
import java.awt.Paint; 
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath; 
import java.awt.geom.Line2D; 
import java.awt.geom.Point2D; 
import java.awt.geom.Rectangle2D; 
import javax.swing.JPanel;
import java.util.Stack;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel implements MouseMotionListener{
	// Список координат точек для построения графика
	private Double[][] graphicsData; 
	// Флаговые переменные, задающие правила отображения графика
	private boolean showAxis = true; 
	private boolean showMarkers = true; 
	private boolean showZone = false; 
	boolean rotateSystem = false;
	// Границы диапазона пространства, подлежащего отображению
	private double minX;
	private double maxX; 
	private double minY; 
	private double maxY; 
	// Используемый масштаб отображения 
	private double scale; 
	private Double[][] zone_points;
	// Различные стили черчения линий
	private BasicStroke graphicsStroke; 
	private BasicStroke axisStroke;
	private BasicStroke markerStroke;
	// Различные шрифты отображения надписей 
	private Font axisFont;
	Graphics2D canvas = null;
	private double mouse_x;
	private double mouse_y;
	private boolean bold = false;
	private boolean mouse_pressed = false;
	private MouseClass mouse = new MouseClass();
	private double[] start_coord = new double[2];
	private double[] finish_coord = new double[2];
	private boolean scaling_mode = false;
	private int point_number;
	private boolean moving_mode = false;
	
	
	public GraphicsDisplay(){ 
		// Цвет заднего фона области отображения - белый 
		setBackground(Color.WHITE); 
		// Сконструировать необходимые объекты, используемые в рисовании 
		// Перо для рисования графика 
		graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {20, 5, 5, 10, 10}, 0.0f); 
		// Перо для рисования осей координат 
		axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f); 
		// Перо для рисования контуров маркеров
		markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f); 
		// Шрифт для подписей осей координат
		axisFont = new Font("Serif", Font.BOLD, 24);
		//MouseClass m = new MouseClass();
		addMouseListener(mouse);
		addMouseMotionListener(this);
		start_coord[0] = -1;
		start_coord[1] = -1;
		finish_coord[0] = -1;
		finish_coord[1] = -1;
	}
	public void showGraphics(Double[][] graphicsData){ 
		// Сохранить массив точек во внутреннем поле класса 
		this.graphicsData = graphicsData; 
		repaint(); 
	} // Методы-модификаторы для изменения параметров отображения графика // Изменение любого параметра приводит к перерисовке области 
	public void setShowAxis(boolean showAxis){ 
		this.showAxis = showAxis; 
		repaint(); 
	} 
	public void setShowMarkers(boolean showMarkers){
		this.showMarkers = showMarkers; 
		repaint();
	}
	public void setShowZone(boolean showZone){
		this.showZone = showZone; 
		repaint();
	}
	public void setRotateSystem(boolean rotateSystem){
		this.rotateSystem = rotateSystem;
		repaint();
	}
	public void show_mouse_coord(){
		repaint();
	}
	public void paintComponent(Graphics g){
		/* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона *
		 *  Эта функциональность - единственное, что осталось в наследство от *
		 *   paintComponent класса JPanel */ 
		super.paintComponent(g); 
		// Шаг 2 - Если данные графика не загружены (при показе компонента при запуске программы) - ничего не делать
		if (graphicsData==null || graphicsData.length==0){
			return; 
		}
		
		// Шаг 3 - Определить минимальное и максимальное значения для координат X и Y 
		// Это необходимо для определения области пространства, подлежащей отображению 
		// Е? верхний левый угол это (minX, maxY) - правый нижний это (maxX, minY) 
		if(mouse.get_level() == 0){
			minX = graphicsData[0][0]; 
			maxX = graphicsData[graphicsData.length-1][0];
			minY = graphicsData[0][1]; 
			maxY = minY;
			for (int i = 1; i<graphicsData.length; i++){ 
				if (graphicsData[i][1]<minY){ 
					minY = graphicsData[i][1];
				} 
				if (graphicsData[i][1]>maxY){
					maxY = graphicsData[i][1]; 
				} 
			}
		}
		else{
			//System.out.println(zero.getX() + "  " + zero.getY());
			double[] temp = new double[2];
			//temp = (double[]) start_st.peek();
			temp = mouse.get(0);
			minX = temp[0];
			minY = temp[1];
			//temp = (double[]) finish_st.peek();
			temp = mouse.get(1);
			maxX = temp[0];
			maxY = temp[1];
			/*start_coord[0] = 0;
			start_coord[1] = 0;
			finish_coord[0] = getSize().getWidth();
			finish_coord[1] = getSize().getHeight();*/
		}
		/* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X и Y - сколько пикселов * приходится на единицу длины по X и по Y */ 
		double scaleX = getSize().getWidth() / (maxX - minX); 
		double scaleY = getSize().getHeight() / (maxY - minY);
		scale = Math.min(scaleX, scaleY); 
		// Шаг 6 - корректировка границ отображаемой области согласно выбранному масштабу 
		if (scale==scaleX){ 
			double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;
			maxY += yIncrement;
			minY -= yIncrement; 
			}
		if (scale==scaleY){ // Если за основу был взят масштаб по оси Y, действовать по аналогии
			double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
			maxX += xIncrement; 
			minX -= xIncrement;
		}
		// Шаг 7 - Сохранить текущие настройки холста 
		canvas = (Graphics2D) g; 
		Stroke oldStroke = canvas.getStroke(); 
		Color oldColor = canvas.getColor(); 
		Paint oldPaint = canvas.getPaint();
		Font oldFont = canvas.getFont(); 
		// Шаг 8 - В нужном порядке вызвать методы отображения элементов графика 
		// Порядок вызова методов имеет значение, т.к. предыдущий рисунок будет затираться последующим
		// Первыми (если нужно) отрисовываются оси координат. 
		func_rotateSystem(canvas);
		if (showAxis){
			paintAxis(canvas); 
		}
		// Затем отображается сам график 
		paintGraphics(canvas);
		// Затем (если нужно) отображаются маркеры точек, по которым строился график. 
		if (showMarkers){
			paintMarkers(canvas); 
		}
		if(showZone){
			paintZone(canvas);
			double Square = calculate_square();
			paintLabel(canvas, Square);
		}
		if(scaling_mode){
			paintRect(canvas);
		}
		if(bold){
			mouse_coord(canvas, mouse_x, mouse_y);
		}
		
		/*this.addMouseMotionListener(new MouseMotionListener(){
			Graphics2D canvas;
			public void mouseDragged(MouseEvent arg0) {
			}
			public void mouseMoved(MouseEvent arg0) {
				System.out.println(1);
				mouse_coord(canvas, arg0.getX(), arg0.getY());
				repaint();
				//mouse_x = arg0.getX();
			}
			public MouseMotionListener setParam(Graphics2D canvas){
				this.canvas = canvas;
				return this;
			}
		}.setParam(canvas));*/
		
		// Шаг 9 - Восстановить старые настройки холста
		canvas.setFont(oldFont);
		canvas.setPaint(oldPaint);
		canvas.setColor(oldColor);
		canvas.setStroke(oldStroke);
		
	}
	
	// Отрисовка графика по прочитанным координатам 
	protected void paintGraphics(Graphics2D canvas){ 
		// Выбрать линию для рисования графика 
		canvas.setStroke(graphicsStroke); 
		// Выбрать цвет линии 
		
		GeneralPath graphics = new GeneralPath(); 
		for (int i=0; i<graphicsData.length; i++){
			// Преобразовать значения (x,y) в точку на экране
			Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
			if (i>0){ 
				// Не первая итерация цикла - вести линию в точку point 
				graphics.lineTo(point.getX(), point.getY()); 
			} 
			else{ 
				// Первая итерация цикла - установить начало пути в точку point 
				graphics.moveTo(point.getX(), point.getY());
			}
			
		}
		// Отобразить график 
		canvas.setColor(Color.RED);
		canvas.draw(graphics);
	}
	
	protected void paintMarkers(Graphics2D canvas) {
		// Шаг 1 - Установить специальное перо для черчения контуров маркеров 
		canvas.setStroke(markerStroke);
		// Выбрать красный цвета для контуров маркеров 
		//canvas.setColor(Color.RED);
		// Выбрать красный цвет для закрашивания маркеров внутри 
		//canvas.setPaint(Color.RED);
		// Шаг 2 - Организовать цикл по всем точкам графика 
		for (Double[] point: graphicsData){ 
			// Инициализировать эллипс как объект для представления маркера 
			GeneralPath path = new GeneralPath();
			Point2D.Double center = xyToPoint(point[0], point[1]);	
			path.append(new Line2D.Double(shiftPoint(center, -5, 0), shiftPoint(center, 5, 0)), false);
			path.append(new Line2D.Double(shiftPoint(center, 5, -5), shiftPoint(center, -5, 5)), false);
			path.append(new Line2D.Double(shiftPoint(center, 0, -5), shiftPoint(center, 0, 5)), false);
			path.append(new Line2D.Double(shiftPoint(center, -5, -5), shiftPoint(center, 5, 5)), false);
			if(check_condition(point[1]) == true){
				canvas.setColor(Color.GREEN);
				canvas.setPaint(Color.GREEN);
			}
			else{
				canvas.setColor(Color.RED);
				canvas.setPaint(Color.RED);
			}
			canvas.draw(path);
			}
	}
	
	protected void paintAxis(Graphics2D canvas){
		// Установить особое начертание для осей 
		canvas.setStroke(axisStroke); 
		// Оси рисуются ч?рным цветом 
		canvas.setColor(Color.BLACK); 
		// Стрелки заливаются ч?рным цветом
		canvas.setPaint(Color.BLACK); 
		// Подписи к координатным осям делаются специальным шрифтом 
		canvas.setFont(axisFont);
		// Создать объект контекста отображения текста - для получения характеристик устройства (экрана) 
		FontRenderContext context = canvas.getFontRenderContext(); 
		// Определить, должна ли быть видна ось Y на графике 
		if (minX<=0.0 && maxX>=0.0){
			canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
			// Стрелка оси Y 
			GeneralPath arrow = new GeneralPath();
			// Установить начальную точку ломаной точно на верхний конец оси Y 
			Point2D.Double lineEnd = xyToPoint(0, maxY);
			arrow.moveTo(lineEnd.getX(), lineEnd.getY()); 
			// Вести левый "скат" стрелки в точку с относительными координатами (5,20)
			arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20); 
			// Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
			arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());
			// Замкнуть треугольник стрелки 
			arrow.closePath(); 
			canvas.draw(arrow);
			// Нарисовать стрелку 
			canvas.fill(arrow); 
			// Закрасить стрелку 
			// Нарисовать подпись к оси Y 
			// Определить, сколько места понадобится для надписи "y" 
			Rectangle2D bounds = axisFont.getStringBounds("y", context);
			Point2D.Double labelPos = xyToPoint(0, maxY); 
			// Вывести надпись в точке с вычисленными координатами 
			canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() - bounds.getY()));
			}
		// Определить, должна ли быть видна ось X на графике 
		if (minY<=0.0 && maxY>=0.0){
			// Она должна быть видна, если верхняя граница показываемой области (maxX) >= 0.0, // а нижняя (minY) <= 0.0
			canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
			// Стрелка оси X 
			GeneralPath arrow = new GeneralPath();
			// Установить начальную точку ломаной точно на правый конец оси X 
			Point2D.Double lineEnd = xyToPoint(maxX, 0);
			arrow.moveTo(lineEnd.getX(), lineEnd.getY());
			// Вести верхний "скат" стрелки в точку с относительными координатами (-20,-5)
			arrow.lineTo(arrow.getCurrentPoint().getX()-20, arrow.getCurrentPoint().getY()-5); 
			// Вести левую часть стрелки в точку с относительными координатами (0, 10)
			arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY()+10); 
			// Замкнуть треугольник стрелки
			arrow.closePath(); 
			canvas.draw(arrow);
			// Нарисовать стрелку 
			canvas.fill(arrow);
			// Закрасить стрелку // Нарисовать подпись к оси X // Определить, сколько места понадобится для надписи "x"
			Rectangle2D bounds = axisFont.getStringBounds("x", context);
			Point2D.Double labelPos = xyToPoint(maxX, 0);
			// Вывести надпись в точке с вычисленными координатами 
			canvas.drawString("x", (float)(labelPos.getX() - bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
		}
	}
	
	private double calculate_square(){
		double Square = 0;
		for(int i=1; i< zone_points.length; i++){
			if(zone_points[i][0] == null){ //в последней части этой переменной могут стоять сплошные null
				break;
			}
			Square += Math.abs(zone_points[i-1][1]+zone_points[i][1])/2*Math.abs(zone_points[i][0]-zone_points[i-1][0]);
		}
		return Square;
	}
	protected void paintZone(Graphics2D canvas){
		zone_points = find_zone_points();
		GeneralPath graphics = new GeneralPath();
		Point2D.Double point = xyToPoint(zone_points[0][0], 0);
		graphics.moveTo(point.getX(), point.getY());
		int iter = 0;
		for(int i=0; i< zone_points.length; i++){
			if(zone_points[i][0] == null){ //в последней части этой переменной могут стоять сплошные null
				break;
			}
			point = xyToPoint(zone_points[i][0], zone_points[i][1]);
			graphics.lineTo(point.getX(), point.getY()); 
			iter += 1;
		}
		point = xyToPoint(zone_points[iter-1][0], 0);
		graphics.lineTo(point.getX(), point.getY());
		canvas.setColor(Color.BLUE);
		canvas.fill(graphics);
	}
	
	private void paintLabel(Graphics2D canvas, double number){
		Double temp = number;
		String str = temp.toString();
		double[] center = new double[2];
		center[0] = center[1] = 0;
		int iter = 0;
		for(int i=0; i<zone_points.length; i++){
			if(zone_points[i][0] == null){ //в последней части этой переменной могут стоять сплошные null
				break;
			}
			center[0] += zone_points[i][0];
			center[1] += zone_points[i][1];
			iter += 1;
		}
		if(iter <= 1){
			return;
		}
		center[0] /= iter-1;
		center[1] /= iter-1;
		FontRenderContext context = canvas.getFontRenderContext();
		Rectangle2D bounds = axisFont.getStringBounds(str, context);
		Point2D.Double labelPos = xyToPoint(center[0], center[1]); 
		// Вывести надпись в точке с вычисленными координатами 
		canvas.setColor(Color.BLACK);
		canvas.drawString(str, (float)(labelPos.getX() - bounds.getWidth()/2), (float)(labelPos.getY() - bounds.getHeight()/2));
	}
	private void paintRect(Graphics2D canvas){
		BasicStroke graphicsStroke0 = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {20, 5}, 0.0f);
		canvas.setStroke(graphicsStroke0);
		canvas.setColor(Color.BLACK);
		canvas.drawRect((int)start_coord[0], (int)start_coord[1], (int)(finish_coord[0]-start_coord[0]), (int)(finish_coord[1]-start_coord[1]));
	}
	protected Point2D.Double xyToPoint(double x, double y){ 
		// Вычисляем смещение X от самой левой точки (minX) 
		double deltaX = x - minX; 
		// Вычисляем смещение Y от точки верхней точки (maxY) 
		double deltaY = maxY - y; 
		return new Point2D.Double(deltaX*scale, deltaY*scale); 
	}
	
	protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY){ 
		// Инициализировать новый экземпляр точки 
		Point2D.Double dest = new Point2D.Double(); 
		// Задать е? координаты как координаты существующей точки + заданные смещения 
		dest.setLocation(src.getX() + deltaX, src.getY() + deltaY); 
		return dest;
	}
	
	private boolean check_condition(double input){
		Double number = input;
		String str = number.toString();
		int length = str.length();
		short to_compare0;
		short to_compare1;
		for(int i=1; i<length; i++){
			if(str.charAt(i) == '.'){
				return true;
			}
			else{
				to_compare0 = (short)str.charAt(i-1);
				to_compare1 = (short)str.charAt(i);
				if(to_compare0 >= to_compare1){
					return false;
				}
			}
		}
		return true;
	}
	
	private Double[][] find_zone_points(){
		Double[][] zone_points = new Double[graphicsData.length][2];
		int j = 0;
		short side = (short)graphicsData[0][1].compareTo((double)0); //1 - выше Ох  -1 - ниже Ox
		short prev;
		boolean to_write = false;
		for(int i=0; i<graphicsData.length; i++){
			prev = side;
			side = (short)graphicsData[i][1].compareTo((double)0);
			if(prev != side){
				if(!to_write){
					to_write = true;
				}
				else{
					to_write = false;
				}
			}
			if(to_write){
				zone_points[j][0] = graphicsData[i][0];
				zone_points[j][1] = graphicsData[i][1];
				j += 1;
			}
		}
		return zone_points;
	}
	
	private void func_rotateSystem(Graphics2D canvas){
		if(rotateSystem == true){
			canvas.translate(getSize().getWidth()*0.2, getSize().getHeight()*1.1);
			canvas.rotate(Math.PI/2*3);
		}
	}
	private boolean check_markers(int x, int y, int radius){
		Point2D.Double m_point = xyToPoint(x, y);
		for(int i=0; i<graphicsData.length; i++){
			Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
			if(Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2) < radius){
				mouse_x = graphicsData[i][0];
				mouse_y = graphicsData[i][1];
				point_number = i;
				return true;
			}
		}
		return false;
	}
	private void move_point(double x, double y){
		Point2D.Double zero = xyToPoint(0, 0);
		graphicsData[point_number][0] = (x - zero.getX())/scale;
		graphicsData[point_number][1] = (zero.getY() - y)/scale;
	}
	public void mouse_coord(Graphics2D canvas, double x, double y){ //в этой фуекции рисую координаты, связанные с мышью
		Double temp0 = x;
		Double temp1 = y;
		Point2D.Double Pos = xyToPoint(x, y); 
		canvas.setColor(Color.BLACK);
		canvas.drawString("X:"+x+" Y:"+y, (float)Pos.getX(), (float)Pos.getY());
	}
	public void mouseDragged(MouseEvent arg0) {
		if(check_markers(arg0.getX(), arg0.getY(), 400) && scaling_mode == false){
			move_point(arg0.getX(), arg0.getY());
			moving_mode = true;
			repaint();
		}
		else if(moving_mode == false){
			mouse_pressed = mouse.getPressed();
			finish_coord[0] = arg0.getX();
			finish_coord[1] = arg0.getY();
			scaling_mode = true;
			repaint();
		}
		
	}
	public void mouseMoved(MouseEvent arg0) {
		if(canvas != null){
			if(check_markers(arg0.getX(), arg0.getY(), 200)){
				bold = true;
				show_mouse_coord();
			}
			else{
				if(bold == true){
					show_mouse_coord();
				}
				bold = false;
				
			}
			mouse_pressed = mouse.getPressed();
		}
	}
	private void change_scale(){
		Point2D.Double zero = xyToPoint(0, 0);
		double minX0 = (start_coord[0] - zero.getX())/scale;
		double minY0 = (zero.getY() - finish_coord[1])/scale;
		double maxX0 = (finish_coord[0] - zero.getX())/scale;
		double maxY0 = (zero.getY() - start_coord[1])/scale;
		double[] start = new double[] {minX0, minY0};
		double[] finish = new double[] {maxX0, maxY0};
		mouse.add(start, finish);
		repaint();
		
	}
	public class MouseClass extends MouseAdapter{
		private boolean pressed = false;
		private Stack start_st = new Stack(); //содержание координат левого верхнего угла
		private Stack finish_st = new Stack(); //содержание координат правого нижнего угла
		private int stack_level = 0;
		private boolean lev_changed = false;
		public void mousePressed(MouseEvent e){
			if(e.getButton() == 1){
				start_coord[0] = e.getX();
				start_coord[1] = e.getY();
			}
			pressed = true;
		}
		
		public void mouseReleased(MouseEvent e){
			if(e.getButton() != 3 && scaling_mode == true){
				scaling_mode = false;
				change_scale();
				repaint();
			}
			pressed = false;
			moving_mode = false;
		}
		
		public void mouseClicked(MouseEvent e){
			if(e.getButton() ==3){
				if(stack_level > 0){
					start_st.pop();
					finish_st.pop();
					stack_level -= 1;
					repaint();
				}
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
	
	public Double[][] export_data(){
		return graphicsData;
	}

}
	
			