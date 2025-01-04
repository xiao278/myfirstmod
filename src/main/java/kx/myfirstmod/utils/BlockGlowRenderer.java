package kx.myfirstmod.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class BlockGlowRenderer {
    private static BlockPos blockPos;
    private static final float[] color = {1,0,0,1};
//    public static final VertexFormat LINES = new VertexFormat(
//            ImmutableMap.<String, VertexFormatElement>builder().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).put("Normal", NORMAL_ELEMENT).put("Padding", PADDING_ELEMENT).build()
//    );

    public static void register() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hitResult) -> {
            // Call the BlockGlowRenderer to render the glow
            BlockGlowRenderer.renderGlowingBlock(context.matrixStack(), color, context.tickDelta());
            return true; // Return true to cancel default block outline rendering
        });
    }

    /**
     * Renders a glowing outline around a block.
     *
     * @param matrices   The matrix stack for rendering transformations.
     * @param color      The color of the glow (RGBA format).
     * @param tickDelta  The partial ticks for smooth rendering.
     */
    public static void renderGlowingBlock(MatrixStack matrices, float[] color, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || blockPos == null) return;

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Box box = new Box(blockPos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        System.out.printf("(%f, %f, %f), (%f, %f, %f)\n", box.maxX, box.maxY, box.maxZ, box.minX, box.minY, box.minZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        

        RenderSystem.lineWidth(5);

        drawBoundingBox(matrices, buffer, box, color[0], color[1], color[2], color[3]);

        RenderSystem.lineWidth(1);

        // We'll get to this bit in the next section.
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw the buffer onto the screen.
        tessellator.draw();
    }

    private static void drawBoundingBox(MatrixStack matrices, BufferBuilder buffer, Box box, float red, float green, float blue, float alpha) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Bottom edges
        buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).next();

        // Top edges
        buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).next();

        // Vertical edges
        buffer.vertex(positionMatrix, minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, maxY, minZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(positionMatrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(positionMatrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
    }

    public static void setBlockPos(BlockPos blockPos) {
        BlockGlowRenderer.blockPos = blockPos;
    }

    public static BlockPos getBlockPos() {
        return blockPos;
    }
}
