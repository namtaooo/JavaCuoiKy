package view;

import java.awt.Component;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor{

	private final JButton button;
    private final IntConsumer onClick;
    private int row;

    public ButtonEditor(String label, IntConsumer onClick) {
        this.onClick = onClick;
        this.button = new JButton(label);
        this.button.addActionListener(e -> {
            onClick.accept(row);
            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
