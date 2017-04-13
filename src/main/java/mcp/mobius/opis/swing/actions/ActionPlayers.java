package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.client.PacketReqData;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.panels.tracking.PanelPlayers;
import mcp.mobius.opis.swing.widgets.JTableStats;

import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.TargetEntity;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.map.JourneyMapPlugin;
import mcp.mobius.opis.map.factory.MarkerOverlayFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class ActionPlayers implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PanelPlayers panel = (PanelPlayers) TabPanelRegistrar.INSTANCE.getTab(SelectedTab.PLAYERS);

        JTableStats table = panel.getTable();
        if (table == null || table.getSelectedRow() == -1) {
            return;
        }
        int indexData = table.convertRowIndexToModel(table.getSelectedRow());
        DataEntity data = (DataEntity) table.getTableData().get(indexData);

        if (e.getSource() == panel.getBtnCenter()) {
            CoordinatesBlock coord = data.pos;
            if (OpisMod.mappingEnabled) {
                BlockPos pos = new BlockPos(coord.x, coord.y, coord.z);
                MarkerOverlayFactory.create(JourneyMapPlugin.getAPI(), pos, data.name.str, " X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());
            }
            //sendToServer(new PacketReqData(Message.OVERLAY_CHUNK_ENTITIES));
            //PacketManager.sendToServer(new PacketReqData(Message.LIST_CHUNK_ENTITIES, data.pos.asCoordinatesChunk()));
        }

        if (e.getSource() == panel.getBtnTeleport()) {
            int eid = data.eid;
            int dim = data.pos.dim;
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_TO_ENTITY, new TargetEntity(eid, dim)));
            Minecraft.getMinecraft().setIngameFocus();
        }

        if (e.getSource() == panel.getBtnPull()) {
            int eid = data.eid;
            int dim = data.pos.dim;
            PacketManager.sendToServer(new PacketReqData(Message.COMMAND_TELEPORT_PULL_ENTITY, new TargetEntity(eid, dim)));
        }
    }

}
