package mcp.mobius.opis.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.OpisMod;
import net.minecraftforge.fml.relauncher.Side;

public class ActionRunOpisClient implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        OpisMod.profilerRunClient = true;
        ProfilerSection.resetAll(Side.CLIENT);
        ProfilerSection.activateAll(Side.CLIENT);
    }
}
