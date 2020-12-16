package sj.posco.part;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import sj.posco.model.Motestatus;

public class TitleComm  {

	Image img_act = AppMain.resizeImg( SWTResourceManager.getImage("images/icon_active.png"), 25,25);
	Image img_inact = AppMain.resizeImg( SWTResourceManager.getImage("images/icon_inactive.png"), 25,25);
	Image img_discon = AppMain.resizeImg( SWTResourceManager.getImage("images/icon_discon.png"), 25,25);
	Image img_danger = AppMain.resizeImg( SWTResourceManager.getImage("images/icon_danger.png"), 25,25);
	Image img_warn = AppMain.resizeImg( SWTResourceManager.getImage("images/icon_warn.png"), 25,25);
	Image img_lowb = AppMain.resizeImg( SWTResourceManager.getImage("images/lowbatt.png"), 25,25);
    CLabel[] tit_lbl = new CLabel[10]; 
	List<Motestatus > motelist ;

	public TitleComm(Composite parent ) {
		
		GridLayoutFactory.fillDefaults().numColumns(5).equalWidth(true).applyTo(parent);
		parent.setBackground(SWTResourceManager.getColor(247,247,255));
		
		for (int i=0; i< tit_lbl.length; i++) {
			tit_lbl[i] = new CLabel(parent,SWT.RIGHT_TO_LEFT | SWT.LINE_DASH) ;
			tit_lbl[i].setImage(img_inact);
			tit_lbl[i].setText("X" + (i<5 ? "T":"B"));
			tit_lbl[i].setFont(SWTResourceManager.getFont("Calibri", 15, SWT.NORMAL));
			tit_lbl[i].setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			tit_lbl[i].setMargins(5, 10, 5, 10);
		}
		refreshMote();
	}
	
	public void refreshMote() {
		EntityManager em = AppMain.emf.createEntityManager();
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
		
		motelist = em.createNamedQuery("Motestatus.sensorList", Motestatus.class).getResultList().stream()
				.sorted(Comparator.comparing(Motestatus::getStand)).collect(Collectors.toList());  ;
		
		em.close();
		int i=0 ,j=5;
		for ( Motestatus mote : motelist) {
			int x = ("T".equals(mote.getTb() ) ? i++ : j++) ;
			if (x < tit_lbl.length) {
				tit_lbl[x].setText( mote.getStand()) ;
				if (mote.getAct() == 0) 
					tit_lbl[x].setImage(img_inact);
				else if (mote.getStatus() == 0)
					tit_lbl[x].setImage(img_act);
				else if (mote.getStatus() == 1)
					tit_lbl[x].setImage(img_warn);
				else
					tit_lbl[x].setImage(img_danger);
			}
		}
		
	}

	public List<Motestatus> getMoteList() {
		return motelist ;
	}

}
