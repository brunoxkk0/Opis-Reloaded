package mcp.mobius.opis.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpisServerEventHandler {

    public static boolean printEntityTrace = false;
    public static boolean printEntityFull = false;

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onEntityConstructed(EntityEvent.EntityConstructing event) {
        if (printEntityTrace) {

            if (event.getEntity() instanceof EntityItem) {
                System.out.printf("Entity %s of type %s [ %s ] created\n", event.getEntity().getEntityId(),
                        event.getEntity().getClass().getName(),
                        ((EntityItem) (event.getEntity())).getItem().getDisplayName());

            } else {
                System.out.printf("Entity %s of type %s created\n", event.getEntity().getEntityId(),
                        event.getEntity().getClass().getName());
            }

            if (printEntityFull) {
                try {
                    throw new RuntimeException();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
