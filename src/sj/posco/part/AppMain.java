package sj.posco.part;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import sj.posco.model.FindMoteInfo;
import sj.posco.model.Moteconfig;
import sj.posco.model.Motestatus;

public class AppMain extends ApplicationWindow {

    public static EntityManagerFactory emf ;
    
    final public static Cursor handc = SWTResourceManager.getCursor( SWT.CURSOR_HAND);
    final public static Cursor busyc = SWTResourceManager.getCursor( SWT.CURSOR_WAIT);
    public static Moteconfig MOTECNF = Moteconfig.getInstance() ;
    
    public String[] stand ; // = {"1T","1B","2T","2B","3T","3B"} ;
    public String[] sno ; 
    public String[] bno ; 
	final public static Color colact = SWTResourceManager.getColor(49,134,255) ;
	final public static Color colinact = SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY) ;
	final public static Color collow = SWTResourceManager.getColor(245,174,0) ;
	final public static Color colout = SWTResourceManager.getColor(SWT.COLOR_RED) ;
	final public static Color coltblh = SWTResourceManager.getColor(222,239,247) ;
	
	final Font font2b = SWTResourceManager.getFont("Calibri", 14, SWT.BOLD);

	public IcallFunc callf = null; 
	public static Composite cur_comp ;
	public static AppMain appmain ;
	final Image img_top = SWTResourceManager.getImage("images/dashboard_t.png");
    Label lblDate;
    Label lblinterval;
    TitleComm titlecomm ;
    FindMoteInfo findMoteinfo = new FindMoteInfo() ;
	List<Motestatus > motelist ;

	/**
	 * Create the application window.
	 */
	public AppMain() {
		super(null);
        String dbip = System.getProperty("DBIP") ;  // 주소:port
        if (dbip == null) dbip = "localhost:3306" ;
        String sEtc = System.getProperty("DBOPT") ;
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.url", "jdbc:mariadb://" + dbip + "/sjdb"  
        			+ (sEtc != null ? sEtc  :"") );
        properties.put("javax.persistence.jdbc.driver","org.mariadb.jdbc.Driver") ;
        emf = Persistence.createEntityManagerFactory("sj.posco", properties);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		
		reloaddata();
		appmain = this ;
//		addStatusLine();
	}

	public void reloaddata() {
		EntityManager em = emf.createEntityManager();
		MOTECNF = em.createNamedQuery("Moteconfig.findAll", Moteconfig.class).getSingleResult() ;
		
		List<String> listStand = 
				em.createNativeQuery("WITH tt AS (SELECT 'T' loc UNION SELECT 'B') SELECT cast(concat(a.standno,t.loc) as char(2))  FROM tb_stand a, tt as t" ) 
				.getResultList() ;
		stand = new String[listStand.size() ] ;
		stand =  listStand.toArray( stand ) ;

		List<Object[]> listSno = 
				em.createNativeQuery("select lpad(m.seq, 2,' '),lpad(m.bno,2,' ') from Motestatus m where m.gubun = 'S' and m.spare = 'N' order by m.seq") 
				.getResultList() ;

		sno =  listSno.stream().map(a -> a[0].toString()).sorted().toArray( String[]::new ) ;
		bno =  listSno.stream().map(a -> a[1].toString()).sorted().toArray( String[]::new ) ;
		
		em.close();
	}
	
	public void delWidget(Composite parent) {
		if (callf != null) callf.finalFunc();
		callf = null ;
		
	    for (Control kid : parent.getChildren()) {
	    	try {
		        kid.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
//	    System.gc();
	}

	public Timestamp time_c = Timestamp.valueOf("1900-01-01 00:00:00") ;
	private Timestamp time_sv = Timestamp.valueOf("1900-01-01 00:00:00") ;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Thread thread1  ;
	private class MyThread extends Thread {
		private Display display = null;
		private int interval ;
		MyThread(Display display, int interval){
			this.display = display ;
			this.interval = interval ;
		}
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted() && !lblinterval.isDisposed()) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						refreshData();
					}
				});
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
//					e.printStackTrace();
				}

			}
		}

	}

	public void refreshData() {
	    
//		Cursor cursor = AppMain.cur_comp.getCursor() ;
//		AppMain.cur_comp.setCursor(AppMain.busyc);
		time_c = findMoteinfo.getLasTime();
		if ( time_c.equals(time_sv)) return ;
//		System.out.println(time_c.toString() + " : " + time_sv.toString());
		time_sv = (Timestamp) time_c.clone() ;
		
		lblinterval.setText( MOTECNF.getMeasure() + "초 단위 수집");
		lblDate.setText(  dateFormat.format(time_c ) );
		lblDate.pack();

		titlecomm.refreshMote();
		if (callf != null )  callf.callFunc();
	}
	
	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
