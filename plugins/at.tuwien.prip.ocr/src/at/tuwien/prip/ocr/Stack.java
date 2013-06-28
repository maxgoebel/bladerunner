package at.tuwien.prip.ocr;
public class Stack{
	public class Node{ int i; int j; Node next;};
	public Node first;
	
	public Stack(){
		this.first=null;
	}
	
	public boolean isEmpty(){
		return (this.first==null);
	}
	
	public void push(int i, int j){
		Node x=new Node();
		x.i=i;
		x.j=j;
		
		
		if (this.isEmpty()) {
			this.first=x;
		}
		else {
			x.next=this.first;
			this.first=x;
		}
	}
	
	public Node pop(){
		Node x=this.first;
		this.first=this.first.next;
		return x;
		
	}
}