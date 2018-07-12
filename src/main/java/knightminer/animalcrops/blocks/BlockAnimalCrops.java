package knightminer.animalcrops.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import knightminer.animalcrops.AnimalCrops;
import knightminer.animalcrops.core.Config;
import knightminer.animalcrops.core.Utils;
import knightminer.animalcrops.items.ItemAnimalSeeds;
import knightminer.animalcrops.tileentity.TileAnimalCrops;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnimalCrops extends BlockCrops implements ITileEntityProvider {

    public BlockAnimalCrops() {
    	super();
    	this.isBlockContainer = true;
    }

    /* Seed logic */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAnimalCrops();
	}

    @Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        // set the crop's entity
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileAnimalCrops) {
        	((TileAnimalCrops)te).setAnimal(Utils.getEntityID(stack.getTagCompound()));
        }
    }
    @Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
    	if(state.getValue(getAgeProperty()) >= getMaxAge()) {
	    	TileEntity te = world.getTileEntity(pos);
	    	if(te instanceof TileAnimalCrops) {
	    		((TileAnimalCrops)te).spawnAnimal();
	    	}
    	}

    	super.breakBlock(world, pos, state);
    }

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		return getSeedItem(world, pos);
	}

    // do not drop anything if max age, no seed drops basically
    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if(state.getValue(getAgeProperty()) < getMaxAge()) {
    		drops.add(getSeedItem(world, pos));
        }
    }

	private ItemStack getSeedItem(IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileAnimalCrops) {
			return getSeed().makeSeed(Utils.getEntityID(((TileAnimalCrops)te).getTileData()));
		}
		return new ItemStack(getSeed());
	}

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		// we pull up a few calls to this point in time because we still have the TE here
		// the execution otherwise is equivalent to vanilla order
		this.onBlockDestroyedByPlayer(world, pos, state);
		if(willHarvest) {
			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
		}

		world.setBlockToAir(pos);
		// return false to prevent the above called functions to be called again
		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
		return false;
	}


    /* Crop properties */

    @Override
	protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() instanceof BlockGrass;
    }

    @Override
	protected Item getCrop() {
        return Items.AIR;
    }

    @Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return Config.canBonemeal;
    }

    @Override
	protected ItemAnimalSeeds getSeed() {
        return AnimalCrops.seeds;
    }
}