//		Composite container = new Composite(parent, SWT.NONE);
		Shell shell = getShell() ;
		shell.setBounds(2, 2, 1900, 1050);
//		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth(5);
		Composite comp1_1 = new Composite(sashForm, SWT.NONE) ;
		comp1_1.setLayout(new GridLayout(1, true));
		comp1_1.setBackgroundMode(SWT.INHERIT_FORCE);
		comp1_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
 
//		new MainMenu(comp1_1);
		menuCreate(comp1_1);
		comp1_1.pack();
		 
		Composite comp1_2 = new Composite(sashForm, SWT.NONE);
		comp1_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comp1_2.setBackgroundMode(SWT.INHERIT_FORCE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(comp1_2); 
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(comp1_2);
// ******************************
		Composite composite_t = new Composite(comp1_2, SWT.NONE);
//		composite_t.setLayoutData(new GridData(1600,60));

		composite_t.setLayout(new GridLayout(3,false) );
		Composite composite_t1 = new Composite(composite_t, SWT.NONE);
		GridDataFactory.fillDefaults().hint(300, -1).align(SWT.FILL, SWT.CENTER).applyTo(composite_t1);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(5, -1) .applyTo(composite_t1);
		lblDate = new Label(composite_t1, SWT.NONE);
		lblDate.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblDate.setFont(font2b);
		lblDate.setText("2019-02-02 00:00:00");

		lblinterval = new Label(composite_t1, SWT.NONE);
		lblinterval.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblinterval.setFont(font2b);
		lblinterval.setText("10초 단위 수집 ");
		
		Label ltit = new Label(composite_t, 0);
		ltit.setImage(img_top);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(ltit);
		
		Composite comp_t2 = new Composite(composite_t, SWT.BORDER_DASH);
//		comp_t2.setLayoutData(new GridData(GridData.END));
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, true).applyTo(comp_t2);
		titlecomm = new TitleComm(comp_t2) ;

		Label label = new Label(composite_t, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(3, 1).applyTo(label);

// ******************************		

		Composite comp1_21 = new Composite(comp1_2, SWT.NONE | SWT.MAX);
		cur_comp = comp1_21 ;		

		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(cur_comp); 
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(cur_comp);
		new DashBoard(cur_comp, SWT.NONE);
		
		comp1_21.requestLayout();
		
//		new RealChart(comp1_2, SWT.NONE);
//		new SensorPos(comp1_2, SWT.NONE);

		sashForm.setWeights(new int[] {18,90});

		cur_comp.setToolTipText("DashBoard");
		thread1 = new MyThread(Display.getCurrent(), MOTECNF.getMeasure() * 1000) ;
		thread1.start();

		return parent;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");

		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			AppMain window = new AppMain();
			window.setBlockOnOpen(true); 
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("POSCO 1선재");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1900, 1050);
	}
	private void menuCreate(Composite parent) {
		Image categoryicon_1 = SWTResourceManager.getImage( "images/categoryicon_1.png");
		categoryicon_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		Image categoryicon_2 = SWTResourceManager.getImage( "images/categoryicon_2.png");
		Image categoryicon_3 = SWTResourceManager.getImage( "images/categoryicon_3.png");
		Label lblDashboard;

		Font fontMenu = SWTResourceManager.getFont( "Tahoma", 16, SWT.BOLD);
		Color colorMenu = SWTResourceManager.getColor(77,89,104);

		Composite composite_4 = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().extendedMargins(40, -1, 15, 15).spacing(-1, 15).applyTo(composite_4);

		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label lblNewLabel = new Label(composite_4, SWT.NONE);
//		lblNewLabel.setLocation(40, 3);
		lblNewLabel.setSize(47, 33);
		lblNewLabel.setImage(categoryicon_1);

		lblDashboard = new Label(composite_4, SWT.BOLD);
		lblDashboard.setFont(SWTResourceManager.getFont( "Calibri", 22, SWT.BOLD));
		lblDashboard.setCursor(handc);
		lblDashboard.setText("Dashboard");
		lblDashboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (cur_comp.getToolTipText() == "DashBoard" ) return;
				delWidget(cur_comp);
				new DashBoard(cur_comp, SWT.NONE);
				cur_comp.requestLayout();
				cur_comp.setToolTipText("DashBoard");
			}
		});

		Label lblDash2 = new Label(composite_4, SWT.BOLD);
		lblDash2.setFont(fontMenu);
		lblDash2.setCursor(handc);
		lblDash2.setText("스탠드별 센서상태");
		lblDash2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				delWidget(cur_comp); 
				new DashBoard2(cur_comp, SWT.NONE);
				cur_comp.layout();
				cur_comp.setToolTipText("Status");
			}
		});

		// monitoring
		GridLayout gl_8 = new GridLayout(1, false);
		gl_8.marginLeft = 40;
		gl_8.marginTop = 15 ;
		gl_8.verticalSpacing = 15;
		gl_8.marginBottom = 15 ;
		Composite composite_8 = new Composite(parent, SWT.NONE);
		composite_8.setLayout(gl_8);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(composite_8);

		Label lblNewLabel4 = new Label(composite_8, SWT.NONE);
		lblNewLabel4.setSize(47, 33);
		lblNewLabel4.setImage(categoryicon_2);

		Label lblNewreal = new Label(composite_8, SWT.NONE);

		lblNewreal.setFont(fontMenu);
		lblNewreal.setCursor(SWTResourceManager.getCursor( SWT.CURSOR_HAND));
		lblNewreal.setText("센서별 이력조회");
		lblNewreal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				delWidget(cur_comp);
				new RealTime(cur_comp, SWT.NONE);
				cur_comp.layout();
				cur_comp.setToolTipText("Monitoring");

			}
		});

		lblNewreal = new Label(composite_8, SWT.NONE);
		lblNewreal.setFont(fontMenu);
		lblNewreal.setCursor(SWTResourceManager.getCursor( SWT.CURSOR_HAND));
		lblNewreal.setText("센서 변경이력조회");
		lblNewreal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				delWidget(cur_comp);
				new MoteChanged(cur_comp, SWT.NONE);
				cur_comp.layout();
				cur_comp.setToolTipText("MoteChanged");

			}
		});

		Label lblChart2 = new Label(composite_8, SWT.NONE);
		lblChart2.setCursor(SWTResourceManager.getCursor(  SWT.CURSOR_HAND));
		lblChart2.setFont(fontMenu);
		lblChart2.setText("데이터 분석");
		lblChart2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblChart2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				delWidget(cur_comp);
				cur_comp.setLayout(new FillLayout());
				new ViewChart(cur_comp, SWT.NONE);
				cur_comp.setSize(cur_comp.getParent().getSize());
				cur_comp.getParent().layout();
				cur_comp.setToolTipText("ViewChart");
			}
		});

		Composite composite_12 = new Composite(parent, SWT.NONE);
		composite_12.setLayout(new GridLayout(1, false));
		GridData gd_composite_12 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_12.heightHint = 50;
		composite_12.setLayoutData(gd_composite_12);


		Label label = new Label(composite_12, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
//		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		// configuration
		Composite composite_13 = new Composite(parent, SWT.NONE);
		
		GridLayoutFactory.fillDefaults().extendedMargins(40, -1, 15, 15).spacing(-1, 15).applyTo(composite_13);
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(composite_13);
//		composite_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		// composite_8.setBackground(new Color (Display.getCurrent(), 159, 170, 222));

		Label lblNewLabel8 = new Label(composite_13, SWT.NONE);
		lblNewLabel8.setLocation(40, 2);
		lblNewLabel8.setSize(42, 42);
//		lblNewLabel8.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel8.setImage(categoryicon_3);

		Label lblNewLabel11 = new Label(composite_13, SWT.NONE);
		lblNewLabel11.setCursor(SWTResourceManager.getCursor(  SWT.CURSOR_HAND));
		lblNewLabel11.setFont(fontMenu);
		lblNewLabel11.setText("무선장치 관리");
//		lblNewLabel11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel11.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				delWidget(cur_comp);
				new RegMote(cur_comp, SWT.NONE);
				cur_comp.layout();
				cur_comp.setToolTipText("RegMote");
			}
		});
		
		Label ldawin = new Label(parent, SWT.NONE);
		ldawin.setFont(SWTResourceManager.getFont( "Calibri", 12, SWT.ITALIC));
		ldawin.setText("CopyRight 2019 by DawinICT");
