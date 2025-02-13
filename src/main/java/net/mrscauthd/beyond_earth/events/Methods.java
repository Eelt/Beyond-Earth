package net.mrscauthd.beyond_earth.events;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.capabilities.oxygen.IOxygenStorage;
import net.mrscauthd.beyond_earth.capabilities.oxygen.OxygenUtil;
import net.mrscauthd.beyond_earth.config.Config;
import net.mrscauthd.beyond_earth.entities.*;
import net.mrscauthd.beyond_earth.events.forgeevents.LanderOrbitTeleportEvent;
import net.mrscauthd.beyond_earth.events.forgeevents.LivingSetFireInHotPlanetEvent;
import net.mrscauthd.beyond_earth.events.forgeevents.LivingSetVenusRainEvent;
import net.mrscauthd.beyond_earth.events.forgeevents.StartRideLanderEvent;
import net.mrscauthd.beyond_earth.guis.screens.planetselection.PlanetSelectionGui;
import net.mrscauthd.beyond_earth.items.VehicleItem;
import net.mrscauthd.beyond_earth.registries.*;

import java.util.Set;
import java.util.function.Function;

public class Methods {

    public static final ResourceKey<Level> moon = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "moon"));
    public static final ResourceKey<Level> moon_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "moon_orbit"));
    public static final ResourceKey<Level> mars = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mars"));
    public static final ResourceKey<Level> mars_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mars_orbit"));
    public static final ResourceKey<Level> mercury = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mercury"));
    public static final ResourceKey<Level> mercury_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mercury_orbit"));
    public static final ResourceKey<Level> venus = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "venus"));
    public static final ResourceKey<Level> venus_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "venus_orbit"));
    public static final ResourceKey<Level> glacio = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "glacio"));
    public static final ResourceKey<Level> glacio_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "glacio_orbit"));
    public static final ResourceKey<Level> overworld = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
    public static final ResourceKey<Level> earth_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID,"earth_orbit"));

    public static final ResourceLocation space_station = new ResourceLocation(BeyondEarthMod.MODID, "space_station");

    public static final Set<ResourceKey<Level>> worldsWithoutRain = Set.of(
            moon,
            moon_orbit,
            mars,
            mars_orbit,
            mercury,
            mercury_orbit,
            venus_orbit,
            glacio_orbit,
            earth_orbit
    );

    private static final Set<ResourceKey<Level>> spaceWorldsWithoutOxygen = Set.of(
            moon,
            moon_orbit,
            mars,
            mars_orbit,
            mercury,
            mercury_orbit,
            venus,
            venus_orbit,
            glacio_orbit,
            earth_orbit
    );

    private static final Set<ResourceKey<Level>> spaceWorlds = Set.of(
            moon,
            moon_orbit,
            mars,
            mars_orbit,
            mercury,
            mercury_orbit,
            venus,
            venus_orbit,
            glacio,
            glacio_orbit,
            earth_orbit
    );

    private static final Set<ResourceKey<Level>> orbitWorlds = Set.of(
            earth_orbit,
            moon_orbit,
            mars_orbit,
            mercury_orbit,
            venus_orbit,
            glacio_orbit
    );

    public static void entityWorldTeleporter(Entity entity, ResourceKey<Level> planet, double high) {
        if (entity.canChangeDimensions()) {

            if (entity.getServer() == null) {
                return;
            }

            ServerLevel nextLevel = entity.getServer().getLevel(planet);

            if (nextLevel == null) {
                System.out.println("World not existing!");
                return;
            }

            entity.changeDimension(nextLevel, new ITeleporter() {

                @Override
                public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                    Vec3 pos = new Vec3(entity.position().x, high, entity.position().z);

                    return new PortalInfo(pos, Vec3.ZERO, entity.getYRot(), entity.getXRot());
                }

                @Override
                public boolean isVanilla() {
                    return false;
                }

                @Override
                public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                    return false;
                }
            });
        }
    }

    public static boolean netheriteSpaceSuitCheck(LivingEntity entity) {
        if (!checkArmor(entity, 3, ItemsRegistry.NETHERITE_OXYGEN_MASK.get())) return false;
        if (!checkArmor(entity, 2, ItemsRegistry.NETHERITE_SPACE_SUIT.get())) return false;
        if (!checkArmor(entity, 1, ItemsRegistry.NETHERITE_SPACE_PANTS.get())) return false;
        if (!checkArmor(entity, 0, ItemsRegistry.NETHERITE_SPACE_BOOTS.get())) return false;

        return true;
    }

    public static boolean spaceSuitCheck(LivingEntity entity) {
        if (!checkArmor(entity, 3, ItemsRegistry.OXYGEN_MASK.get())) return false;
        if (!checkArmor(entity, 2, ItemsRegistry.SPACE_SUIT.get())) return false;
        if (!checkArmor(entity, 1, ItemsRegistry.SPACE_PANTS.get())) return false;
        if (!checkArmor(entity, 0, ItemsRegistry.SPACE_BOOTS.get())) return false;

        return true;
    }

    public static boolean spaceSuitCheckBoth(LivingEntity entity) {
        boolean item3 = checkArmor(entity, 3, ItemsRegistry.OXYGEN_MASK.get());
        boolean item3b = checkArmor(entity, 3, ItemsRegistry.NETHERITE_OXYGEN_MASK.get());

        if (!item3 && !item3b) {
            return false;
        }

        boolean item2 = checkArmor(entity, 2, ItemsRegistry.SPACE_SUIT.get());
        boolean item2b = checkArmor(entity, 2, ItemsRegistry.NETHERITE_SPACE_SUIT.get());

        if (!item2 && !item2b) {
            return false;
        }

        boolean item1 = checkArmor(entity, 1, ItemsRegistry.SPACE_PANTS.get());
        boolean item1b = checkArmor(entity, 1, ItemsRegistry.NETHERITE_SPACE_PANTS.get());

        if (!item1 && !item1b) {
            return false;
        }

        boolean item0 = checkArmor(entity, 0, ItemsRegistry.SPACE_BOOTS.get());
        boolean item0b = checkArmor(entity, 0, ItemsRegistry.NETHERITE_SPACE_BOOTS.get());

        if (!item0 && !item0b) {
            return false;
        }

        return true;
    }

    public static boolean checkArmor(LivingEntity entity, int number, Item item) {
        return entity.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, number)).getItem() == item;
    }

    public static boolean isSpaceWorld(Level world) {
        return spaceWorlds.contains(world.dimension());
    }

    public static boolean isSpaceWorldWithoutOxygen(Level world) {
        return spaceWorldsWithoutOxygen.contains(world.dimension());
    }

    public static boolean isOrbitWorld(Level world) {
        return orbitWorlds.contains(world.dimension());
    }

    public static boolean isWorld(Level world, ResourceKey<Level> loc) {
        return world.dimension() == loc;
    }

    public static void oxygenDamage(LivingEntity entity) {
        entity.hurt(DamageSourcesRegistry.DAMAGE_SOURCE_OXYGEN, 1.0F);
    }

    public static boolean isRocket(Entity entity) {
        return entity instanceof IRocketEntity;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof VehicleEntity;
    }

    public static void dropRocket(Player player) {
        Item item1 = player.getMainHandItem().getItem();
        Item item2 = player.getOffhandItem().getItem();

        if (item1 instanceof VehicleItem && item2 instanceof VehicleItem) {

            ItemEntity spawn = new ItemEntity(player.level, player.getX(),player.getY(),player.getZ(), new ItemStack(item2));
            spawn.setPickUpDelay(0);
            player.level.addFreshEntity(spawn);

            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(capability -> {
                capability.extractItem(40, 1, false); //40 is offhand
            });

        }
    }

    /** If a entity should not get Fire add it to the Tag "venus_fire" */
    public static void planetFire(LivingEntity entity, ResourceKey<Level> planet) {
        Level level = entity.level;

        if (!Methods.isWorld(level, planet)) {
            return;
        }

        if ((entity instanceof Mob || entity instanceof Player) && (Methods.netheriteSpaceSuitCheck(entity) || entity.hasEffect(MobEffects.FIRE_RESISTANCE) || entity.fireImmune())) {
            return;
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (player.isSpectator() || player.getAbilities().instabuild) {
                return;
            }
        }

        if (MinecraftForge.EVENT_BUS.post(new LivingSetFireInHotPlanetEvent(entity, planet))) {
            return;
        }

        if (tagCheck(entity, TagsRegistry.PLANET_FIRE_TAG)) {
            return;
        }

        entity.setSecondsOnFire(10);
    }

    /** If a entity should not get Damage add it to the Tag "venus_rain" */
    public static void venusRain(LivingEntity entity, ResourceKey<Level> planet) {
        if (!Methods.isWorld(entity.level, planet)) {
            return;
        }

        if (entity.isPassenger() && (Methods.isRocket(entity.getVehicle()) || entity.getVehicle() instanceof LanderEntity)) {
            return;
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (player.isSpectator() || player.getAbilities().instabuild) {
                return;
            }
        }

        if (MinecraftForge.EVENT_BUS.post(new LivingSetVenusRainEvent(entity, planet))) {
            return;
        }

        if (tagCheck(entity, TagsRegistry.VENUS_RAIN_TAG)) {
            return;
        }

        if (entity.level.getLevelData().isRaining() && entity.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(entity.getX()), (int) Math.floor(entity.getZ())) <= Math.floor(entity.getY()) + 1) {
            entity.hurt(DamageSourcesRegistry.DAMAGE_SOURCE_ACID_RAIN, 1);
        }
    }

    /** If a entity should get oxygen damage add it to the tag "oxygen" */
    public static void entityOxygen(LivingEntity entity, Level world) {
        if (entity instanceof Player) {
            return;
        }

        if (Config.ENTITY_OXYGEN_SYSTEM.get() && Methods.isSpaceWorldWithoutOxygen(world) && tagCheck(entity, TagsRegistry.OXYGEN_TAG)) {

            if (!entity.hasEffect(EffectsRegistry.OXYGEN_EFFECT.get())) {

                entity.getPersistentData().putDouble(BeyondEarthMod.MODID + ":oxygen_tick", entity.getPersistentData().getDouble(BeyondEarthMod.MODID + ":oxygen_tick") + 1);

                if (entity.getPersistentData().getDouble(BeyondEarthMod.MODID + ":oxygen_tick") > 15) {

                    if(!world.isClientSide) {
                        Methods.oxygenDamage(entity);
                    }

                    entity.getPersistentData().putDouble(BeyondEarthMod.MODID + ":oxygen_tick", 0);
                }
            }
        }

        //Out of Space
        if (Config.ENTITY_OXYGEN_SYSTEM.get() && entity.hasEffect(EffectsRegistry.OXYGEN_EFFECT.get())) {
            entity.setAirSupply(300);
        }
    }

    public static void vehicleRotation(Entity vehicle, float rotation) {
        vehicle.setYRot(vehicle.getYRot() + rotation);
        vehicle.setYBodyRot(vehicle.getYRot());
        vehicle.yRotO = vehicle.getYRot();
    }

    public static boolean tagCheck(Entity entity, TagKey<EntityType<?>> tag) {
        return entity.getType().is(tag);
    }
    
    public static boolean tagCheck(Fluid fluid, TagKey<Fluid> tag) {
        return fluid.is(tag);
    }

    public static boolean tagCheck(ItemStack item, TagKey<Item> tag) {
        return item.is(tag);
    }

    public static void landerTeleport(Player player, ResourceKey<Level> newPlanet) {
        LanderEntity lander = (LanderEntity) player.getVehicle();

        if (lander.getY() < 1) {

            /** CALL LANDER ORBIT TELEPORT PRE EVENT */
            MinecraftForge.EVENT_BUS.post(new LanderOrbitTeleportEvent.Pre(lander, player));

            ItemStack slot_0 = lander.getInventory().getStackInSlot(0);
            ItemStack slot_1 = lander.getInventory().getStackInSlot(1);
            lander.remove(Entity.RemovalReason.DISCARDED);

            Methods.entityWorldTeleporter(player, newPlanet, 700);

            Level newWorld = player.level;

            if (!player.level.isClientSide) {
                LanderEntity entityToSpawn = new LanderEntity(EntitiesRegistry.LANDER.get(), newWorld);
                entityToSpawn.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
                newWorld.addFreshEntity(entityToSpawn);

                entityToSpawn.getInventory().setStackInSlot(0, slot_0);
                entityToSpawn.getInventory().setStackInSlot(1, slot_1);

                player.startRiding(entityToSpawn);

                /** CALL LANDER ORBIT TELEPORT POST EVENT */
                MinecraftForge.EVENT_BUS.post(new LanderOrbitTeleportEvent.Post(lander, player));
            }
        }
    }

    public static void rocketTeleport(Player player, ResourceKey<Level> planet, ItemStack rocketItem, Boolean SpaceStation) {
        if (!Methods.isWorld(player.level, planet)) {
            Methods.entityWorldTeleporter(player, planet, 700);
        } else {
            player.setPos(player.getX(), 700, player.getZ());

            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).connection.teleport(player.getX(), 700, player.getZ(), player.getYRot(), player.getXRot());
            }
        }

        Level world = player.level;

        if (!world.isClientSide) {
            LanderEntity landerSpawn = new LanderEntity(EntitiesRegistry.LANDER.get(), world);
            landerSpawn.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
            world.addFreshEntity(landerSpawn);

            String itemId = player.getPersistentData().getString(BeyondEarthMod.MODID + ":slot0");

            landerSpawn.getInventory().setStackInSlot(0, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), 1));
            landerSpawn.getInventory().setStackInSlot(1, rocketItem);

            if (SpaceStation) {
                createSpaceStation(player, (ServerLevel) world);
            }

            /** CALL START RIDE LANDER EVENT */
            MinecraftForge.EVENT_BUS.post(new StartRideLanderEvent(landerSpawn, player));

            cleanUpPlayerNBT(player);

            player.startRiding(landerSpawn);
        }
    }

    public static void createSpaceStation(Player player, ServerLevel serverWorld) {
        BlockPos pos = new BlockPos(player.getX() - 15.5, 100, player.getZ() - 15.5);
        serverWorld.getStructureManager().getOrCreate(space_station).placeInWorld(serverWorld, pos, pos, new StructurePlaceSettings(), serverWorld.random, 2);
    }

    public static void cleanUpPlayerNBT(Player player) {
        player.getPersistentData().putBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open", false);
        player.getPersistentData().putString(BeyondEarthMod.MODID + ":rocket_type", "");
        player.getPersistentData().putString(BeyondEarthMod.MODID + ":slot0", "");
    }

    public static void openPlanetGui(Player player) {
        if (!(player.containerMenu instanceof PlanetSelectionGui.GuiContainer) && player.getPersistentData().getBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open")) {
            if (player instanceof ServerPlayer) {

                NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TextComponent("Planet Selection");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                        packetBuffer.writeUtf(player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type"));
                        return new PlanetSelectionGui.GuiContainer(id, inventory, packetBuffer);
                    }
                }, buf -> {
                    buf.writeUtf(player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type"));
                });
            }
        }
    }

    public static void teleportButton(Player player, ResourceKey<Level> planet, boolean SpaceStation) {
        ItemStack itemStack = new ItemStack(Items.AIR, 1);

        if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t1")) {
            itemStack = new ItemStack(ItemsRegistry.TIER_1_ROCKET_ITEM.get(),1);
        }
        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t2")) {
            itemStack = new ItemStack(ItemsRegistry.TIER_2_ROCKET_ITEM.get(),1);
        }
        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t3")) {
            itemStack = new ItemStack(ItemsRegistry.TIER_3_ROCKET_ITEM.get(),1);
        }
        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t4")) {
            itemStack = new ItemStack(ItemsRegistry.TIER_4_ROCKET_ITEM.get(),1);
        }

        Methods.rocketTeleport(player, planet, itemStack, SpaceStation);
    }

    public static void landerTeleportOrbit(Player player, Level world) {
        if (Methods.isWorld(world, Methods.earth_orbit)) {
            Methods.landerTeleport(player, Methods.overworld);
        }
        else if (Methods.isWorld(world, Methods.moon_orbit)) {
            Methods.landerTeleport(player, Methods.moon);
        }
        else if (Methods.isWorld(world, Methods.mars_orbit)) {
            Methods.landerTeleport(player, Methods.mars);
        }
        else if (Methods.isWorld(world, Methods.glacio_orbit)) {
            Methods.landerTeleport(player, Methods.glacio);
        }
        else if (Methods.isWorld(world, Methods.mercury_orbit)) {
            Methods.landerTeleport(player, Methods.mercury);
        }
        else if (Methods.isWorld(world, Methods.venus_orbit)) {
            Methods.landerTeleport(player, Methods.venus);
        }
    }

    public static void entityFallToPlanet(Level world, Entity entity) {
        ResourceKey<Level> world2 = world.dimension();

        if (world2 == Methods.earth_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.overworld, 450);
        }
        else if (world2 == Methods.moon_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.moon, 450);
        }
        else if (world2 == Methods.mars_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.mars, 450);
        }
        else if (world2 == Methods.mercury_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.mercury, 450);
        }
        else if (world2 == Methods.venus_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.venus, 450);
        }
        else if (world2 == Methods.glacio_orbit) {
            Methods.entityWorldTeleporter(entity, Methods.glacio, 450);
        }
    }

	public static void extractArmorOxygenUsingTimer(ItemStack itemstack, Player player) {
		if (!player.getAbilities().instabuild && !player.isSpectator() && Methods.spaceSuitCheckBoth(player) && !player.hasEffect(EffectsRegistry.OXYGEN_EFFECT.get()) && Config.PLAYER_OXYGEN_SYSTEM.get() && (Methods.isSpaceWorldWithoutOxygen(player.level) || player.isEyeInFluid(FluidTags.WATER))) {
			IOxygenStorage oxygenStorage = OxygenUtil.getItemStackOxygenStorage(itemstack);

            CompoundTag persistentData = player.getPersistentData();
			String key = BeyondEarthMod.MODID + ":oxygen_timer";
			int oxygenTimer = persistentData.getInt(key);
			oxygenTimer++;

			if (oxygenStorage.getOxygenStored() > 0 && oxygenTimer > 3) {
				oxygenStorage.extractOxygen(1, false);
				oxygenTimer = 0;
			}

			persistentData.putInt(key, oxygenTimer);
		}
	}

    public static void rocketSounds(Entity entity, Level world) {
        world.playSound(null, entity, SoundsRegistry.ROCKET_SOUND.get(), SoundSource.NEUTRAL,1,1);
    }

    public static void noFuelMessage(Player player) {
        if (!player.level.isClientSide) {
            player.displayClientMessage(new TranslatableComponent("message." + BeyondEarthMod.MODID + ".no_fuel"), false);
        }
    }

    public static void holdSpaceMessage(Player player) {
        if (!player.level.isClientSide) {
            player.displayClientMessage(new TranslatableComponent("message." + BeyondEarthMod.MODID + ".hold_space"), false);
        }
    }
}
