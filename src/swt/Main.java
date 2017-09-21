package swt;

import java.util.Arrays;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Main {

	static int[] data = new int[0];

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Table table = new Table(shell, SWT.BORDER | SWT.VIRTUAL|SWT.CHECK);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event e) {
				TableItem item = (TableItem) e.item;
				int index = table.indexOf(item);
				item.setText("Item " + data[index]);
			}
		});

		Thread thread = new Thread() {
			public void run() {
				int count = 0;
				Random random = new Random();
				while (count++ < 500) {
					if (table.isDisposed())
						return;
					// add 10 random numbers to array and sort
					int grow = 100;
					int[] newData = new int[data.length + grow];
					System.arraycopy(data, 0, newData, 0, data.length);
					int index = data.length;
					for (int j = 0; j < grow; j++) {
						newData[index++] = Math.abs(random.nextInt());
					}
					Arrays.sort(newData);
					display.syncExec(new Runnable() {
						public void run() {
							if (table.isDisposed())
								return;
							data = newData;
							table.setItemCount(data.length);
							table.clearAll();
						}
					});
					try {
						Thread.sleep(1);
					} catch (Throwable t) {
					}
				}
			}
		};
		thread.start();

		shell.pack();
		shell.setSize(200, 200);
		shell.setLocation(center(display, shell));
		shell.open();
		Display.getDefault().asyncExec(() -> {
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static Point center(Display display, Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		Point location = new Point(x, y);
		return location;
	}

}
