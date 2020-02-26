package net.lrsoft.mets.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import ic2.api.item.ElectricItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlasmaAirCannon extends UniformElectricItem {
	private final static double storageEnergy = 200000d, transferSpeed = 128d;
	public PlasmaAirCannon()
	{
		super("plasma_air_cannon", storageEnergy, transferSpeed, 2);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, true);
        if (ret != null)
        {
        	return ret;
        }
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			int expandSize= this.getMaxItemUseDuration(stack) - timeLeft;
			expandSize = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, expandSize, true);
			if (expandSize < 0)	return;

			if(ElectricItem.manager.use(stack, 1000, entityplayer)) 
			{
				float pitch = entityplayer.rotationPitch , yaw = entityplayer.rotationYaw;
				Vec3d currentPostion = new Vec3d(entityplayer.posX, entityplayer.posY, entityplayer.posZ);
				Vec3d lookPosition = new Vec3d(-MathHelper.sin(yaw * 0.0174F) * MathHelper.cos(pitch * 0.0174F),
						-MathHelper.sin(pitch * 0.0174F), MathHelper.cos(yaw * 0.0174F) * MathHelper.cos(pitch * 0.0174F));
				Vec3d targetPostion = new Vec3d(entityplayer.posX+lookPosition.x * expandSize,
						entityplayer.posY+lookPosition.y * expandSize, 
						entityplayer.posZ+lookPosition.z * expandSize); 
				
		
				List<Entity> list = worldIn.getEntitiesInAABBexcluding(entityplayer, 
						new AxisAlignedBB(currentPostion.x,currentPostion.y, currentPostion.z, targetPostion.x, targetPostion.y, targetPostion.z)
						.expand(1.0f, 1.0f, 1.0f), ATTACK_TARGETS);
				
				double velocity = expandSize / 3;
				for(int index=0; index < list.size(); index++)
				{
					 Entity entity1 = list.get(index);
					 double distance = currentPostion.distanceTo(new Vec3d(entity1.posX, entity1.posY, entity1.posZ));
					 double damage = 10 + expandSize / (expandSize == 0d ? 1.0d : distance);
					 
				     entity1.addVelocity(lookPosition.x * velocity, lookPosition.y * velocity, lookPosition.z * velocity);
				     entity1.attackEntityFrom(DamageSource.causePlayerDamage(entityplayer), (float) damage);
				}
				
				worldIn.playSound((EntityPlayer)null, 
						entityplayer.posX , entityplayer.posY, entityplayer.posZ , 
			    			SoundEvents.ENTITY_GENERIC_EXPLODE, entityplayer.getSoundCategory(), 1.0f * (expandSize/15+1), 1.0F);	
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
						entityplayer.posX, entityplayer.posY + 0.5f, entityplayer.posZ, lookPosition.x * 2, lookPosition.y * 2, lookPosition.z * 2, 3);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) 
	{
		return 6000;
	}

	private static final Predicate<Entity> ATTACK_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE,
			new Predicate<Entity>() {
		@Override
		public boolean apply(Entity input) {
			// TODO Auto-generated method stub
			return input.canBeCollidedWith();
		}
	});
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