//		ldawin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		ldawin.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		ldawin.setLayoutData(new GridData(SWT.CENTER,SWT.BOTTOM,true,true));


	}	
	
	public static int sendReload( ) {

        String s = System.getenv("MONIP") ;
        if (s == null) s = "localhost" ;

		String url="http://"+ s + ":9977/reload"  ; 

		try {
			System.out.println("reload call") ;
			URL obj = new URL(url);
			URLConnection conn = obj.openConnection();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char[] buff = new char[512];
			
			while( ( br.read(buff)) != -1) {
				//	           System.out.print(new String(buff, 0, len));
			}
			br.close();
		}
		catch (Exception e) {
			System.out.println(url + " http send error !!") ;
		}
		return 1;
	}

	public static int sendMeasur(String meas ) {
        String s = System.getenv("MONIP") ;
        if (s == null) s = "localhost" ;
        System.out.println("MONIP:"+s);

		String url="http://"+ s + ":9977?meas=" + meas ;

		try {
			System.out.println(url) ;
			URL obj = new URL(url);
			URLConnection conn = obj.openConnection();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char[] buff = new char[512];

			while(  br.read(buff) != -1) {
				//	           System.out.print(new String(buff, 0, len));
			}
			br.close();
		}
		catch (Exception e) {
			System.out.println(url + " http send error !!") ;
		}
		return 1;
	}
	
    public static Image resizeImg(Image image, int width, int height) {
		  Image scaled = new Image(Display.getCurrent(), width, height);
		  GC gc = new GC(scaled);
		  gc.setAntialias(SWT.ON);
		  gc.setInterpolation(SWT.HIGH);
		  gc.drawImage(image, 0, 0,image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		  gc.dispose();
//		  image.dispose(); // don't forget about me!
		  return scaled;
	}
	
    public static void exportTable(TableViewer tv, int si, String fname )  {
        // TODO: add logic to ask user for the file location
    	
    	String[] ext = { "csv" }  ;
    	   final FileDialog dlg = new FileDialog ( Display.getDefault().getActiveShell() , SWT.APPLICATION_MODAL | SWT.SAVE );
    	    dlg.setFileName(fname);
    	    dlg.setFilterExtensions ( ext );
    	    dlg.setOverwrite ( true );
    	    dlg.setText ( "파일저장" );

    	    String fileName = dlg.open ();
    	    if ( fileName == null )
    	    {
    	        return ;
    	    }
    	    if (!fileName.matches("\\.csv$") ) fileName += ".csv" ;
//      File  file = new File(fileName + "." + ext[ dlg.getFilterIndex() ] );
    	    
    	    
//        BufferedWriter bw = new BufferedWriter(osw);
        
        try {
    		FileOutputStream fos = new FileOutputStream(fileName );
    		OutputStreamWriter osw = new OutputStreamWriter(fos, "MS949");
        	BufferedWriter writer = new BufferedWriter(new BufferedWriter(osw)) ;
            final Table table = tv.getTable();
            final int[] columnOrder = table.getColumnOrder();
 
            for(int columnOrderIndex = si; columnOrderIndex < columnOrder.length; 
                    columnOrderIndex++) {
                int columnIndex = columnOrder[columnOrderIndex];
                TableColumn tableColumn = table.getColumn(columnIndex);

                if (tableColumn.getText().equals("ID")) 
                	writer.write("'ID");
                else
                    writer.write(tableColumn.getText());
                if ( columnOrderIndex+1 < columnOrder.length ) writer.write(",");
            }
            writer.write("\r\n");
            
            final int itemCount = table.getItemCount();
            for(int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                TableItem item = table.getItem(itemIndex);
                
                for(int columnOrderIndex = si; 
                        columnOrderIndex < columnOrder.length; 
                        columnOrderIndex++) {
                    int columnIndex = columnOrder[columnOrderIndex];

                    writer.write(item.getText(columnIndex));
                    if ( columnOrderIndex+1 < columnOrder.length ) writer.write(",");
                }
                writer.write("\r\n");
            }
            writer.close();
        } catch(IOException ioe) {
            // TODO: add logic to inform the user of the problem
            System.err.println("trouble exporting table data to file");
            ioe.printStackTrace();
		}
        
    }

}
