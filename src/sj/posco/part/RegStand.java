package sj.posco.part;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;

import sj.posco.model.TbStand;

public class RegStand extends Dialog {

	final Font font = SWTResourceManager.getFont("Calibri", 13, SWT.NORMAL);
	final Font fonth = SWTResourceManager.getFont("맑은 고딕", 13, SWT.NORMAL);
	List<TbStand> tempList;
	EntityManager em = AppMain.emf.createEntityManager();
	TableViewer tv;

	private class ContentProvider implements IStructuredContentProvider {
		/**
		 * 
		 */
		@Override
		public Object[] getElements(Object input) {
			return ((List<TbStand>) input).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected RegStand(Shell parentShell) {
		super(parentShell);

		// TODO Auto-generated constructor stub
	}

	String[] cols = { "", "Stand No", "", "경고온도", "", "위험온도", "", "표시범위(시간)" };
//	String[] PROPS = { "","1", "2", "3","4"}; 

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("나눔고딕코딩", 12, SWT.NORMAL));
		// TODO Auto-generated method stub
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(container);

		Composite titcomp = new Composite(container, 0);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(titcomp);
		GridLayoutFactory.fillDefaults().numColumns(7).equalWidth(false).spacing(1, 1).applyTo(titcomp);

		CLabel lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText(cols[1]);
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(1, 2).hint(80, -1)
				.applyTo(lbl1);

		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText(cols[3]);
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(2, 1).hint(120, -1)
				.applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText(cols[5]);
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(2, 1).hint(120, -1)
				.applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText(cols[7]);
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(2, 1).hint(120, -1)
				.applyTo(lbl1);

		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경전");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경후");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경전");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경후");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경전");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);
		lbl1 = new CLabel(titcomp, SWT.PUSH);
		lbl1.setText("변경후");
		lbl1.setAlignment(SWT.CENTER);
		lbl1.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lbl1);

		tv = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		tv.setUseHashlookup(true);
		Table table = tv.getTable();

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
//		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);

		table.setHeaderBackground(AppMain.coltblh);

		int[] iw = { 0, 82, 65, 65, 65, 65, 65, 65 };

		TableViewerColumn tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
		tvcol.getColumn().setWidth(0);

		for (int i = 1; i < iw.length; i++) {
			tvcol = new TableViewerColumn(tv, SWT.NONE | SWT.CENTER);
			tvcol.getColumn().setAlignment(SWT.CENTER);
			tvcol.getColumn().setWidth(iw[i]);

//			if ( i%2 != 0) tvcol (SWTResourceManager.getColor(SWT.COLOR_WHITE));
			tvcol.getColumn().setText(cols[i]);
		}

		tv.setContentProvider(new ContentProvider());
		tv.setLabelProvider(new MyLabelProvider());
		tv.setInput(tempList);
		tv.setColumnProperties(cols);

		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = (int) (event.gc.getFontMetrics().getHeight() * 1.8);
			}

		});

		// CellEditor 생성
		CellEditor[] CELL_EDITORS = new CellEditor[cols.length];
		for (int i = 1; i < CELL_EDITORS.length; i++) {
			CELL_EDITORS[i] = new TextCellEditor(table);
		}
		tv.setCellEditors(CELL_EDITORS);
		tv.setCellModifier(new MyCellModifier(tv));
		refreshList();

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "저장", true).setFont(fonth);
		createButton(parent, IDialogConstants.CANCEL_ID, "취소", false).setFont(fonth);

//        parent.getShell().pack();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 500);
	}

	@Override
	protected void okPressed() {

		em.getTransaction().begin();
		for (TbStand m : tempList) {
			em.merge(m);
		}
		em.getTransaction().commit();
		em.close();
		super.okPressed();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Stand No 등록");
	}

	public void refreshList() {
		tempList = new ArrayList<TbStand>();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		tempList = em.createQuery("select t from TbStand t ", TbStand.class).getResultList();

		tv.setInput(tempList);
		tv.refresh();

//		}

	}

	private class MyLabelProvider implements ITableLabelProvider {
		/**
		 * Returns the image
		 * 
		 * @param element     the element
		 * @param columnIndex the column index
		 * @return Image
		 */

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * Returns the column text
		 * 
		 * @param element     the element
		 * @param columnIndex the column index
		 * @return String
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			TbStand tbstand = (TbStand) element;
			switch (columnIndex) {
			case 1:
				return tbstand.getStandNo() + "";
			case 2:
				return tbstand.getTempW() + "";
			case 3:
				return tbstand.getTempW() + "";
			case 4:
				return tbstand.getTempD() + "";
			case 5:
				return tbstand.getTempD() + "";
			case 6:
				return tbstand.getXrang() + "";
			case 7:
				return tbstand.getXrang() + "";
			}
			return null;
		}

		/**
		 * Adds a listener
		 * 
		 * @param listener the listener
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
			// Ignore it
		}

		@Override
		public void dispose() {
			// Nothing to dispose
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// Ignore
		}
	}

	private class MyCellModifier implements ICellModifier {
		private Viewer viewer;

		public MyCellModifier(Viewer viewer) {
			this.viewer = viewer;
		}

		/**
		 * Returns whether the property can be modified
		 * 
		 * @param element  the element
		 * @param property the property
		 * @return boolean
		 */
		@Override
		public boolean canModify(Object element, String property) {

			if (property.equals(cols[3]))
				return true;
			else if (property.equals(cols[5]))
				return true;
			else if (property.equals(cols[7]))
				return true;
			else
				return false;
		}

		/**
		 * Returns the value for the property
		 * 
		 * @param element  the element
		 * @param property the property
		 * @return Object
		 */
		@Override
		public Object getValue(Object element, String property) {
			TbStand m = (TbStand) element;
			if (property.equals(cols[1]))
				return m.getStandNo();
			else if (property.equals(cols[3]))
				return m.getTempW() + "";
			else if (property.equals(cols[5]))
				return m.getTempD() + "";
			else if (property.equals(cols[7]))
				return m.getXrang() + "";
			else
				return null;
		}

		/**
		 * Modifies the element
		 * 
		 * @param element  the element
		 * @param property the property
		 * @param value    the value
		 */
		@Override
		public void modify(Object element, String property, Object value) {
			if (value == null)
				return;
			if (value.toString().isEmpty())
				return;

			if (element instanceof Item)
				element = ((Item) element).getData();

			TbStand m = (TbStand) element;
			if (property.equals(cols[3]))
				m.setTempW(Float.valueOf(value.toString()));
			else if (property.equals(cols[5]))
				m.setTempD(Float.valueOf(value.toString()));
			else if (property.equals(cols[7]))
				m.setXrang(Float.valueOf(value.toString()));

			// Force the viewer to refresh
			viewer.refresh();
		}
	}

}
