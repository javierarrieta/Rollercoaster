package robomuss.rc.block.te;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import robomuss.rc.block.BlockTrackBase;
import robomuss.rc.chat.ChatHandler;
import robomuss.rc.track.TrackHandler;
import robomuss.rc.track.TrackManager;
import robomuss.rc.track.extra.TrackExtra;
import robomuss.rc.track.piece.TrackPiece;
import net.minecraft.block.Block;
import robomuss.rc.track.style.TrackStyle;

/*@Optional.InterfaceList(
       @Optional.Interface(iface = "pneumaticCraft.api.tileentity.IPneumaticMachine", modid = "PneumaticCraft")
)*/
public class TileEntityTrackBase extends TileEntity {
	//private IAirHandler airHandler;

	public BlockTrackBase track;
	public int trackMeta;
    public ForgeDirection direction;

	public boolean converted = false;
	public TrackExtra extra;
	public int colour = 0;
	public TrackStyle style;

	private TrackPiece trackPieceListHolder;
	private String trackPieceListID;
	private int trackPieceListIndex = -1;

	public TileEntityTrackBase() {} //required to instantiate for network handler
	public TileEntityTrackBase(World world, int trackMeta, BlockTrackBase track) {
		this.worldObj = world;
		this.trackMeta = trackMeta;
		this.track = track;
	}

	public void setListTypeAndIndex(TrackPiece type, int index) {
		if (type != null) {
			this.trackPieceListHolder = type;
			this.trackPieceListID = type.unlocalized_name;
			this.trackPieceListIndex = index;
		}
	}

	public TrackPiece getTrackPieceListHolder() {
		return this.trackPieceListHolder;
	}

	public String getTrackPieceListID() {
		return this.trackPieceListID;
	}

	public int getTrackPieceListIndex() {
		return this.trackPieceListIndex;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		this.colour = compound.getInteger("colour");
//		System.out.println("Colour: " + this.colour);

		this.converted = compound.getBoolean("converted");
//		System.out.println("Converted: " + this.converted);

		for (TrackStyle style : TrackHandler.styles) {
			if (style.getId().contains(compound.getString("styleName"))) {
				this.style = style;
//				System.out.println("Style: " + this.style.name);
			}
		}

		int extraID = compound.getInteger("extraID");
//		System.out.println("ExtraID: " + extraID);

		if (extraID == -1) {
			this.extra = null;
//			System.out.println("extra null");
		} else {
			track.extra = TrackHandler.extras.get(extraID);
//			System.out.println("Extra: " + this.extra.name);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		if (this.extra != null) {
			compound.setInteger("extraID", track.extra.id);
		}

		if (!this.converted || this.style == null) {
			this.style = TrackHandler.findTrackStyle("corkscrew");
			this.converted = true;
		}

		compound.setInteger("colour", this.colour);
		compound.setString("styleName", this.style.getId());
		compound.setBoolean("converted", this.converted);

		if (this.extra != null) {
			compound.setInteger("extraID", this.extra.id);
		} else {
			compound.setInteger("extraID", -1);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
//		findTrack();
//		System.out.println("getDescriptionPacket!");
		NBTTagCompound compound = new NBTTagCompound();
		this.writeToNBT(compound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
//		findTrack();
//		ChatHandler.broadcastChatMessageToPlayers("packet received!");
		readFromNBT(packet.func_148857_g());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Block block = this.getWorldObj().getBlock(xCoord, yCoord, zCoord);
		TrackPiece type = TrackHandler.findTrackTypeFull(block.getUnlocalizedName());
		if (type != null) {
			return type.getRenderBoundingBox(this.getWorldObj(), xCoord, yCoord, zCoord);
		} else {
			return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
		}
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	/*@Optional.Method(modid = "PneumaticCraft")
	@Override
	public IAirHandler getAirHandler() {
		if(airHandler == null) {
			airHandler = AirHandlerSupplier.getAirHandler(5, 7, 50, 2000);
		}
		return airHandler;
	}

	@Optional.Method(modid = "PneumaticCraft")
	@Override
	public boolean isConnectedTo(ForgeDirection side) {
		if(this.extra != null && this.extra instanceof TrackExtraAirLauncher) {
			return true;
		}
		else {
			return false;
		}
	}*/

	/*@Override
	public void updateEntity() {
		super.updateEntity();
		getAirHandler().updateEntityI();
	}

	@Override
	public void validate() {
		super.validate();
		getAirHandler().validateI(this);
	}*/
}
