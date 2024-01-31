package com.ultreon.mods.advanceddebug.util;

import com.ultreon.libs.commons.v0.tuple.Pair;
import com.ultreon.libs.commons.v0.util.EnumUtils;
import com.ultreon.libs.functions.v0.consumer.DoubleConsumer;
import com.ultreon.libs.functions.v0.consumer.*;
import com.ultreon.libs.functions.v0.supplier.ByteSupplier;
import com.ultreon.libs.functions.v0.supplier.FloatSupplier;
import com.ultreon.libs.functions.v0.supplier.ShortSupplier;
import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.*;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.*;

public class ImGuiEx {
    private static final ImGuiFileDialogPaneFun DUMP_NBT_CALLBACK = new ImGuiFileDialogPaneFun() {
        @Override
        public void paneFun(String filter, long userDatas, boolean canContinue) {
            ImGui.text("Filter: " + filter);
        }
    };

    public static void text(String label, Supplier<Object> value) {
        ImGui.text(label);
        ImGui.sameLine();
        Object o;
        try {
            o = value.get();
        } catch (Throwable t) {
            o = "~@# " + t.getClass().getName() + " #@~";
        }
        if (o instanceof Component component) {
            ImGui.text(component.getString());
        } else if (o instanceof Tag nbt) {
            ImGui.button("Copy NBT");
            if (ImGui.isItemClicked()) {
                String visit = new StringTagVisitor().visit(nbt);
                Minecraft.getInstance().keyboardHandler.setClipboard(visit);
            }
            ImGui.sameLine();
            if (nbt instanceof CompoundTag compoundTag) {
                if (ImGui.button("Dump NBT")) {
                    ImGuiFileDialog.openModal("browse-key", "Choose File", ".dat", ".", DUMP_NBT_CALLBACK, 250, 1, 42, ImGuiFileDialogFlags.ConfirmOverwrite);
                }

                if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                    if (ImGuiFileDialog.isOk()) {
                        String filePathName = ImGuiFileDialog.getFilePathName();
                        try(var stream = new FileOutputStream(filePathName)) {
                            NbtIo.writeCompressed(compoundTag, stream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ImGuiFileDialog.close();
                }
            }
        } else {
            ImGui.text(String.valueOf(o));
        }
    }

    public static void nbt(String label, Supplier<Tag> o) {
        ImGui.text(label);

        ImGui.sameLine();

        ImGui.button("Copy NBT");
        if (ImGui.isItemClicked()) {
            Tag nbt = o.get();
            String visit = new StringTagVisitor().visit(nbt);
            Minecraft.getInstance().keyboardHandler.setClipboard(visit);
        }

        ImGui.sameLine();

        if (ImGui.button("Dump NBT")) {
            ImGuiFileDialog.openModal("browse-key", "Choose File", ".dat", ".", DUMP_NBT_CALLBACK, 250, 1, 42, ImGuiFileDialogFlags.ConfirmOverwrite);
        }

        if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
            if (ImGuiFileDialog.isOk()) {
                Tag nbt = o.get();
                CompoundTag compoundTag;
                if (nbt instanceof CompoundTag) {
                    compoundTag = (CompoundTag) nbt;
                } else {
                    compoundTag = new CompoundTag();
                    compoundTag.put("Data", nbt);
                }

                String filePathName = ImGuiFileDialog.getFilePathName();
                try(var stream = new FileOutputStream(filePathName)) {
                    NbtIo.writeCompressed(compoundTag, stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ImGuiFileDialog.close();
        }
    }

    public static void editString(String label, String id, Supplier<String> value, Consumer<String> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImString i = new ImString(value.get(), 512);
            if (ImGui.inputText("##" + id, i, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editString(String label, String id, @NotNull String value, Consumer<String> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImString i = new ImString(value, 512);
            if (ImGui.inputText("##" + id, i, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    /**
     * {@code List<T> sorted = registry.stream().sorted(Comparator.comparing(registry::getKey)).toList();}
     * </pre>
     */
    public static <T> void editEntry(String label, String id, Registry<T> registry, List<T> sorted, Supplier<T> getter, Consumer<T> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            T e = getter.get();
            int ordinal = sorted.indexOf(e);
            ImInt index = new ImInt(ordinal);
            if (ImGui.combo("##" + id, index, sorted.stream().map(registry::getKey).map(Objects::toString).toArray(String[]::new))) {
                setter.accept(sorted.get(index.get()));
            }
        } catch (ResourceLocationException ignored) {

        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static <T> void editEntry(String label, String id, Function<T, ResourceLocation> map, Supplier<List<T>> sorted, Supplier<T> getter, Consumer<T> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            T e = getter.get();
            List<T> list = sorted.get();
            int ordinal = list.indexOf(e);
            ImInt index = new ImInt(ordinal);
            if (ImGui.combo("##" + id, index, list.stream().map(map).map(Objects::toString).toArray(String[]::new))) {
                setter.accept(list.get(index.get()));
            }
        } catch (ResourceLocationException ignored) {

        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editId(String label, String id, Supplier<ResourceLocation> value, Consumer<ResourceLocation> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImString i = new ImString(value.get().toString(), 160);
            if (ImGui.inputText("##" + id, i, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(new ResourceLocation(i.get()));
            }
        } catch (ResourceLocationException ignored) {

        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editId(String label, String id, @NotNull ResourceLocation value, Consumer<ResourceLocation> setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImString i = new ImString(value.toString(), 160);
            if (ImGui.inputText("##" + id, i, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(new ResourceLocation(i.get()));
            }
        } catch (ResourceLocationException ignored) {

        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editByte(String label, String id, ByteSupplier value, ByteConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImShort i = new ImShort(value.getByte());
            if (ImGui.inputScalar("##" + id, ImGuiDataType.U8, i)) {
                setter.accept((byte) i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editByte(String label, String id, byte value, ByteConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImShort i = new ImShort(value);
            if (ImGui.inputScalar("##" + id, ImGuiDataType.U8, i)) {
                setter.accept((byte) i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editShort(String label, String id, ShortSupplier value, ShortConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImShort i = new ImShort(value.getShort());
            if (ImGui.inputScalar("##" + id, ImGuiDataType.S16, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editShort(String label, String id, short value, ShortConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImShort i = new ImShort(value);
            if (ImGui.inputScalar("##" + id, ImGuiDataType.S16, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editInt(String label, String id, IntSupplier value, IntConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImInt i = new ImInt(value.getAsInt());
            if (ImGui.inputInt("##" + id, i, 1, 100, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }

    }

    public static void editInt(String label, String id, int value, IntConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImInt i = new ImInt(value);
            if (ImGui.inputInt("##" + id, i, 1, 100, ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editLong(String label, String id, LongSupplier value, LongConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImLong i = new ImLong(value.getAsLong());
            if (ImGui.inputScalar("##" + id, ImGuiDataType.S64, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editLong(String label, String id, long value, LongConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImLong i = new ImLong(value);
            if (ImGui.inputScalar("##" + id, ImGuiDataType.S64, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editFloat(String label, String id, FloatSupplier value, FloatConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImFloat i = new ImFloat(value.getFloat());
            if (ImGui.inputFloat("##" + id, i, 0, 0, "%.3f", ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editFloat(String label, String id, float value, FloatConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImFloat i = new ImFloat(value);
            if (ImGui.inputFloat("##" + id, i, 0, 0, "%.3f", ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editDouble(String label, String id, DoubleSupplier value, DoubleConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImDouble i = new ImDouble(value.getAsDouble());
            if (ImGui.inputDouble("##" + id, i, 0, 0, "%.6f", ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editDouble(String label, String id, double value, DoubleConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImDouble i = new ImDouble(value);
            if (ImGui.inputDouble("##" + id, i, 0, 0, "%.6f", ImGuiInputTextFlags.EnterReturnsTrue)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editBool(String label, String id, BooleanSupplier value, BooleanConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImBoolean i = new ImBoolean(value.getAsBoolean());
            if (ImGui.checkbox("##" + id, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void editBool(String label, String id, boolean value, BooleanConsumer setter) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImBoolean i = new ImBoolean(value);
            if (ImGui.checkbox("##" + id, i)) {
                setter.accept(i.get());
            }
        } catch (Throwable e) {
            ExceptionList.push(e);
        }
    }

    public static void bool(String label, BooleanSupplier value) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            ImGui.checkbox("##", value.getAsBoolean());
        } catch (Throwable t) {
            ImGui.text("~@# " + t.getClass().getName() + " #@~");
        }
    }

    public static void slider(String label, String id, IntSupplier value, int min, int max, IntConsumer onChange) {
        ImGui.text(label);
        ImGui.sameLine();
        int[] v = new int[]{value.getAsInt()};
        if (ImGui.sliderInt("##" + id, v, min, max)) {
            onChange.accept(v[0]);
        }
    }

    public static void slider(String label, String id, int value, int min, int max, IntConsumer onChange) {
        ImGui.text(label);
        ImGui.sameLine();
        int[] v = new int[]{value};
        if (ImGui.sliderInt("##" + id, v, min, max)) {
            onChange.accept(v[0]);
        }
    }

    public static void button(String label, String id, Runnable func) {
        ImGui.text(label);
        ImGui.sameLine();
        try {
            if (ImGui.button("##" + id, 120, 16)) {
                func.run();
            }
        } catch (Exception e) {
            ImGui.text(String.valueOf(e));
        }
    }

    public static void editColor3(String color, String s, Supplier<@NotNull Color> getter, Consumer<@NotNull Color> setter) {
        ImGui.text(color);
        ImGui.sameLine();
        try {
            Color c = getter.get();
            float[] floats = {c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1f};
            if (ImGui.colorEdit3("##" + s, floats)) {
                setter.accept(new Color(floats[0], floats[1], floats[2], 1f));
            }
        } catch (Exception e) {
            ImGui.text(String.valueOf(e));
        }
    }

    public static void editColor4(String color, String s, Supplier<Color> getter, Consumer<Color> setter) {
        ImGui.text(color);
        ImGui.sameLine();
        try {
            Color c = getter.get();
            float[] floats = {c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f};
            if (ImGui.colorEdit4("##" + s, floats)) {
                setter.accept(new Color(floats[0], floats[1], floats[2], floats[3]));
            }
        } catch (Exception e) {
            ImGui.text(String.valueOf(e));
        }
    }

    public static <T extends Enum<T>> void editEnum(String s, String s1, Supplier<T> getter, Consumer<T> setter) {
        ImGui.text(s);
        ImGui.sameLine();
        try {
            T e = getter.get();
            ImInt index = new ImInt(e.ordinal());
            if (ImGui.combo("##" + s1, index, Arrays.stream(e.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new))) {
                setter.accept(EnumUtils.byOrdinal(index.get(), e));
            }
        } catch (Exception e) {
            ImGui.text(String.valueOf(e));
        }
    }
}
