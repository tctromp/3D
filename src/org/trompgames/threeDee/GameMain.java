package org.trompgames.threeDee;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameMain {

	
	public static void main(String[] args) {
		
		new GameHandler();
		
	}
	
	public static class GameHandler{
		
		private ArrayList<Vector3> nodes = new ArrayList<>();
		private ArrayList<Edge> edges = new ArrayList<>();
		private GameFrame gameFrame;
		private double scale = 1;
		
		private int globalXOffset = 0;
		private int globalYOffset = 0;
		
		private Vector3 center = new Vector3(0,0,0);
		private Vector3 minCords = new Vector3(0,0,0);
		private Vector3 maxCords = new Vector3(0,0,0);

		
		public GameHandler(){
			
					
			//loadFile("C:/Users/Thomas/Documents/OBJ Files/stanfordBunny.obj");
			loadFile("F:/OBJFiles/stanfordBunny.obj");
			calcCenter();
			/*
			Vector3 node0 = new Vector3(20 + center.getX(), minCords.getY(), 20 + center.getZ());
			Vector3 node1 = new Vector3(-20 + center.getX(), minCords.getY(), -20 + center.getZ());
			Vector3 node2 = new Vect), minCords.getY()-1, -20 + center.getZ());
			Vector3 node6 = new Vector3(20 + center.getX(), minCords.getY()-1, -20 + center.getZ());
			Vector3 node7 = new Vector3(-20 + center.getX(), minCords.getY()-1, 20 + center.getZ());
			or3(20 + center.getX(), minCords.getY(), -20 + center.getZ());
			Vector3 node3 = new Vector3(-20 + center.getX(), minCords.getY(), 20 + center.getZ());

			Vector3 node4 = new Vector3(20 + center.getX(), minCords.getY()-1, 20 + center.getZ());
			Vector3 node5 = new Vector3(-20 + center.getX(
			nodes.add(node0);
			nodes.add(node1);
			nodes.add(node2);
			nodes.add(node3);

			nodes.add(node4);
			nodes.add(node5);
			nodes.add(node6);
			nodes.add(node7);
			
			edges.add(new Edge(node0, node2));
			edges.add(new Edge(node2, node1));
			edges.add(new Edge(node3, node0));
			edges.add(new Edge(node3, node1));
			
			edges.add(new Edge(node4, node6));
			edges.add(new Edge(node6, node5));
			edges.add(new Edge(node7, node4));
			edges.add(new Edge(node7, node5));
			
			edges.add(new Edge(node0, node4));
			edges.add(new Edge(node1, node5));
			edges.add(new Edge(node2, node6));
			edges.add(new Edge(node3, node7));
			*/
			gameFrame = new GameFrame(this);
			
			GameThread gt = new GameThread(this);
			((Thread) gt).start();
		}
		
		public Vector3 getCenter(){
			return center;
		}
		
		public int getGlobalXOffset(){
			return globalXOffset;
		}
		
		public int getGlobalYOffset(){
			return globalYOffset;
		}
		
		public void setGlobalXOffset(int x){
			this.globalXOffset = x;
		}
		
		public void setGlobalYOffset(int y){
			this.globalYOffset = y;
		}
		
		public double getScale(){
			return scale;
		}
		
		public void setScale(double scale){
			this.scale = scale;
		}
		
		public void gameLoop(){			
			gameFrame.repaint();			
		}
		
		public void rotateZ(double theta){
			double sin = Math.sin(Math.toRadians(theta));
			double cos = Math.cos(Math.toRadians(theta));
			
			for(Vector3 node : nodes){				
				double x = node.getX();
				double y = node.getY();				
				node.setX((x * cos) - (y * sin));
				node.setY((y * cos) + (x * sin));
			}			
		}
		
		public void rotateY(double theta){
			double sin = Math.sin(Math.toRadians(theta));
			double cos = Math.cos(Math.toRadians(theta));
			
			for(Vector3 node : nodes){				
				double x = node.getX();
				double z = node.getZ();				
				node.setX((x * cos) - (z * sin));
				node.setZ((z * cos) + (x * sin));
			}			
		}
		
		public void rotateX(double theta){
			double sin = Math.sin(Math.toRadians(theta));
			double cos = Math.cos(Math.toRadians(theta));
			
			for(Vector3 node : nodes){				
				double y = node.getY();
				double z = node.getZ();				
				node.setY((y * cos) - (z * sin));
				node.setZ((z * cos) + (y * sin));
			}			
		}
		
		public ArrayList<Vector3> getNodes(){
			return nodes;
		}
		
		public ArrayList<Edge> getEdges(){
			return edges;
		}
		
		public void calcCenter(){
			
			double maxX = Double.MIN_VALUE;
			double maxY = Double.MIN_VALUE;
			double maxZ = Double.MIN_VALUE;

			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			double minZ = Double.MAX_VALUE;
			
			
			for(Vector3 node : nodes){
				if(node.getX() > maxX) maxX = node.getX();
				if(node.getY() > maxY) maxY = node.getY();
				if(node.getZ() > maxZ) maxZ = node.getZ();
				if(node.getX() < minX) minX = node.getX();
				if(node.getY() < minY) minY = node.getY();
				if(node.getZ() < minZ) minZ = node.getZ();				
			}
			maxCords = new Vector3(maxX, maxY, maxZ);
			minCords = new Vector3(minX, minY, minZ);
			center = new Vector3((maxX + minX)/2, (minY + maxY)/2, (maxZ + minZ)/2);
			


			for(Vector3 node : nodes){
				node.setX(node.getX() - center.getX());
				node.setY(node.getY() - center.getY());
				node.setZ(node.getZ() - center.getZ());
			}
			
		}
		
		public void loadFile(String file){
			nodes.clear();
			edges.clear();
			try(Stream<String> stream = Files.lines(Paths.get(file))){
				String line = "";
				Iterator<String> it = stream.iterator();
				while(it.hasNext() && (line = it.next()) != null){
					if(line.length() > 1 && line.indexOf("v ") == 0){
						Vector3 v = getVector(line);
						nodes.add(v);
					}else if(line.length() > 1 && line.indexOf("f ") == 0){
						getEdges(line);
					}
				}
				System.out.println("Loaded File: Vertices: " + nodes.size() + " Faces: " + nodes.size()/3);
			}catch(IOException e){
				e.printStackTrace();
			}		
			
			
		}
		
		public void getEdges(String line){
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			
			int[] vs = new int[st.countTokens()];
			StringTokenizer stv = null;
			for(int i = 0; i < vs.length; i++){
				stv = new StringTokenizer(st.nextToken(), "/");
				vs[i] = Integer.parseInt(stv.nextToken()) - 1;
			}
			
			for(int i = 0; i < vs.length; i++){
				if(i == vs.length-1){
					edges.add(new Edge(nodes.get(i), nodes.get(0)));
				}else{
					edges.add(new Edge(nodes.get(vs[i]), nodes.get(vs[i+1])));
				}				
			}			
		}
		
		public Vector3 getVector(String line){
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			
			double x = Double.parseDouble(st.nextToken());
			double y = Double.parseDouble(st.nextToken());
			double z = Double.parseDouble(st.nextToken());
			return new Vector3(x,y,z);
			
			
		}
		
		
	}
	
	
	public static class GamePanel extends JPanel{
		
		private GameHandler handler;

		public GamePanel(GameHandler handler){
			this.handler = handler;
		}
		
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			
			double scale = handler.getScale();
			
			double xOffset = this.getWidth()/2 + 200 + handler.getGlobalXOffset();
			double yOffset = this.getHeight()/2 + 200 + handler.getGlobalYOffset();
			
			
			
			g2d.setColor(Color.red);
			
			for(Edge edge : handler.getEdges()){
				
				int x1 = (int) (xOffset + edge.getV1().getX()*scale);
				int y1 = (int) (yOffset + edge.getV1().getY()*scale);
				int x2 = (int) (xOffset + edge.getV2().getX()*scale);
				int y2 = (int) (yOffset + edge.getV2().getY()*scale);
				
				g2d.drawLine(x1, y1, x2, y2);
			}
			
			g2d.setColor(Color.black);
			
			for(Vector3 node : handler.getNodes()){
				g2d.drawRect((int) (xOffset + (node.getX())*scale), (int) (yOffset + (node.getY())*scale), 0, 0);
			}
			
			g2d.setColor(Color.BLUE);
			
			g2d.drawRect((int) (handler.getCenter().getX()-1), (int) (handler.getCenter().getY()-1), 10, 10);
			
			//System.out.println(handler.getCenter());
			
		}
		
		
	}
	
	public static class GameFrame extends JFrame{
		
		private boolean shiftPressed = false;

		
		public GameFrame(GameHandler handler){
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setSize(400, 400);
			this.add(new GamePanel(handler));
			this.setVisible(true);
			
			Vector3 prevLoc = new Vector3(0,0,0);
			
			this.addMouseWheelListener(new MouseWheelListener(){
				@Override
				public void mouseWheelMoved(MouseWheelEvent event) {
					int i = event.getWheelRotation();
					handler.setScale(handler.getScale() + i);
				}
				
			});
			
			
			this.addKeyListener(new KeyListener(){
				
				@Override
				public void keyPressed(KeyEvent event) {
					if(event.getKeyChar() == 'w'){
						handler.setScale(handler.getScale()+50);
					}if(event.getKeyChar() == 'a'){
						handler.setGlobalXOffset(handler.getGlobalXOffset()+5);
					}if(event.getKeyChar() == 's'){
						handler.setScale(handler.getScale()-50);
					}if(event.getKeyChar() == 'd'){
						handler.setGlobalXOffset(handler.getGlobalXOffset()-5);

					}if(event.getKeyCode() == 16){ //SHIFT Pressed
						shiftPressed = true;
					}
				}

				@Override
				public void keyReleased(KeyEvent event) {
					if(event.getKeyCode() == 16){ //SHIFT Pressed
						shiftPressed = false;
					}
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
				}
				
				
				
			});
			
			this.addMouseListener(new MouseListener(){

				@Override
				public void mousePressed(MouseEvent event) {
					// TODO Auto-generated method stub
					prevLoc.setX(event.getX());
					prevLoc.setY(event.getY());
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			this.addMouseMotionListener(new MouseMotionListener(){

				@Override
				public void mouseDragged(MouseEvent event) {
					int x = event.getX();
					int y = event.getY();
					
					if(event.getModifiersEx() == 2048){
						handler.setGlobalXOffset((int) (handler.getGlobalXOffset() + (x - prevLoc.getX())));
						handler.setGlobalYOffset((int) (handler.getGlobalYOffset() + (y - prevLoc.getY())));
						prevLoc.setX(x);
						prevLoc.setY(y);
						return;
					}
					
					if(shiftPressed){
						if(Math.abs(x - prevLoc.getX()) > Math.abs(y - prevLoc.getY())){
							handler.rotateX((y - prevLoc.getY())/12);
							prevLoc.setX(x);
							prevLoc.setY(y);
						}else{
							handler.rotateY((x - prevLoc.getX())/12);
							prevLoc.setX(x);
							prevLoc.setY(y);
						}
						
						
					}else{
						handler.rotateY((x - prevLoc.getX())/12);
						handler.rotateX((y - prevLoc.getY())/12);
						prevLoc.setX(x);
						prevLoc.setY(y);
					}
					
					
				}

				@Override
				public void mouseMoved(MouseEvent event) {
					// TODO Auto-generated method stub
					
				}
				
				
			});
		}
		
	}
	
	public static class GameThread extends Thread{
		
		private GameHandler handler;
		
		public GameThread(GameHandler handler){
			this.handler = handler;
		}
		
		@Override
		public void run(){
			
			long lastTime = System.currentTimeMillis();
			while(true){				
				if(System.currentTimeMillis() - 17 < lastTime) continue;	
				
				handler.gameLoop();
				lastTime = System.currentTimeMillis();
				
				
			}
			
		}
		
	}
	
	public static class Polygon{
		
		private ArrayList<Vector3> nodes;
		private ArrayList<Edge> edges;
		
		public Polygon(ArrayList<Vector3> nodes, ArrayList<Edge> edges){
			this.nodes = nodes;
			this.edges = edges;
		}

		public ArrayList<Vector3> getNodes() {
			return nodes;
		}

		public ArrayList<Edge> getEdges() {
			return edges;
		}
		
		
		
	}
	
	public static class Edge{
		
		private Vector3 v1;
		private Vector3 v2;
		
		public Edge(Vector3 v1, Vector3 v2){
			this.v1 = v1;
			this.v2 = v2;
		}

		public Vector3 getV1() {
			return v1;
		}

		public void setV1(Vector3 v1) {
			this.v1 = v1;
		}

		public Vector3 getV2() {
			return v2;
		}

		public void setV2(Vector3 v2) {
			this.v2 = v2;
		}
		
		
		
	}
	
	public static class Vector3{
		
		private double x;
		private double y;
		private double z;
		
		public Vector3(double x, double y, double z){
			this.x = x;
			this.y = y;
			this.z = z;		
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getZ() {
			return z;
		}

		public void setZ(double z) {
			this.z = z;
		}
		
		@Override
		public String toString(){
			return "X: " + x + " Y: " + y + " Z: " + z;
		}
		
		
		
		
	}
	
	
	
}
