package com.bioxx.tfc.Blocks.Flora;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Blocks.Vanilla.BlockCustomIce;
import com.bioxx.tfc.Blocks.Vanilla.BlockCustomLiquid;
import com.bioxx.tfc.Core.ColorizerFoliageTFC;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.TileEntities.TESeaWeed;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTallSeaGrass extends BlockCustomLiquid implements ITileEntityProvider
{
	@SideOnly(Side.CLIENT)
	private IIcon pondWeed;
	private IIcon seaWeed;

	public BlockTallSeaGrass()
	{
		super(null, Material.water);
		float var3 = 0.5F;
		this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 1.0F, 0.5F + var3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 16777215;
	}

	//	@Override
	//	public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	//	{
	//		float var5 = par1IBlockAccess.getBlock(par2, par3, par4).getLightValue();
	//		float var6 = par1IBlockAccess.getBlock(par2, par3+1, par4).getLightValue();
	//		return var5 > var6 ? var5 : var6;
	//	}

	@Override
	public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		int var5 = par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, 0);
		int var6 = par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3+1, par4, 0);
		int var7 = var5 & 255;
		int var8 = var6 & 255;
		int var9 = var5 >> 16 & 255;
		int var10 = var6 >> 16 & 255;
		return (var7 > var8 ? var7 : var8) | (var9 > var10 ? var9 : var10) << 16;
	}

	@Override
	public int getRenderColor(int par1)
	{
		return par1 == 0 ? 16777215 : ColorizerFoliageTFC.getFoliageColorBasic();
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
	 * when first determining what to render.
	 */
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		if (this.blockMaterial != Material.water)
			return 16777215;
		else
			return TerraFirmaCraft.proxy.waterColorMultiplier(par1IBlockAccess, par2, par3, par4);
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return null;
	}

	@Override
	public int getRenderType()
	{
		return TFCBlocks.seaWeedRenderId;
	}

	@Override
	public int quantityDroppedWithBonus(int par1, Random par2Random)
	{
		return 1 + par2Random.nextInt(par1 * 2 + 1);
	}

	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	{
		Block var5 = par1World.getBlock(par2, par3 - 1, par4);
		boolean correctSoil = TFC_Core.isSoil(var5) || TFC_Core.isSand(var5) || TFC_Core.isGravel(var5);
		return !correctSoil ? false : canThisPlantGrowUnderThisBlock(par1World.getBlock(par2, par3 + 1, par4)) && par1World.getBlock(par2, par3, par4).getMaterial() == Material.water;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int l)
	{
		TESeaWeed te = (TESeaWeed)(world.getTileEntity(i, j, k));
		int type = -1;
		if(te != null)
			type = te.getType();

		if(block == Blocks.ice)
			world.setBlock(i, j, k, TFCBlocks.SeaGrassFrozen);

		super.breakBlock(world, i, j, k, block, l);
		if(block instanceof BlockCustomIce){
			world.setBlockMetadataWithNotify(i, j, k, type, 1);
			te = (TESeaWeed)(world.getTileEntity(i, j, k));
			te.setType(type);
		}

		if(world.isAirBlock(i, j, k))
		{
			if(te != null)
			{
				if(type == 1 || type == 2)
					world.setBlock(i, j, k, TFCBlocks.FreshWater, 0, 1);
				else if(type==0)
					world.setBlock(i, j, k, TFCBlocks.SaltWater, 0, 1);
			}
		}
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int l)
	{
		super.harvestBlock(world, player, i, j, k, l);
	}

	protected boolean canThisPlantGrowUnderThisBlock(Block par1)
	{
		return true;//TFC_Core.isSaltWater(par1)|| TFC_Core.isFreshWater(par1);
	}

	/*@Override
	protected void updateFlow(World par1World, int par2, int par3, int par4)
	{
		int m = par1World.getBlockMetadata(par2, par3, par4);
		TESeaWeed te = (TESeaWeed)(par1World.getTileEntity(par2, par3, par4));
		int type = -1;
		if(te!=null)
			type = te.getType();
		par1World.setBlock(par2, par3, par4, TFCBlocks.SeaGrassStill, m, 2);
		te = (TESeaWeed)(par1World.getTileEntity(par2, par3, par4));
		if(te!=null)
			te.setType(type);
	}*/

	/**
	 * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
	 */
	@Override
	public boolean canBlockStay(World par1World, int par2, int par3, int par4)
	{
		boolean a,b,c;
		a = (par1World.getFullBlockLightValue(par2, par3, par4) >= 0);
		b = this.canThisPlantGrowUnderThisBlock(par1World.getBlock(par2, par3 + 1, par4));
		c = this.canThisPlantGrowOnThisBlock(par1World.getBlock(par2, par3 - 1, par4));
		return  a &&
				b &&
				c;
	}

	@Override
	public void onBlockAdded(World world,int i,int j,int k){
		int type = 0;
		if(!world.isAirBlock(i, j+1, k))
			type = TFC_Core.isFreshWater(world.getBlock(i, j+1, k))?1:0;
		else if(TFC_Core.isFreshWater(world.getBlock(i+1, j, k))||
				TFC_Core.isFreshWater(world.getBlock(i, j, k+1))||
				TFC_Core.isFreshWater(world.getBlock(i-1, j, k))||
				TFC_Core.isFreshWater(world.getBlock(i, j, k-1)))
			type = 2;

		TESeaWeed te = (TESeaWeed)(world.getTileEntity(i,j,k));
		te.setType(type);
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return true;
	}

	//FIXME had to disable due to the water blocks update to forge standards
	/*@Override
	protected void flowIntoBlock(World world, int x, int y, int z, int oldX , int oldY , int oldZ, int newFlowDecay)
	{
		if (this.liquidCanDisplaceBlock(world, x, y, z))
		{
			Block i1 = world.getBlock(x, y, z);
			if (i1 == Blocks.air)
			{
				if (this.blockMaterial == Material.lava)
					this.triggerLavaMixEffects(world, x, y, z);
				else
					i1.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			}

			TESeaWeed te = (TESeaWeed)(world.getTileEntity(oldX, oldY, oldZ));
			int type = -1;
			if(te != null)
				type = te.getType();
			switch(type)
			{
			case 0: world.setBlock(x, y, z, TFCBlocks.SaltWaterStill, newFlowDecay, 2); break;
			case 1:
			case 2: world.setBlock(x, y, z, TFCBlocks.FreshWaterFlowing, newFlowDecay, 2); break;
			default: break;
			}
		}
	}*/

	/**
	 * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
	 * blockID passed in. Args: blockID
	 */
	protected boolean canThisPlantGrowOnThisBlock(Block par1)
	{
		return TFC_Core.isSoil(par1) || TFC_Core.isSand(par1) || TFC_Core.isGravel(par1);
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
	{
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
		this.checkFlowerChange(par1World, par2, par3, par4);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		this.checkFlowerChange(par1World, par2, par3, par4);
		TESeaWeed te = (TESeaWeed)(par1World.getTileEntity(par2, par3, par4));
		int type = -1;
		if(te!= null){
			type = te.getType();
		}
		super.updateTick(par1World, par2, par3, par4, par5Random);
		te = (TESeaWeed)(par1World.getTileEntity(par2, par3, par4));
		if(te!= null){
			te.setType(type);
		}
	}

	protected final void checkFlowerChange(World par1World, int par2, int par3, int par4)
	{
		if (!this.canBlockStay(par1World, par2, par3, par4))
		{
			this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
			par1World.setBlockToAir(par2, par3, par4);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	public int getRenderBlockPass()
	{
		return this.blockMaterial == Material.water ? 1 : 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister registerer)
	{
		seaWeed = registerer.registerIcon("tallgrass");//registerer.registerIcon(Reference.ModID + ":" + "plants/Sea Grass");
		pondWeed = registerer.registerIcon("fern");//registerer.registerIcon(Reference.ModID + ":" + "plants/Pond Grass");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return TFCBlocks.FreshWater.getIcon(par1, par2);//this.seaWeed;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var2)
	{
		return new TESeaWeed();
	}
}