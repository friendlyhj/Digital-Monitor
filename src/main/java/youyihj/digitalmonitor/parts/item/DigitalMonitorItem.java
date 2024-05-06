package youyihj.digitalmonitor.parts.item;

import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import appeng.core.CreativeTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import youyihj.digitalmonitor.Tags;
import youyihj.digitalmonitor.parts.PartDigitalMonitor;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public class DigitalMonitorItem extends Item implements IPartItem<PartDigitalMonitor> {
    public DigitalMonitorItem() {
        this.setRegistryName(Tags.MOD_ID, "digital_monitor");
        this.setTranslationKey(Tags.MOD_ID + ".digital_monitor");
        this.setCreativeTab(CreativeTab.instance);
    }

    @Nullable
    @Override
    public PartDigitalMonitor createPartFromItemStack(ItemStack itemStack) {
        return new PartDigitalMonitor(itemStack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return AEApi.instance().partHelper().placeBus(player.getHeldItem(hand), pos, facing, player, hand, worldIn);
    }
}
