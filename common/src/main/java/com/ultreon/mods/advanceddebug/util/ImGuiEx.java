package com.ultreon.mods.advanceddebug.util;

import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

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
                        try {
                            NbtIo.writeCompressed(compoundTag, new File(filePathName));
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
                try {
                    NbtIo.writeCompressed(compoundTag, new File(filePathName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ImGuiFileDialog.close();
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
}
