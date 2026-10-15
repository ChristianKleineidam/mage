package mage.client.util.gui;

import mage.client.dialog.PreferencesDialog;
import mage.client.util.GUISizeHelper;
import org.junit.Assume;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class GuiRefreshThreadingTest extends GuiScalingTestSupport {
    @Test
    public void downloaderRefreshRunsOnTheEventDispatchThread() throws Exception {
        checkWorkerRefresh(false);
    }

    @Test
    public void themeRefreshRunsOnTheEventDispatchThread() throws Exception {
        checkWorkerRefresh(true);
    }

    private void checkWorkerRefresh(boolean reloadTheme) throws Exception {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        AtomicBoolean watching = new AtomicBoolean();
        AtomicBoolean offEdt = new AtomicBoolean();
        AtomicInteger updates = new AtomicInteger();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        JFrame[] host = new JFrame[1];
        JInternalFrame[] dialog = new JInternalFrame[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                JButton command = new JButton("Download") {
                    @Override
                    public void updateUI() {
                        if (watching.get()) {
                            offEdt.compareAndSet(false, !SwingUtilities.isEventDispatchThread());
                            updates.incrementAndGet();
                        }
                        super.updateUI();
                    }
                };
                JPanel controls = new JPanel();
                controls.add(new JComboBox<>(new String[]{"All sets", "Selected sets"}));
                controls.add(new JTextField("Images", 12));
                controls.add(command);
                JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Download images", new JScrollPane(controls));
                dialog[0] = new JInternalFrame("Download images", true);
                dialog[0].add(tabs);
                dialog[0].setSize(450, 200);
                dialog[0].setVisible(true);
                JDesktopPane desktop = new JDesktopPane();
                desktop.setPreferredSize(new Dimension(800, 500));
                desktop.add(dialog[0]);
                host[0] = new JFrame();
                host[0].setAutoRequestFocus(false);
                host[0].add(desktop);
                host[0].pack();
                layoutTree(host[0]);
                PreferencesDialog.saveValue(PreferencesDialog.KEY_GUI_DIALOG_FONT_SIZE, "34");
                watching.set(true);
                Thread downloader = new Thread(() -> {
                    try {
                        GUISizeHelper.refreshGUIAndCards(reloadTheme);
                    } catch (Throwable error) {
                        failure.set(error);
                    }
                }, "test-image-downloader");
                downloader.start();
                try {
                    downloader.join(5000);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(error);
                }
                assertFalse("The worker queues the refresh without waiting for the UI thread", downloader.isAlive());
                assertNull("The downloader completes without a Swing failure", failure.get());
                assertEquals("Controls stay installed while the UI thread is busy", 0, updates.get());
            });
            // Runs after the queued refresh, like repainting or dragging a dialog after download completion.
            SwingUtilities.invokeAndWait(() -> {
                assertTrue("The queued refresh updated the live controls", updates.get() > 0);
                assertFalse("Every UI delegate update runs on the event dispatch thread", offEdt.get());
                assertEquals(34, GUISizeHelper.dialogFont.getSize());
                JDesktopPane desktop = dialog[0].getDesktopPane();
                desktop.getDesktopManager().beginDraggingFrame(dialog[0]);
                desktop.getDesktopManager().dragFrame(dialog[0], 60, 40);
                desktop.getDesktopManager().endDraggingFrame(dialog[0]);
                layoutTree(host[0]);
                BufferedImage image = new BufferedImage(800, 500, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                try {
                    desktop.paint(graphics);
                } finally {
                    graphics.dispose();
                }
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                watching.set(false);
                if (host[0] != null) {
                    host[0].dispose();
                }
            });
        }
    }
}
