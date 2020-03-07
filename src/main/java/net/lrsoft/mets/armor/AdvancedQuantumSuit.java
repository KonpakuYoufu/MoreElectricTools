package net.lrsoft.mets.armor;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IHazmatLike;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.init.Localization;
import ic2.core.item.IPseudoDamageItem;
import ic2.core.item.armor.jetpack.IJetpack;
import net.lrsoft.mets.MoreElectricTools;
import net.lrsoft.mets.manager.ConfigManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AdvancedQuantumSuit extends ItemArmor
		implements ISpecialArmor, IPseudoDamageItem, IElectricItem, IItemHudInfo, IJetpack, IHazmatLike {
	private static ArmorMaterial defaultMaterial = EnumHelper.addArmorMaterial(
			"advanced_quantum_suit", MoreElectricTools.MODID + ":advanced_quantum_suit", 50, new int[]{6, 12, 9, 6}, 40, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 9);
	private static double maxStorageEnergy = 100000000d, transferSpeed = 8192d;
	private static int suitTier = 5;
	
	public static double damageEnergyCost = 100000d;
	
    public AdvancedQuantumSuit() {
		super(defaultMaterial, 0, EntityEquipmentSlot.CHEST);
		setUnlocalizedName("mets.advanced_quantum_chest");
		setRegistryName(MoreElectricTools.MODID, "advanced_quantum_chest");
		setCreativeTab(MoreElectricTools.CREATIVE_TAB);
		setMaxDamage(2333);
		setMaxStackSize(1);
		setNoRepair();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
    	IC2.platform.profilerStartSection("QuantumBodyarmor");
    	player.extinguish();
    	float currentHealth = player.getHealth();
		if(currentHealth < player.getMaxHealth())
		{
			if (ElectricItem.manager.use(itemStack, ConfigManager.ElectricFirstAidLifeSupport, player)) {
				player.setHealth(currentHealth+1);
			}
		}
    	IC2.platform.profilerEndSection();
    }
   
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage,
			int slot) {
		int energyPerDamage = (int) damageEnergyCost;
		int damageLimit = Integer.MAX_VALUE;
		if (energyPerDamage > 0)
			damageLimit = (int) Math.min(damageLimit, 25.0D * ElectricItem.manager.getCharge(armor) / energyPerDamage);
		return new ISpecialArmor.ArmorProperties(8, 0.5, damageLimit);
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEntityLivingDeadEvent(LivingDeathEvent event)
	{
		World world = event.getEntity().getEntityWorld();
		if (world.isRemote) return;
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		
	    EntityPlayer player = (EntityPlayer)event.getEntityLiving();
	    if(player.inventory.armorInventory.contains(this))
	    {
	    	ItemStack stack  =  player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
	    	if(ElectricItem.manager.getCharge(stack) > 1000000D)
	    	{
	    		ElectricItem.manager.discharge(stack, 1000000D, 2147483647, true, false, false);
	    		event.setCanceled(true);
	    		player.setHealth(player.getMaxHealth());
	    	}
	    }
	}
	

	@Override
	public boolean addsProtection(EntityLivingBase entity, EntityEquipmentSlot slot, ItemStack stack) {
		return (ElectricItem.manager.getCharge(stack) > 0.0D);
	}

	@Override
	public boolean drainEnergy(ItemStack pack, int amount) {
		return (ElectricItem.manager.discharge(pack, (amount + 6), Integer.MAX_VALUE, true, false, false) > 0.0D);
	}

	@Override
	public double getChargeLevel(ItemStack arg0) {return 5;}

	@Override
	public float getDropPercentage(ItemStack arg0) {return 0.0f;}

	@Override
	public float getHoverMultiplier(ItemStack arg0, boolean arg1) {return 0.1f;}

	@Override
	public float getPower(ItemStack arg0) {return 1.0f;}

	@Override
	public float getWorldHeightDivisor(ItemStack arg0) {return 0.9f;}

	@Override
	public boolean isJetpackActive(ItemStack arg0) {return true;}
	
	@Override
	public void setStackDamage(ItemStack stack, int damage) {setDamage(stack, damage);}

	@Override
	public List<String> getHudInfo(ItemStack stack, boolean advanced) {
		 List<String> info = new LinkedList<>();
		 info.add(ElectricItem.manager.getToolTip(stack));
		 info.add(Localization.translate("ic2.item.tooltip.PowerTier", new Object[] { Integer.valueOf(this.suitTier) }));
		 return info;
	}

	@Override
	public boolean canProvideEnergy(ItemStack stack) {return false;}

	@Override
	public double getMaxCharge(ItemStack stack) {return maxStorageEnergy;}

	@Override
	public int getTier(ItemStack stack) {return suitTier;}

	@Override
	public double getTransferLimit(ItemStack stack) {return transferSpeed;}
	
	public Item getChargedItem(ItemStack itemStack) {return this;}

	public Item getEmptyItem(ItemStack itemStack) {return this;}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {return 0;}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}
	
	@Override
	public boolean isRepairable() {return false;}
}
