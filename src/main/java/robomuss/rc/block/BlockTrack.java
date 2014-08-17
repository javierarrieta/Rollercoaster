package robomuss.rc.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import robomuss.rc.block.te.TileEntityTrack;
import robomuss.rc.item.ItemExtra;
import robomuss.rc.item.RCItems;
import robomuss.rc.tracks.TrackHandler;
import robomuss.rc.tracks.TrackType;
import robomuss.rc.util.IPaintable;

public class BlockTrack extends BlockContainer implements IPaintable {

	public BlockTrack(TrackType track) {
		super(Material.iron);
		setHardness(1F);
		setResistance(3F);
		
		AxisAlignedBB bounds = track.getBlockBounds();
		setBlockBounds((float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityTrack();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			if(player.getHeldItem() != null) {
				if(player.getHeldItem().getItem() == RCItems.hammer) {
					TileEntityTrack tet = (TileEntityTrack) world.getTileEntity(x, y, z);
					if(tet.direction == 3) {
						tet.direction = 0;
					}
					else {
						tet.direction++;
					}
					world.markBlockForUpdate(x, y, z);
					return true;
				}
				else if(player.getHeldItem().getItem() == RCItems.brush) {
					TileEntityTrack tet = (TileEntityTrack) world.getTileEntity(x, y, z);
					tet.colour = player.getHeldItem().getItemDamage();
					world.markBlockForUpdate(x, y, z);
					return true;
				}
				else if(player.getHeldItem().getItem() instanceof ItemExtra) {
					TileEntityTrack tet = (TileEntityTrack) world.getTileEntity(x, y, z);
					tet.extra = TrackHandler.extras.get(((ItemExtra) player.getHeldItem().getItem()).id);
					world.markBlockForUpdate(x, y, z);
					return true;
				}
				else {
					return false;
				}
				
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return 110;
	}
	
	@Override
	public int damageDropped(int dmg) {
		return dmg;
	}
	
	@Override
	public int getPaintMeta(World world, int x, int y, int z) {
		return ((TileEntityTrack) world.getTileEntity(x, y, z)).colour;
	}
}