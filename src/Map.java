import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

public class Map {

	//settngs
	public static int textureWraps=3;
	public static int mapheight=384;
	public static int tileSize=128;
	
	private static int[][] Block;
	private static int[][] Floor;
	private static int[][] Ceil;
	public static int width=200;
	public static int height=200;
	
	//map data
	private static int Mins=5;
	private static int Maxs=30;
	private static int rooms=5;
	private static int[] rx;
	private static int[] ry;
	private static int[] rw;
	private static int[] rh;
	
	
	public Map() {
		Block = new int[width][height];
		Floor = new int[width][height];
		Ceil = new int[width][height];

		Fillf(3,0,0,width,height);
		Fillc(4,0,0,width,height);
		
		Fill(0,0,0,width,height);
		Fill(-1,1,1,width-2,height-2);
		
		generateMap();
	}
	private static void generateMap() {
		Random rand = new Random();
		rx = new int[rooms];
		ry = new int[rooms];
		rw = new int[rooms];
		rh = new int[rooms];
		rw[0]=Mins+rand.nextInt(Maxs);
		rh[0]=Mins+rand.nextInt(Maxs);
		rx[0]=width/2-rw[0]/2;
		ry[0]=height/2-rh[0]/2;
		for (int t=1;t<rooms;t++){
			rw[t]=Mins+rand.nextInt(Maxs);
			rh[t]=Mins+rand.nextInt(Maxs);
			boolean br=true;
			while(br){
				rx[t]=rand.nextInt(width-rw[t]);
				ry[t]=rand.nextInt(height-rh[t]);
				for (int t2=0;t2<rooms;t2++){
					if ((rx[t]+rw[t]>rx[t2]) && (rx[t] < rx[t2]+rw[t2])){
						if ((ry[t]+rh[t]>ry[t2]) && (ry[t] < ry[t2]+rh[t2])){
							br=false;		
						}
					}
					break;
				}
			}
		}

		for(int t=0;t<rooms;t++){
			Fill(rand.nextInt(5),rx[t]-1,ry[t]-1,rw[t]+2,rh[t]+2);
		}
		for(int t=0;t<rooms;t++){
			Fill(-1,rx[t],ry[t],rw[t],rh[t]);
			Fillf(rand.nextInt(5),rx[t]-1,ry[t]-1,rw[t]+2,rh[t]+2);
			Fillc(rand.nextInt(5),rx[t]-1,ry[t]-1,rw[t]+2,rh[t]+2);
		}
	}
	private static void Fill(int numb, int x, int y, int wid, int hei){

		for(int t=0;t<wid;t++){
			for(int t2=0;t2<hei;t2++){
				//System.out.println((x+t) + ", " + (y+t));
				//System.out.println(numb);
				Block[x+t][y+t2]=(numb);
			}
		}
	}
	private static void Fillf(int numb, int x, int y, int wid, int hei){
		for(int t=0;t<wid;t++){
			for(int t2=0;t2<hei;t2++){
				Floor[x+t][y+t2]=numb;
			}
		}
	}
	private static void Fillc(int numb, int x, int y, int wid, int hei){
		for(int t=0;t<wid;t++){
			for(int t2=0;t2<hei;t2++){
				Ceil[x+t][y+t2]=numb;
			}
		}
	}
	private static int gx(int x){
		if (x>=height || x<0){
			x=-1;
		}
		return (x);
	}
	private static int gy(int y){
		if (y>=height || y<0){
			y=-1;
		}
		return (y);
	}
	public static int getTile(int x, int y){
		int rx=gx(x);
		int ry=gy(y);
		int r;
		if (ry==-1 || rx==-1){
			r=-1;
		}else{
			r = Block[rx][ry];
		}
		return (r);
	}
	public static int getFloor(int x, int y){
		int rx=gx(x);
		int ry=gy(y);
		int r;
		if (ry==-1 || rx==-1){
			r=-1;
		}else{
			r = Floor[rx][ry];
		}
		return (r);
	}
	public static int getCeil(int x, int y){
		int rx=gx(x);
		int ry=gy(y);
		int r;
		if (ry==-1 || rx==-1){
			r=-1;
		}else{
			r = Ceil[rx][ry];
		}
		return (r);
	}
}
