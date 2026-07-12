package com.pcmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = PcMod.MODID, name = PcMod.NAME, version = PcMod.VERSION)
public class PcMod {
    public static final String MODID = "pcmod";
    public static final String NAME = "PC Assembly Mod";
    public static final String VERSION = "1.0.0";

    @Mod.Instance
    public static PcMod instance;

    // ============ ПЕРЕЧИСЛЕНИЯ ============
    public enum CompType { CPU, GPU, MOTHERBOARD, RAM, STORAGE, PSU, COOLER }
    
    public enum Socket { 
        LGA1700("LGA1700"), 
        AM4("AM4"), 
        AM5("AM5");
        public final String name; 
        Socket(String n) { this.name = n; }
    }
    
    public enum RamType { 
        DDR4("DDR4"), 
        DDR5("DDR5");
        public final String name; 
        RamType(String n) { this.name = n; }
    }
    
    public enum StorageType { 
        HDD("HDD"), 
        SSD("SSD"), 
        NVME("NVMe");
        public final String name; 
        StorageType(String n) { this.name = n; }
    }
    
    public enum PsuCert { 
        BRONZE("80+ Bronze"), 
        GOLD("80+ Gold"), 
        PLATINUM("80+ Platinum"), 
        TITANIUM("80+ Titanium");
        public final String name; 
        PsuCert(String n) { this.name = n; }
    }

    // ============ СПИСКИ ============
    public static final List<Item> ALL_ITEMS = new ArrayList<>();
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();

    // ============ РЕГИСТРАЦИЯ ВСЕХ КОМПОНЕНТОВ ============
    static { registerAllComponents(); }

