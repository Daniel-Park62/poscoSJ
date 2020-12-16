package sj.posco.part;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import sj.posco.model.FindMoteInfo;
import sj.posco.model.Moteinfo;
import sj.posco.model.Motestatus;

public class DashBoard {


	Image img_1 = SWTResourceManager.getImage("images/dashboard_1.png");
	Image img_c = SWTResourceManager.getImage("images/category.png");
	Image img_c1 = SWTResourceManager.getImage( "images/categoryicon_1.png");

	Image img_act =  SWTResourceManager.getImage("images/icon_active.png");
	Image img_inact =  SWTResourceManager.getImage("images/icon_inactive.png");
	Image img_discon =  SWTResourceManager.getImage("images/icon_discon.png");
	Image img_danger =  SWTResourceManager.getImage("images/icon_danger.png");
	Image img_warn = SWTResourceManager.getImage("images/icon_warn.png");
	Image img_lowb =  SWTResourceManager.getImage("images/lowbatt.png");
	Image img_setup =  SWTResourceManager.getImage("images/setupC.png");
	Image img_legend =  SWTResourceManager.getImage("images/legend.png");
	
    Label lblApActive;
    Label lblApInactive;
    Label lblTagActive;
    Label lblTagInactive;
    Label lblAlertActive;
    Label lblAlertInactive;
    
//    DateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");
    Font font1 = SWTResourceManager.getFont("HY견고딕", 24 , SWT.BOLD ) ;
    Font font2 = SWTResourceManager.getFont("HY견고딕", 14, SWT.NORMAL);
    Font font12 = SWTResourceManager.getFont("맑은 고딕", 12, SWT.NORMAL ) ;
    Font fontT = SWTResourceManager.getFont("Tahoma", 20, SWT.BOLD  ) ;
    Thread uiUpdateThread ;
    
    FindMoteInfo findMoteinfo = new FindMoteInfo() ;
	List<Motestatus > motelist ;

    MiniChart[] minic = new MiniChart[5];
    CLabel[][] mote_lbl = new CLabel[2][11]; 
    ArrayList<CLabel> clarr = new ArrayList<CLabel>();
    
	int activeCnt = 0;
	int inactiveCnt = 0;

	int activeSsCnt = 0;
	int failCnt = 0;
	int moteLBCnt = 0;
	int oBCnt = 0;

	private Timestamp time_c = Timestamp.valueOf("1900-01-01 00:00:00") ;
	private Timestamp time_sv = Timestamp.valueOf("1900-01-01 00:00:00") ;
    EntityManager em = AppMain.emf.createEntityManager();

	private Composite[] comp_s = new Composite[11];
    
