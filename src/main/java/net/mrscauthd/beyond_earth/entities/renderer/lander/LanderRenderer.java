package net.mrscauthd.beyond_earth.entities.renderer.lander;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.entities.LanderEntity;
import net.mrscauthd.beyond_earth.entities.renderer.VehicleRenderer;

@OnlyIn(Dist.CLIENT)
public class LanderRenderer extends VehicleRenderer<LanderEntity, LanderModel<LanderEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(BeyondEarthMod.MODID, "textures/vehicles/lander.png");

    public LanderRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new LanderModel<>(renderManagerIn.bakeLayer(LanderModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LanderEntity p_114482_) {
        return TEXTURE;
    }
}