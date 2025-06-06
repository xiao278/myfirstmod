package kx.myfirstmod.rendering;

import com.google.common.collect.Lists;
import kx.myfirstmod.entities.BeamWeaponEntity;
import kx.myfirstmod.items.BeamWeapon;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BeamWeaponProjectileRenderer extends EntityRenderer<BeamWeaponEntity> {
    //    private static final Vec3d THIRD_PERSON_BEAM_OFFSET = new Vec3d(0.5, -0.1, 0);
    private static final Vec3d THIRD_PERSON_BEAM_OFFSET = new Vec3d(0, 0, 0);
    private static final Vec3d FIRST_PERSON_BEAM_OFFSET = new Vec3d(0, 0, 0);
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");
    public static final float INNER_BEAM_MAX_WIDTH = 0.1F;
    public static final float INNER_BEAM_MIN_WIDTH = 0.1F;

    public BeamWeaponProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(BeamWeaponEntity entity, Frustum frustum, double x, double y, double z) {
//        super.shouldRender(entity, frustum, x, y, z);
        return true;
    }

    @Override
    public Identifier getTexture(BeamWeaponEntity entity) {
        return BEAM_TEXTURE;
    }

    @Override
    public void render(BeamWeaponEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        float smoothPitch = 0;
        float smoothYaw = 0;
        if (!(entity.getOwner() instanceof PlayerEntity) || entity.getProjectileSpecialization() != BeamWeaponEntity.ProjectileSpecialization.LONGSHOT) {
            smoothPitch = entity.getPitch();
            smoothYaw = entity.getYaw();
        }
        else {
            PlayerEntity owner = (PlayerEntity) entity.getOwner();
            smoothPitch = MathHelper.clamp(-owner.getPitch(tickDelta), -90, 90);
            smoothYaw = MathHelper.wrapDegrees(-owner.getYaw(tickDelta));

            Vec3d negLerpedPos = entity.getLerpedPos(tickDelta).multiply(-1);
            matrices.translate(negLerpedPos.x, negLerpedPos.y, negLerpedPos.z);

            Vec3d newLerpedPos = this.fromLerpedPosition(owner, BeamWeapon.getOffset(owner), tickDelta);
            matrices.translate(newLerpedPos.x, newLerpedPos.y, newLerpedPos.z);
        }

        Quaternionf pitchQuaternion = new Quaternionf().rotateX((90f - smoothPitch)
                * (float) Math.PI / 180f);
        Quaternionf yawQuaternion = new Quaternionf().rotateY(smoothYaw
                * (float) Math.PI / 180f);    // Rotation around Y-axis
        Quaternionf combinedQuaternion = yawQuaternion.mul(pitchQuaternion);
        matrices.multiply(combinedQuaternion, 0, 0, 0);
        renderBeamHelper(matrices, vertexConsumers, tickDelta, entity);
        matrices.pop();
    }

    private static void renderBeamHelper(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, BeamWeaponEntity entity) {
        long worldTime = entity.getEntityWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = Lists.newArrayList();
        float[] color = new float[]{1,0,0};
        for (int i = 0; i < entity.getBeamLength(); i++) {
            list.add(new BeaconBlockEntity.BeamSegment(color));
        }

        float beamWidthModifier = Math.max((entity.getBeamTicks() - entity.age - tickDelta), 0) / entity.getBeamTicks();
        float beamWidthModifierInner = 1;
        float beamWidthModifierOuter = 1;
        if (entity.getProjectileSpecialization() == BeamWeaponEntity.ProjectileSpecialization.LONGSHOT) {
            float stablePortion = 0.85f;
            float shrinkPortion = 1 - stablePortion;
            float wavelength = 0.16f;
            float t = wavelength / 2;
            int k = Math.round((stablePortion - stablePortion % t) / t);
            float sin_wave = beamWidthModifier > shrinkPortion ?
                    (float) Math.sin(k * Math.PI * (
                            beamWidthModifier - shrinkPortion
                    ) / stablePortion)
                    : 0;
            beamWidthModifierOuter = Math.min(beamWidthModifier, shrinkPortion) / shrinkPortion + sin_wave * 0.07f;
            beamWidthModifierInner = Math.max(beamWidthModifierOuter - 0.05f, 0);
        }
        else {
            beamWidthModifierOuter = (float) Math.log(50 - 49 * beamWidthModifier);
            beamWidthModifierInner = Math.min(beamWidthModifier * 3, 2) / 2;
        }
        int k = 0;
        for (int i = 0; i < list.size(); i++) {
            BeaconBlockEntity.BeamSegment segment = list.get(i);
            float end = list.size();
            float radius = MathHelper.lerp(Math.min(i / end, 1), INNER_BEAM_MAX_WIDTH, INNER_BEAM_MIN_WIDTH);
            float segHeight = Math.min(entity.getBeamLength() - k, 1);
            renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1F, worldTime, k, segHeight, color, radius * beamWidthModifierInner, INNER_BEAM_MAX_WIDTH * beamWidthModifierOuter, 0.25F * beamWidthModifier);
            k += segment.getHeight();
        }
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, float maxY, float[] color, float innerRadius, float outerRadius, float alpha) {
        float i = yOffset + maxY;
        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        f *= 2;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2F - (float)MathHelper.floor(g * 0.1F));
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
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
        renderBeamLayerInner(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), color[0], color[1], color[2], 1.0F, yOffset, i, 0.0F, innerRadius, innerRadius, 0.0F, q, 0.0F, 0.0F, t, 0.0F, 1.0F, x, w);
        m = -outerRadius;
        float n = -outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        u = 0.0F;
        v = 1.0F;
        w = -1.0F + h;
        x = (float)maxY * heightScale + w;
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
        renderBeamLayerOuter(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), color[0], color[1], color[2], alpha, yOffset, i, m, n, outerRadius, p, q, outerRadius, outerRadius, outerRadius, 0.0F, 1.0F, x, w);
        matrices.pop();
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

    private static void renderBeamLayerInner(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, float height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        // x1 is 0,
        // z1 is radius
        // x2 is radius
        // z2 is 0
        // x3 is -radius (q)
        // z3 is 0
        // x4 is 0
        // z4 is -radius (t)
        // u1 is 0
        // u2 is 1
        // v1 is (float)maxY * heightScale * (0.5F / innerRadius) + w; (x)
        // v2 is -1.0F + h; (w)
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
        // render far end of beam
        if (height - yOffset < 1 || height == BeamWeapon.BEAM_RANGE) {
            renderBeamEndFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, height, x1, z1, x2, z2, x4, z4, x3, z3, u1, v1, u2, v2);
        }
        //render close end of beam
        if (yOffset <= 0) {
            renderBeamEndFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, x3, z3, x4, z4, x2, z2, x1, z1, u1, v1, u2, v2);
        }
    }

    private static void renderBeamLayerOuter(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, float height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
        if (height <= height - yOffset) {
            //render close end of beam
            renderBeamEndFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, x3, z3, x4, z4, x2, z2, x1, z1, u1, v1, u2, v2);
        }
        if (height >= BeamWeapon.BEAM_RANGE) {
            //render far end of beam
            renderBeamEndFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, height, x1, z1, x2, z2, x4, z4, x3, z3, u1, v1, u2, v2);
        }
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, float height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamEndFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, float yOffset, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float v1, float u2, float v2) {
//        System.out.printf("(%f, %f), (%f, %f), (%f, %f), (%f, %f)\n", x1, z1, x2, z2, x3, z3, x4, z4);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u1, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x3, z3, u2, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x4, z4, u2, v1);
    };

    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, float y, float x, float z, float u, float v) {
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
        Vec3d original_offseet = BeamWeapon.getOffset(player).rotateY((float) Math.toRadians(smoothed_body_yaw)).add(isFirstPerson ? FIRST_PERSON_BEAM_OFFSET : THIRD_PERSON_BEAM_OFFSET);
        Vec3d eye_offset = new Vec3d(0, player.getEyeHeight(player.getPose()), 0);
        return original_offseet.subtract(eye_offset);
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, Vec3d offset, float delta) {
        double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX()) + offset.getX();
        double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + offset.getY();
        double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ()) + offset.getZ();
        return new Vec3d(d, e, f);
    }
}
