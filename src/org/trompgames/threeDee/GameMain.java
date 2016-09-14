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
		private ArrayList<Face> faces = new ArrayList<>();
		private GameFrame gameFrame;
		private double scale = 50;
		
		private int globalXOffset = 0;
		private int globalYOffset = 0;
		
		private Vector3 center = new Vector3(0,0,0);
		private Vector3 minCords = new Vector3(0,0,0);
		private Vector3 maxCords = new Vector3(0,0,0);

		
		public GameHandler(){
			
					
			//loadFile("C:/Users/Thomas/Documents/OBJ Files/stanfordBunny.obj");
			loadFile("box.obj");
			calcCenter();
			
			Vector3 node0 = new Vector3(center.getX(), minCords.getY(), 0 + center.getZ());
			Vector3 node1 = new Vector3(center.getX(), minCords.getY(), 100 + center.getZ());

			nodes.add(node0);
			nodes.add(node1);
			edges.add(new Edge(node0, node1));

			/*
			Vector3 node2 = new Vector3(center.getX(), minCords.getY(),center.getZ());
			Vector3 node3 = new Vector3(center.getX(), -100 + minCords.getY(),center.getZ());

			nodes.add(node2);
			nodes.add(node3);
			edges.add(new Edge(node2, node3));
			
		
			Vector3 node4 = new Vector3(center.getX(), minCords.getY(), 0 + center.getZ());
			Vector3 node5 = new Vector3(-100 + center.getX(), minCords.getY(), center.getZ());

			nodes.add(node4);
			nodes.add(node5);
			edges.add(new Edge(node4, node5));
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
		
		public ArrayList<Face> getFaces(){
			return faces;
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
			faces.clear();
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
			
			ArrayList<Vector3> points = new ArrayList<>();
			
			for(int i = 0; i < vs.length; i++){
				points.add(nodes.get(vs[i]));

				if(i == vs.length-1){
					edges.add(new Edge(nodes.get(i), nodes.get(0)));
				}else{
					edges.add(new Edge(nodes.get(vs[i]), nodes.get(vs[i+1])));
				}				
			}
			faces.add(new Face(points));
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
	
	public static class Face{
		
		private ArrayList<Vector3> vs;
		private Color color;
		
		public Face(ArrayList<Vector3> vs){
			this.vs = vs;
			this.color = new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255));
		}
		
		public ArrayList<Vector3> getNodes(){
			return vs;
		}
		
		public Color getColor(){
			return color;
		}
		
		public void setColor(Color color){
			this.color = color;
		}
		
		public double getMinZ(){
			double d = vs.get(0).getZ();
			for(Vector3 v : vs){
				if(v.getZ() < d) d = v.getZ();
			}
			return d;
			
		}
		
		public double getMaxZ(){
			double d = vs.get(0).getZ();
			for(Vector3 v : vs){
				if(v.getZ() > d) d = v.getZ();
			}
			return d;
			
		}
		
	}
	
	
	public static class GamePanel extends JPanel{
		
		private GameHandler handler;

		public GamePanel(GameHandler handler){
			this.handler = handler;
		}
		
		public ArrayList<Face> sortFaces(){
			
			ArrayList<Face> fs = (ArrayList<Face>) handler.getFaces().clone();
			Face[] ffs = new Face[fs.size()];
			int a = 0;
			for(Face f : fs){
				ffs[a] = f;
				a++;
			}
			
			int c = 0;
			int low;
			double lowMax;
			for(int i = 0; i < ffs.length; i++){
				low = i;
				lowMax = ffs[low].getMaxZ();
				for(int j = i; j < ffs.length; j++){	
					double mz = ffs[j].getMaxZ();
					if(mz > lowMax){
						low = j;				
						lowMax = mz;
					}
					c++;
				}
				
				Face f1 = ffs[i];
				
				ffs[i] = ffs[low];
				ffs[low] = f1;
				
			}
			fs.clear();
			for(int i = 0; i < ffs.length; i++){
				fs.add(ffs[i]);
			}
			
			long init = System.currentTimeMillis();

			
			//System.out.println("Time Taken: " + (System.currentTimeMillis()-init) + " C: " + c);
			//System.out.println(ffs.length);
			//for(face)
			
			return fs;
			
		}
		
		Color[][] col;
		
		public void paintPolygon(int[] xs, int[] ys, int tot, Color color){
			
			if(col == null || col.length != colors.length || col[0].length != colors[0].length) col = new Color[colors.length][colors[0].length];
			
			for(int y = 0; y < col[0].length; y++){
				for(int x = 0; x < col.length; x++){
					col[x][y] = null;
				}
			}

			
			int xMax = max(xs);
			int xMin = min(xs);
			
			if(xMax > colors.length) xMax = colors.length-1;
			if(xMin < 0) xMin = 0;
			
			int yMax = max(ys);
			int yMin = min(ys);
			
			if(yMax > colors[0].length) yMax = colors[0].length-1;
			if(yMin < 0) yMin = 0;
			
			
			for(int i = 0; i < xs.length; i++){
				if(i+1 >= xs.length){
					drawLine(col, color, xs[i], ys[i], xs[0], ys[0]);					
					break;
				}
				drawLine(col, color, xs[i], ys[i], xs[i+1], ys[i+1]);
			
			}
			

			//System.out.println("XMAX: " + xMax + " XMIN: " + xMin);
			//System.out.println("YMAX: " + yMax + " YMIN: " + yMin);
			long startTime = System.currentTimeMillis();

			for(int y = 0; y < col[0].length; y++){
				boolean search = false;
				int startPix = 0;
				for(int x = 1; x < col.length; x++){
					
					//If pixel colored
					if(col[x][y] != null && (col[x-1][y] == null)){
						if(search == true){
							search = false;
							for(int i = startPix; i <= x; i++) setColor(col, color, i, y);
							continue;
						}
						search = true;
						startPix = x;	
					}
				}
			}


			for(int y = 0; y < col[0].length; y++){
				for(int x = 0; x < col.length; x++){
					if(col[x][y] != null) setColor(colors, color, x, y);
				}
			}
			//System.out.println("Time: " + (System.currentTimeMillis()-startTime) + "ms");

		}
		/*
		private void drawLine(Color[][] col, Color color, int x1, int y1, int x2, int y2){
			int dx = Math.abs(x2 - x1);
			int dy = Math.abs(y2 - y1);

			int sx = (x1 < x2) ? 1 : -1;
			int sy = (y1 < y2) ? 1 : -1;

			int err = dx - dy;

			while (true) {
				setColor(col, color, x1, y1);

			    if (x1 == x2 && y1 == y2) {
			        break;
			    }

			    int e2 = 2 * err;

			    if (e2 > -dy) {
			        err = err - dy;
			        x1 = x1 + sx;
			    }

			    if (e2 < dx) {
			        err = err + dx;
			        y1 = y1 + sy;
			    }
			}
		}
		*/
		
		private void drawLine(Color[][] col, Color color, int x1, int y1, int x2, int y2) {
	        // delta of exact value and rounded value of the dependant variable
	        int d = 0;
	 
	        int dy = Math.abs(y2 - y1);
	        int dx = Math.abs(x2 - x1);
	 
	        int dy2 = (dy << 1); // slope scaling factors to avoid floating
	        int dx2 = (dx << 1); // point
	 
	        int ix = x1 < x2 ? 1 : -1; // increment direction
	        int iy = y1 < y2 ? 1 : -1;
	 
	        if (dy <= dx) {
	            for (;;) {
	            	setColor(col, color, x1, y1);
	                if (x1 == x2)
	                    break;
	                x1 += ix;
	                d += dy2;
	                if (d > dx) {
	                    y1 += iy;
	                    d -= dx2;
	                }
	            }
	        } else {
	            for (;;) {
	            	setColor(col, color, x1, y1);
	                if (y1 == y2)
	                    break;
	                y1 += iy;
	                d += dx2;
	                if (d > dy) {
	                    x1 += ix;
	                    d -= dy2;
	                }
	            }
	        }
	    }
		
		private boolean setColor(Color[][] colors, Color color, int x, int y){
			//System.out.println(colors.length);
			if(x < 0 || y < 0 || x >= colors.length || y >= colors[0].length) return false;
			if(colors[x][y] != null) return false; //!= null
			colors[x][y] = color;
			return true;
		}
		
		private int max(int[] m){
			int max = m[0];
			for(int i = 1; i < m.length; i++){
				if(m[i] > max) max = m[i];
			}
			return max;
			
		}
		
		private int min(int[] m){
			int min = m[0];
			for(int i = 1; i < m.length; i++){
				if(m[i] < min) min = m[i];
			}
			return min;
		}
		
		
		
		
		Color[][] colors;

		
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;

			double scale = handler.getScale();
			
			double xOffset = this.getWidth()/2 + 200 + handler.getGlobalXOffset();
			double yOffset = this.getHeight()/2 + 200 + handler.getGlobalYOffset();
			
			
			g2d.setColor(Color.gray);
			
			if(colors == null || this.getWidth() != colors.length || this.getHeight() != colors[0].length) colors = new Color[this.getWidth()][this.getHeight()];
			
			for(int i = 0; i < colors.length; i++){
				for(int j = 0; j < colors[0].length; j++){
					colors[i][j] = null;
				}
			}

			ArrayList<Face> fs = sortFaces();
			//ArrayList<Face> fs = handler.getFaces();
			

			double a = 0;
			double colorScale = 1.0*255/fs.size();
			
			for(int i = fs.size()-1; i >= 0; i--){
			//for(int i = 0; i < fs.size(); i++){
				Face f = fs.get(i);
				a+=colorScale;

				if(f.getColor() == null)
					f.setColor(new Color((int) a, (int) a, (int) a));
					
				g2d.setColor(f.getColor());
				int[] xs = new int[f.getNodes().size()];
				int[] ys = new int[f.getNodes().size()];
				
				
				
				int b = 0;
				for(Vector3 v : f.getNodes()){
					xs[b] = (int) (xOffset + v.getX()*scale);
					ys[b] = (int) (yOffset + v.getY()*scale);
					b++;
				}
				
				paintPolygon(xs, ys, f.getNodes().size(), f.getColor());
				//g2d.fillPolygon(xs, ys, f.getNodes().size());

			}
			//g2d.setColor(Color.red);
			//for(Edge e : handler.getEdges()){
			//	g2d.drawLine((int) (xOffset + e.getV1().getX()*scale), (int) (yOffset+ e.getV1().getY()*scale), (int) (xOffset + e.getV2().getX()*scale), (int) (yOffset+ e.getV2().getY()*scale));
			//}

			g2d.setColor(Color.black);

			for(int i = 0; i < colors.length; i++){
				for(int j = 0; j < colors[0].length; j++){
					if(colors[i][j] == null) continue;
					g2d.setColor(colors[i][j]);
					g2d.drawRect(i, j, 0, 0);
				}
			}
			
			
			/*
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
			*/
			
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
