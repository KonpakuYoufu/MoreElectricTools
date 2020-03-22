package net.lrsoft.mets.item.bauble;

import baubles.api.BaublesApi;
import baubles.common.Baubles;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.lrsoft.mets.MoreElectricTools;
import net.lrsoft.mets.item.crafting.ItemCraftingManager;
import net.lrsoft.mets.manager.ItemManager;
import net.lrsoft.mets.util.ItemStackUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Loader;

public class ItemBaublesManager {
	public static Item electricFireProofNecklace;
	public static Item energyCrystalBelt ;
	public static Item lapotronCrystalBelt;
	public static Item electricLifeSupportRing;

	public static void onBaublesInit(RegistryEvent.Register<Item> event) 
	{
		electricFireProofNecklace = new ElectricFireProofNecklace();
		energyCrystalBelt = new EnergyCrystalBelt();
		lapotronCrystalBelt = new LapotronCrystalBelt();
		electricLifeSupportRing = new ElectricLifeSupportRing();
		
		event.getRegistry().register(electricFireProofNecklace);
		event.getRegistry().register(energyCrystalBelt);
		event.getRegistry().register(lapotronCrystalBelt);
		event.getRegistry().register(electricLifeSupportRing);
		onRecipeInit();
	}
	
	
	private static void onRecipeInit() 
	{
		Recipes.advRecipes.addRecipe(new ItemStack(electricFireProofNecklace), 
				new Object[] {
						"SSS",
						"BCB",
						"LSL",
						'S', ItemCraftingManager.titanium_casing,
						'C', IC2Items.getItem("crafting", "advanced_circuit"),
						'L', Items.BLAZE_ROD,
						'B', ItemStackUtils.getAllTypeStack(ItemManager.lithiumBattery)
				});	
		
		Recipes.advRecipes.addRecipe(new ItemStack(energyCrystalBelt), 
				new Object[] {
						"PSP",
						"BCB",
						"PSP",
						'P', IC2Items.getItem("crafting", "carbon_plate"),
						'S', IC2Items.getItem("crafting", "advanced_circuit"),
						'C', ItemCraftingManager.titanium_plate,
						'B', ItemStackUtils.getAllTypeStack(IC2Items.getItem("energy_crystal"))
				});	
		
		Recipes.advRecipes.addRecipe(new ItemStack(lapotronCrystalBelt), 
				new Object[] {
						"PSP",
						"BTB",
						"PSP",
						'P', ItemCraftingManager.niobium_titanium_plate,
						'S', ItemCraftingManager.super_circuit,
						'T', ItemStackUtils.getAllTypeStack(energyCrystalBelt),
						'B', ItemStackUtils.getAllTypeStack(IC2Items.getItem("lapotron_crystal"))
				});	
		
		Recipes.advRecipes.addRecipe(new ItemStack(electricLifeSupportRing), 
				new Object[] {
						"PAP",
						"LSN",
						"PBP",
						'P', ItemCraftingManager.super_iridium_compress_plate,
						'A', ItemStackUtils.getAllTypeStack(ItemManager.electricForceFieldGenerator),
						'L', ItemStackUtils.getAllTypeStack(ItemManager.electricFirstAidLifeSupport),
						'S', ItemStackUtils.getAllTypeStack(lapotronCrystalBelt),
						'N', ItemStackUtils.getAllTypeStack(ItemManager.electricNutritionSupply),
						'B', ItemStackUtils.getAllTypeStack(ItemManager.superLapotronCrystal)
				});	
	}
	
	public static void onBaublesModelInit()
	{
		
		ModelLoader.setCustomModelResourceLocation(electricFireProofNecklace, 0,
				new ModelResourceLocation(electricFireProofNecklace.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(energyCrystalBelt, 0,
				new ModelResourceLocation(energyCrystalBelt.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(lapotronCrystalBelt, 0,
				new ModelResourceLocation(lapotronCrystalBelt.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(electricLifeSupportRing, 0,
				new ModelResourceLocation(electricLifeSupportRing.getRegistryName(), "inventory"));

	}
	
	
}
