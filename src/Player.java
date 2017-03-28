
public class Player {
	
	//settings
	static int standingheight=64;
	public static float maxAngle=60; //maximum pitch that you can look up or down
	public static double sensitivity=1;
	
	//status variables
	private static double xspeed=0;
	private static double yspeed=0;
	private static double zspeed=0;
	private static double anglespeed=0;
	private static double anglezspeed=0;
	static float x=(Map.width/2)*Map.tileSize;
	static float y=(Map.height/2)*Map.tileSize;
	static float z=standingheight;
	static float angle=0;
	static float anglez=0;
	public static boolean left=false; //keypressed
	public static boolean right=false; //keypressed
	public static boolean up=false; //keypressed
	public static boolean down=false; //keypressed
	public static boolean jump=false; //keypressed
	public static boolean onground=false;
	
	public static void doframe() {
		doinput();
		dophysics();
	}
	private static void doinput(){
		anglespeed-=(MouseEvents.get_mouseposx()-100)*0.8*sensitivity;
		anglezspeed-=(MouseEvents.get_mouseposy()-100)*0.4*sensitivity;
		MouseEvents.set_mouseposxy(100,100);
		if (onground==true){
			if (up==true){
				yspeed+=Math.sin(Math.toRadians(-angle))*1.5;
				xspeed+=Math.cos(Math.toRadians(-angle))*1.5;
			}
			if (down==true){
				yspeed+=Math.sin(Math.toRadians(-angle+180))*1.5;
				xspeed+=Math.cos(Math.toRadians(-angle+180))*1.5;
			}
			if (right==true){
				yspeed+=Math.sin(Math.toRadians(-angle+90))*1.5;
				xspeed+=Math.cos(Math.toRadians(-angle+90))*1.5;
			}
			if (left==true){
				yspeed+=Math.sin(Math.toRadians(-angle-90))*1.5;
				xspeed+=Math.cos(Math.toRadians(-angle-90))*1.5;
			}
		}else{
			if (up==true){
				yspeed+=Math.sin(Math.toRadians(-angle))*0.5;
				xspeed+=Math.cos(Math.toRadians(-angle))*0.5;
			}
			if (down==true){
				yspeed+=Math.sin(Math.toRadians(-angle+180))*0.5;
				xspeed+=Math.cos(Math.toRadians(-angle+180))*0.5;
			}
			if (right==true){
				yspeed+=Math.sin(Math.toRadians(-angle+90))*0.5;
				xspeed+=Math.cos(Math.toRadians(-angle+90))*0.5;
			}
			if (left==true){
				yspeed+=Math.sin(Math.toRadians(-angle-90))*0.5;
				xspeed+=Math.cos(Math.toRadians(-angle-90))*0.5;
			}
		}
		if (jump==true){
			jump=false;
			if (onground==true){
				zspeed=11;
				onground=false;
			}
		}
	}
	private static void dophysics(){
		//gravity
		if (onground==false){
			zspeed-=0.25;
			if (zspeed<=0){
				if (z<=standingheight){
					z=standingheight;
					zspeed=0;
					onground=true;
				}
			}
		}
		
		//translate and rotate
		x+=xspeed;
		y+=yspeed;
		z+=zspeed;
		angle+=anglespeed;
		anglez+=anglezspeed;
		
		//movement and rotation damping
		if (onground==true){
			xspeed=xspeed*0.8;
			yspeed=yspeed*0.8;
		}else{
			xspeed=xspeed*0.95;
			yspeed=yspeed*0.95;
		}
		anglespeed=anglespeed*0;
		anglezspeed=anglezspeed*0;
		
		//angle bounds and wrapping
		if (angle<0){
			angle+=360;
		}
		if (angle>=360){
			angle-=360;
		}
		if (anglez<=-maxAngle){
			anglez=-maxAngle;
		}
		if (anglez>=maxAngle){
			anglez=maxAngle;
		}
	}
}
