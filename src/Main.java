import java.awt.*;
import javax.swing.JFrame;

public class Main {
	
	//controls:
	//look: mouse
	//move: a s d w
	//jump: space

	//settings
	private static int FPS=90;

	public static Camera displayobj = new Camera();
	private static long frames;
	public static boolean Escape;
	private static JFrame fram;

	public static void main(String[] args) {
		fram = new JFrame("");
		fram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fram.addKeyListener(new KeyEvents());
		fram.addMouseListener(new MouseEvents());
		fram.add(displayobj);
		fram.setSize(900,700);
		fram.setVisible(true);
		new Player();
		new Map();
		startGameLoop();
	}

	public static void startGameLoop(){
		Thread loop = new Thread(){
			public void run(){
				gameloop();
			}
		};
		loop.start();
	}

	public static void gameloop(){
		
		long nanocheck=0;
		long lastnano=0;
		long totalnano=0;
		long timepass=0;
		
		while (!Escape){

			lastnano=nanocheck;
			nanocheck = System.nanoTime();
			totalnano+=(nanocheck-lastnano);
			frames++;
			step();
			
			if (totalnano>=1_000_000_000){
				System.out.println(frames);
				totalnano=0;
				frames=0;
			}

			timepass=(System.nanoTime()-nanocheck);
			if ((1_000/FPS)-(timepass/1_000_000)>=0){
				//System.out.println("sleep for: "+((1_000_000_000/1_000_000/60)-(timepass/1_000_000)));
				try {Thread.sleep( (int)((1_000/FPS)-(timepass/1_000_000)) );} catch (InterruptedException e) {}
			}
			else
			{
				//System.out.println("frame skip");
			}
		}
		fram.dispose();
	}

	public static void step(){
		displayobj.x=Player.x;
		displayobj.y=Player.y;
		displayobj.z=Player.z;
		displayobj.angle=Player.angle;
		displayobj.anglez=Player.anglez;
		Player.doframe();
		displayobj.updatescreen();
	}

}
