package sj.posco.part;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;

import sj.posco.model.FindMoteInfo;
import sj.posco.model.Moteconfig;
import sj.posco.model.Motestatus;
import sj.posco.model.TbStand2;

public class MiniChart {
	
	final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
	final Color BLUE = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	final Color GREEN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
	final Color CYAN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN);
	
	Motestatus motestatus ;
	FindMoteInfo findMoteinfo = new FindMoteInfo();
	final Browser browser ;
	TbStand2 tbsB, tbsT ;
	/**
     * Create the composite.
     *
     * @param parent
     * @param style
     * @wbp.parser.entryPoint
     */

	int sensorno = 0;
	Label lblDate, lblTime,  lblfrom ,lblfromd, lblto, lbltod ;
	
	final CLabel  lbltit, lblmaxT, lblmaxB ;
	final int standno ;
	double py ;
	final Font font = SWTResourceManager.getFont("HY견고딕", 12, SWT.NORMAL);
	final Font font2 = SWTResourceManager.getFont("HY견고딕", 34, SWT.BOLD);
	final Color colact = SWTResourceManager.getColor(49,136,248) ;
	final Color colinact = SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY) ;
	final Color collow = SWTResourceManager.getColor(245,174,0) ;
	final Color colout = SWTResourceManager.getColor(SWT.COLOR_RED) ;
	
    public MiniChart(Composite parent, int standno, int hval, String tm ) {

    	this.standno = standno;

    	final Composite sash = new Composite(parent, SWT.NONE | SWT.NO_SCROLL) ;
    	
    	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(sash);
//    	sash.setSashWidth(3);
//    	sash.setBackground(SWTResourceManager.getColor(250, 250, 252));
    	Composite comp1 = new Composite(sash, SWT.LINE_DASH);
    	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(comp1);
    	GridDataFactory.fillDefaults().grab(false, true).align(SWT.FILL, SWT.TOP).applyTo(comp1);
    	comp1.setBackground(SWTResourceManager.getColor(250, 250, 252));
		comp1.setBackgroundMode(SWT.INHERIT_FORCE);

		Label l0 = new Label(comp1,SWT.SEPARATOR | SWT.HORIZONTAL );
		
    	lbltit = new CLabel(comp1, SWT.NONE) ;
    	lbltit.setText( "" + standno );
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(lbltit);
    	lbltit.setFont(font2);
    	lbltit.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
    	lbltit.setCursor(AppMain.handc);
    	lbltit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				EntityManager em = AppMain.emf.createEntityManager();

				List<Object[]> listSno = 
						em.createNativeQuery("select lpad(m.seq, 1,' '),lpad(m.bno,2,' ') from Motestatus m where m.gubun = 'S' and m.spare = 'N' and standno = ?1 ")
						.setParameter(1, standno)
						.getResultList() ;

				String[] bno =  listSno.stream().map(a -> a[1].toString()).toArray( String[]::new ) ;
				
				em.close();
				
				LocalDateTime ldt =  AppMain.appmain.time_c.toLocalDateTime() ;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String fdt =  ldt.minusHours((long) tbsB.getXrang()).format(formatter) ;
				
//				System.out.println(fdt);
				AppMain.appmain.delWidget(AppMain.cur_comp);
				AppMain.cur_comp.setLayout(new FillLayout());
				try {
					new ViewChart(AppMain.cur_comp, SWT.NONE, bno, fdt);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppMain.cur_comp.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				AppMain.cur_comp.setSize(AppMain.cur_comp.getParent().getSize());
				AppMain.cur_comp.getParent().layout();
				AppMain.cur_comp.setToolTipText("ViewChart");
			}
		});
    	
    	l0 = new Label(comp1,SWT.SEPARATOR | SWT.HORIZONTAL );
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(l0);
		
		lblmaxT = new CLabel(comp1, SWT.NONE) ;
		lblmaxT.setText( "99"  );
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(lblmaxT);
		lblmaxT.setFont(font);
		lblmaxT.setForeground(GREEN);
		l0 = new Label(comp1, SWT.NONE);
		l0.setText("Top Max");
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(l0);
		l0.setForeground(GREEN);
		
		lblmaxB = new CLabel(comp1, SWT.NONE) ;
		lblmaxB.setText( "99" );
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(lblmaxB);
		lblmaxB.setFont(font);
		lblmaxB.setForeground(RED);
		l0 = new Label(comp1, SWT.NONE);
		l0.setText("Bottom Max");
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.TOP).applyTo(l0);
		l0.setForeground(RED);

		tbsB = findMoteinfo.getTbStand(standno + "B");
		tbsT = findMoteinfo.getTbStand(standno + "T");
		
		browser = new Browser(sash, SWT.NONE);
    	GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(browser);
		browser.setBackground(SWTResourceManager.getColor(250, 250, 252));

		String s = System.getenv("MONIP") ;
        if (s == null) s = "localhost" ;

		browser.setUrl("http://"+ s + ":9977/chart01.html");

		
		browser.setJavascriptEnabled(true); 
		browser.addProgressListener(new ProgressAdapter() {
			  @Override
			public void completed(ProgressEvent event) {
				changeSz(hval, tm);
			  }
		});
		sash.layout();

//		parent.getShell().addListener(SWT.Resize, new Listener() {
//			
//			@Override
//			public void handleEvent(Event arg0) {
//		        Rectangle rect = browser.getClientArea ();
//		        changeSz(rect.y, tm);
//				
//			}
//		});
//    	sash.setWeights(new int[] {15,85});
//    	sash.setSashWidth(0);

    }
    
    
	public void refreshData(String tm) {
	
	  browser.execute("updChart(" + standno + "," + tm + ");");
	  EntityManager em = AppMain.emf.createEntityManager();
	  em.clear();
	  em.getEntityManagerFactory().getCache().evictAll();

	  Object[]  maxval = (Object[]) em.createNativeQuery(
			 "SELECT MAX(case when stand LIKE'%T' then temp END)  , \r\n" + 
	  		    "    MAX(case when stand LIKE'%B' then temp END ) " +
				" FROM moteinfo a join tb_stand b on (a.standno = b.standno)  " +
	  			   String.format("WHERE a.standno = %d AND tm BETWEEN DATE_ADD( '%s', INTERVAL -(b.xrang) HOUR) AND '%s' ", standno, tm, tm))
			  .getSingleResult() ;

	  lblmaxT.setText(String.format("%.1f", (double) (maxval[0]==null? 0.0 :maxval[0])   ));
	  lblmaxB.setText(String.format("%.1f", (double) (maxval[1]==null? 0.0 :maxval[1]) ));

	  lblmaxT.requestLayout();
	  lblmaxB.requestLayout();
	  em.close();

    }
	public void changeSz(int hval, String tm) {
		String val = String.join(",", hval+"" , standno+"", String.format("%.0f", tbsB.getXrang()), tm ) ;
//		System.out.println(val);
		browser.execute("changesize(" + val + ");");
	}
	
	public void refresh() {
		browser.refresh();
	}

}
