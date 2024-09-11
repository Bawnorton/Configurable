package com.bawnorton.configurable.ap.helper;

public class MappingsHelper {
    //? if yarn {
    public static String getMinecraftClient() {
        return "net.minecraft.client.MinecraftClient";
    }

    public static String getConfirmScreen() {
        return "net.minecraft.client.gui.screen.ConfirmScreen";
    }

    public static String getScreen() {
        return "net.minecraft.client.gui.screen.Screen";
    }

    public static String getScreenTexts() {
        return "net.minecraft.screen.ScreenTexts";
    }

    public static String getText() {
        return "net.minecraft.text.Text";
    }

    public static String getUtil() {
        return "net.minecraft.util.Util";
    }

    public static String getIdentifier() {
        return "net.minecraft.util.Identifier";
    }

    public static String getItem() {
        return "net.minecraft.item.Item";
    }

    //?} elif mojmap {
    /*public static String getMinecraftClient() {
        return "net.minecraft.client.Minecraft";
    }

    public static String getConfirmScreen() {
        return "net.minecraft.client.gui.screens.ConfirmScreen";
    }

    public static String getScreen() {
        return "net.minecraft.client.gui.screens.Screen";
    }

    public static String getScreenTexts() {
        return "net.minecraft.network.chat.CommonComponents";
    }

    public static String getText() {
        return "net.minecraft.network.chat.Component";
    }

    public static String getUtil() {
        return "net.minecraft.Util";
    }

    public static String getIdentifier() {
        return "net.minecraft.resources.ResourceLocation";
    }

    public static String getItem() {
        return "net.minecraft.world.item.Item";
    }
    *///?}
}
