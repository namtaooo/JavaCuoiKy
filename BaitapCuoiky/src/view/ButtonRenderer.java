package view;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JButton implements TableCellRenderer {
	public ButtonRenderer(String text) {
        setText(text);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        return this;
    }

}