    static void registerAllComponents() {
        // ПРОЦЕССОРЫ INTEL
        addCPU("cpu_i9_13900k",  "Intel Core i9-13900K",  "Intel", 5, 100, 150, Socket.LGA1700);
        addCPU("cpu_i7_13700k",  "Intel Core i7-13700K",  "Intel", 4,  80, 125, Socket.LGA1700);
        addCPU("cpu_i5_13600k",  "Intel Core i5-13600K",  "Intel", 3,  60, 125, Socket.LGA1700);
        addCPU("cpu_i3_13100",   "Intel Core i3-13100",   "Intel", 2,  35,  60, Socket.LGA1700);
        
        // ПРОЦЕССОРЫ AMD AM5
        addCPU("cpu_r9_7950x",   "AMD Ryzen 9 7950X",    "AMD",   5,  95, 170, Socket.AM5);
        addCPU("cpu_r7_7800x3d", "AMD Ryzen 7 7800X3D",  "AMD",   5,  90, 120, Socket.AM5);
        addCPU("cpu_r5_7600x",   "AMD Ryzen 5 7600X",    "AMD",   3,  55, 105, Socket.AM5);
        
        // ПРОЦЕССОРЫ AMD AM4
        addCPU("cpu_r9_5950x",   "AMD Ryzen 9 5950X",    "AMD",   5,  85, 105, Socket.AM4);
        addCPU("cpu_r7_5800x3d", "AMD Ryzen 7 5800X3D",  "AMD",   4,  75, 105, Socket.AM4);
        addCPU("cpu_r5_5600x",   "AMD Ryzen 5 5600X",    "AMD",   3,  50,  65, Socket.AM4);
        addCPU("cpu_r3_4100",    "AMD Ryzen 3 4100",     "AMD",   2,  25,  65, Socket.AM4);

        // ВИДЕОКАРТЫ NVIDIA
        addGPU("gpu_rtx4090", "NVIDIA RTX 4090",   "NVIDIA", 5, 100, 450);
        addGPU("gpu_rtx4080", "NVIDIA RTX 4080",   "NVIDIA", 5,  85, 320);
        addGPU("gpu_rtx4070", "NVIDIA RTX 4070",   "NVIDIA", 4,  70, 200);
        addGPU("gpu_rtx4060", "NVIDIA RTX 4060",   "NVIDIA", 3,  50, 115);
        addGPU("gpu_rtx3060", "NVIDIA RTX 3060",   "NVIDIA", 3,  40, 170);
        addGPU("gpu_gtx1660", "NVIDIA GTX 1660",   "NVIDIA", 2,  25, 120);
        
        // ВИДЕОКАРТЫ AMD
        addGPU("gpu_rx7900xtx","AMD RX 7900 XTX",  "AMD",   5,  95, 355);
        addGPU("gpu_rx7800xt", "AMD RX 7800 XT",   "AMD",   4,  75, 263);
        addGPU("gpu_rx6700xt", "AMD RX 6700 XT",   "AMD",   3,  55, 230);
        addGPU("gpu_rx6600",   "AMD RX 6600",      "AMD",   2,  35, 132);

        // МАТЕРИНСКИЕ ПЛАТЫ
        addMB("mb_z790",  "ASUS ROG Z790",    "ASUS",     5, 80, Socket.LGA1700, RamType.DDR5, 4);
        addMB("mb_z690",  "MSI Z690",          "MSI",      4, 65, Socket.LGA1700, RamType.DDR5, 4);
        addMB("mb_b660",  "Gigabyte B660",     "Gigabyte", 3, 45, Socket.LGA1700, RamType.DDR4, 2);
        addMB("mb_h610",  "ASRock H610",       "ASRock",   2, 25, Socket.LGA1700, RamType.DDR4, 2);
        addMB("mb_x670e", "ASUS X670E",        "ASUS",     5, 85, Socket.AM5,     RamType.DDR5, 4);
        addMB("mb_b650",  "MSI B650",          "MSI",      3, 50, Socket.AM5,     RamType.DDR5, 4);
        addMB("mb_x570",  "Gigabyte X570",     "Gigabyte", 4, 60, Socket.AM4,     RamType.DDR4, 4);
        addMB("mb_b550",  "ASUS B550",         "ASUS",     3, 40, Socket.AM4,     RamType.DDR4, 4);
        addMB("mb_b450",  "MSI B450",          "MSI",      2, 30, Socket.AM4,     RamType.DDR4, 2);

        // ОПЕРАТИВНАЯ ПАМЯТЬ
        addRAM("ram_ddr5_32gb", "Corsair Dominator 32GB DDR5", "Corsair",  5, 85, 32, 6000, RamType.DDR5);
        addRAM("ram_ddr5_16gb", "G.Skill Trident 16GB DDR5",   "G.Skill",  4, 60, 16, 5600, RamType.DDR5);
        addRAM("ram_ddr4_32gb", "Corsair Vengeance 32GB DDR4", "Corsair",  4, 55, 32, 3600, RamType.DDR4);
        addRAM("ram_ddr4_16gb", "Kingston Fury 16GB DDR4",     "Kingston", 3, 40, 16, 3200, RamType.DDR4);
        addRAM("ram_ddr4_8gb",  "Crucial 8GB DDR4",            "Crucial",  2, 20,  8, 2666, RamType.DDR4);

        // НАКОПИТЕЛИ
        addStorage("nvme_2tb",   "Samsung 990 Pro 2TB NVMe",  "Samsung", 5, 95, StorageType.NVME, 2000);
        addStorage("nvme_1tb",   "WD Black SN850 1TB NVMe",   "WD",      5, 80, StorageType.NVME, 1000);
        addStorage("nvme_500gb", "Samsung 970 EVO 500GB NVMe","Samsung", 4, 60, StorageType.NVME, 500);
        addStorage("ssd_1tb",    "Samsung 870 EVO 1TB SSD",   "Samsung", 3, 40, StorageType.SSD,  1000);
        addStorage("ssd_500gb",  "Crucial MX500 500GB SSD",   "Crucial", 2, 30, StorageType.SSD,  500);
        addStorage("hdd_2tb",    "Seagate Barracuda 2TB HDD", "Seagate", 1, 15, StorageType.HDD,  2000);
        addStorage("hdd_1tb",    "WD Blue 1TB HDD",           "WD",      1, 10, StorageType.HDD,  1000);

        // БЛОКИ ПИТАНИЯ
        addPSU("psu_1200w","Corsair HX1200",       "Corsair",  5, 80, 1200, PsuCert.PLATINUM);
        addPSU("psu_1000w","EVGA SuperNOVA 1000",  "EVGA",     5, 65, 1000, PsuCert.GOLD);
        addPSU("psu_850w", "Corsair RM850x",       "Corsair",  4, 50,  850, PsuCert.GOLD);
        addPSU("psu_750w", "SeaSonic Focus 750",   "SeaSonic", 3, 40,  750, PsuCert.GOLD);
        addPSU("psu_650w", "Corsair RM650x",       "Corsair",  3, 30,  650, PsuCert.GOLD);
        addPSU("psu_550w", "EVGA 550 BR",          "EVGA",     2, 20,  550, PsuCert.BRONZE);
        addPSU("psu_450w", "Corsair CV450",        "Corsair",  1, 10,  450, PsuCert.BRONZE);

        // КУЛЕРЫ
        addCooler("cooler_aio_360",     "Corsair H150i 360mm",   "Corsair",  5, 95, 300);
        addCooler("cooler_aio_280",     "NZXT Kraken X63 280mm", "NZXT",     4, 80, 250);
        addCooler("cooler_aio_240",     "Corsair H100i 240mm",   "Corsair",  4, 70, 200);
        addCooler("cooler_air_noctua",  "Noctua NH-D15",         "Noctua",   4, 65, 200);
        addCooler("cooler_air_cm",      "Cooler Master Hyper 212","CM",       2, 35, 150);
        addCooler("cooler_stock_amd",   "AMD Wraith Stealth",    "AMD",      1, 15,  65);
        addCooler("cooler_stock_intel", "Intel Stock Cooler",    "Intel",    1, 10,  65);

        // ИНСТРУМЕНТЫ
        Item screwdriver = new Item()
            .setUnlocalizedName("screwdriver")
            .setRegistryName("screwdriver")
            .setMaxStackSize(1)
            .setCreativeTab(CreativeTabsPC.TAB);
        ALL_ITEMS.add(screwdriver);

        Item thermalPaste = new Item()
            .setUnlocalizedName("thermal_paste")
            .setRegistryName("thermal_paste")
            .setMaxStackSize(16)
            .setCreativeTab(CreativeTabsPC.TAB);
        ALL_ITEMS.add(thermalPaste);

        // БЛОК ПК
        BlockPcCase pcBlock = new BlockPcCase();
        pcBlock.setUnlocalizedName("pc_case")
               .setRegistryName("pc_case")
               .setHardness(3.0F)
               .setResistance(10.0F)
               .setCreativeTab(CreativeTabsPC.TAB);
        ALL_BLOCKS.add(pcBlock);
        ALL_ITEMS.add(new ItemBlock(pcBlock).setRegistryName("pc_case"));
    }

