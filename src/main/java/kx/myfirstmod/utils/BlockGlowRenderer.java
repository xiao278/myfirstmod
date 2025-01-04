package kx.myfirstmod.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlockGlowRenderer {
    private static BlockPos blockPos;
    private static final Identifier OUTLINE_TEXTURE = new Identifier("myfirstmod", "textures/dummy_texture.png");
    private static final float[] color = {1,0,0,0.5F};
    private static final float thickness = 5;

    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context) -> {
            // Call the BlockGlowRenderer to render the glow
            BlockGlowRenderer.renderGlowingBlock(context, color, context.tickDelta(), context.consumers());
        });
    }

    /**
     * Renders a glowing outline around a block.
     *
     * @param context
     * @param color      The color of the glow (RGBA format).
     * @param tickDelta  The partial ticks for smooth rendering.
     */
    public static void renderGlowingBlock(WorldRenderContext context, float[] color, float tickDelta, VertexConsumerProvider consumers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || blockPos == null) return;

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Box box = new Box(blockPos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        box = box.expand(0.125);
//        System.out.printf("(%f, %f, %f), (%f, %f, %f)\n", box.maxX, box.maxY, box.maxZ, box.minX, box.minY, box.minZ);

        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest(); // Optional: Disable depth for full visibility
        RenderSystem.depthMask(false); // Disable depth writing

//        MatrixStack adjustedMatrixStack = new MatrixStack();
//        adjustedMatrixStack.multiplyPositionMatrix(context.matrixStack().peek().getPositionMatrix());

        drawBoundingBox(context.matrixStack(), buffer, box, cameraPos, color[0], color[1], color[2], color[3]);

        // Reset RenderSystem to default values
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableDepthTest(); // Re-enable depth testing
        RenderSystem.depthMask(true); // Disable depth writing

    }

    private static void drawBoundingBox(MatrixStack matrices, VertexConsumer buffer, Box box, Vec3d cameraPos, float red, float green, float blue, float alpha) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        float nx = 0.0F, ny = 1.0F, nz = 0.0F;

        float[][] corners = {
                {(float) box.minX, (float) box.minY, (float) box.minZ}, // 0
                {(float) box.maxX, (float) box.minY, (float) box.minZ}, // 1
                {(float) box.minX, (float) box.maxY, (float) box.minZ}, // 2
                {(float) box.maxX, (float) box.maxY, (float) box.minZ}, // 3
                {(float) box.minX, (float) box.minY, (float) box.maxZ}, // 4
                {(float) box.maxX, (float) box.minY, (float) box.maxZ}, // 5
                {(float) box.minX, (float) box.maxY, (float) box.maxZ}, // 6
                {(float) box.maxX, (float) box.maxY, (float) box.maxZ}  // 7
        };

        // Define the edges of the box
        int[][] edges = {
                {0, 1}, {1, 3}, {3, 2}, {2, 0}, // Bottom face
                {4, 5}, {5, 7}, {7, 6}, {6, 4}, // Top face
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Vertical edges
        };

        int[][] faces = {
                {0, 1, 3, 2}, // Front face
                {4, 5, 7, 6}, // Back face
                {0, 4, 6, 2}, // Left face
                {1, 5, 7, 3}, // Right face
                {2, 3, 7, 6}, // Top face
                {0, 1, 5, 4}  // Bottom face
        };

        for (int[] face: faces) {
            for (int corner_index: face) {
                float[] pos = corners[corner_index];
                buffer.vertex(positionMatrix, pos[0], pos[1], pos[2])
                        .color(red, green, blue, alpha)
                        .normal(normalMatrix, nx, ny, nz)
                        .next();
            }
        }

//        // Loop through edges and render them
//        for (int[] edge : edges) {
//            int start = edge[0];
//            int end = edge[1];
//
//            // Start and end points of the edge
//            float x1 = corners[start][0];
//            float y1 = corners[start][1];
//            float z1 = corners[start][2];
//            float x2 = corners[end][0];
//            float y2 = corners[end][1];
//            float z2 = corners[end][2];
//
//            // Direction vector of the edge
//            Vec3d direction = new Vec3d(x2 - x1, y2 - y1, z2 - z1).normalize();
//
//            // Perpendicular vector for thickness
//            Vec3d perpendicular = direction.crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(thickness / 2);
//
//            // Define the quad vertices
//            Vec3d v1 = new Vec3d(x1, y1, z1).add(perpendicular);
//            Vec3d v2 = new Vec3d(x1, y1, z1).subtract(perpendicular);
//            Vec3d v3 = new Vec3d(x2, y2, z2).add(perpendicular);
//            Vec3d v4 = new Vec3d(x2, y2, z2).subtract(perpendicular);
//
//            // Calculate a normal for the quad (use the direction vector or cross product)
//            Vec3d quadNormal = direction.crossProduct(perpendicular).normalize();
//
//            // Render the quad
//            renderQuad(buffer, positionMatrix, normalMatrix,
//                    v1, v2, v3, v4,
//                    (float) quadNormal.x, (float) quadNormal.y, (float) quadNormal.z,
//                    red, green, blue, alpha);
//        }
    }

    private static void renderQuad(VertexConsumer buffer, Matrix4f positionMatrix, Matrix3f normalMatrix,
                                   Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4,
                                   float nx, float ny, float nz,
                                   float red, float green, float blue, float alpha) {
        // Bottom-left
        buffer.vertex(positionMatrix, (float) v1.x, (float) v1.y, (float) v1.z)
                .color(red, green, blue, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();

        // Bottom-right
        buffer.vertex(positionMatrix, (float) v2.x, (float) v2.y, (float) v2.z)
                .color(red, green, blue, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();

        // Top-right
        buffer.vertex(positionMatrix, (float) v4.x, (float) v4.y, (float) v4.z)
                .color(red, green, blue, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();

        // Top-left
        buffer.vertex(positionMatrix, (float) v3.x, (float) v3.y, (float) v3.z)
                .color(red, green, blue, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();
    }

    public static void setBlockPos(BlockPos blockPos) {
        BlockGlowRenderer.blockPos = blockPos;
    }

    public static BlockPos getBlockPos() {
        return blockPos;
    }
}
