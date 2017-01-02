package mcp.mobius.opis.swing.panels.tracking;

import java.util.HashMap;

import mcp.mobius.opis.api.ITabPanel;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntityPerClass;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.actions.ActionAmountTileEnts;
import mcp.mobius.opis.swing.widgets.JButtonAccess;
import mcp.mobius.opis.swing.widgets.JPanelMsgHandler;
import mcp.mobius.opis.swing.widgets.JTableStats;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PanelAmountTileEnts extends JPanelMsgHandler implements ITabPanel {

    private final JButtonAccess btnRefresh;

    public PanelAmountTileEnts() {
        setLayout(new MigLayout("", "[][][grow][]", "[][grow][]"));

        btnRefresh = new JButtonAccess("Refresh", AccessLevel.NONE);
        add(btnRefresh, "cell 3 0");
        btnRefresh.addActionListener(new ActionAmountTileEnts());

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 0 1 4 1,grow");

        table = new JTableStats(
                new String[]{"Name", "Mod", "Amount"},
                new Class[]{String.class, String.class, Integer.class},
                new int[]{SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.CENTER}
        );
        scrollPane.setViewportView(table);
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_AMOUNT_TILEENTS: {
                this.cacheData(msg, rawdata);

                this.getTable().setTableData(rawdata.array);

                DefaultTableModel model = table.getModel();
                int row = this.getTable().clearTable(DataBlockTileEntityPerClass.class);

                HashMap<String, DataBlockTileEntityPerClass> cumData = new HashMap<>();

                rawdata.array.stream().map((o) -> (DataBlockTileEntityPerClass) o).forEachOrdered((data) -> {
                    String name = ModIdentification.getStackName(data.id, data.meta);

                    if (!cumData.containsKey(name)) {
                        cumData.put(name, new DataBlockTileEntityPerClass(data.id, data.meta));
                    }

                    cumData.get(name).add(data.amount, data.update.timing);
                });

                cumData.keySet().forEach((s) -> {
                    String name = s;
                    String modID = ModIdentification.getModStackName(cumData.get(s).id, cumData.get(s).meta);
                    model.addRow(new Object[]{name, modID, cumData.get(s).amount});
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
        return SelectedTab.AMOUNTTES;
    }

    @Override
    public boolean refreshOnString() {
        return true;
    }
}
