package at.tuwien.prip.ocr;

/*Diana Negoescu '09
 * program 'reads' a road sign by counting the similar pixels between letters
 * execution: java Img picture_name.jpg<database.txt
 */


import java.awt.Color;

import at.tuwien.prip.ocr.Stack.Node;

public class Img
{

	//function that computes the luminance of a pixel
	public static double lum(Color color)
	{
		int r=color.getRed();
		int g=color.getGreen();
		int b=color.getBlue();
		return .299*r+.587*g+.114*b;
	}

	//function that transforms a picture into a black and white one of same
	//dimension
	public static Picture blackwhite(String filename)
	{
		Picture source=new Picture(filename);
		int w=source.width();
		int h=source.height();
		Picture result=new Picture(w,h);
		Color black=new Color(0,0,0);
		Color white=new Color(255, 255, 255);
		double max=0.0;
		double min=255.0;
		double av=0;
		for (int i=0; i<h; i++)
		{
			for (int j=0; j<w; j++)
			{
				Color color=source.get(j,i);
				av=av+lum(color);
			}
		}
		av=av/(w*h);
		for (int i=0; i<h; i++)
		{
			for (int j=0; j<w; j++)
			{
				Color color=source.get(j,i);
				if (lum(color)>av+20) result.set(j,i,white);
				else result.set(j,i,black);
			}
		}
		result.show();
		return result;

	}

	//label each pixel according to which object it belongs
	public static void floodfillstack(Picture pic, 
			int[][] a, 
			Stack s, Color c,
			int k)
	{ 
		while (!s.isEmpty())
		{
			Stack.Node x=(Node) s.pop();
			if ((x.i>=pic.height())||(x.j>=pic.width())||(x.i<0)||(x.j<0)){
				continue;
			}
			Color col=pic.get(x.j,x.i);
			if (col.equals(Color.BLACK)) continue;
			if (a[x.i][x.j]!=0) continue;
			s.push(x.i+1,x.j);
			s.push(x.i,x.j-1);
			s.push(x.i-1,x.j);
			s.push(x.i,x.j+1);
			a[x.i][x.j]=k;
		}
	}


	static boolean less(double x, double y) {
		return (x < y);
	}

	static void exch(Letter[] a,int[] mmin, int i, int j) {
		Letter swapl = a[i];
		a[i] = a[j];
		a[j] = swapl;
		int swap=mmin[i];
		mmin[i]=mmin[j];
		mmin[j]=swap;
	} 

	/**
	 * 
	 * @param a
	 * @param mmin
	 * @param left
	 * @param right
	 * @return
	 */
	static int partition(Letter[] a, int[] mmin, int left, int right) 
	{
		int i = left - 1;
		int j = right;

		while(true) { 
			while (less(mmin[++i], mmin[right]))   // left pointer
			if (i == right) break;

			while (less(mmin[right], mmin[--j]))   // right pointer
				if (j == left) break;

			if (i >= j) break;               // pointers cross?

			exch(a, mmin, i, j);                   // swap left and right
		}

		exch(a, mmin, i, right);                  // swap pivot
		return i;
	}

	public static void quicksortgen(Letter[] a, int b, int i, int[] mmin) 
	{
		quicksort(a, mmin, b, i);
	}

	private static void quicksort(Letter[] a,int[] mmin, int left,int right)
	{
		if (right <= left) return;
		int i = partition(a, mmin, left, right);
		quicksort(a, mmin, left, i-1);
		quicksort(a, mmin, i+1, right);
	}


	//function that extracts the letters from a 'flood' matrix
	public static Letter[] extract(int[][] a, int h, int w, int k)
	{
		//create an array of letters
		Letter[] letters = new Letter[k-1];
		int[] mmin=new int[k-1];
		int[] jmin=new int[k-1];
		int[] jmax=new int[k-1];
		for (int i=1; i<k; i++)
		{
			//find out the maximum and minimum coordinates of each letter
			jmin[i-1]=h;
			jmax[i-1]=0;
			mmin[i-1]=w;
			int mmax=0;
			for (int j=0; j<h; j++)
			{
				for (int m=0; m<w; m++)
				{
					if ((a[j][m]==i)&&(j<jmin[i-1])) 
						jmin[i-1]=j;
					if ((a[j][m]==i)&&(j>jmax[i-1])) 
						jmax[i-1]=j;
					if ((a[j][m]==i)&&(m<mmin[i-1])) 
						mmin[i-1]=m;
					if ((a[j][m]==i)&&(m>mmax)) mmax=m;
				}
			}

			//create a smaller matrix just fitting the letter
			int[][] x = new int[jmax[i-1]-jmin[i-1]+1][mmax-mmin[i-1]+1];

			//put 1's where the letter's pixels were
			for (int j=0; j<h; j++)
			{
				for (int m=0; m<w; m++)
				{
					if (a[j][m]==i) 
					{
						x[j-jmin[i-1]][m-mmin[i-1]]=1;
					}
				}
			}

			letters[i-1] = new Letter(mmin[i-1], jmin[i-1], x);
		}

		int b=0;

//		//sort the letters
//		for (int i=0; i<k-2; i++)
//		{
//			if (jmax[i]<jmin[i+1])
//			{
//				//sort the objects on the same line so that they are in the right order
//				quicksortgen(letters, b, i, mmin);
//				b=i+1;
//			}
//		}
//		quicksortgen(letters, b, k-2, mmin);

		return letters;
	}