	public DashBoard(Composite parent, int style) {

//		super(parent, style) ;
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
		
		Composite composite_l = new Composite(composite, SWT.NONE);
		composite_l.setLayout(new GridLayout(3,false) );
		Composite composite_l1 = new Composite(composite_l, SWT.NONE);
		composite_l1.setLayoutData(new GridData(420, 260));
		
		composite_l1.setBackgroundImage(img_1);
		Label lblT = new Label(composite_l1,SWT.NONE) ;
		lblT.setText("스탠드별 상태");
		lblT.setFont(fontT);
		lblT.setBounds(80, 5, 200, 40);
		
		int iy = 80 ;
		lblApActive = new Label(composite_l1, SWT.NONE);
		lblApActive.setAlignment(SWT.RIGHT);
		lblApActive.setFont(font2);
		lblApActive.setBounds(90, iy, 40, 20);
		lblApActive.setText("99");
		
		lblApInactive = new Label(composite_l1, SWT.NONE);
		lblApInactive.setAlignment(SWT.RIGHT);
		lblApInactive.setFont(font2);
		lblApInactive.setBounds(90, iy+25 , 40, 20);
		lblApInactive.setText("99");
		
		lblTagActive = new Label(composite_l1, SWT.NONE);
		lblTagActive.setAlignment(SWT.RIGHT);
		lblTagActive.setFont(font2);
		lblTagActive.setBounds(90, iy+90, 40, 20);
		lblTagActive.setText("99");
		
		lblTagInactive = new Label(composite_l1, SWT.NONE);
		lblTagInactive.setAlignment(SWT.RIGHT);
		lblTagInactive.setFont(font2);
		lblTagInactive.setBounds(90, iy+90+25, 40, 20);
		lblTagInactive.setText(" 0");
		
		lblAlertActive = new Label(composite_l1, SWT.NONE);
		lblAlertActive.setAlignment(SWT.LEFT);
		lblAlertActive.setFont(font2);
		lblAlertActive.setBounds(280, iy+10, 100, 20);
		lblAlertActive.setText(" 0      ");
		lblAlertActive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblAlertActive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblAlertActive.setCursor(AppMain.handc);

		lblAlertInactive = new Label(composite_l1, SWT.NONE);
		lblAlertInactive.setAlignment(SWT.LEFT);
		lblAlertInactive.setFont(font2);
		lblAlertInactive.setBounds(280, iy+40, 100, 20);
		lblAlertInactive.setText(" 0      ");
		lblAlertInactive.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblAlertInactive.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblAlertInactive.setCursor(AppMain.handc);
		
		Label btnlow = new Label(composite_l1, 0) ;
		
		btnlow.setBounds(240, iy+90, 60,60);
		btnlow.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
//		btnlow.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));

		Label btnob = new Label(composite_l1, 0) ;
		
		btnob.setBounds(320, iy+90, 60,60);
//		btnob.setFont( SWTResourceManager.getFont("Tahoma", 8, SWT.NORMAL ));
		
		btnlow.setCursor(AppMain.handc);
		btnob.setCursor(AppMain.handc);

		btnlow.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 1 ) ;
				valert.open() ;
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		btnob.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				ViewAlert valert = new ViewAlert(parent.getShell(), 2 ) ;
				valert.open() ;
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Composite composite_l2 = new Composite(composite_l, SWT.NONE);
		GridLayout gl_l2 = new GridLayout(11,true) ;
		gl_l2.marginHeight= 10;
		gl_l2.horizontalSpacing=20;		
		composite_l2.setLayout(gl_l2);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.TOP).grab(true, true).applyTo(composite_l2);
//		composite_l2.setBackground(SWTResourceManager.getColor(244,246,252));
		GridLayout gl_s = new GridLayout(1,false) ;
		gl_s.verticalSpacing=15 ;
		String[] snum = {"0","1","2","3","4","5","5A","6","7","8","9",""} ;
		CLabel lbl ;
		for (int i=0; i < gl_l2.numColumns ; i++) {
			comp_s[i] = new Composite(composite_l2, SWT.NONE) ;
			comp_s[i].setLayout(gl_s);
			lbl = new CLabel(comp_s[i], SWT.NONE ) ;
			lbl.setFont(font1);
			lbl.setText(snum[i]);
			lbl.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
			GridDataFactory.fillDefaults().align(SWT.CENTER,SWT.CENTER).applyTo(lbl);
			
			mote_lbl[0][i] = new CLabel(comp_s[i], SWT.NONE ) ;
			mote_lbl[0][i].setBackground(img_discon);
			mote_lbl[0][i].setText("");
			mote_lbl[0][i].setAlignment(SWT.CENTER);
			mote_lbl[0][i].setFont(font1);
			mote_lbl[0][i].setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			GridDataFactory.fillDefaults().align(SWT.CENTER,SWT.CENTER).hint(61, 62).applyTo(mote_lbl[0][i]);
			
			
			mote_lbl[1][i] = new CLabel(comp_s[i], SWT.NONE) ;
			mote_lbl[1][i].setBackground(img_discon);
			mote_lbl[1][i].setText("");
			mote_lbl[1][i].setAlignment(SWT.CENTER);
			mote_lbl[1][i].setFont(font1);
			mote_lbl[1][i].setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			GridDataFactory.fillDefaults().align(SWT.CENTER,SWT.CENTER).hint(61, 62).applyTo(mote_lbl[1][i]);
		}
		
		lbl = new CLabel(composite_l, SWT.NONE) ;
		lbl.setImage(img_c);
		lbl.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, true));
		lbl.setTopMargin(20);

		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(15, -1).spacing(20, -1).applyTo(composite_2);
		composite_2.setLayoutData(new GridData(GridData.FILL_BOTH));

		SashForm sash1 = new SashForm(composite_2, SWT.VERTICAL);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.FILL).grab(true, true).applyTo(sash1);
		
		String tm = ymd.format(findMoteinfo.getLasTime()) ;
		Composite comp_s = new Composite(sash1, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10, -1).applyTo(comp_s);
