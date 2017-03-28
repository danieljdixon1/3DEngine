import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	static BufferedImage imgs[] = new BufferedImage[10000];
	static String name[] = new String[10000];
	static int number=0;
	public Image() {
		//image[0] is the default image to be returned in case of error or null input.
		name[number] = null;
		number++;
	}
	public static BufferedImage getimage(String getname){
		//returns the value of the given image.
		//automaticaly loads image into memory if its not already there.
		for(int t=0; t<10000;t++){
			if (getname == name[t]){
				//System.out.println("found image "+getname+" in memmory.");
				return(imgs[t]);
			}
		}
		return(loadimage(getname));
	}
	public static BufferedImage loadimage(String filename){
	//loads the given image into memory from the working directory.
	BufferedImage img = imgs[0];
		try {
			//System.out.println(System.getProperty("user.dir")+"\\"+filename);
			img = ImageIO.read(new File(System.getProperty("user.dir")+"\\"+filename));
			imgs[number]= img;
			name[number]= filename;
			System.out.println("logged image "+name[number]+" into memmory.");
			number++;
		} catch (IOException e) {
		}
		return(img);
	}
}
