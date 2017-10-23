package mcp.mobius.opis.swing.panels.tracking;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionPlayers;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

public class PanelPlayers extends JPanelMsgHandler implements ITabPanel {

    private final JButtonAccess btnTeleport;
    private final JButtonAccess btnPull;

    public PanelPlayers() {
        setLayout(new MigLayout("", "[][][][][][grow]", "[][grow]"));

        btnTeleport = new JButtonAccess("Teleport", AccessLevel.PRIVILEGED);
        add(btnTeleport, "cell 0 0");
        btnTeleport.addActionListener(new ActionPlayers());

        btnPull = new JButtonAccess("Pull", AccessLevel.PRIVILEGED);
        add(btnPull, "cell 1 0");
        btnPull.addActionListener(new ActionPlayers());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 6 1,grow");

        table = new JTableStats(
                new String[]{"Name", "Dimension", "Coordinates"},
                new Class[]{String.class, Integer.class, Object.class}
        );
        scrollPane.setViewportView(table);
    }

    public JButton getBtnTeleport() {
        return btnTeleport;
    }

    public JButton getBtnPull() {
        return btnPull;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_PLAYERS: {
                this.cacheData(msg, rawdata);

                this.getTable().setTableData(rawdata.array);

                DefaultTableModel model = this.getTable().getModel();
                int row = this.getTable().clearTable(DataEntity.class);

                rawdata.array.stream().map((o) -> (DataEntity) o).forEachOrdered((player) -> {
                    model.addRow(new Object[]{
                        player.name,
                        player.pos.dim,
                        String.format("[ %4d %4d %4d ]", player.pos.x, player.pos.y, player.pos.z),});
                });

                this.getTable().dataUpdated(row);

                break;
            }

            default:
                return false;

        }
        return true;

    }

    @Override
    public SelectedTab getSelectedTab() {
        return SelectedTab.PLAYERS;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
