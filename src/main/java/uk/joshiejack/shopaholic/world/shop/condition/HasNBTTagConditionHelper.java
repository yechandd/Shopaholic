package uk.joshiejack.shopaholic.world.shop.condition;

import net.minecraft.nbt.CompoundTag;

public class HasNBTTagConditionHelper {
    public static boolean matches(CompoundTag data, EntityHasNBTTagCondition.FieldType dataType, String key, String value) {
        if (data == null || !data.contains(key)) return false;
        return switch (dataType) {
            case BYTE -> data.getByte(key) == Byte.parseByte(value);
            case SHORT -> data.getShort(key) == Short.parseShort(value);
            case INT -> data.getInt(key) == Integer.parseInt(value);
            case LONG -> data.getLong(key) == Long.parseLong(value);
            case FLOAT -> data.getFloat(key) == Float.parseFloat(value);
            case DOUBLE -> data.getDouble(key) == Double.parseDouble(value);
            case BYTE_ARRAY -> {
                byte[] array = data.getByteArray(key);
                for (byte i : array) {
                    if (i == Byte.parseByte(value)) {
                        yield true;
                    }
                }

                yield false;
            }
            case STRING -> data.getString(key).equals(value);
            case INT_ARRAY -> {
                int[] array = data.getIntArray(key);
                for (int i : array) {
                    if (i == Integer.parseInt(value)) {
                        yield true;
                    }
                }

                yield false;
            }
            case LONG_ARRAY -> {
                long[] array = data.getLongArray(key);
                for (long i : array) {
                    if (i == Long.parseLong(value)) {
                        yield true;
                    }
                }

                yield false;
            }
        };
    }
}