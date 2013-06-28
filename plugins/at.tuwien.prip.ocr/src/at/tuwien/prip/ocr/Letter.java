package at.tuwien.prip.ocr;
public class Letter
{
	private int[][] mat;
	private int width, height;
	private char ch;

	private int x, y;

	public Letter(int w, int h)
	{
		width=w;
		height=h;
		mat= new int[h][w];
		ch=' ';
	}

	public Letter(int x, int y, int[][] mat)
	{
		this.mat=mat;
		width=mat[0].length;
		height=mat.length;
		ch='*';
		this.x = x;
		this.y = y;
	}

	public Letter(int[][] x, char c)
	{
		mat=x;
		height=x.length;
		width=x[0].length;
		ch=c;
	}

	public int width(){
		return this.width;
	}

	public int height(){
		return this.height;
	}

	public char character(){
		return ch;
	}

	public void set(int i,int j){
		this.mat[i][j]=1;
	}

	public int get(int i, int j){
		return this.mat[i][j];
	}

	public int[][] getmat(){
		return this.mat;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void show(){
		for (int i=0; i<this.height; i++){
			for (int j=0; j<this.width; j++){
				System.out.print(this.mat[i][j]);
			}
			System.out.println();
		}
	}
}
