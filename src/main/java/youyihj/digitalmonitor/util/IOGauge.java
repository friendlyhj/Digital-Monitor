package youyihj.digitalmonitor.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;

/**
 * @author youyihj
 */
public class IOGauge {
    private final long[] values;
    private final int sampleInterval;
    private int cursor;

    public static IOGauge fromNBT(NBTTagCompound tag) {
        IOGauge gauge = new IOGauge(tag.getInteger("interval"));
        tag.getTagList("values", Constants.NBT.TAG_LONG).forEach(it -> gauge.put(((NBTTagLong) it).getLong()));
        gauge.cursor = tag.getInteger("cursor");
        return gauge;
    }

    public IOGauge(int sampleInterval) {
        this.sampleInterval = sampleInterval;
        this.values = new long[sampleInterval * 2];
    }

    public void put(long l) {
        values[++cursor % values.length] = l;
    }

    public void reset() {
        cursor = 0;
        Arrays.fill(values, 0L);
    }

    public long getFlow() {
        int part = cursor % sampleInterval;
        long first = values[part];
        long second = values[sampleInterval + part];
        long update = cursor % values.length < sampleInterval ? first - second : second - first;
        return update / sampleInterval;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("interval", sampleInterval);
        NBTTagList nbtValues = new NBTTagList();
        for (long value : values) {
            nbtValues.appendTag(new NBTTagLong(value));
        }
        tag.setTag("values", nbtValues);
        tag.setInteger("cursor", cursor);
        return tag;
    }
}
