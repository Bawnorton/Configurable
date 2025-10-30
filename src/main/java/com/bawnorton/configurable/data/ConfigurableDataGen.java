package com.bawnorton.configurable.data;

//? if fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

import java.util.Optional;

@Entrypoint("fabric-datagen")
public final class ConfigurableDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack mainPack = fabricDataGenerator.createPack();
		mainPack.addProvider((FabricDataGenerator.Pack.Factory<PackMetadataGenerator>) output -> new PackMetadataGenerator(output)
				.add(
						//? if >=1.21.10 {
						PackMetadataSection.SERVER_TYPE,
						new PackMetadataSection(
								Component.literal("${mod_description}"),
								InclusiveRange.create(
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA)
								).getOrThrow()
						)
						//?} else {
						/*PackMetadataSection.TYPE,
						new PackMetadataSection(
								Component.literal("${mod_description}"),
								//? if >=1.21.8 {
								DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
								//?} else {
								/^DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
								^///?}
								Optional.empty()
						)
						*///?}
				)
		);
	}
}
//?} else {
/*import com.bawnorton.configurable.ConfigurableMain;
import net.minecraft.DetectedVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ConfigurableMain.MOD_ID)
public final class ConfigurableDataGen {
	@SubscribeEvent
	//? if >=1.21.5 {
	public static void gatherServerData(GatherDataEvent.Server event) {
		DataGenerator gen = event.getGenerator();
	//?} else {
	/^public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
	^///?}
		PackOutput mainPack = gen.getPackOutput();
		gen.addProvider(true, new PackMetadataGenerator(mainPack)
				.add(
						//? if >=1.21.10 {
						PackMetadataSection.SERVER_TYPE,
						new PackMetadataSection(
								Component.literal("${mod_description}"),
								InclusiveRange.create(
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
										DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA)
								).getOrThrow()
						)
						//?} else {
						/^PackMetadataSection.TYPE,
						new PackMetadataSection(
								Component.literal("${mod_description}"),
								//? if >=1.21.8 {
								DetectedVersion.BUILT_IN.packVersion(PackType.SERVER_DATA),
								//?} else {
								/^¹DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
								¹^///?}
								java.util.Optional.empty()
						)
						^///?}
				)
		);
	}
}
*///?}