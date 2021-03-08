package furgl.gui;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class InventoryCloudPlayer extends InventoryPlayer {

	public InventoryCloudPlayer(EntityPlayer playerIn) {
		super(playerIn);
		for (int i=0; i<playerIn.inventory.mainInventory.size(); ++i)
			this.mainInventory.set(i, playerIn.inventory.mainInventory.get(i));
		//this.mainInventory = playerIn.inventory.mainInventory;
		this.updateMainInventory();
	}

	/** Update main inventory to be first 36 items in cloud storage */
	public void updateMainInventory() {
		// NonNullList<ItemStack> items =
		// this.player.getCapability(StorageManager.CAPABILITY,
		// null).getStorage().getStacks();
		// for (int slot=0; slot<this.mainInventory.size(); ++slot)
		// this.mainInventory.set(slot, slot<items.size() ? items.get(slot) :
		// ItemStack.EMPTY);
	}

	@Override
	public int getFirstEmptyStack() {
		int slot = super.getFirstEmptyStack();

		/*// if no empty slot, expand mainInventory and send new slot
		if (slot == -1) {
			this.mainInventory.add(ItemStack.EMPTY);
			slot = this.mainInventory.size() - 1;
		}*/
		return slot;
	}

	@Override
	public void decrementAnimations() {
		super.decrementAnimations();

		if (this.player.ticksExisted % 10 == 0)
			this.updateMainInventory();
	}

	/**Change to infinite stack limit*/
	@Override
	public int getInventoryStackLimit() {
		return Integer.MAX_VALUE;
	}

	/**Copied - but uses our canMergeStacks()*/
	@Override
	public int storeItemStack(ItemStack itemStackIn) {
		if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn))
		{
			return this.currentItem;
		}
		else if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn))
		{
			return 40;
		}
		else
		{
			for (int i = 0; i < this.mainInventory.size(); ++i)
			{
				if (this.canMergeStacks(this.mainInventory.get(i), itemStackIn))
				{
					return i;
				}
			}

			return -1;
		}
	}

	/**Copied - ignore max stack size*/
	private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1
				.isStackable()/* && stack1.getCount() < stack1.getMaxStackSize()*/ && stack1.getCount() < this.getInventoryStackLimit();
	}

	/**Copied bc private*/
	private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/**Copied to use custom addResource()*/
	@Override
	public boolean add(int p_191971_1_, final ItemStack p_191971_2_)
	{
		if (p_191971_2_.isEmpty())
		{
			return false;
		}
		else
		{
			try
			{
				if (p_191971_2_.isItemDamaged())
				{
					if (p_191971_1_ == -1)
					{
						p_191971_1_ = this.getFirstEmptyStack();
					}

					if (p_191971_1_ >= 0)
					{
						this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
						((ItemStack)this.mainInventory.get(p_191971_1_)).setAnimationsToGo(5);
						p_191971_2_.setCount(0);
						return true;
					}
					else if (this.player.capabilities.isCreativeMode)
					{
						p_191971_2_.setCount(0);
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					int i;

					while (true)
					{
						i = p_191971_2_.getCount();

						if (p_191971_1_ == -1)
						{
							p_191971_2_.setCount(this.storePartialItemStack(p_191971_2_));
						}
						else
						{
							p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
						}

						if (p_191971_2_.isEmpty() || p_191971_2_.getCount() >= i)
						{
							break;
						}
					}

					if (p_191971_2_.getCount() == i && this.player.capabilities.isCreativeMode)
					{
						p_191971_2_.setCount(0);
						return true;
					}
					else
					{
						return p_191971_2_.getCount() < i;
					}
				}
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
				crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(p_191971_2_.getItem())));
				crashreportcategory.addCrashSection("Item data", Integer.valueOf(p_191971_2_.getMetadata()));
				crashreportcategory.addDetail("Registry Name", () -> String.valueOf(p_191971_2_.getItem().getRegistryName()));
				crashreportcategory.addDetail("Item Class", () -> p_191971_2_.getItem().getClass().getName());
				crashreportcategory.addDetail("Item name", new ICrashReportDetail<String>()
				{
					public String call() throws Exception
					{
						return p_191971_2_.getDisplayName();
					}
				});
				throw new ReportedException(crashreport);
			}
		}
	}

	/**Copied bc private*/
	private int storePartialItemStack(ItemStack itemStackIn)
	{
		int i = this.storeItemStack(itemStackIn);

		if (i == -1)
		{
			i = this.getFirstEmptyStack();
		}

		return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
	}

	/**Ignore max stack size*/
	private int addResource(int p_191973_1_, ItemStack p_191973_2_)
	{
		Item item = p_191973_2_.getItem();
		int i = p_191973_2_.getCount();
		ItemStack itemstack = this.getStackInSlot(p_191973_1_);

		if (itemstack.isEmpty())
		{
			itemstack = p_191973_2_.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
			itemstack.setCount(0);

			if (p_191973_2_.hasTagCompound())
			{
				itemstack.setTagCompound(p_191973_2_.getTagCompound().copy());
			}

			this.setInventorySlotContents(p_191973_1_, itemstack);
		}

		int j = i;

		/*if (i > itemstack.getMaxStackSize() - itemstack.getCount())
		{
			j = itemstack.getMaxStackSize() - itemstack.getCount();
		}*/

		if (j > this.getInventoryStackLimit() - itemstack.getCount())
		{
			j = this.getInventoryStackLimit() - itemstack.getCount();
		}

		if (j == 0)
		{
			return i;
		}
		else
		{
			i = i - j;
			itemstack.grow(j);
			itemstack.setAnimationsToGo(5);
			return i;
		}
	}

	/**Copied to ignore max stack size*/
	@Override
	public void placeItemBackInInventory(World world, ItemStack stack) {
        if (!world.isRemote)
        {
            while (!stack.isEmpty())
            {
                int i = this.storeItemStack(stack);

                if (i == -1)
                {
                    i = this.getFirstEmptyStack();
                }

                if (i == -1)
                {
                    this.player.dropItem(stack, false);
                    break;
                }

				int j = /*stack.getMaxStackSize() - */this.getStackInSlot(i).getCount();

                if (this.add(i, stack.splitStack(j)))
                {
                    ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(-2, i, this.getStackInSlot(i)));
                }
            }
        }
    }


}