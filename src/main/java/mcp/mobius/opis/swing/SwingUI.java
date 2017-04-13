package mcp.mobius.opis.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;

import javax.swing.SwingConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.widgets.JButtonAccess;

public final class SwingUI extends JFrame implements WindowListener, ChangeListener, IMessageHandler {

    public static SwingUI INSTANCE = new SwingUI();
    
    public static SwingUI instance() {
        return INSTANCE;
    }
    
    public SwingUI() {
        setupWindow();
    }
    
    public boolean swingOpen = false;
    
    public HashSet<JButtonAccess> registeredButtons = new HashSet<>();

    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    
    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        swingOpen = false;
        PacketManager.sendToServer(new PacketReqData(Message.COMMAND_UNREGISTER_SWING));
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case STATUS_ACCESS_LEVEL: {
                AccessLevel level = AccessLevel.values()[((SerialInt) rawdata.value).value];
                registeredButtons.forEach((button) -> {
                    if (level.ordinal() < button.getAccessLevel().ordinal()) {
                        button.setEnabled(false);
                    } else {
                        button.setEnabled(true);
                    }
                });
                break;
            }
            case CLIENT_SHOW_SWING: {
                showUI();
                break;
            }
            default:
                return false;

        }
        return true;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Component source = ((JTabbedPane) e.getSource()).getSelectedComponent();

        if (source instanceof ITabPanel) {
            ITabPanel panel = (ITabPanel) source;
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }

        if (source instanceof JTabbedPane) {
            JTabbedPane pane = (JTabbedPane) source;
            ITabPanel panel = (ITabPanel) pane.getSelectedComponent();
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }
    }

    public void setupWindow() {
        setTitle("Opis Control Panel");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 893, 455);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.addChangeListener(this);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        this.addWindowListener(this);
        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(60 * 1000);
    }

    public void showUI() {
        EventQueue.invokeLater(() -> {
            swingOpen = true;
            setVisible(true);
            //Minecraft.getMinecraft().displayGuiScreen(new GuiChat());
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(SelectedTab.SUMMARY.ordinal())));
        });
    }

}