    // ============ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ============
    static void addCPU(String id, String name, String brand, int tier, int perf, int tdp, Socket s) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, tdp, CompType.CPU).setData(s));
    }
    static void addGPU(String id, String name, String brand, int tier, int perf, int tdp) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, tdp, CompType.GPU));
    }
    static void addMB(String id, String name, String brand, int tier, int perf, Socket s, RamType r, int slots) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, 10, CompType.MOTHERBOARD).setData(s, r, slots));
    }
    static void addRAM(String id, String name, String brand, int tier, int perf, int gb, int mhz, RamType t) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, gb > 16 ? 10 : 5, CompType.RAM).setData(gb, mhz, t));
    }
    static void addStorage(String id, String name, String brand, int tier, int perf, StorageType t, int gb) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, t == StorageType.HDD ? 8 : 5, CompType.STORAGE).setData(t, gb));
    }
    static void addPSU(String id, String name, String brand, int tier, int perf, int w, PsuCert c) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, 0, CompType.PSU).setData(w, c));
    }
    static void addCooler(String id, String name, String brand, int tier, int perf, int tdp) {
        ALL_ITEMS.add(new ComponentItem(id, name, brand, tier, perf, 0, CompType.COOLER).setData(tdp));
    }

    // ============ КЛАСС ПРЕДМЕТА-КОМПОНЕНТА ============
    public static class ComponentItem extends Item {
        public final String displayName, brand;
        public final int tier, perf, power;
        public final CompType type;
        private Object[] extraData = {};

        ComponentItem(String id, String name, String brand, int tier, int perf, int power, CompType type) {
            setUnlocalizedName(id);
            setRegistryName(id);
            setMaxStackSize(1);
            setCreativeTab(CreativeTabsPC.TAB);
            this.displayName = name;
            this.brand = brand;
            this.tier = tier;
            this.perf = perf;
            this.power = power;
            this.type = type;
        }

        ComponentItem setData(Object... data) { this.extraData = data; return this; }
        public Socket getSocket() { return findData(Socket.class); }
        public RamType getRamType() { return findData(RamType.class); }
        public int getRamGB() { return findInt(0); }
        public int getRamMHz() { return findInt(1); }
        public int getSlots() { return findInt(2); }
        public StorageType getStorageType() { return findData(StorageType.class); }
        public int getStorageGB() { return findInt(1); }
        public int getWattage() { return findInt(0); }
        public PsuCert getCert() { return findData(PsuCert.class); }
        public int getTDP() { return findInt(0); }

        @SuppressWarnings("unchecked")
        private <T> T findData(Class<T> clazz) {
            for (Object o : extraData) if (clazz.isInstance(o)) return (T) o;
            return null;
        }
        private int findInt(int idx) {
            int count = 0;
            for (Object o : extraData) {
                if (o instanceof Integer) {
                    if (count == idx) return (Integer) o;
                    count++;
                }
            }
            return 0;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
            tooltip.add("§7Бренд: §e" + brand);
            StringBuilder stars = new StringBuilder("§7Тир: §b");
            for (int i = 0; i < 5; i++) stars.append(i < tier ? "★" : "☆");
            tooltip.add(stars.toString());
            tooltip.add("§7Производительность: §a" + perf + "%");
            if (power > 0) tooltip.add("§7Потребление: §c" + power + "W");
            switch (type) {
                case CPU: tooltip.add("§7Сокет: §6" + (getSocket() != null ? getSocket().name : "Н/Д")); break;
                case GPU: tooltip.add("§7Видеокарта"); break;
                case MOTHERBOARD:
                    tooltip.add("§7Сокет: §6" + (getSocket() != null ? getSocket().name : "Н/Д"));
                    tooltip.add("§7ОЗУ: §e" + (getRamType() != null ? getRamType().name : "Н/Д"));
                    tooltip.add("§7Слотов ОЗУ: §b" + getSlots());
                    break;
                case RAM:
                    tooltip.add("§7Тип: §e" + (getRamType() != null ? getRamType().name : "Н/Д"));
                    tooltip.add("§7Объем: §a" + getRamGB() + "GB");
                    tooltip.add("§7Частота: §b" + getRamMHz() + "MHz");
                    break;
                case STORAGE:
                    tooltip.add("§7Тип: §d" + (getStorageType() != null ? getStorageType().name : "Н/Д"));
                    tooltip.add("§7Объем: §a" + getStorageGB() + "GB");
                    break;
                case PSU:
                    tooltip.add("§7Мощность: §e" + getWattage() + "W");
                    tooltip.add("§7Сертификат: §6" + (getCert() != null ? getCert().name : "Н/Д"));
                    break;
                case COOLER:
                    tooltip.add("§7TDP: §c" + getTDP() + "W");
                    break;
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            return displayName;
        }
    }

    // ============ БЛОК КОРПУСА ПК ============
    public static class BlockPcCase extends Block {
        BlockPcCase() { super(Material.IRON); }

        @Override
        public boolean hasTileEntity(IBlockState state) { return true; }

        @Override
        public TileEntity createTileEntity(World world, IBlockState state) {
            return new TileEntityPc();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                         EntityPlayer player, EnumHand hand,
                                         EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityPc) {
                    player.openGui(PcMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
            return true;
        }

        @Override
        public void breakBlock(World world, BlockPos pos, IBlockState state) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityPc) {
                TileEntityPc pc = (TileEntityPc) te;
                for (int i = 0; i < pc.inv.getSlots(); i++) {
                    ItemStack stack = pc.inv.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        spawnAsEntity(world, pos, stack);
                    }
                }
            }
            super.breakBlock(world, pos, state);
        }
    }

    // ============ TILEENTITY ПК ============
    public static class TileEntityPc extends TileEntity implements ITickable {
        public final ItemStackHandler inv = new ItemStackHandler(7) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (!(stack.getItem() instanceof ComponentItem)) return false;
                CompType t = ((ComponentItem) stack.getItem()).type;
                return t.ordinal() == slot;
            }

            @Override
            protected void onContentsChanged(int slot) {
                TileEntityPc.this.markDirty();
            }
        };

        private boolean assembled = false;
        private int totalPerf = 0;
        private int totalPower = 0;
        private String errorMsg = "";

        @Override
        public void update() {
            if (!world.isRemote && world.getTotalWorldTime() % 20 == 0) {
                validateAssembly();
            }
        }

        void validateAssembly() {
            ItemStack cpuS = inv.getStackInSlot(0);
            ItemStack gpuS = inv.getStackInSlot(1);
            ItemStack mbS  = inv.getStackInSlot(2);
            ItemStack ramS = inv.getStackInSlot(3);
            ItemStack strS = inv.getStackInSlot(4);
            ItemStack psuS = inv.getStackInSlot(5);
            ItemStack clrS = inv.getStackInSlot(6);

            if (cpuS.isEmpty() || mbS.isEmpty() || ramS.isEmpty() || strS.isEmpty() || psuS.isEmpty()) {
                assembled = false;
                errorMsg = "Не хватает компонентов! Нужны: CPU, MB, RAM, Storage, PSU";
                return;
            }

            ComponentItem cpu = (ComponentItem) cpuS.getItem();
            ComponentItem mb  = (ComponentItem) mbS.getItem();
            ComponentItem ram = (ComponentItem) ramS.getItem();
            ComponentItem psu = (ComponentItem) psuS.getItem();
            ComponentItem clr = clrS.isEmpty() ? null : (ComponentItem) clrS.getItem();
            ComponentItem gpu = gpuS.isEmpty() ? null : (ComponentItem) gpuS.getItem();

            if (cpu.getSocket() != mb.getSocket()) {
                assembled = false;
                errorMsg = "Несовместимый сокет! CPU: " + cpu.getSocket().name
                         + " | MB: " + mb.getSocket().name;
                return;
            }

            if (ram.getRamType() != mb.getRamType()) {
                assembled = false;
                errorMsg = "Несовместимая ОЗУ! MB поддерживает: " + mb.getRamType().name
                         + " | Установлена: " + ram.getRamType().name;
                return;
            }

            totalPower = cpu.power;
            if (gpu != null) totalPower += gpu.power;
            totalPower += ram.power;
            totalPower += 50;

            if (totalPower > psu.getWattage()) {
                assembled = false;
                errorMsg = "Недостаточно мощности БП! Требуется: " + totalPower
                         + "W | БП: " + psu.getWattage() + "W";
                return;
            }

            if (clr != null && clr.getTDP() < cpu.power) {
                assembled = false;
                errorMsg = "Слабое охлаждение! CPU TDP: " + cpu.power
                         + "W | Кулер: " + clr.getTDP() + "W";
                return;
            }
            if (clr == null && cpu.power > 65) {
                assembled = false;
                errorMsg = "Установите кулер для этого процессора!";
                return;
            }

            float perf = cpu.perf;
            if (gpu != null) perf = perf * 0.4f + gpu.perf * 0.6f;
            perf = perf * (1.0f + ram.perf / 200.0f);
            totalPerf = Math.min(100, (int) perf);

            assembled = true;
            errorMsg = "";
        }

        public boolean isAssembled() { return assembled; }
        public int getTotalPerf() { return totalPerf; }
        public int getTotalPower() { return totalPower; }
        public String getErrorMsg() { return errorMsg; }
        public String getCpuName() {
            ItemStack s = inv.getStackInSlot(0);
            return s.isEmpty() ? "Нет" : s.getDisplayName();
        }
        public String getGpuName() {
            ItemStack s = inv.getStackInSlot(1);
            return s.isEmpty() ? "Нет" : s.getDisplayName();
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setTag("Inventory", inv.serializeNBT());
            return tag;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            inv.deserializeNBT(tag.getCompoundTag("Inventory"));
        }
    }

    // ============ КОНТЕЙНЕР ============
    public static class ContainerPc extends Container {
        private final TileEntityPc tile;

        public ContainerPc(InventoryPlayer playerInv, TileEntityPc te) {
            this.tile = te;

            for (int i = 0; i < 7; i++) {
                addSlotToContainer(new SlotItemHandler(te.inv, i, 20, 16 + i * 22));
            }

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    addSlotToContainer(new Slot(playerInv, col + row * 9 + 9,
                            56 + col * 18, 174 + row * 18));
                }
            }

            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInv, col, 56 + col * 18, 232));
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index) {
            ItemStack stack = ItemStack.EMPTY;
            Slot slot = inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack stackInSlot = slot.getStack();
                stack = stackInSlot.copy();

                if (index < 7) {
                    if (!mergeItemStack(stackInSlot, 7, 43, true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!mergeItemStack(stackInSlot, 0, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (stackInSlot.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
            }

            return stack;
        }
    }

    // ============ GUI ============
    @SideOnly(Side.CLIENT)
    public static class GuiPc extends GuiContainer {
        private final TileEntityPc tile;
        private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MODID, "textures/gui/pc.png");

        public GuiPc(InventoryPlayer playerInv, TileEntityPc te) {
            super(new ContainerPc(playerInv, te));
            this.tile = te;
            this.xSize = 220;
            this.ySize = 256;
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GUI_TEXTURE);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

            if (tile.isAssembled()) {
                fontRenderer.drawString("§a✓ Система собрана!", guiLeft + 6, guiTop + 4, 0xFFFFFF);
                fontRenderer.drawString("§7CPU: §f" + tile.getCpuName(), guiLeft + 6, guiTop + 16, 0xFFFFFF);
                fontRenderer.drawString("§7GPU: §f" + tile.getGpuName(), guiLeft + 6, guiTop + 26, 0xFFFFFF);
                fontRenderer.drawString("§7Производительность: §b" + tile.getTotalPerf() + "%", guiLeft + 6, guiTop + 38, 0xFFFFFF);
                fontRenderer.drawString("§7Потребление: §e" + tile.getTotalPower() + "W", guiLeft + 6, guiTop + 48, 0xFFFFFF);

                StringBuilder bar = new StringBuilder("§8[");
                int bars = tile.getTotalPerf() / 5;
                for (int i = 0; i < 20; i++) {
                    if (i < bars) {
                        if (tile.getTotalPerf() >= 80) bar.append("§a|");
                        else if (tile.getTotalPerf() >= 50) bar.append("§e|");
                        else bar.append("§c|");
                    } else {
                        bar.append("§7|");
                    }
                }
                bar.append("§8]");
                fontRenderer.drawString(bar.toString(), guiLeft + 6, guiTop + 60, 0xFFFFFF);

                String grade;
                if (tile.getTotalPerf() >= 90) grade = "§6Класс: S (Топовый)";
                else if (tile.getTotalPerf() >= 70) grade = "§5Класс: A (Высокий)";
                else if (tile.getTotalPerf() >= 50) grade = "§9Класс: B (Средний)";
                else if (tile.getTotalPerf() >= 30) grade = "§aКласс: C (Базовый)";
                else grade = "§7Класс: D (Офисный)";
                fontRenderer.drawString(grade, guiLeft + 6, guiTop + 72, 0xFFFFFF);
            } else {
                fontRenderer.drawString("§c✗ " + tile.getErrorMsg(), guiLeft + 6, guiTop + 4, 0xFFFFFF);
            }
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            fontRenderer.drawString("Сборка ПК", 56, 6, 0x404040);
            fontRenderer.drawString("Инвентарь", 56, 164, 0x404040);

            String[] slotNames = {"CPU", "GPU", "MB", "RAM", "Storage", "PSU", "Cooler"};
            for (int i = 0; i < 7; i++) {
                fontRenderer.drawString(slotNames[i], 42, 20 + i * 22, 0x404040);
            }
        }
    }

    // ============ GUI HANDLER ============
    public static class GuiHandler implements IGuiHandler {
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityPc) {
                return new ContainerPc(player.inventory, (TileEntityPc) te);
            }
            return null;
        }

        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityPc) {
                return new GuiPc(player.inventory, (TileEntityPc) te);
            }
            return null;
        }
    }

    // ============ КРЕАТИВ-ТАБ ============
    public static class CreativeTabsPC extends CreativeTabs {
        public static final CreativeTabs TAB = new CreativeTabsPC();

        CreativeTabsPC() { super(MODID); }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            for (Item item : ALL_ITEMS) {
                if (item.getRegistryName().getResourcePath().equals("cpu_i9_13900k")) {
                    return new ItemStack(item);
                }
            }
            return new ItemStack(ALL_ITEMS.get(0));
        }
    }

    // ============ РЕГИСТРАЦИЯ ============
    @Mod.EventBusSubscriber
    public static class RegistryEvents {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(ALL_BLOCKS.toArray(new Block[0]));
            GameRegistry.registerTileEntity(TileEntityPc.class, new ResourceLocation(MODID, "pc"));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(ALL_ITEMS.toArray(new Item[0]));
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void registerModels(ModelRegistryEvent event) {
            for (Item item : ALL_ITEMS) {
                ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }

    // ============ ИНИЦИАЛИЗАЦИЯ ============
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Рецепты можно добавить здесь
    }
}