package kx.myfirstmod.rendering;

import com.google.common.collect.Lists;
import kx.myfirstmod.items.BeamWeapon;
import kx.myfirstmod.items.ModItems;
import kx.myfirstmod.utils.BlockGlowRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BeamWeaponFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static BeamWeaponFeatureRenderer<?,?> INSTANCE;
//    private static final Vec3d THIRD_PERSON_BEAM_OFFSET = new Vec3d(0.5, -0.1, 0);
    private static final Vec3d THIRD_PERSON_BEAM_OFFSET = new Vec3d(0, 0, 0);
    private static final Vec3d FIRST_PERSON_BEAM_OFFSET = new Vec3d(0, 0, 0);
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");
    public static final float INNER_BEAM_MAX_WIDTH = 0.2F;
    public static final float INNER_BEAM_MIN_WIDTH = 0.01F;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            // Call the BlockGlowRenderer to render the glow
//            BlockGlowRenderer.render(context.matrixStack(), context.consumers(), context.tickDelta(), context.consumers());
//            BlockGlowRenderer.renderEntityOutline(context);
            INSTANCE.firstPersonRender(context);
        });
    }

    public BeamWeaponFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    public void firstPersonRender(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || (!client.options.getPerspective().isFirstPerson())) return;
        if (!BeamWeapon.canShoot(player, player.getWorld())) return;
        MatrixStack matrices = context.matrixStack();
        float tickDelta = context.tickDelta();
        VertexConsumerProvider vertexConsumers = context.consumers();

//
//        matrices = new MatrixStack();
//        matrices.push();
////        Quaternionf quat = new Quaternionf().rotateTo(new Vector3f(0,0,1), getRotationVector(player.getPitch() * 0, player.getYaw(tickDelta)).toVector3f());
//        Quaternionf yawQuat = new Quaternionf().rotateY((float) Math.toRadians(player.getYaw(tickDelta)));
//        Quaternionf pitchQuat = new Quaternionf().rotateY((float) Math.toRadians(player.getPitch(tickDelta)));
//        matrices.multiply(yawQuat, 0,0,0);
//        matrices.multiply(pitchQuat, 0,0,0);
//        render(matrices, vertexConsumers, 0xF000F0, (T) player, 0, 0, tickDelta, 0, 0, 0);
//        matrices.pop();

        matrices = new MatrixStack();
        matrices.push();

        Vec3d offset = calcOffset(player, tickDelta, true);
        matrices.translate(offset.x, offset.y, offset.z);
//        matrices.translate(0.35 * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1),  -0.5, -0.5);

        matrices.multiply(
                new Quaternionf().rotateLocalX((float) Math.toRadians(-90F)),
                0,0,0
        );

        renderBeamHelper(matrices, vertexConsumers, tickDelta, player);

        matrices.pop();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
// Check if the player is holding an item
        if (!(entity instanceof PlayerEntity)) return;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = (PlayerEntity) entity;
        if (!BeamWeapon.canShoot(player, player.getWorld())) return;

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.getItem() == ModItems.BEAM_WEAPON) {
            matrices.push();
            float smoothed_body_yaw = (MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw));
            Vec3d offset = calcOffset(player, tickDelta, false);
            matrices.translate(-offset.x, -offset.y, -offset.z);
//            matrices.translate(-0.4 * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1),  0.6, -0.2);
            Quaternionf quat = new Quaternionf().rotateTo(new Vector3f(0,-1,0), getRotationVector(player.getPitch() + 0.5F, smoothed_body_yaw - player.getYaw(tickDelta) - 0.5F).toVector3f());
            matrices.multiply(quat, 0, 0, 0);
            renderBeamHelper(matrices, vertexConsumers, tickDelta, player);
            matrices.pop();
        }
    }

    private static void renderBeamHelper(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, PlayerEntity player) {
        long l = player.getWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = Lists.newArrayList();
        float[] color = new float[]{1,0,0};
        for (int i = 0; i < BeamWeapon.BEAM_RANGE; i++) {
            list.add(new BeaconBlockEntity.BeamSegment(color));
        }

        float beamWidthModifier = (BeamWeapon.getShootTicksLeft(player, player.getWorld()) - tickDelta) / BeamWeapon.DAMAGE_TICKS;

        int k = 0;
        for (int i = 0; i < list.size(); i++) {
//            renderBeam(matrices, vertexConsumers, tickDelta, l, k, beamSegment.getHeight(), beamSegment.getColor());
            BeaconBlockEntity.BeamSegment segment = list.get(i);
            float radius = MathHelper.lerp((float) i / list.size(), INNER_BEAM_MAX_WIDTH, INNER_BEAM_MIN_WIDTH) * beamWidthModifier;
            renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0F, l, k, segment.getHeight(), color, radius, radius + 0.05F);
            k += segment.getHeight();
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, float[] color) {
        renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0F, worldTime, yOffset, maxY, color, 0.2F, 0.25F);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, float[] color, float innerRadius, float outerRadius) {
        int i = yOffset + maxY;
        matrices.push();

//        matrices.translate((double)0.5F, (double)0.0F, (double)0.5F);
        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2F - (float)MathHelper.floor(g * 0.1F));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25F - 45.0F));
        float m = 0.0F;
        float p = 0.0F;
        float q = -innerRadius;
        float r = 0.0F;
        float s = 0.0F;
        float t = -innerRadius;
        float u = 0.0F;
        float v = 1.0F;
        float w = -1.0F + h;
        float x = (float)maxY * heightScale * (0.5F / innerRadius) + w;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), j, k, l, 1.0F, yOffset, i, 0.0F, innerRadius, innerRadius, 0.0F, q, 0.0F, 0.0F, t, 0.0F, 1.0F, x, w);
        matrices.pop();
        m = -outerRadius;
        float n = -outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        u = 0.0F;
        v = 1.0F;
        w = -1.0F + h;
        x = (float)maxY * heightScale + w;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), j, k, l, 0.125F, yOffset, i, m, n, outerRadius, p, q, outerRadius, outerRadius, outerRadius, 0.0F, 1.0F, x, w);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, (float)y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    public boolean rendersOutsideBoundingBox(BeaconBlockEntity beaconBlockEntity) {
        return true;
    }

    public int getRenderDistance() {
        return 256;
    }

    public boolean isInRenderDistance(BeaconBlockEntity beaconBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(beaconBlockEntity.getPos()).multiply((double)1.0F, (double)0.0F, (double)1.0F).isInRange(vec3d.multiply((double)1.0F, (double)0.0F, (double)1.0F), (double)this.getRenderDistance());
    }

    public final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180F);
        float g = -yaw * ((float)Math.PI / 180F);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }

    private Vec3d calcOffset(LivingEntity player, float tickDelta, boolean isFirstPerson) {
        float smoothed_body_yaw = (MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw));
        Vec3d original_offseet = BeamWeapon.getOffset(player, player.getActiveHand()).rotateY((float) Math.toRadians(smoothed_body_yaw)).add(isFirstPerson ? FIRST_PERSON_BEAM_OFFSET : THIRD_PERSON_BEAM_OFFSET);
        Vec3d eye_offset = new Vec3d(0, player.getEyeHeight(player.getPose()), 0);
        return original_offseet.subtract(eye_offset);
    }
}