//		comp_s.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		CLabel lsetup = new CLabel(comp_s, SWT.NONE) ;
		lsetup.setImage(img_c1);
		lsetup.setText("실시간 온도 추이");
		lsetup.setFont(fontT);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.FILL).grab(true, true).span(2, 1).applyTo(lsetup);

		lsetup = new CLabel(comp_s, SWT.NONE) ;
		lsetup.setImage(img_setup);
		lsetup.setText("스탠드설정");
		lsetup.setFont(font12);
		lsetup.setCursor(AppMain.handc);
		GridDataFactory.fillDefaults().align(SWT.LEFT,SWT.FILL).grab(true, true).applyTo(lsetup);
		lsetup.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
				RegStand2 newmote = new RegStand2(parent.getShell() ) ;

				if (newmote.open() == Window.OK) {
					AppMain.appmain.reloaddata();
					for (MiniChart chart : minic) {
						chart.refresh();
					}
					refreshSensorList();
				}
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		lsetup = new CLabel(comp_s, SWT.NONE) ;
		lsetup.setImage(img_legend);
		GridDataFactory.fillDefaults().align(SWT.END,SWT.FILL).grab(true, true).applyTo(lsetup);

		minic[0] = new MiniChart(sash1, 1,220,tm);
		minic[1] = new MiniChart(sash1, 2,220,tm);
		sash1.setWeights(new int[] {20,50,50});
		SashForm sash2 = new SashForm(composite_2, SWT.VERTICAL);
		GridDataFactory.fillDefaults().align(SWT.FILL,SWT.FILL).grab(true, true).applyTo(sash2);
//		GridLayoutFactory.fillDefaults().margins(5, -1).applyTo(sash2);
		minic[2] = new MiniChart(sash2, 3,180,tm);
		minic[3] = new MiniChart(sash2, 6,180, tm);
		minic[4] = new MiniChart(sash2, 8,180, tm);

		sash1.setSashWidth(5);
		sash2.setSashWidth(5);
		
		refreshSensorList();
		AppMain.appmain.callf = new IcallFunc() {
			
			@Override
			public void callFunc() {
				refreshSensorList();
			}

			@Override
			public void finalFunc() {
				// TODO Auto-generated method stub
				uiUpdateThread.interrupt();
			}
		};
		
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
//                for(CLabel clbl : clarr ) {
//                	clbl.setVisible(false);
//                }
//                try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//                for(CLabel clbl : clarr ) {
//                	clbl.setVisible(true);
//                }
                System.out.println("*** jj ***" + clarr.size());
            }
        };
//        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//        service.scheduleAtFixedRate(runnable, 1, 2, TimeUnit.SECONDS);

		uiUpdateThread = new MyThread(Display.getCurrent() );
		uiUpdateThread.start();

	}

	private class MyThread extends Thread {
		private Display display = null;
		MyThread(Display display ){
			this.display = display ;
		}
		private boolean vb = false ;
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted() && !display.isDisposed()) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						if ( mote_lbl[0][0].isDisposed() )  return ;
		                for(CLabel clbl : clarr ) {
		                	clbl.setVisible(vb);
		                }
		                try {
							Thread.currentThread();
							Thread.sleep(900);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                vb = !vb ;
		                
		                if (clarr.size() == 0) {
		                	for(CLabel clbl :  mote_lbl[0] ) {
			                	clbl.setVisible(true);
			                }
		                	for(CLabel clbl :  mote_lbl[1] ) {
			                	clbl.setVisible(true);
			                }
		                }

					}
				});
				

			}
		}

	}

    final DateFormat ymd = new SimpleDateFormat("yyyyMMddHHmmss");

