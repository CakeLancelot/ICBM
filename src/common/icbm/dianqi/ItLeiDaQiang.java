package icbm.dianqi;

import icbm.ICBM;
import icbm.ICBMPacketManager.ICBMPacketType;
import icbm.extend.TFaSheQi;
import icbm.jiqi.TFaSheShiMuo;
import icbm.jiqi.TXiaoFaSheQi;

import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.Vector3;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItLeiDaQiang extends ItemElectric
{
	public static final int YONG_DIAN_LIANG = 1000;
	
    public ItLeiDaQiang(String name, int par1, int par2)
    {
    	super(par1);
        this.iconIndex = par2;
        this.setItemName(name);
    }
    
    @Override
	public String getTextureFile()
    {
        return ICBM.ITEM_TEXTURE_FILE;
    }
    
    /**
     * Allows items to add custom lines of information to the mouseover description
     */
    @Override
	public void addInformation(ItemStack par1ItemStack, List par2List)
    {
    	super.addInformation(par1ItemStack, par2List);
    	Vector3 coord = getSavedCoord(par1ItemStack);
    	par2List.add("\uaa74Saved Coordinates:");
    	par2List.add("X: "+(int)coord.x+", Y: "+(int)coord.y+", Z: "+(int)coord.z);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if(par2World.isRemote)
    	{
	        MovingObjectPosition objectMouseOver = par3EntityPlayer.rayTrace(1000, 1);
	
	        if (objectMouseOver != null)
	        {
	        	TileEntity tileEntity = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
	
	        	//Do not scan if the target is a missile launcher
	        	if(!(tileEntity instanceof TFaSheQi))
	            {
		        	//Check for electricity
		            if(this.getJoules(par1ItemStack) > YONG_DIAN_LIANG)
		        	{
		            	PacketDispatcher.sendPacketToServer(PacketManager.getPacketWithID(ICBM.CHANNEL, (int)ICBMPacketType.RADAR_GUN.ordinal(), objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
			            par3EntityPlayer.addChatMessage("Scanned Coordinates: X:" + objectMouseOver.blockX + ", Y:" + objectMouseOver.blockY + ", Z:" + objectMouseOver.blockZ);
		        	}
		            else
		        	{
		        		par3EntityPlayer.addChatMessage("Radar gun out of electricity!");
		        	}
	        	}
	        }
    	}
    	
        return par1ItemStack;
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
    	int blockId = par3World.getBlockId(x, y, z);
    	int blockMetadata = par3World.getBlockMetadata(x, y, z);
    	
    	if(blockId == ICBM.blockJiQi.blockID)
        {
   		 	TileEntity tileEntity = par3World.getBlockTileEntity(x, y, z);

    		 if(tileEntity != null)
    		 {
    			 if(tileEntity instanceof TFaSheShiMuo)
    			 {
    				 TFaSheShiMuo missileLauncher = (TFaSheShiMuo)tileEntity;

        			 Vector3 savedCords = this.getSavedCoord(par1ItemStack);
        			 
        			 //If the vector is NOT 0
        			 if(!savedCords.isEqual(new Vector3()))
        			 {
        				 if(missileLauncher.getTarget() == null)
        				 {
        					 missileLauncher.setTarget(new Vector3());
        				 }
        				 
        				 missileLauncher.getTarget().x = (int)savedCords.x;
        				 missileLauncher.getTarget().z = (int)savedCords.z;
        				 
        				 if(par3World.isRemote)
        				 {
	        	    		 PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, missileLauncher, (int)2, savedCords.x, missileLauncher.getTarget().y, savedCords.z));
	        	    		 par2EntityPlayer.addChatMessage("Coordinate information transfered!");
        				 }
        			 }
        			 else
        			 {
        				 if(par3World.isRemote)
        				 par2EntityPlayer.addChatMessage("You must scan a coordinate!");
        			 }
    			 }
    			 else if(tileEntity instanceof TXiaoFaSheQi)
    			 {
    				 TXiaoFaSheQi missileLauncher = (TXiaoFaSheQi)tileEntity;

        			 Vector3 savedCords = this.getSavedCoord(par1ItemStack);
        			 
        			 if(!savedCords.isEqual(new Vector3()))
        			 {
        				 if(missileLauncher.getTarget() == null)
        				 {
        					 missileLauncher.setTarget(new Vector3());
        				 }
        				 
        				 missileLauncher.setTarget(new Vector3(savedCords.x, savedCords.y, savedCords.z));
        				 
        				 if(par3World.isRemote)
        				 {
	        	    		 PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ICBM.CHANNEL, missileLauncher, (int)2, savedCords.x, savedCords.y, savedCords.z));
	        				 par2EntityPlayer.addChatMessage("Coordinate information transfered!");
        				 }
        			 }
        			 else
        			 {
        				 if(par3World.isRemote)
        				 par2EntityPlayer.addChatMessage("You must scan a coordinate!");
        			 }
    			 }
    		 }
        }
    	
    	return false;
    }
    
    public Vector3 getSavedCoord(ItemStack par1ItemStack)
    {
    	if (par1ItemStack.stackTagCompound == null)
		{
    		return new Vector3();
		}
    	
    	return new Vector3(par1ItemStack.stackTagCompound.getInteger("x"), par1ItemStack.stackTagCompound.getInteger("y"), par1ItemStack.stackTagCompound.getInteger("z"));		
    }

	@Override
	public double getVoltage()
	{
		return 20;
	}
	
	@Override
	public double getMaxJoules()
	{
		return 100000;
	}
}