	/**
	 * 
	 * @param a
	 * @param H
	 * @param W
	 * @return
	 */
	public static Letter scale(Letter a,int H, int W)
	{
		int h=a.height();
		int w=a.width();
		int[][] m=new int[H][W];
		for (int i=0; i<H; i++){
			for (int j=0; j<W; j++){
				double x=(h*i)/H;
				double y=(w*j)/W;
				int ii=(int) Math.round(x);
				int jj=(int) Math.round(y);
				if ((ii>=0)&&(ii<h)&&(jj>=0)&&(jj<w))
					m[i][j]=a.get(ii,jj);
			}
		}
		return new Letter(a.getX(), a.getY(), m);
	}

	/**
	 * 
	 * @param a
	 * @param r
	 * @return
	 */
	public static int compare(Letter a, Letter r)
	{
		int dif=0;
		for (int i=0; i<a.height(); i++){
			for (int j=0; j<a.width(); j++){
				int x=a.get(i,j);
				int y=r.get(i,j);
				if (x!=y) dif++;
			}
		}
		return dif;
	}

	/**
	 * 
	 * @param reference
	 * @param letters
	 */
	public static void correlation(Letter[] reference, Letter[] letters)
	{
		int H=reference[0].height();
		int W=reference[0].width();
		//scale the letters
		for (int i=0; i< letters.length; i++){
			letters[i]=scale(letters[i], H, W);
		}
		//compare each letter to the database
		for (int i=0; i<letters.length; i++)
		{
			int min =H*W;
			for (int j=0; j<reference.length; j++)
			{
				int dif=compare(letters[i], reference[j]);
				if (dif<min) min=dif;
			}
			for (int j=0; j<reference.length; j++)
			{
				int dif=compare(letters[i], reference[j]);
				if (dif==min){
					System.out.print(reference[j].character());
				}
			}
		}
	}

	/**
	 * Test driver
	 * @param args
	 */
	public static void main(String[] args)
	{  
//		//read the database file
//		int N = 68;//StdIn.readInt();
//		int h = 24;//StdIn.readInt();
//		int w = 34;//StdIn.readInt();
//		Letter[] reference=new Letter[N];
//
//		//for every letter
//		for (int i=0; i<N; i++)
//		{
//			String c=StdIn.readString();
//			//System.out.println(c);
//			int[][] m=new int[h][w];
//			for (int j=0; j<h; j++)
//			{
//				String x= StdIn.readString(); 
//				//System.out.println(x.length());
//				for (int k=0; k<w; k++)
//				{
//					m[j][k]=Integer.parseInt(x.substring(k, k+1));   
//
//				}
//			}
//			reference[i]=new Letter(m, c.charAt(0));
//		}

		//read the file to be scanned
		String file=args[0];
		Picture pic=new Picture(file);
		pic.show();
		Picture pic2=blackwhite(file);
		
		//create a matrix of the same dimension as the picture, storing
		//the labels that say to which object each pixel corresponds 
		int[][] a=new int[pic2.height()][pic2.width()];
		int k=1;
		Stack s=new Stack();                  

		//floodfill until every pixel has been visited
		for (int i=0; i<pic2.height(); i++){
			for (int j=0; j<pic2.width(); j++){  
				if (a[i][j]==0) {
					Color c=pic2.get(j,i);
					if (c.equals(Color.WHITE)) {
						s.push(i,j);
						floodfillstack(pic2, a, s, c,k);      
						k++;
					}
				}
			}
		}

		//generate the array of letters
		Letter[] letters=new Letter[k-1];
		letters=extract(a,pic.height(),pic.width(),k);
		for (Letter letter : letters)
		{
			
		}

//		correlation(reference, letters);
	}
}
