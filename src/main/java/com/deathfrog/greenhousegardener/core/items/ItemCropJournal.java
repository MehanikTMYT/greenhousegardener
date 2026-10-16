package com.deathfrog.greenhousegardener.core.items;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.deathfrog.greenhousegardener.api.colony.buildings.BuildingGreenhouse;
import com.deathfrog.greenhousegardener.core.blocks.ModBlocks;
import com.deathfrog.greenhousegardener.core.client.gui.modules.WindowCropJournal;
import com.minecolonies.api.items.component.ColonyId;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Portable crop journal bound to one Greenhouse building. */
public class ItemCropJournal extends Item
{
    private static final String TAG_GREENHOUSE = "greenhouse";

    public ItemCropJournal(final Properties properties)
    {
        super(properties.stacksTo(1));
    }

    @SuppressWarnings("null")
    @Override
    public @NotNull InteractionResult useOn(final @Nonnull UseOnContext context)
    {
        if (context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.blockHutGreenhouse.get()))
        {
            if (!context.getLevel().isClientSide()
                && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileEntityColonyBuilding tile
                && tile.getBuilding() instanceof BuildingGreenhouse)
            {
                final ItemStack stack = context.getItemInHand();
                tile.writeColonyToItemStack(stack);
                writeGreenhousePosition(stack, context.getClickedPos());
                // 26.1.2: Player.displayClientMessage(Component, boolean) was removed; the overlay=true case is
                // now its own dedicated method, Player.sendOverlayMessage(Component).
                context.getPlayer().sendOverlayMessage(Component.translatable(
                    "item.greenhousegardener.crop_journal.bound", tile.getColony().getName()));
            }
            return InteractionResult.SUCCESS;
        }

        return open(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand());
    }

    @SuppressWarnings("null")
    @Override
    public @NotNull InteractionResult use(
        final @Nonnull Level level, final @Nonnull Player player, final @Nonnull InteractionHand hand)
    {
        // 26.1.2: Item.use returns InteractionResult directly; InteractionResultHolder was removed.
        final ItemStack stack = player.getItemInHand(hand);
        return open(level, player, hand, stack);
    }

    @Override
    public void appendHoverText(final @Nonnull ItemStack stack, @Nullable final @Nonnull TooltipContext context,
        final @Nonnull TooltipDisplay display, final @Nonnull Consumer<Component> tooltip, final @Nonnull TooltipFlag flag)
    {
        final BlockPos greenhouse = readGreenhousePosition(stack);
        if (greenhouse != null)
        {
            tooltip.accept(Component.translatable("item.greenhousegardener.crop_journal.greenhouse",
                greenhouse.getX(), greenhouse.getY(), greenhouse.getZ()).withStyle(ChatFormatting.GRAY));
        }
        else
        {
            tooltip.accept(Component.translatable("item.greenhousegardener.crop_journal.unbound").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, display, tooltip, flag);
    }

    /**
     * Read the bound Greenhouse position from an item stack.
     *
     * @param stack journal stack to inspect
     * @return bound position, or {@code null} when unbound
     */
    @SuppressWarnings("null")
    public static @Nullable BlockPos readGreenhousePosition(final ItemStack stack)
    {
        final CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(TAG_GREENHOUSE) ? BlockPos.of(tag.getLongOr(TAG_GREENHOUSE, 0L)) : null;
    }

    /**
     * Store a Greenhouse position without disturbing other custom item data.
     *
     * @param stack journal stack to update
     * @param position Greenhouse position to store
     */
    @SuppressWarnings("null")
    private static void writeGreenhousePosition(final ItemStack stack, final BlockPos position)
    {
        final CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putLong(TAG_GREENHOUSE, position.asLong());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /**
     * Request the journal contents on the logical client.
     *
     * @param level current level
     * @param player player using the journal
     * @param hand hand holding the journal
     * @param stack journal stack
     * @return successful interaction result
     */
    @SuppressWarnings("null")
    private static @Nonnull InteractionResult open(final Level level, final Player player, final InteractionHand hand, final ItemStack stack)
    {
        final BlockPos greenhouse = readGreenhousePosition(stack);
        if (greenhouse == null || !ColonyId.readFromItemStack(stack).hasColonyId())
        {
            if (level.isClientSide())
            {
                player.sendOverlayMessage(Component.translatable("item.greenhousegardener.crop_journal.need_greenhouse"));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide())
        {
            WindowCropJournal.request(hand, greenhouse);
        }
        return InteractionResult.SUCCESS;
    }
}
