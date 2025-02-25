package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.menu.menus.*;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores menus.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class Menus {

    private final UltraCosmetics ultraCosmetics;
    private final Map<Category, CosmeticMenu<?>> categoryMenus = new HashMap<>();
    private Menu mainMenu;
    private MenuPurchaseFactory menuPurchaseFactory = StandardMenuPurchase::new;

    public Menus(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        categoryMenus.put(Category.EMOTES, new MenuEmotes(ultraCosmetics));
        categoryMenus.put(Category.GADGETS, new MenuGadgets(ultraCosmetics));
        categoryMenus.put(Category.EFFECTS, new MenuParticleEffects(ultraCosmetics));
        categoryMenus.put(Category.HATS, new MenuHats(ultraCosmetics));
        categoryMenus.put(Category.MORPHS, new MenuMorphs(ultraCosmetics));
        categoryMenus.put(Category.MOUNTS, new MenuMounts(ultraCosmetics));
        categoryMenus.put(Category.PETS, new MenuPets(ultraCosmetics));
        categoryMenus.put(Category.PROJECTILE_EFFECTS, new MenuProjectileEffects(ultraCosmetics));
        categoryMenus.put(Category.DEATH_EFFECTS, new MenuDeathEffects(ultraCosmetics));
        MenuSuits ms = new MenuSuits(ultraCosmetics);
        categoryMenus.put(Category.SUITS_HELMET, ms);
        categoryMenus.put(Category.SUITS_CHESTPLATE, ms);
        categoryMenus.put(Category.SUITS_LEGGINGS, ms);
        categoryMenus.put(Category.SUITS_BOOTS, ms);
        this.mainMenu = new MenuMain(ultraCosmetics);
        // Load the class so it's available on disable, when we can't load more classes.
        // Otherwise sometimes errors occur when hotswapping the jar
        new CosmeticsInventoryHolder();
    }

    public void setMainMenu(Menu menu) {
        this.mainMenu = menu;
    }

    /**
     * Opens UC's main menu OR runs the custom main menu command specified in config.yml
     *
     * @param ultraPlayer The player to show the menu to
     */
    public void openMainMenu(UltraPlayer ultraPlayer) {
        if (ultraCosmetics.getConfig().getBoolean("Categories.Back-To-Main-Menu-Custom-Command.Enabled")) {
            String command = ultraCosmetics.getConfig().getString("Categories.Back-To-Main-Menu-Custom-Command.Command").replace("/", "").replace("{player}", ultraPlayer.getBukkitPlayer().getName()).replace("{playeruuid}", ultraPlayer.getUUID().toString());
            Bukkit.dispatchCommand(ultraCosmetics.getServer().getConsoleSender(), command);
            return;
        }
        mainMenu.open(ultraPlayer);
    }

    public CosmeticMenu<?> getCategoryMenu(Category category) {
        return categoryMenus.get(category);
    }

    public void setCategoryMenu(Category category, CosmeticMenu<?> menu) {
        categoryMenus.put(category, menu);
    }

    /**
     * Opens Ammo Purchase Menu.
     */
    public void openAmmoPurchaseMenu(GadgetType type, UltraPlayer player, Runnable menuReturnFunc) {
        String itemName = MessageManager.getLegacyMessage("Buy-Ammo-Description",
                Placeholder.unparsed("amount", String.valueOf(type.getResultAmmoAmount())),
                Placeholder.unparsed("price", String.valueOf(type.getAmmoPrice())),
                Placeholder.component("gadgetname", type.getName())
        );
        ItemStack display = ItemFactory.create(type.getMaterial(), itemName);
        PurchaseData pd = new PurchaseData();
        pd.setPrice(type.getAmmoPrice());
        pd.setShowcaseItem(display);
        pd.setOnPurchase(() -> {
            player.addAmmo(type, type.getResultAmmoAmount());
            menuReturnFunc.run();
        });
        pd.setOnCancel(menuReturnFunc);
        MenuPurchase mp = menuPurchaseFactory.createPurchaseMenu(ultraCosmetics, MessageManager.getMessage("Menu.Buy-Ammo.Title"), pd);
        player.getBukkitPlayer().openInventory(mp.getInventory(player));
    }

    public MenuPurchaseFactory getMenuPurchaseFactory() {
        return menuPurchaseFactory;
    }

    public void setMenuPurchaseFactory(MenuPurchaseFactory factory) {
        this.menuPurchaseFactory = factory;
    }
}
