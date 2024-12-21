package kx.myfirstmod;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GuardianLaserEntityRenderer extends EntityRenderer<GuardianLaserEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/guardian.png");
    private static final Identifier EXPLOSION_BEAM_TEXTURE = new Identifier("textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER;
    protected GuardianLaserEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(GuardianLaserEntity entity) {
        return new Identifier("minecraft", "textures/item/diamond.png");
    }

    public boolean shouldRender(GuardianLaserEntity GLEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender(GLEntity, frustum, d, e, f)) {
            return true;
        } else {
            if (GLEntity.hasBeamTarget()) {
                LivingEntity livingEntity = GLEntity.getBeamTarget();
                if (livingEntity != null) {
                    Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * (double)0.5F, 1.0F);
                    Vec3d vec3d2 = this.fromLerpedPosition(GLEntity.getBeamTarget(), (double)GLEntity.getStandingEyeHeight(), 1.0F);
                    return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
                }
            }

            return false;
        }
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, Vec3d offset, float delta) {
        double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX()) + offset.getX();
        double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + offset.getY();
        double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ()) + offset.getZ();
        return new Vec3d(d, e, f);
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        return fromLerpedPosition(entity, new Vec3d(0,yOffset,0), delta);
    }

    public void render(GuardianLaserEntity GLEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(GLEntity, f, g, matrixStack, vertexConsumerProvider, i);
//        System.out.printf("GLEntity: %s\n, Target: %s\n", GLEntity, GLEntity.getBeamTarget());
        LivingEntity target = GLEntity.getBeamTarget();
        PlayerEntity owner = GLEntity.getOwner();
        if (target != null && owner != null) {
            float h = GLEntity.getBeamProgress(g);
            float j = GLEntity.getBeamTicks() + g;
            float k = j * 0.5F % 1.0F;
//            Vec3d l = owner.getBoundingBox().getCenter().subtract(owner.getPos());
            Vec3d l = new Vec3d(0.0, 0.8999999761581421, 0.0);
            matrixStack.push();
            matrixStack.translate(l.getX(), l.getY(), l.getZ());
            Vec3d vec3d = this.fromLerpedPosition(target, 0, g);
            Vec3d vec3d2 = this.fromLerpedPosition(owner, owner.getHandPosOffset(ModItems.GUARDIAN_LASER), g);
            Vec3d vec3d3 = vec3d2.subtract(vec3d);
            float m = (float)(vec3d3.length() - (double) 0.5F);
            vec3d3 = vec3d3.normalize();
            float n = (float)Math.acos(vec3d3.y);
            float o = (float)Math.atan2(vec3d3.z, vec3d3.x);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((((float)Math.PI / 2F) - o) * (180F / (float)Math.PI)));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(n * (180F / (float)Math.PI)));
            int p = 1;
            float q = j * 0.05F * -1.5F;
            float r = h * h;
            int s = 64 + (int)(r * 191.0F);
            int t = 32 + (int)(r * 191.0F);
            int u = 128 - (int)(r * 64.0F);
            float v = 0.2F;
            float w = 0.282F;
            float x = MathHelper.cos(q + 2.3561945F) * 0.282F;
            float y = MathHelper.sin(q + 2.3561945F) * 0.282F;
            float z = MathHelper.cos(q + ((float)Math.PI / 4F)) * 0.282F;
            float aa = MathHelper.sin(q + ((float)Math.PI / 4F)) * 0.282F;
            float ab = MathHelper.cos(q + 3.926991F) * 0.282F;
            float ac = MathHelper.sin(q + 3.926991F) * 0.282F;
            float ad = MathHelper.cos(q + 5.4977875F) * 0.282F;
            float ae = MathHelper.sin(q + 5.4977875F) * 0.282F;
            float af = MathHelper.cos(q + (float)Math.PI) * 0.2F;
            float ag = MathHelper.sin(q + (float)Math.PI) * 0.2F;
            float ah = MathHelper.cos(q + 0.0F) * 0.2F;
            float ai = MathHelper.sin(q + 0.0F) * 0.2F;
            float aj = MathHelper.cos(q + ((float)Math.PI / 2F)) * 0.2F;
            float ak = MathHelper.sin(q + ((float)Math.PI / 2F)) * 0.2F;
            float al = MathHelper.cos(q + ((float)Math.PI * 1.5F)) * 0.2F;
            float am = MathHelper.sin(q + ((float)Math.PI * 1.5F)) * 0.2F;
            float ao = 0.0F;
            float ap = 0.4999F;
            float aq = -1.0F + k;
            float ar = m * 2.5F + aq;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
            MatrixStack.Entry entry = matrixStack.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            vertex(vertexConsumer, matrix4f, matrix3f, af, m, ag, s, t, u, 0.4999F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, af, 0.0F, ag, s, t, u, 0.4999F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, ah, 0.0F, ai, s, t, u, 0.0F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, ah, m, ai, s, t, u, 0.0F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, aj, m, ak, s, t, u, 0.4999F, ar);
            vertex(vertexConsumer, matrix4f, matrix3f, aj, 0.0F, ak, s, t, u, 0.4999F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, al, 0.0F, am, s, t, u, 0.0F, aq);
            vertex(vertexConsumer, matrix4f, matrix3f, al, m, am, s, t, u, 0.0F, ar);
            float as = 0.0F;
            if (GLEntity.age % 2 == 0) {
                as = 0.5F;
            }

            vertex(vertexConsumer, matrix4f, matrix3f, x, m, y, s, t, u, 0.5F, as + 0.5F);
            vertex(vertexConsumer, matrix4f, matrix3f, z, m, aa, s, t, u, 1.0F, as + 0.5F);
            vertex(vertexConsumer, matrix4f, matrix3f, ad, m, ae, s, t, u, 1.0F, as);
            vertex(vertexConsumer, matrix4f, matrix3f, ab, m, ac, s, t, u, 0.5F, as);
            matrixStack.pop();
        }

    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(red, green, blue, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    public Identifier getTexture(GuardianEntity guardianEntity) {
        return TEXTURE;
    }

    static {
        LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);
    }
}
