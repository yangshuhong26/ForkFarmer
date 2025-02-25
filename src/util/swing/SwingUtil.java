package util.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

import util.LimitedQueue;

public class SwingUtil {
	
	public static <T> List<T> getSelected(JTable t,List<T> list) {
		
		List<T> res = new ArrayList<>();
		for (int i : t.getSelectedRows()) {
			if (i < list.size()) {
				T obj = list.get(t.convertRowIndexToModel(i));
				if (null != obj) {
					res.add(obj);
				}
			}
		}
		return res;
	}

	public static <T> void removeSelected(JTable t, List<T> list) {
		int selectedRow = t.getSelectedRow();
		
		list.removeAll(getSelected(t,list));
		((AbstractTableModel)t.getModel()).fireTableDataChanged();
		if (selectedRow > list.size()-1)
			selectedRow = list.size()-1;
		if (-1 != selectedRow)
			t.setRowSelectionInterval(selectedRow, selectedRow);
	}

	public static void mapChanges(JTextComponent tc, Consumer<String> c) {
		tc.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				c.accept(tc.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				c.accept(tc.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				c.accept(tc.getText());
			}
			
		});
		
	}
	
	public static DocumentListener addDocListener(Consumer<DocumentEvent> deConsumer ) {
		return new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent de) {
				deConsumer.accept(de);
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				deConsumer.accept(de);
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				deConsumer.accept(de);
			}
			
		};
	}
	
	
	public static void addSelectionListener(JTable table, Consumer<Boolean> wasSelected)
	{
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				if (lse.getValueIsAdjusting())
					return;
				wasSelected.accept(-1 != table.getSelectedRow());
			}
		});
	}
	
	@SuppressWarnings("serial")
	public static class NoBorderTableRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(noFocusBorder);
			return this;
		}
	}

	public static void addDoubleClickAction(JTable t, Consumer<Integer> rowConsumer) {
		t.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		        JTable table =(JTable) mouseEvent.getSource();
		        Point point = mouseEvent.getPoint();
		        int row = table.rowAtPoint(point);
		        if (-1 == row)
		        	return;
		        if (mouseEvent.getClickCount() == 2)
		        	rowConsumer.accept(row);
		    }
		});
	}
	
	public static <T> void addDoubleClickAction(JTable t, LimitedQueue<T> l, Consumer<T> rowConsumer) {
		t.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		        JTable table =(JTable) mouseEvent.getSource();
		        Point point = mouseEvent.getPoint();
		        int row = table.rowAtPoint(point);
		        if (-1 == row)
		        	return;
		        if (mouseEvent.getClickCount() == 2)
		        	rowConsumer.accept(l.get(row));
		    }
		});
	}

	public static void setComponentsEnabled(Component[] cArray, boolean status) {
		for (Component c : cArray)
			c.setEnabled(status);
	}
	
}
