package kx.myfirstmod.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import kx.myfirstmod.MyFirstMod;
import kx.myfirstmod.items.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.GlowSquidEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockGlowRenderer {
    private static BlockPos blockPos;
    private static Entity entity;
    private static final Identifier OUTLINE_TEXTURE = new Identifier(MyFirstMod.MOD_ID, "textures/dummy_texture.png");
    private static final float[] color = {0.5f,1f,0.75f,0.25F};
    private static final float thickness = 5;
    private static final RenderLayer TEST_LAYER = CustomRenderLayer.createCustomLayer(OUTLINE_TEXTURE);

    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context) -> {
            // Call the BlockGlowRenderer to render the glow
            BlockGlowRenderer.renderGlowingBlock(context, color, context.tickDelta(), context.consumers());
        });
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            // Call the BlockGlowRenderer to render the glow
            BlockGlowRenderer.renderEntityTarget(context, color, context.tickDelta(), context.consumers());
//            BlockGlowRenderer.renderEntityOutline(context.matrixStack(), entity, color[0], color[1], color[2], color[3]);
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
        if (client.world == null || client.player == null) return;
        Box box = null;

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();

        if (client.player.getActiveItem().getItem() != ModItems.ARROW_RAIN) return;

        if (blockPos != null) {
            box = new Box(blockPos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            box = box.expand(0.01);
        }

        if (box == null) return;

//        System.out.printf("(%f, %f, %f), (%f, %f, %f)\n", box.maxX, box.maxY, box.maxZ, box.minX, box.minY, box.minZ);

        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugQuads());

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

//        MatrixStack adjustedMatrixStack = new MatrixStack();
//        adjustedMatrixStack.multiplyPositionMatrix(context.matrixStack().peek().getPositionMatrix());

        drawBoundingBox(context.matrixStack(), buffer, box, cameraPos, color[0], color[1], color[2], color[3]);

        // Reset RenderSystem to default values
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
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

    private static void renderEntityOutline(MatrixStack matrices, Entity entity, float red, float green, float blue, float alpha) {
        if (entity == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        EntityRenderer<?> renderer = dispatcher.getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer && entity instanceof  LivingEntity livingEntity) {
//            System.out.println("isworking");
            // Get the model from the renderer
            VertexConsumerProvider.Immediate bufferProvider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

            // Set up a custom render layer for the outline
            RenderLayer outlineLayer = RenderLayer.getOutline(OUTLINE_TEXTURE);
            VertexConsumer buffer = bufferProvider.getBuffer(outlineLayer);
            Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
            Vec3d toEntity = entity.getPos().subtract(cameraPos);

            // Render the model
            matrices.push();
//            matrices.translate(1,1,1); // Slightly offset to avoid z-fighting
            matrices.translate(toEntity.x, toEntity.y + entity.getEyeHeight(entity.getPose()), toEntity.z);
            float yaw = (float) Math.toRadians(-entity.getBodyYaw());
            float pitch = (float) Math.toRadians(180);
            Quaternionf pitchRotation = new Quaternionf().rotateX(pitch);
            Quaternionf yawRotation = new Quaternionf().rotateY(yaw);
            Quaternionf totalRot = yawRotation.mul(pitchRotation);
            matrices.multiply(totalRot);

            // Apply entity-specific scaling
            if (entity instanceof GhastEntity) {
                matrices.scale(4.0F, 4.0F, 4.0F); // Apply Ghast's scaling
            } else if (entity instanceof SlimeEntity slime) {
                float sizeScale = slime.getSize() / 2.0F;
                matrices.scale(sizeScale, sizeScale, sizeScale);
            } else if (entity instanceof MobEntity && ((MobEntity) entity).isBaby()) {
                matrices.scale(0.5F, 0.5F, 0.5F); // Scale for baby entities
            } else {
                matrices.scale(1.0F, 1.0F, 1.0F); // Default scale
            }

            // Pass the entity model and outline color
//            livingRenderer.getModel().render(matrices, buffer, 15728880, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);

            matrices.pop();
            bufferProvider.draw();
        }
    }

    private static void renderEntityTarget(WorldRenderContext context, float[] color, float tickDelta, VertexConsumerProvider consumers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || entity == null || client.player == null) return;
        PlayerEntity player = client.player;
        if (client.player.getActiveItem().getItem() != ModItems.ARROW_RAIN) return;
//        MatrixStack adjustedMatrixStack = new MatrixStack();
//        adjustedMatrixStack.multiplyPositionMatrix(context.matrixStack().peek().getPositionMatrix());

        Vec3d entityCenterPos = entity.getPos().add(0,entity.getHeight() / 2,0);

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d planeNormal = cameraPos.subtract(entityCenterPos).normalize();

        VertexConsumer buffer = consumers.getBuffer(TEST_LAYER);

        float width = 0.125f;
        float height = 1.625f;

        Vec2f[][] planeQuads = {
                {
                        new Vec2f(-height,-width), new Vec2f(-height,width), new Vec2f(height,width), new Vec2f(height,-width)
                },
                {
                        new Vec2f(-width,-height), new Vec2f(-width,height), new Vec2f(width,height), new Vec2f(width,-height)
                },
        };

        MatrixStack.Entry entry = context.matrixStack().peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();
        float nx = 0.0F, ny = 1.0F, nz = 0.0F;

        float z_offset = 0;
        for (Vec2f[] quad: planeQuads) {
            for (Vec2f point: quad) {
                Vec3d newPoint = map2DTo3D(point, entityCenterPos.subtract(cameraPos).multiply(1 + z_offset), player.getPitch(), -player.getYaw());
                buffer.vertex(positionMatrix, (float) newPoint.x, (float) newPoint.y,  (float) newPoint.z)
                        .color(color[0], color[1], color[2], color[3])
//                        .normal(normalMatrix, nx, ny, nz)
                        .texture(0,0)
                        .next();
            }
            z_offset += 0.01f;
        }
    }

    public static Vec3d map2DTo3D(Vec2f point2f, Vec3d planeOrigin, float pitch, float yaw) {
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);

        // Create quaternions for pitch and yaw
        Quaternionf pitchQuaternion = new Quaternionf().rotateX(pitchRadians); // Rotation around X-axis
        Quaternionf yawQuaternion = new Quaternionf().rotateY(yawRadians);    // Rotation around Y-axis

        // Combine pitch and yaw
        Quaternionf combinedQuaternion = yawQuaternion.mul(pitchQuaternion);

        Vector3f rotatedPoint = new Vector3f(point2f.x, point2f.y, 0).rotate(combinedQuaternion);
        return planeOrigin.add(rotatedPoint.x, rotatedPoint.y, rotatedPoint.z);
    }

    public static void setBlockPos(BlockPos bPos) {
        blockPos = bPos;
    }

    public static BlockPos getBlockPos() {
        return blockPos;
    }

    public static void setEntity(Entity e) {
        entity = e;
    }

    public static Entity getEntity() {
        return entity;
    }
}
