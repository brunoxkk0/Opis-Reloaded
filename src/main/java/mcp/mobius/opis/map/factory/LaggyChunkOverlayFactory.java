package mcp.mobius.opis.map.factory;

import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import journeymap.client.api.util.UIState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.awt.geom.Point2D;
import java.util.Random;
import mcp.mobius.opis.OpisMod;

public class LaggyChunkOverlayFactory {

    public static PolygonOverlay create(Integer dimension, ChunkPos chunkCoords, String id, String group, String label) {

        // Style the polygon
        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(2)
                .setStrokeColor(0x00ff00).setStrokeOpacity(.7f)
                .setFillColor(0x00ff00).setFillOpacity(.4f);

        // Style the text
        TextProperties textProps = new TextProperties()
                .setBackgroundColor(0x000022)
                .setBackgroundOpacity(.5f)
                .setColor(0x00ff00)
                .setOpacity(1f)
                .setMinZoom(2)
                .setFontShadow(true);

        // Define the shape
        MapPolygon polygon = PolygonHelper.createChunkPolygon(chunkCoords.x, 70, chunkCoords.z);

        // Create the overlay
        PolygonOverlay slimeChunkOverlay = new PolygonOverlay(OpisMod.MODID, id, dimension, shapeProps, polygon);

        // Set the text
        slimeChunkOverlay.setOverlayGroupName(group)
                .setLabel(label)
                .setTextProperties(textProps);

        // Add a listener for mouse events
        IOverlayListener overlayListener = new LaggyChunkListener(slimeChunkOverlay);
        slimeChunkOverlay.setOverlayListener(overlayListener);

        return slimeChunkOverlay;
    }

    /**
     * Listener for events on a slime chunk overlay instance.
     */
    private static class LaggyChunkListener implements IOverlayListener {

        final PolygonOverlay overlay;
        final ShapeProperties sp;
        final int fillColor;
        final int strokeColor;
        final float strokeOpacity;

        private LaggyChunkListener(final PolygonOverlay overlay) {
            this.overlay = overlay;
            sp = overlay.getShapeProperties();
            fillColor = sp.getFillColor();
            strokeColor = sp.getStrokeColor();
            strokeOpacity = sp.getStrokeOpacity();
        }

        @Override
        public void onActivate(UIState uiState) {
            // Reset
            resetShapeProperties();
        }

        @Override
        public void onDeactivate(UIState uiState) {
            // Reset
            resetShapeProperties();
        }

        @Override
        public void onMouseMove(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            sp.setStrokeColor(new Random().nextInt(0xffffff));
            sp.setStrokeOpacity(1f);

            String title = "%s blocks away";
            BlockPos playerLoc = Minecraft.getMinecraft().player.getPosition();
            int distance = (int) Math.sqrt(playerLoc.distanceSq(blockPosition.getX(), playerLoc.getY(), blockPosition.getZ()));
            overlay.setTitle(String.format(title, distance));
        }

        @Override
        public void onMouseOut(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            // Reset
            resetShapeProperties();
            overlay.setTitle(null);
        }

        @Override
        public boolean onMouseClick(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
            //sp.setFillColor(new Random().nextInt(0xffffff));
            return false;
        }

        /**
         * Reset properties back to original
         */
        private void resetShapeProperties() {
            sp.setFillColor(fillColor);
            sp.setStrokeColor(strokeColor);
            sp.setStrokeOpacity(strokeOpacity);
        }
    }
}
