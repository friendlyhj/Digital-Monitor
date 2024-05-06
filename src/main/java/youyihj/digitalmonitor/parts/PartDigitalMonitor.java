package youyihj.digitalmonitor.parts;

import appeng.api.AEApi;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.render.TesrRenderHelper;
import appeng.me.GridAccessException;
import appeng.parts.reporting.AbstractPartReporting;
import appeng.parts.reporting.PartStorageMonitor;
import appeng.util.ReadableNumberConverter;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import youyihj.digitalmonitor.util.IOGauge;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author youyihj
 */
public class PartDigitalMonitor extends PartStorageMonitor implements IGridTickable {
    private IOGauge gauge = new IOGauge(5);
    private long flow;

    public PartDigitalMonitor(ItemStack is) {
        super(is);
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        flow = data.readLong();
        return super.readFromStream(data);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        data.writeLong(flow);
        super.writeToStream(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("gauge")) {
            gauge = IOGauge.fromNBT(data.getCompoundTag("gauge"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("gauge", gauge.toNBT());
    }

    @Nonnull
    @Override
    public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
        return new TickingRequest(20, 20, false, false);
    }

    @Nonnull
    @Override
    public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
        try {
            IAEStack<?> displayed = getDisplayed();
            if (displayed instanceof IAEItemStack) {
                IMEMonitor<IAEItemStack> inventory = this.getProxy()
                                                         .getStorage()
                                                         .getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                IAEItemStack itemInNetwork = inventory.getStorageList().findPrecise(((IAEItemStack) displayed));
                gauge.put(itemInNetwork == null ? 0 : itemInNetwork.getStackSize());
                long flowNow = gauge.getFlow();
                if (flow != flowNow) {
                    flow = flowNow;
                    updateTileEntity(node);
                }
            }
        } catch (GridAccessException ignored) {
            gauge.reset();
            flow = 0;
        }
        return TickRateModulation.SAME;
    }

    @Override
    public void renderDynamic(double x, double y, double z, float partialTicks, int destroyStage) {
        int flags = AbstractPartReporting.CHANNEL_FLAG | AbstractPartReporting.POWERED_FLAG;
        if ((this.getClientFlags() & flags) == flags) {
            IAEStack<?> ais = this.getDisplayed();
            if (ais != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5, y + 0.6, z + 0.5);
                EnumFacing facing = this.getSide().getFacing();
                TesrRenderHelper.moveToFace(facing);
                TesrRenderHelper.rotateToFace(facing, this.getSpin());
                if (ais instanceof IAEItemStack) {
                    TesrRenderHelper.renderItem2d(ais.asItemStackRepresentation(), 0.6f);
                    GlStateManager.translate(0, 0.12, 0);
                    renderString(ReadableNumberConverter.INSTANCE.toWideReadableForm(ais.getStackSize()), 0.012);
                    GlStateManager.translate(0, 0.12, 0);
                    renderString(ReadableNumberConverter.INSTANCE.toWideReadableForm(flow) + "/s", 0.012);
                }

                GlStateManager.popMatrix();
            }
        }
    }

    private static void renderString(String s, double scale) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-0.5F * fontRenderer.getStringWidth(s), 0.0F, 0.5F);
        fontRenderer.drawString(s, 0, 0, 0);
        GlStateManager.popMatrix();
    }

    private void updateTileEntity(IGridNode node) {
        BlockPos pos = node.getGridBlock().getLocation().getPos();
        IBlockState blockState = node.getWorld().getBlockState(pos);
        node.getWorld().notifyBlockUpdate(pos, blockState, blockState, Constants.BlockFlags.SEND_TO_CLIENTS);
    }
}
