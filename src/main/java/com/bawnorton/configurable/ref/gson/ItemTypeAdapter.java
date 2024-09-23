package com.bawnorton.configurable.ref.gson;

import com.bawnorton.configurable.ConfigurableMain;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.lang.reflect.Type;

public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String itemName = json.getAsString();
            //? if <1.21 {
            /*Identifier id = new Identifier(itemName);
            *///?} else {
            Identifier id = Identifier.of(itemName);
            //?}
            return Registries.ITEM.getOrEmpty(id).orElse(Items.AIR);
        } catch (Exception e) {
            ConfigurableMain.LOGGER.warn("Failed to parse item from json: \"%s\"".formatted(json));
            return Items.AIR;
        }
    }

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Registries.ITEM.getId(src).toString());
    }
}