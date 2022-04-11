package net.cancheta.ai.path.world;

import java.util.function.IntConsumer;
import org.jetbrains.annotations.Nullable;

import org.apache.commons.lang3.Validate;

public class BitArray {
    private static final int[] field_232981_a_ = new int[]{-1, -1, 0, -2147483648, 0, 0, 1431655765, 1431655765, 0, -2147483648, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, -2147483648, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, -2147483648, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, -2147483648, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, -2147483648, 0, 5};
    private final long[] longArray;
    private final int bitsPerEntry;
    private final long maxEntryValue;
    private final int arraySize;
    private final int field_232982_f_;
    private final int field_232983_g_;
    private final int field_232984_h_;
    private final int field_232985_i_;

    public BitArray(int p_i46832_1_, int p_i46832_2_) {
        this(p_i46832_1_, p_i46832_2_, (long[])null);
    }

    public BitArray(int p_i47901_1_, int p_i47901_2_, @Nullable long[] p_i47901_3_) {
        Validate.inclusiveBetween(1L, 32L, (long)p_i47901_1_);
        this.arraySize = p_i47901_2_;
        this.bitsPerEntry = p_i47901_1_;
        this.maxEntryValue = (1L << p_i47901_1_) - 1L;
        this.field_232982_f_ = (char)(64 / p_i47901_1_);
        int lvt_4_1_ = 3 * (this.field_232982_f_ - 1);
        this.field_232983_g_ = field_232981_a_[lvt_4_1_ + 0];
        this.field_232984_h_ = field_232981_a_[lvt_4_1_ + 1];
        this.field_232985_i_ = field_232981_a_[lvt_4_1_ + 2];
        int lvt_5_1_ = (p_i47901_2_ + this.field_232982_f_ - 1) / this.field_232982_f_;
        if (p_i47901_3_ != null) {
            if (p_i47901_3_.length != lvt_5_1_) {
                throw new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + lvt_5_1_);
            }

            this.longArray = p_i47901_3_;
        } else {
            this.longArray = new long[lvt_5_1_];
        }

    }

    private int func_232986_b_(int p_232986_1_) {
        long lvt_2_1_ = Integer.toUnsignedLong(this.field_232983_g_);
        long lvt_4_1_ = Integer.toUnsignedLong(this.field_232984_h_);
        return (int)((long)p_232986_1_ * lvt_2_1_ + lvt_4_1_ >> 32 >> this.field_232985_i_);
    }

    public int swapAt(int p_219789_1_, int p_219789_2_) {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_219789_1_);
        Validate.inclusiveBetween(0L, this.maxEntryValue, (long)p_219789_2_);
        int lvt_3_1_ = this.func_232986_b_(p_219789_1_);
        long lvt_4_1_ = this.longArray[lvt_3_1_];
        int lvt_6_1_ = (p_219789_1_ - lvt_3_1_ * this.field_232982_f_) * this.bitsPerEntry;
        int lvt_7_1_ = (int)(lvt_4_1_ >> lvt_6_1_ & this.maxEntryValue);
        this.longArray[lvt_3_1_] = lvt_4_1_ & ~(this.maxEntryValue << lvt_6_1_) | ((long)p_219789_2_ & this.maxEntryValue) << lvt_6_1_;
        return lvt_7_1_;
    }

    public void setAt(int p_188141_1_, int p_188141_2_) {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188141_1_);
        Validate.inclusiveBetween(0L, this.maxEntryValue, (long)p_188141_2_);
        int lvt_3_1_ = this.func_232986_b_(p_188141_1_);
        long lvt_4_1_ = this.longArray[lvt_3_1_];
        int lvt_6_1_ = (p_188141_1_ - lvt_3_1_ * this.field_232982_f_) * this.bitsPerEntry;
        this.longArray[lvt_3_1_] = lvt_4_1_ & ~(this.maxEntryValue << lvt_6_1_) | ((long)p_188141_2_ & this.maxEntryValue) << lvt_6_1_;
    }

    public int getAt(int p_188142_1_) {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188142_1_);
        int lvt_2_1_ = this.func_232986_b_(p_188142_1_);
        long lvt_3_1_ = this.longArray[lvt_2_1_];
        int lvt_5_1_ = (p_188142_1_ - lvt_2_1_ * this.field_232982_f_) * this.bitsPerEntry;
        return (int)(lvt_3_1_ >> lvt_5_1_ & this.maxEntryValue);
    }

    public long[] getBackingLongArray() {
        return this.longArray;
    }

    public int size() {
        return this.arraySize;
    }

    public void getAll(IntConsumer p_225421_1_) {
        int lvt_2_1_ = 0;
        long[] var3 = this.longArray;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            long lvt_6_1_ = var3[var5];

            for(int lvt_8_1_ = 0; lvt_8_1_ < this.field_232982_f_; ++lvt_8_1_) {
                p_225421_1_.accept((int)(lvt_6_1_ & this.maxEntryValue));
                lvt_6_1_ >>= this.bitsPerEntry;
                ++lvt_2_1_;
                if (lvt_2_1_ >= this.arraySize) {
                    return;
                }
            }
        }

    }
}
