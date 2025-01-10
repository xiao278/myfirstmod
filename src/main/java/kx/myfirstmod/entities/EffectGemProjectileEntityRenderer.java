package kx.myfirstmod.entities;

import kx.myfirstmod.MyFirstMod;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EffectGemProjectileEntityRenderer extends FlyingItemEntityRenderer<EffectGemProjectileEntity> {
    public EffectGemProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
