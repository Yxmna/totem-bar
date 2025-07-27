package io.github.yxmna.totembar;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

class TotemBarModMenuScreen {

    static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Totem Bar Settings"));

        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Enabled toggle
        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), TotemBarConfig.enabled)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable or disable the totem bar rendering entirely."))
                .setSaveConsumer(value -> TotemBarConfig.enabled = value)
                .build());

        // Display mode
        category.addEntry(entryBuilder.startEnumSelector(
                        Text.literal("Display Mode"),
                        TotemBarConfig.RenderMode.class,
                        TotemBarConfig.renderMode)
                .setDefaultValue(TotemBarConfig.RenderMode.COMBINED)
                .setEnumNameProvider(mode -> switch (mode) {
                    case TotemBarConfig.RenderMode.COMBINED -> Text.literal("Full");
                    case TotemBarConfig.RenderMode.INVENTORY_ONLY -> Text.literal("In stock only");
                    default -> Text.literal("");
                })
                .setTooltip(
                        Text.literal("Choose how the totems are displayed:"),
                        Text.literal("- Full: hand totems use full icons,"),
                        Text.literal("  inventory totems use empty icons."),
                        Text.literal("- In stock only: only inventory totems are shown as full icons."),
                        Text.literal("  hand totems are ignored.")
                )
                .setSaveConsumer(value -> TotemBarConfig.renderMode = value)
                .build());

        // Y Offset
        category.addEntry(entryBuilder.startIntField(Text.literal("Vertical Offset (Y)"), TotemBarConfig.yOffset)
                .setDefaultValue(0)
                .setTooltip(Text.literal("Adjust the vertical position of the totem bar."),
                        Text.literal("Positive values move it upward, negative downward."))
                .setSaveConsumer(value -> TotemBarConfig.yOffset = value)
                .build());

        builder.setSavingRunnable(TotemBarConfig::save);
        return builder.build();
    }
}
