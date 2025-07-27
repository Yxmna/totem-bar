package io.github.yxmna.totembar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.fabricmc.loader.api.FabricLoader;

public class TotemBarClient implements ClientModInitializer {

	private static final Identifier LAYER_ID = Identifier.of("totembar", "totem_bar");

	private static final Identifier TEX_FULL   = Identifier.of("totembar", "textures/gui/sprites/totem.png");
	private static final Identifier TEX_EMPTY  = Identifier.of("totembar", "textures/gui/sprites/totem_empty.png");

	@Override
	public void onInitializeClient() {
		TotemBarConfig.load();
		HudLayerRegistrationCallback.EVENT.register(layers ->
				layers.attachLayerAfter(IdentifiedLayer.MISC_OVERLAYS, LAYER_ID, TotemBarClient::render));
	}


	private static void render(DrawContext ctx, Object ticks) {
		if (TotemBarConfig.enabled) {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player == null || client.options.hudHidden) return;
			if (client.player.isCreative() || client.player.isSpectator()) return;

			int totInventory = countTotems(client);
			int handTotems   = countHandTotems(client);
			drawIcons(ctx, totInventory, handTotems);
		}
	}


	private static void drawIcons(DrawContext ctx, int totInventory, int hand) {
		int iconSize = 9;
		int xSpace  = -1;
		int ySpace = 1;
		int yOffset = 30;
		int xOffset = 91;
		int step = iconSize + xSpace;

		int total = Math.min(totInventory, 10);
		if (TotemBarConfig.renderMode == TotemBarConfig.RenderMode.INVENTORY_ONLY) {
			total = total - hand;
		}
		if (total == 0) return;

		MinecraftClient client = MinecraftClient.getInstance();
		int sw = client.getWindow().getScaledWidth();
		int sh = client.getWindow().getScaledHeight();

		int rightEdge = sw / 2 + xOffset;

		int totalWidth = iconSize + (total - 1) * step;

		int x0 = rightEdge - totalWidth;

		int y  = sh - yOffset - ySpace - (iconSize*2);

		int moreOffset = getTotemBarYOffset(client, iconSize+ ySpace);
		y = y - moreOffset;


		for (int i = 0; i < total; i++) {
			// boolean isHandIcon = i >= total - hand;

			boolean isFull = switch (TotemBarConfig.renderMode) {
				case INVENTORY_ONLY -> true;
				case COMBINED       -> i >= (total - hand);
			};

			Identifier tex = isFull ? TEX_FULL : TEX_EMPTY;
			ctx.drawTexture(
					RenderLayer::getGuiTextured,
					tex,
					x0 + i * step,
					y,
					0f, 0f,
					iconSize, iconSize,
					iconSize, iconSize);
		}
	}

	private static int getTotemBarYOffset(MinecraftClient client, int spacing) {
		int offset = 0;

		offset += TotemBarConfig.yOffset;

		boolean airBar = client.player.isSubmergedInWater() || client.player.getAir() < client.player.getMaxAir();
		if (airBar) {
			offset += spacing;
		}

		Entity vehicle = client.player.getVehicle();
		if (vehicle instanceof LivingEntity mount) {

			float hp = mount.getMaxHealth();

			if (hp > 0f) {
				if (hp > 20f) {
					int extraLines = (int) Math.ceil((hp - 20f) / 20f);
					offset += extraLines * spacing;
				}

				if (FabricLoader.getInstance().isModLoaded("bettermounthud")) {
					offset += spacing;
				}
			}
		}

		return offset;
	}


	/* ---------- counts ---------- */

	private static int countTotems(MinecraftClient cl) {
		return cl.player.getInventory().count(Items.TOTEM_OF_UNDYING);
	}

	private static int countHandTotems(MinecraftClient cl) {
		int h = 0;
		if (cl.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) h++;
		if (cl.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING))  h++;
		return h;
	}


}
