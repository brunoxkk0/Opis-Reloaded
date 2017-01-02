package mcp.mobius.opis.api;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.SwingUI;

public enum TabPanelRegistrar implements ChangeListener {
    INSTANCE;

    private final HashMap<String, JTabbedPane> sections = new HashMap<>();
    private final HashMap<SelectedTab, ITabPanel> lookup = new HashMap<>();

    public JTabbedPane registerSection(String name) {
        JTabbedPane pane = new JTabbedPane();
        pane.addChangeListener(this);
        this.sections.put(name, pane);
        SwingUI.instance().getTabbedPane().addTab(name, pane);
        return pane;
    }

    public ITabPanel registerTab(ITabPanel panel, String name) {
        this.lookup.put(panel.getSelectedTab(), panel);
        SwingUI.instance().getTabbedPane().addTab(name, (JPanel) panel);
        return panel;
    }

    public ITabPanel registerTab(ITabPanel panel, String name, String section) {
        this.lookup.put(panel.getSelectedTab(), panel);
        this.sections.get(section).addTab(name, (JPanel) panel);
        return panel;
    }

    public ITabPanel getTab(SelectedTab refname) {
        return lookup.get(refname);
    }

    public JPanel getTabAsPanel(SelectedTab refname) {
        return (JPanel) lookup.get(refname);
    }

    public void refreshAll() {
        this.lookup.values().stream().filter((panel) -> (panel.refreshOnString())).forEachOrdered((panel) -> {
            panel.refresh();
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Component source = ((JTabbedPane) e.getSource()).getSelectedComponent();

        if (source instanceof ITabPanel) {
            ITabPanel panel = (ITabPanel) source;
            PacketManager.sendToServer(new PacketReqData(Message.SWING_TAB_CHANGED, new SerialInt(panel.getSelectedTab().ordinal())));
        }

    }
}
