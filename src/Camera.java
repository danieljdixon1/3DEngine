import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Camera extends JPanel {

	//Display Settings
	public static int resW = 400; //Horizontal Resolution
	public static int resH = 300; //Vertical Resolution
	public static int screenW = 800;
	public static int screenH = 600;
	public static int plateWidth=12; //projection plate.
	public static int plateHeight=8;
	public static int plateDist=4;
	
	//Fog Settings
	public static int fogstart=1024;
	public static int fogdist=256;
	public static Color fogColor= new Color(0,0,0,255); //color of the fog

	//wall vars
	public static BufferedImage[] TextureWall;
	private float[] rowdist;
	private int[] rowtype;
	private double[] rowtext;
	private Boolean[] row;
	private float rat;
	
	//status variables
	public static float x=0;
	public static float y=0;
	public static float z=16;
	public static float angle=0; //yaw
	public static float anglez=0; //pitch
	public static float topz;
	public static float botz;
	public static float sizez;
	public static int horizon=0;
	
	//displays
	BufferedImage offscreen;
	BufferedImage onscreen;
	Graphics2D draw;
	
	//floor objects
	private Floor f1;
	private Floor f2;
	private Floor f3;
	private Floor f4;
	private Floor f5;

	//constructor
	public Camera(){
		//initialize floor objects
		f1 = new Floor(true, 0, 8, (fogstart+fogdist));
		f2 = new Floor(true, 0, 32, (fogstart+fogdist)/4);
		f3 = new Floor(true, 0, 128, (fogstart+fogdist)/8);
		//ceiling
		f4 = new Floor(false, Map.mapheight, 8, (fogstart+fogdist));
		f5 = new Floor(false, Map.mapheight, 32, (fogstart+fogdist)/3);

		//load images
		TextureWall= new BufferedImage[5];
		TextureWall[0] = Image.getimage("bricks.bmp");
		TextureWall[1] = Image.getimage("rocks.bmp");
		TextureWall[2] = Image.getimage("bluerocks.bmp");
		TextureWall[3] = Image.getimage("Tile.bmp");
		TextureWall[4] = Image.getimage("TileRed.bmp");
		
		offscreen = new BufferedImage(resW,resH,BufferedImage.TYPE_INT_ARGB);
		onscreen = new BufferedImage(screenW,screenH,BufferedImage.TYPE_INT_ARGB);
		
		draw = (Graphics2D) offscreen.getGraphics();
		rowdist = new float[resW];
		rowtype = new int[resW];
		rowtext = new double[resW];
		row = new Boolean[resW];
		rat=1/(float) Math.cos(Math.atan((float)plateWidth/2/plateDist));
	}

	//draw
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.setBackground(Color.gray);
		g.drawImage(onscreen,0,0,screenW,screenH,null);
	}
	
	//render frame
	public void updatescreen(){
		//render offscreen
		draw.setColor(fogColor);
		draw.fillRect(0, 0, resW, resH);
		calcz();
		f1.draw(draw);//floor far
		f2.draw(draw);//floor medium
		f3.draw(draw);//floor close
		f4.draw(draw);//ceiling far
		f5.draw(draw);//ceiling close
		calcrows();
		drawrows(draw);//walls
		
		//utilizes double buffering
		onscreen.getGraphics().drawImage(offscreen,0,0,screenW,screenH,null);
		repaint();
	}

	//walls are drawn by casting a ray from the camera for every
	//vertical collumn of screen pixels(row). once the ray hits
	//a wall the position of the texture and distance is
	//calculated and a stored in an array. after every row is
	//calculated vertical rows of texture are drawn on the screen
	//at different scales to give perspective.
	
	//calculate vertical camera data
	private void calcz(){
		float hitz=(float)(z+(float)Math.sin(Math.toRadians(anglez))/(float)Math.cos(Math.toRadians(anglez))*(float)plateDist);
		topz=hitz+plateHeight/2;
		botz=hitz-plateHeight/2;
		sizez=topz-botz;
		horizon= (int) ((topz-z)/plateHeight*resH);
	}

	//gets wall slice(Row) data and stores it in arrays
	private void calcrows(){
		float lpx=(float)(x+Math.cos(Math.toRadians(-angle))*(float)plateDist+Math.cos(Math.toRadians(-angle-90))*(float)plateWidth/2);
		float lpy=(float)(y+Math.sin(Math.toRadians(-angle))*(float)plateDist+Math.sin(Math.toRadians(-angle-90))*(float)plateWidth/2);
		float rpx=(float)(x+Math.cos(Math.toRadians(-angle))*(float)plateDist+Math.cos(Math.toRadians(-angle+90))*(float)plateWidth/2);
		float rpy=(float)(y+Math.sin(Math.toRadians(-angle))*(float)plateDist+Math.sin(Math.toRadians(-angle+90))*(float)plateWidth/2);
		
		for (int t=0;t<resW;t++){
			float cx=lpx+(rpx-lpx)/(float)resW*t;
			float cy=lpy+(rpy-lpy)/(float)resW*t;
			
			raycast(t,cx,cy);
		}
	}
	
	//cast a ray to determine wall row data for a single wall slice(Row)
	public void raycast(int mrow ,float cx ,float cy){
		int type=0;
		int type2=0;
		row[mrow]=false;

		float colx;
		float coly;
		float slope = ((y-cy)/(x-cx));
		boolean lr=false;
		if (x>cx){
			lr=false;
			colx= Math.round((x-Map.tileSize/2)/(float)Map.tileSize)*(float)Map.tileSize;
			coly = y + ((colx-x) * slope);
		}else{
			lr=true;
			colx= (Math.round((x-Map.tileSize/2)/(float)Map.tileSize)+1)*(float)Map.tileSize;
			coly = y + ((colx-x) * slope);
		}
		int distgone=0;
		while (true){
			if (lr){
				if (Map.getTile(Math.round(colx/Map.tileSize), (int)Math.round((coly/Map.tileSize)-0.5))!=-1){
					row[mrow]=true;
					type=Map.getTile(Math.round(colx/Map.tileSize), (int)Math.round((coly/Map.tileSize)-0.5));
					break;
				}
				colx+=Map.tileSize;
				coly = y + ((colx-x) * slope);
			}else{
				if (Map.getTile(Math.round(colx/Map.tileSize)-1, (int)Math.round((coly/Map.tileSize)-0.5))!=-1){
					row[mrow]=true;
					type=Map.getTile(Math.round(colx/Map.tileSize)-1, (int)Math.round((coly/Map.tileSize)-0.5));
					break;
				}
				colx-=Map.tileSize;
				coly = y + ((colx-x) * slope);
			}
			distgone+=Map.tileSize;//*0.5;
			if (distgone>(float)(fogstart+fogdist)*rat){
				break;
			}
		}
	
		float colxF;
		float colyF;
		float slope2 = ((x-cx)/(y-cy));
		boolean ud=false;
		if (y>cy){
			ud=false;
			colyF = (Math.round((y-Map.tileSize/2)/(float)Map.tileSize))*(float)Map.tileSize;
			colxF = x + ((colyF-y) * slope2);
		}else{
			ud=true;
			colyF= (Math.round((y-Map.tileSize/2)/(float)Map.tileSize)+1)*(float)Map.tileSize;
			colxF = x + ((colyF-y) * slope2);
		}
		
		distgone=0;
		while (true){
			if (ud){
				if (Map.getTile((int)Math.round((colxF/Map.tileSize)-0.5), Math.round(colyF/Map.tileSize))!=-1){
					row[mrow]=true;
					type2=Map.getTile((int)Math.round((colxF/Map.tileSize)-0.5), Math.round(colyF/Map.tileSize));
					break;
				}
				colyF+=Map.tileSize;
				colxF = x + ((colyF-y) * slope2);
			}else{
				if (Map.getTile((int)Math.round((colxF/Map.tileSize)-0.5), Math.round(colyF/Map.tileSize)-1)!=-1){
					row[mrow]=true;
					type2=Map.getTile((int)Math.round((colxF/Map.tileSize)-0.5), Math.round(colyF/Map.tileSize)-1);
					break;
				}
				colyF-=Map.tileSize;
				colxF = x + ((colyF-y) * slope2);
			}
			distgone+=Map.tileSize;
			if (distgone>(float)(fogstart+fogdist)*rat){
				break;
			}
		}

		double dist=Math.sqrt(Math.pow(Math.abs(x-colx),2)+Math.pow(Math.abs(y-coly),2));
		double dist2=Math.sqrt(Math.pow(Math.abs(x-colxF),2)+Math.pow(Math.abs(y-colyF),2));
		double dif;
		
		if (dist2<dist){
			type=type2;
			colx=colxF;
			coly=colyF;
			dist=dist2;
			dif = Math.round(colx/Map.tileSize+0.5+1)*Map.tileSize-colx;
		}else{
			dif = Math.round(coly/Map.tileSize+0.5)*Map.tileSize-coly;
		}

		double rslope=(((double)mrow/(double)resW)-0.5)*plateWidth/plateDist;
		double dir=Math.atan(rslope);
		double rdist=Math.cos(dir)*dist;

		if (rdist>fogstart+fogdist){
			row[mrow]=false;
		}
		rowdist[mrow]=(float) rdist;
		rowtype[mrow]=type;
		rowtext[mrow]=(double)((int)(dif/Map.tileSize*100)/100.00);
		
	}

	//draw wall slices to screen
	private void drawrows(Graphics2D g){
		for (int t=0;t<resW;t++){
			int top=(int)(horizon+((z-Map.mapheight)/((float)rowdist[t])*plateDist)/plateHeight*resH)-1;
			int bot=(int)(horizon+(z/((float)rowdist[t])*plateDist)/plateHeight*resH)+1;
			if (row[t]){
				if(rowdist[t]>0){
					g.drawImage(TextureWall[rowtype[t]], t, top, t+1, bot, (int)(rowtext[t]*TextureWall[rowtype[t]].getWidth()/2), 0, (int)(rowtext[t]*TextureWall[rowtype[t]].getWidth()/2)+1, TextureWall[rowtype[t]].getHeight()*Map.textureWraps/3,Color.black,null);
				}
			}else{
				g.setColor(fogColor);
				g.drawLine(t, top, t, bot);
			}
			if	(rowdist[t]>fogstart){
				if	(rowdist[t]<fogstart+fogdist){
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)(rowdist[t]-fogstart)/fogdist));
					g.setColor(fogColor);
					g.drawLine(t, top, t, bot);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
				}
			}
		}		
	}
	
}
