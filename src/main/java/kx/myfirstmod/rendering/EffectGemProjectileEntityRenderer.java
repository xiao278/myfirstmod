package kx.myfirstmod.rendering;

import kx.myfirstmod.entities.EffectGemProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class EffectGemProjectileEntityRenderer extends FlyingItemEntityRenderer<EffectGemProjectileEntity> {
    public EffectGemProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}
