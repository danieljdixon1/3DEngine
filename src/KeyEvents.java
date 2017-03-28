import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyEvents extends KeyAdapter  {	
	public void keyPressed(KeyEvent event) {
		char ch = event.getKeyChar();
		if (ch=='w'){
			Player.up=true;
		}
		if (ch=='d'){
			Player.right=true;
		}
		if (ch=='s'){
			Player.down=true;
		}
		if (ch=='a'){
			Player.left=true;
		}
		if (ch==KeyEvent.VK_SPACE){
			Player.jump=true;
		}
		if (ch==KeyEvent.VK_ESCAPE){
			Main.Escape=true;
		}
	}
	public void keyReleased(KeyEvent event) {
	    char ch = event.getKeyChar();
		if (ch=='w'){
			Player.up=false;
		}
		if (ch=='d'){
			Player.right=false;
		}
		if (ch=='s'){
			Player.down=false;
		}
		if (ch=='a'){
			Player.left=false;
		}
	}
}

