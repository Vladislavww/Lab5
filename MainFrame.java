package task4;

import java.awt.BorderLayout; 
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction; 
import javax.swing.Action; 
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser; 
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane; 
import javax.swing.event.MenuEvent; 
import javax.swing.event.MenuListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;


public class MainFrame extends JFrame{
	// ��������� ������� ���� ���������� 
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600; 
	// ������ ����������� ���� ��� ������ ������
	private JFileChooser fileChooser = null; 
	// ������ ���� private
	private JCheckBoxMenuItem showAxisMenuItem;
	private JCheckBoxMenuItem showMarkersMenuItem;
	private JCheckBoxMenuItem showGraphicsSizeMenuItem;
	private JCheckBoxMenuItem rotateSystemMenuItem;
	// ���������-������������ ������� 
	private GraphicsDisplay display = new GraphicsDisplay();
	// ����, ����������� �� ������������� ������ �������
	private boolean fileLoaded = false;
	
	public MainFrame() { 
		// ����� ������������ ������ Frame
		super("���������� �������� ������� �� ������ ������� �������������� ������"); 
		// ��������� �������� ���� 
		setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		// �������������� ���� ���������� �� ������
		setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2); 
		// ����?�������� ���� �� ���� �����
		setExtendedState(MAXIMIZED_BOTH);
		// ������� � ���������� ������ ���� 
		JMenuBar menuBar = new JMenuBar(); 
		setJMenuBar(menuBar);
		// �������� ����� ���� "����" 
		JMenu fileMenu = new JMenu("����");
		menuBar.add(fileMenu); 
		// ������� �������� �� �������� �����
		Action openGraphicsAction = new AbstractAction("������� ���� � ��������"){
			public void actionPerformed(ActionEvent event){ 
				if (fileChooser==null){ 
					fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
				}
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
					openGraphics(fileChooser.getSelectedFile());
				}
			}};
		// �������� ��������������� ������� ���� 
		fileMenu.add(openGraphicsAction);
		// ������� ����� ���� "������"
		JMenu graphicsMenu = new JMenu("������");
		menuBar.add(graphicsMenu);
		// ������� �������� ��� ������� �� ��������� �������� "���������� ��� ���������" 
		Action showAxisAction = new AbstractAction("���������� ��� ���������") { 
			public void actionPerformed(ActionEvent event) { 
				// �������� showAxis ������ GraphicsDisplay ������, ���� ������� ���� 
				// showAxisMenuItem ������� �������, � ���� - � ��������� ������ 
				display.setShowAxis(showAxisMenuItem.isSelected());
			} 
		}; 
		showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction); 
		// �������� ��������������� ������� � ���� 
		graphicsMenu.add(showAxisMenuItem);
		// ������� �� ��������� ������� (������� �������)
		showAxisMenuItem.setSelected(true); 
		// ��������� �������� ��� �������� "���������� ������� �����" 
		Action showMarkersAction = new AbstractAction("���������� ������� �����") {
			public void actionPerformed(ActionEvent event) {
				// �� �������� � showAxisMenuItem 
				display.setShowMarkers(showMarkersMenuItem.isSelected()); 
			}
		}; 
		showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction); 
		graphicsMenu.add(showMarkersMenuItem);
		showMarkersMenuItem.setSelected(true);
		// ���������������� ���������� �������, ��������� � ���� "������" 
		graphicsMenu.addMenuListener(new GraphicsMenuListener());
		// ���������� GraphicsDisplay � ���� ��������� ���������� 
		
		Action showGraphicsSizeAction = new AbstractAction("�������� ��������� �������") {
			public void actionPerformed(ActionEvent event) {
				display.setShowZone(showGraphicsSizeMenuItem.isSelected()); 
			}
		};
		showGraphicsSizeMenuItem = new JCheckBoxMenuItem(showGraphicsSizeAction);
		graphicsMenu.add(showGraphicsSizeMenuItem);
		showGraphicsSizeMenuItem.setSelected(false);
		Action rotateSystemAction = new AbstractAction("��������� ��� �� 90 �������� �����") {
			public void actionPerformed(ActionEvent event) {
				display.setRotateSystem(rotateSystemMenuItem.isSelected()); 
			}
		};
		rotateSystemMenuItem = new JCheckBoxMenuItem(rotateSystemAction);
		graphicsMenu.add(rotateSystemMenuItem);
		rotateSystemMenuItem.setSelected(false);
		getContentPane().add(display, BorderLayout.CENTER); 
		
		
	}
	
	protected void openGraphics(File selectedFile){
		try { 
			// ��� 1 - ������� ����� ������ ������, ��������� � ������� �������� ������� 
			DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
			/* ��� 2 - ���� ���?� ������ � ������ ����� ����� ���������, * ������� ������ ����� ��������������� � �������: * ����� ���� � ������ - in.available() ����; * ������ ������ ����� Double - Double.SIZE ���, ��� Double.SIZE/8 ����; * ��� ��� ����� ������������ ������, �� ����� ��� ������ � 2 ���� */ 
			Double[][] graphicsData = new Double[in.available()/(Double.SIZE/8)/2][]; 
			// ��� 3 - ���� ������ ������ (���� � ������ ���� ������)
			int i = 0;
			while (in.available()>0){
				// ������ �� ������ �������� ���������� ����� X 
				Double x = in.readDouble();
				// ����� - �������� ������� Y � ����� X 
				Double y = in.readDouble() - 5; 
				// ����������� ���� ��������� ����������� � ������ 
				graphicsData[i++] = new Double[] {x, y}; 
			}
			// ��� 4 - ��������, ������� �� � ������ � ���������� ������ ���� �� ���� ���� ��������� 
			if (graphicsData!=null && graphicsData.length>0){ 
				// �� - ���������� ���� ������������� ������
				fileLoaded = true;
				// �������� ����� ����������� ������� 
				display.showGraphics(graphicsData); 
			} 
			// ��� 5 - ������� ������� ����� 
			in.close(); 
		} 
		catch (FileNotFoundException ex){ 
			// � ������ �������������� �������� ���� "���� �� ������" �������� ��������� �� ������
			JOptionPane.showMessageDialog(MainFrame.this, "��������� ���� �� ������", "������ �������� ������", JOptionPane.WARNING_MESSAGE);
			return;
		} 
		catch (IOException ex){ 
			// � ������ ������ ����� �� ��������� ������ �������� ��������� �� ������ 
			JOptionPane.showMessageDialog(MainFrame.this, "������ ������ ��������� ����� �� �����", "������ �������� ������", JOptionPane.WARNING_MESSAGE);
			return;
		}
	}
	
	public static void main(String[] args){ 
		MainFrame frame = new MainFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private class GraphicsMenuListener implements MenuListener{ 
		// ����������, ���������� ����� ������� ���� 
		public void menuSelected(MenuEvent e){
			// ����������� ��� ������������� ��������� ���� "������" ������������ �������������� ������
			showAxisMenuItem.setEnabled(fileLoaded); 
			showMarkersMenuItem.setEnabled(fileLoaded); 
			showGraphicsSizeMenuItem.setEnabled(fileLoaded); 
			rotateSystemMenuItem.setEnabled(fileLoaded);
		} 
		
		public void menuDeselected(MenuEvent e){ 
		}
		public void menuCanceled(MenuEvent e){
		}
	}
}

			
		
		