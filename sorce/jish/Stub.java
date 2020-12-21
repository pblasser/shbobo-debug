import java.awt.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.undo.*;
import java.io.*;

public class Stub extends Container 
implements DragGestureListener,DropTargetListener,
DragSourceListener,Transferable,
KeyListener , ComponentListener,
FocusListener, MouseListener { 
 static Stub draggerman;
 static UndoManager um;
 boolean scoot, fudge;
 static {
 	um = new UndoManager();
 }
 DragGestureRecognizer dgr;
 DropTarget dt;
 int cumulopasty;
 Stub() {
 	cumulopasty = 0;
	 dgr = DragSource.getDefaultDragSource()
	  .createDefaultDragGestureRecognizer(this,
	  DnDConstants.ACTION_MOVE, this);
	 setVisible(true);
	 setSize(new Dimension(20,50));
	 //setLayout(this);//new FlowLayout());
	 
	 addKeyListener(this);
	 setFocusable(true);
	 addFocusListener(this);
	 addMouseListener(this);
	 addComponentListener(this);
	 //requestFocusInWindow();

 }	

 public void addNotify() {
	 super.addNotify();
	 dt = new DropTarget(this, DnDConstants.ACTION_NONE, this, true);
 }
 public void removeNotify() {
 	super.removeNotify();
 	dt.removeDropTargetListener(this);
 }
 public void visitPastry(Recipe r) {
  r.cumulopasty += cumulopasty;
 }

   public int rand() {
    return (int)(Math.random()*256);
   }
  public BufferedImage imago() {
    BufferedImage r = new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB);
    int n = rand()%256;
   r.setRGB(1,1,new Color(n,n,0,64).getRGB()); n = rand();
  r.setRGB(0,0,new Color(n,n,0,64).getRGB());n = rand();
  r.setRGB(0,1,new Color(n,n,0,64).getRGB());n = rand();
  r.setRGB(1,0,new Color(n,n,0,64).getRGB());
  return r;//.getScaledInstance(rand()%16+1,rand()%16+1,Image.SCALE_FAST);
  }


 public void stubbornPaint(Graphics g) {
	 Graphics2D g2D = (Graphics2D) g;
	 if (isFocusOwner()) {
	 	TexturePaint tp = new TexturePaint(imago(), new Rectangle(0,0,(int)(Math.random()*10+1),10));
   g2D.setPaint(tp);
   //g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.25f));
  // g2D.fill(r);
	 }
	 
 }

 //DragGestureListener
	public void dragGestureRecognized(DragGestureEvent dge) {
		BufferedImage i = new BufferedImage(getWidth(),getHeight(),
		 BufferedImage.TYPE_INT_ARGB);
		Graphics g  = i.createGraphics();
		System.out.println("draggest");
		paint(g);
		g.dispose();
		DragSource ds = dge.getDragSource();
		fudge=scoot=true;
		ds.startDrag(dge,Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR),
		 i, new Point(10,10), this, this); 
		draggerman = this;
	}

 public void chng(String s) {}
 //DragSourceListener
	public void dragDropEnd(DragSourceDropEvent dsde) {

	}
	public void dragEnter(DragSourceDragEvent dsde) {}
 public void dragExit(DragSourceEvent dse) {}
 public void dragOver(DragSourceDragEvent dsde) {}
 public void dropActionChanged(DragSourceDragEvent dsde) {}

 //DropTargetListener
	public void dragEnter(DropTargetDragEvent dtde) {}
	public void dragOver(DropTargetDragEvent dtde) {
		 requestFocusInWindow();
   int xer = dtde.getLocation().x;
   if (xer>getWidth()*2/3) { 
   	scoot = false; fudge = true;
   } else if (xer < getWidth()/3)  {
    fudge=false; scoot=true;
   } else {
    fudge =scoot = false;
   } repaint();
	}
	public void dropActionChanged(DropTargetDragEvent dtde) {}
	public void dragExit(DropTargetEvent dte) {
		fudge = scoot = true;
  repaint();
	}
	public boolean isParentOf(Container curse) {
   while (curse!=null) {
   	if (this == curse) return true; 
   	curse = curse.getParent();
   }
   return false;
	}
	public void stubbornDrop(Stub s) {

	}
	public void drop(DropTargetDropEvent dtde) {
		Stub s = null;
		s = draggerman;
	//	try { s
	//		=(Stub)(dtde.getDropTargetContext().getComponent());
			// = (Stub)(dtde.getTransferable().getTransferData(new DataFlavor(this.getClass(),"stub")));

	//		}
	//		 catch (Exception e) {System.out.println(e.getMessage());}
		if ((s==null)||s.isParentOf(this)) {dtde.rejectDrop(); return;}
		System.out.println("stub"+s.cumulopasty);
		um.addEdit(new UndoTake(s));
	dtde.acceptDrop(DnDConstants.ACTION_MOVE);
		
		um.addEdit(new UndoFudge(s,this));
		fudge = scoot = true;
		validate();

	}

 //Transferable
	public Object getTransferData(DataFlavor flavor) { return this; }
 public DataFlavor[] getTransferDataFlavors() {return new DataFlavor[]{};}
 public boolean isDataFlavorSupported(DataFlavor flavor) { return true; }




 public void boundsVisit(Rectangle r) {
 	validate();
  if (getHeight()>r.y) r.y=getHeight();
  setLocation(r.x,r.height);
  r.x += getWidth();
  if (r.x>r.width) r.width=r.x;
 }
 public void visitInsert(Stub s) {
   um.addEdit(new UndoFudge(s,this));
  }
  public void visitAlnum(char c) {
   System.out.println("nothere");
  }
  public void keyPressed(KeyEvent e) { }
  public void keyReleased(KeyEvent e) {}

  public static Stub parze(int c) {
   if (c=='(') return new Bloque(0);
   if (c=='{') return new Bloque(1);
   if (c=='[') return new Bloque(2);
   if (c=='<') return new Bloque(3);
   if (c==';') return new Bubble(true);
   if (c=='\n') return new Bubble(false);
   if ((c==')')||(c=='}')||(c==']')||(c=='>')) 
   	return null;
   if (c==32) return new Guts();
   if (c>32) return new Guts(c);
   return null;
  }
 public void keyTyped(KeyEvent e) {
  	char c = e.getKeyChar();
   Stub s = parze(c);
   if (s!=null) visitInsert(s);
   if (c==8) um.addEdit(new UndoTake(this));
 }

 public void parze(PushbackReader pr) {}
 //focuslistener
  public void focusGained(FocusEvent e) {
   repaint();
  }
  public void 	focusLost(FocusEvent e) {
  	repaint();
  }
 
public void  componentHidden(ComponentEvent e) {}
public void  componentMoved(ComponentEvent e) {}
 public void  componentResized(ComponentEvent e) {
   Julia j = getJulia();
   if (j!=null) j.validate();

 }
 public Julia getJulia() {
 	Container verse=this;
 	while (verse instanceof Julia != true) {
 		verse = verse.getParent();
 		if (verse == null) return null;
 	} return (Julia)verse;
 }

public void  componentShown(ComponentEvent e) {}

  //mouse listener
  public void 	mouseClicked(MouseEvent e) {
  	fudge = scoot = true;
  	requestFocusInWindow();
  	//System.out.println("hutslabelclick");
  }
  public void 	mouseEntered(MouseEvent e) {}
 public void 	mouseExited(MouseEvent e) {}
 public void 	mousePressed(MouseEvent e) {}
 public void 	mouseReleased(MouseEvent e) {}

}