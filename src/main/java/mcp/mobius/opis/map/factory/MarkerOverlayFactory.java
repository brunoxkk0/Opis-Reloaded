package mcp.mobius.opis.map.factory;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.util.UIState;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.geom.Point2D;
import mcp.mobius.opis.OpisMod;

public class MarkerOverlayFactory {

    public static MarkerOverlay create(IClientAPI jmAPI, BlockPos toMark, String name, String label) {
        ResourceLocation sprites = new ResourceLocation("examplemod:images/sprites.png");
        int spriteX = 0, spriteY = 0;
        int iconSize = 64;
        int iconColumns = 8;
        int iconRows = 4;

        int colorIndex = 0;

        // Lets tint the icon using one Minecraft's map colors (usable range is 1-35)
        colorIndex++;
        if (colorIndex > 35) {
            colorIndex = 1;
        }
        int color = MapColor.COLORS[colorIndex].colorValue;

        MapImage icon = new MapImage(sprites, spriteX, spriteY, iconSize, iconSize, color, 1f);

        // Build the overlay
        MarkerOverlay markerOverlay = new MarkerOverlay(OpisMod.MODID, name, toMark, icon);
        markerOverlay.setDimension(0).setTitle(name).setLabel(label);

        // Add a listener to it
        markerOverlay.setOverlayListener(new MarkerListener(jmAPI, markerOverlay));

        try {
            jmAPI.show(markerOverlay);
        } catch (Exception e) {
            OpisMod.LOGGER.error("Can't add marker overlay", e);
        }
        return markerOverlay;
    }

    private static class MarkerListener implements IOverlayListener {

        final IClientAPI jmAPI;
        final MarkerOverlay overlay;
        final int color;
        final float opacity;

        private MarkerListener(IClientAPI jmAPI, final MarkerOverlay overlay) {
            this.jmAPI = jmAPI;
            this.overlay = overlay;
            this.color = overlay.getIcon().getColor();
            this.opacity = overlay.getIcon().getOpacity();
        }

        @Override
        public void onActivate(UIState uiState) {
            refresh(uiState);
        }

        @Override
        public void onDeactivate(UIState uiState) {
            refresh(uiState);
        }

        @Override
        public void onMouseMove(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            double size = uiState.blockSize * 10;

            if (overlay.getIcon().getDisplayWidth() != size) {
                overlay.getIcon()
                        .setDisplayWidth(size)
                        .setDisplayHeight(size)
                        .setAnchorX(size / 2)
                        .setAnchorY(size);

                overlay.flagForRerender();
            }
        }

        @Override
        public void onMouseOut(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            refresh(uiState);
        }

        @Override
        public boolean onMouseClick(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
            //jmAPI.remove(overlay);
            return true;
        }

        private void refresh(UIState uiState) {
            double size = uiState.blockSize * 8;
            overlay.getIcon()
                    .setColor(color)
                    .setOpacity(opacity)
                    .setDisplayWidth(size)
                    .setDisplayHeight(size)
                    .setAnchorX(size / 2)
                    .setAnchorY(size);

            overlay.flagForRerender();
        }
    }
}
