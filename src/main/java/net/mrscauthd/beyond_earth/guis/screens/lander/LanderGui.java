package net.mrscauthd.beyond_earth.guis.screens.lander;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.*;
import net.minecraftforge.network.IContainerFactory;
import net.mrscauthd.beyond_earth.entities.LanderEntity;
import net.mrscauthd.beyond_earth.guis.helper.ContainerHelper;
import net.mrscauthd.beyond_earth.registries.ScreensRegistry;
import org.jetbrains.annotations.NotNull;

public class LanderGui {

	public static class GuiContainerFactory implements IContainerFactory<GuiContainer> {
		public GuiContainer create(int id, Inventory inv, FriendlyByteBuf extraData) {
			return new GuiContainer(id, inv, extraData);
		}
	}

	public static class GuiContainer extends AbstractContainerMenu {
		Entity lander;

		public GuiContainer(int id, Inventory inv, FriendlyByteBuf extraData) {
			super(ScreensRegistry.LANDER_GUI.get(), id);

			this.lander = inv.player.level.getEntity(extraData.readVarInt());

			IItemHandlerModifiable itemHandler = ((LanderEntity) lander).getItemHandler();

			this.addSlot(new SlotItemHandler(itemHandler, 0, 31, 35) {
				@Override
				public boolean mayPlace(@NotNull ItemStack stack) {
					return false;
				}
			});

			this.addSlot(new SlotItemHandler(itemHandler, 1, 80, 35) {
				@Override
				public boolean mayPlace(@NotNull ItemStack stack) {
					return false;
				}
			});

			ContainerHelper.addInventorySlots(this, inv, 8, 84, this::addSlot);
		}

		@Override
		public boolean stillValid(Player p_38874_) {
			return !lander.isRemoved();
		}

		@Override
		public ItemStack quickMoveStack(Player playerIn, int index) {
			return ContainerHelper.transferStackInSlot(this, playerIn, index, 0, 2, this::moveItemStackTo);
		}
	}
}