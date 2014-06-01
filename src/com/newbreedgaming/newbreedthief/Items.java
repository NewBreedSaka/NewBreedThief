package com.newbreedgaming.newbreedthief;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
	
	public static ItemStack createArtifact() {
		ItemStack artifact = new ItemStack(Material.DRAGON_EGG, 1);
		ItemMeta artifactMeta = artifact.getItemMeta();
		artifactMeta.setDisplayName("§6Artifact");
		ArrayList<String> artifactLore = new ArrayList<String>();
		artifactLore.add("§7Get this back to the thieves guild!");
		artifactMeta.setLore(artifactLore);
		artifact.setItemMeta(artifactMeta);
		return(artifact);
	}

	public static ItemStack createEgg(int amount) {
		ItemStack item = new ItemStack(Material.EGG, amount);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§5Blinding Bomb");
		ArrayList<String> itemLore = new ArrayList<String>();
		itemLore.add("§7Throw this at the guards to blind them!");
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return(item);
	}
	
	public static ItemStack createThiefDagger(String action) {
		ItemStack item = new ItemStack(Material.FEATHER);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§5Thieves dagger");
		itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		ArrayList<String> itemLore = new ArrayList<String>();
		itemLore.add("§9Left click §7to attack");
		if (action != null)	itemLore.add("§9Right click §7to activate " + action + "!");
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return(item);
	}
	
	public static Inventory createClassMenu(String invToReturn){
		//Setup guards inventory
		Inventory inventory;
		Inventory guardInv;
		Inventory thiefInv;
		
		ItemStack guardDefault, guardScout, guardHeavy, guardMage;
		ItemStack thiefDefault, thiefSmoke, thiefBlink;
		
		guardInv = Bukkit.createInventory(null, 9, "Pick Your Guard Class"); 

		guardDefault = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta guardDefaultItemMeta = guardDefault.getItemMeta();
		ArrayList<String> guardDefaultLore = new ArrayList<String>();
		guardDefaultLore.add("§7Normal armour");
		guardDefaultLore.add("§7No access to walkways");
		guardDefaultLore.add("§7Special ability grants brute force");
		//TODO: add vault check if player has purchased as permission node will be added for all classes
		guardDefaultLore.add("§fCost: None");
		guardDefaultItemMeta.setDisplayName(ChatColor.AQUA + "Default Guard");
		guardDefaultItemMeta.setLore(guardDefaultLore);
		guardDefault.setItemMeta(guardDefaultItemMeta);		


		guardScout = new ItemStack(Material.POTION);
		ItemMeta guardScoutItemMeta = guardScout.getItemMeta();
		ArrayList<String> guardScoutLore = new ArrayList<String>();
		guardScoutLore.add("§7Light armour");
		guardScoutLore.add("§7Can use Thief pathways");
		guardScoutLore.add("§7Special ability grants dash");
		guardScoutLore.add("§fCost: 100 coins");
		guardScoutItemMeta.setDisplayName(ChatColor.AQUA + "Scout Guard");
		guardScoutItemMeta.setLore(guardScoutLore);
		guardScout.setItemMeta(guardScoutItemMeta);	

		guardHeavy = new ItemStack(Material.BREAD);
		ItemMeta guardHeavyItemMeta = guardHeavy.getItemMeta();
		ArrayList<String> guardHeavyLore = new ArrayList<String>();
		guardHeavyLore.add("§7Heavy armour");
		guardHeavyLore.add("§7No access to walkways");
		guardHeavyLore.add("§7Special ability grants vitality");
		guardHeavyLore.add("§fCost: 100 coins");
		guardHeavyItemMeta.setDisplayName(ChatColor.AQUA + "Heavy Gaurd");
		guardHeavyItemMeta.setLore(guardHeavyLore);
		guardHeavy.setItemMeta(guardHeavyItemMeta);	

		guardMage = new ItemStack(Material.FIRE);
		ItemMeta guardMageItemMeta = guardMage.getItemMeta();
		ArrayList<String> guardMageLore = new ArrayList<String>();
		guardMageLore.add("§7Light armour");
		guardMageLore.add("§7No access to walkways");
		guardMageLore.add("§7Special ability casts fire of revealing");
		guardMageLore.add("§6VIP Only");
		guardMageItemMeta.setDisplayName(ChatColor.GOLD + "Mage Gaurd");
		guardMageItemMeta.setLore(guardMageLore);
		guardMage.setItemMeta(guardMageItemMeta);	

		guardInv.addItem(new ItemStack(guardDefault));
		guardInv.addItem(new ItemStack(guardScout));
		guardInv.addItem(new ItemStack(guardHeavy));
		guardInv.addItem(new ItemStack(guardMage));
		
		//Setup thieves inventory

		thiefInv = Bukkit.createInventory(null, 9, "Pick Your Thief Class"); 

		thiefDefault = new ItemStack(Material.FEATHER);
		ItemMeta thiefDefaultItemMeta = thiefDefault.getItemMeta();
		thiefDefaultItemMeta.setDisplayName(ChatColor.AQUA + "Default Thief");
		thiefDefaultItemMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Backstabs Guards"));
		ArrayList<String> thiefDefaultLore = new ArrayList<String>();
		thiefDefaultLore.add("§7Special ability casts dash");
		thiefDefaultLore.add("§fCost: None");
		thiefDefaultItemMeta.setLore(thiefDefaultLore);
		thiefDefault.setItemMeta(thiefDefaultItemMeta);		


		thiefSmoke = new ItemStack(Material.EGG);
		ItemMeta thiefSmokeItemMeta = thiefSmoke.getItemMeta();
		thiefSmokeItemMeta.setDisplayName(ChatColor.AQUA + "Smoke Thief");
		ArrayList<String> thiefSmokeLore = new ArrayList<String>();
		thiefSmokeLore.add("§7Can use smoke eggs");
		thiefSmokeLore.add("§fCost: 100 coins");
		thiefSmokeItemMeta.setLore(thiefSmokeLore);
		thiefSmoke.setItemMeta(thiefSmokeItemMeta);	

		thiefBlink = new ItemStack(Material.COMPASS);
		ItemMeta thiefBlinkItemMeta = thiefBlink.getItemMeta();
		thiefBlinkItemMeta.setDisplayName(ChatColor.GOLD + "Shadow Step Thief");
		ArrayList<String> thiefBlinkLore = new ArrayList<String>();
		thiefBlinkLore.add("§7Special ability casts Shadow Step");
		thiefBlinkLore.add("§6VIP Only");
		thiefBlinkItemMeta.setLore(thiefBlinkLore);
		thiefBlink.setItemMeta(thiefBlinkItemMeta);	

		thiefInv.addItem(new ItemStack(thiefDefault));
		thiefInv.addItem(new ItemStack(thiefSmoke));
		thiefInv.addItem(new ItemStack(thiefBlink));
		
		if (invToReturn == "Thief"){
			inventory = thiefInv;
		} else {
			inventory = guardInv;
		}
		
		return inventory;
	}
}
