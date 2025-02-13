package net.mrscauthd.beyond_earth.registries;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.entities.*;
import net.mrscauthd.beyond_earth.entities.alien.AlienEntity;
import net.mrscauthd.beyond_earth.entities.pygro.PygroEntity;

@Mod.EventBusSubscriber(modid = BeyondEarthMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeRegistry {

    @SubscribeEvent
    public static void defaultAttributes(EntityAttributeCreationEvent event) {
        event.put(EntitiesRegistry.ALIEN.get(), AlienEntity.setCustomAttributes().build());
        event.put(EntitiesRegistry.PYGRO.get(), PygroEntity.setCustomAttributes().build());
        event.put(EntitiesRegistry.PYGRO_BRUTE.get(), PygroBruteEntity.setCustomAttributes().build());
        event.put(EntitiesRegistry.MOGLER.get(), MoglerEntity.setCustomAttributes().build());
        event.put(EntitiesRegistry.MARTIAN_RAPTOR.get(), MartianRaptor.setCustomAttributes().build());
        event.put(EntitiesRegistry.ALIEN_ZOMBIE.get(), AlienZombieEntity.setCustomAttributes().build());
        event.put(EntitiesRegistry.STAR_CRAWLER.get(), StarCrawlerEntity.setCustomAttributes().build());
    }
}