//	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
	    
//		Cursor cursor = AppMain.cur_comp.getCursor() ;
//		AppMain.cur_comp.setCursor(AppMain.busyc);
		time_c = findMoteinfo.getLasTime();
//		if ( time_c.equals(time_sv)) return ;
//		System.out.println(time_c.toString() + " : " + time_sv.toString());
		time_sv = (Timestamp) time_c.clone() ;
		
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		motelist = em.createQuery("select m from Motestatus m where m.spare = 'N'", Motestatus.class).getResultList() ;

		List<Moteinfo> moteinfo = em.createQuery("select m from Moteinfo m, LasTime l where m.tm = l.lastm ", Moteinfo.class)
				.getResultList() ;

		activeCnt = 0;
		inactiveCnt = 0;
		activeSsCnt = 0;
		failCnt = 0;
		moteLBCnt = 0;
		oBCnt = 0;

		activeCnt = (int) motelist.stream().filter(m -> m.getAct() == 2).count() ;
		inactiveCnt = (int) motelist.stream().filter(m -> m.getAct() != 2).count() ;
		activeSsCnt = (int) motelist.stream().filter(m -> m.getAct() == 2 && m.getGubun().equals("S") ).count() ;
		failCnt = (int) motelist.stream().filter(m -> m.getAct() != 2 && m.getGubun().equals("S") ).count() ;
		moteLBCnt = (int) motelist.stream().filter( m -> m.getBatt() > 0 && m.getBatt() < AppMain.MOTECNF.getBatt() ).count() ;
		oBCnt = (int) motelist.stream().filter(m -> m.getStatus() > 0 ).count() ;
		
		lblApActive.setText(activeCnt+"");
		lblApInactive.setText(inactiveCnt+"");

		lblTagActive.setText(activeSsCnt+"");
		lblTagInactive.setText(failCnt+"");
		lblAlertActive.setText(String.format  ("%2d  배터리", moteLBCnt));
		lblAlertInactive.setText(String.format("%2d   온도", oBCnt) );
		lblAlertActive.requestLayout();
		lblAlertInactive.requestLayout();
		clarr.clear();
		for(Motestatus mote: motelist) {
			int i = mote.getTb().equals("T") ? 0 : 1 ;
			int j = mote.getStandNo() + ( mote.getStandNo() > 5 ? 1 : 0) ;
			if (mote.getAct() == 0) 
				mote_lbl[i][j].setBackground(img_inact);
			else if (mote.getStatus() == 0)
				mote_lbl[i][j].setBackground(img_act);
			else if (mote.getStatus() == 1) {
				mote_lbl[i][j].setBackground(img_warn);
				clarr.add(mote_lbl[i][j]) ;
			} else {
				mote_lbl[i][j].setBackground(img_danger);
				clarr.add(mote_lbl[i][j]) ;
			}
//			if (mote.getBatt() < AppMain.MOTECNF.getBatt()) {
//				mote_lbl[i][mote.getStandNo()].setImage(img_lowb);
//			}
			
			Optional<Moteinfo> sensor = moteinfo.stream().filter(t -> t.getStand().equals(mote.getStand())).findAny() ;
			String temp = sensor.isPresent() && sensor.get().getTemp() != 0 ? String.format("%.0f",sensor.get().getTemp()) : "" ;
			mote_lbl[i][j].setText( temp );
			
		}

		String tm = ymd.format(time_c) ;
		for (MiniChart chart : minic) {
			chart.refreshData(tm);
		}
//		AppMain.cur_comp.setCursor(cursor);

	}
	

}