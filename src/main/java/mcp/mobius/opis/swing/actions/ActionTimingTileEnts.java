package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mcp.mobius.opis.ModOpis;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.timingserver.PanelTimingTileEnts;
import mcp.mobius.opis.swing.widgets.JTableStats;

import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import net.minecraft.client.Minecraft;

public class ActionTimingTileEnts implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelTimingTileEnts panel = (PanelTimingTileEnts) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.TIMINGTILEENTS);

        JTableStats table = panel.getTable();
        if (table == null || table.getSelectedRow() == -1) {
            return;
        }
        int indexData = table.convertRowIndexToModel(table.getSelectedRow());
        DataBlockTileEntity data = (DataBlockTileEntity) table.getTableData().get(indexData);

        if (e.getSource() == panel.getBtnCenter()) {
            CoordinatesBlock coord = data.pos;
        }

        if (e.getSource() == panel.getBtnTeleport()) {
            CoordinatesBlock coord = data.pos;
            ModOpis.selectedBlock = coord;
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_BLOCK, coord));
            Minecraft.getMinecraft().setIngameFocus();
        }

        if (e.getSource() == panel.getBtnReset()) {
            ModOpis.selectedBlock = null;
        }
    }

}
