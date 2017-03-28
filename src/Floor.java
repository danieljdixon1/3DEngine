import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Floor {
	//used in calculations
	private float rat;
	private int floorwid;

	//array of floor textures
	private BufferedImage[] Texture;

	//floor images
	private BufferedImage floor;
	private BufferedImage floorRot;

	//settings copied from Camera because accessing a public
	//variable is slower than using a private variable
	private int resW;
	private int resH;
	private int plateDist;
	private int plateWidth;
	private int plateHeight;
	private int fogstart;
	private int fogdist;
	private Color fogColor;

	//floor settings
	private float z;
	private int res;
	private int dist;
	private boolean isFloor;

	public Floor(boolean isFloor, float z, int res, int dist) {
		this.isFloor=isFloor;
		this.z=z;
		this.res=res;
		this.dist=dist;
		
		resW = Camera.resW;
		resH = Camera.resH;
		plateDist = Camera.plateDist;
		plateWidth = Camera.plateWidth;
		plateHeight = Camera.plateHeight;
		fogstart = Camera.fogstart;
		fogdist = Camera.fogdist;
		fogColor = Camera.fogColor;

		//load floor textures
		Texture = new BufferedImage[5];
		Texture[0] = Image.getimage("bricks.bmp");
		Texture[1] = Image.getimage("rocks.bmp");
		Texture[2] = Image.getimage("bluerocks.bmp");
		Texture[3] = Image.getimage("Tile.bmp");
		Texture[4] = Image.getimage("TileRed.bmp");
		
		//initialize floor buffers
		rat=1/(float) Math.cos(Math.atan((float)plateWidth/2/plateDist));
		floor = new BufferedImage((int)((float)res/Map.tileSize*dist*2*rat), (int)((float)res/Map.tileSize*dist*2*rat), BufferedImage.TYPE_INT_ARGB);
		floorRot = new BufferedImage((int)((float)res/Map.tileSize*dist*plateWidth/plateDist*2),(int)((float)res/Map.tileSize*dist/*+Map.tileSize*/),BufferedImage.TYPE_INT_ARGB);
		floorwid=(int) ((float)res/Map.tileSize*dist*Math.sin(Math.atan((float)plateWidth/2/plateDist)));	}

	//draws the floor instance to the screen
	public void draw(Graphics2D g){
		drawfloor();
		calcfloor();
		proj(g);
	}
	
	//floor are drawn in several steps to give them the correct
	//perspective
	//1. the floor tiles are drawn to an image.
	//2. the image is rotated so that it is oriented the same
	//   way as the camera
	//3. a scaled strip of that image is drawn to the screen for
	//   every horizontal row of pixels of the screen.
	
	//draw floor tiles unto the floor buffer
	private void drawfloor(){
		Graphics2D bob = (Graphics2D) floor.getGraphics();
		
		bob.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
		bob.setColor(new Color(255,255,255,0));
		bob.fillRect(0, 0, floor.getWidth(), floor.getHeight());
		bob.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		
		int cx=(int)Math.round(Camera.x/Map.tileSize-0.5);
		int cy=(int)Math.round(Camera.y/Map.tileSize-0.5);
		int l=(int) Math.round((Camera.x-(fogstart+fogdist)*rat)/Map.tileSize);
		int r=(int) Math.round((Camera.x+(fogstart+fogdist)*rat)/Map.tileSize);
		int u=(int) Math.round((Camera.y-(fogstart+fogdist)*rat)/Map.tileSize);
		int b=(int) Math.round((Camera.y+(fogstart+fogdist)*rat)/Map.tileSize);
		for (int t2=u;t2<b;t2++){
			//if (Math.abs(t2-cy)>5){
			//	continue;
			//}
			for (int t=l;t<r;t++){
				if (Map.getCeil(t, t2)!=-1){
					int texid;
					if (isFloor){
						texid = Map.getFloor(t, t2);
					}else{
						texid = Map.getCeil(t, t2);
					}
					floor.getGraphics().drawImage(Texture[texid], (int)-((float)Camera.x/Map.tileSize*res)+t*res+floor.getWidth()/2, (int)-((float)Camera.y/Map.tileSize*res)+t2*res+floor.getHeight()/2, (int)-((float)Camera.x/Map.tileSize*res)+(t+1)*res+floor.getWidth()/2, (int)-((float)Camera.y/Map.tileSize*res)+(t2+1)*res+floor.getHeight()/2, 0, 0, (int)Texture[texid].getWidth()/2, (int)Texture[texid].getHeight()/3, null);
				}
			}	
		}
	}

	//rotate floor buffer and prepare it for projection unto the screen buffer
	private void calcfloor(){
		
		Graphics2D temp = (Graphics2D) floorRot.getGraphics();

		temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
		temp.setColor(new Color(255,255,255,0));
		temp.fillRect(0, 0, floorRot.getWidth(), floorRot.getHeight());
		temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		
		temp.rotate(Math.toRadians(Camera.angle+90),(double)(floorRot.getWidth()/2),0);//(double)(floor.getHeight()-((float)Map.tileres/(float)Map.tileSize*(float)Map.tileSize)));
		temp.drawImage(floor,(floorRot.getWidth()/2)-(floor.getWidth()/2),-(floor.getHeight()/2),null);
	}

	//project floor unto the screen buffer
	private void proj(Graphics2D g){
		int start=Camera.horizon;
		int end=Camera.horizon;
		if (z<Camera.z){
			start=Camera.horizon;
			end=resH;
			if (start<0){
				start=0;
			}
		}else{
			start=0;
			end=Camera.horizon;
			if (end>resH){
				end=resH;
			}
		}
		for (int t=start;t<end;t++){
			float cz = (Camera.topz-t*Camera.sizez/resH);
			float slope=plateDist/(cz-Camera.z);
			float hitdist=(z-Camera.z)*slope;

			if (hitdist<dist){
				
				int textpos = (int)(hitdist*res/Map.tileSize);
	
				float hitx=(float)(floorwid)/hitdist*plateDist;
				float wid=hitx/plateWidth*resW*2*Map.tileSize/res*rat*2;
				
				g.drawImage(floorRot,(int)(-wid/2+resW/2),t,(int)(wid/2+resW/2),t+1,floorRot.getWidth(),textpos,0,textpos+1,null);
				if (hitdist>fogstart){
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)(hitdist-fogstart)/fogdist));
					g.setColor(fogColor);
					g.drawLine(0, t, resW, t);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
				}
			}
		}
	}

}

