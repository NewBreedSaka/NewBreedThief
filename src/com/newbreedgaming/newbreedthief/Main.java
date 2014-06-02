package com.newbreedgaming.newbreedthief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Chest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.HelixLocationEffect;

public class Main extends JavaPlugin implements Listener {
	private EffectManager effectManager;
	public int countdowntimer = 30;
	public boolean gameBegun = false;
	public Scoreboard board;
	public Objective obj;
	public Team thiefTeam;
	public Team guardTeam;
	public Score artifactsScore;

	//cooldowns
	public HashMap<String, Long> fireOfRevealing = new HashMap<String, Long>();
	public HashMap<String, Long> shadowMeldCooldown = new HashMap<String, Long>();
	//classes
	public HashMap<String, String> selectedKit = new HashMap<String, String>();

	ItemStack kitSelector;

	Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		getConfig().options().copyDefaults(true);

		kitSelector = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = kitSelector.getItemMeta();
		im.setDisplayName("Kit Selector");
		im.setLore(Arrays.asList("Choose your kit!"));
		kitSelector.setItemMeta(im);

		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.getConfig().options().copyDefaults(true);
		EffectLib lib = EffectLib.instance();
		effectManager = new EffectManager(lib);
		saveConfig();

		board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		obj = board.registerNewObjective("master", "dummy");
		thiefTeam = board.registerNewTeam("thieves");
		guardTeam = board.registerNewTeam("guards");

		obj.setDisplayName(ChatColor.GOLD + "--*+Thief+*--");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		artifactsScore = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + "Artifacts:"));
		artifactsScore.setScore(0);

		thiefTeam.setCanSeeFriendlyInvisibles(true);
		thiefTeam.setAllowFriendlyFire(false);
		guardTeam.setCanSeeFriendlyInvisibles(false);
		guardTeam.setAllowFriendlyFire(false);

		Teams.clearTeams();    

		for (Player p : Bukkit.getOnlinePlayers()) {
			//add player to team with less players


			if(p.getInventory().contains(Material.NETHER_STAR) == false){
				p.getInventory().addItem(kitSelector);
			}

			if(Teams.getGuards().size() < Teams.getThieves().size()) {
				Teams.addToTeam(TeamType.GUARD, p, thiefTeam, guardTeam);
			} else {
				Teams.addToTeam(TeamType.THIEF, p, thiefTeam, guardTeam);
			}

			// If enough players joined start game
			if(Bukkit.getOnlinePlayers().length >= Integer.parseInt(getConfig().getString("Playerstostart"))) {
				startGame();
			}

			p.setScoreboard(board);
		}

	}

	public void onDisable() {
		Teams.clearTeams();
		thiefTeam.unregister();
		guardTeam.unregister();
		obj.unregister();
		effectManager.dispose();
		HandlerList.unregisterAll((Listener) this);
		saveConfig();
		for(Player player : Bukkit.getOnlinePlayers()){
			player.getInventory().clear();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		
			sqlf.createPlayer(player);
		
		
		player.sendMessage("§6Welcome to thief");

		player.setScoreboard(board);

		//add player to team with less players
		if(Teams.getGuards().size() < Teams.getThieves().size()) {
			Teams.addToTeam(TeamType.GUARD, player, thiefTeam, guardTeam);
		} else {
			Teams.addToTeam(TeamType.THIEF, player, thiefTeam, guardTeam);
		}

		//Add nether star to players inventory if they do now have one
		if(player.getInventory().contains(Material.NETHER_STAR) == false){
			player.getInventory().addItem(kitSelector);
		}

		// If enough players joined start game
		if(Bukkit.getOnlinePlayers().length >= Integer.parseInt(getConfig().getString("Playerstostart"))) {
			startGame();
		}

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e ){
		Player player = (Player) e.getPlayer();
		player.getInventory().remove(Material.NETHER_STAR);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.isOp() == true) {
				if(commandLabel.equalsIgnoreCase("thief")){
					if(args.length == 0){

						player.sendMessage(ChatColor.RED + "Specifiy another command!");
						return true;
					}

					if (args[0].equalsIgnoreCase("setthievesspawn")) {

						getConfig().set("Thieves.x",
								player.getLocation().getBlockX());
						getConfig().set("Thieves.y",
								player.getLocation().getBlockY());
						getConfig().set("Thieves.z",
								player.getLocation().getBlockZ());
						saveConfig();
						player.sendMessage(ChatColor.GOLD + "Thieves spawn set");

						return true;

					} else if (args[0].equalsIgnoreCase("setguardsspawn")) {

						getConfig().set("Guards.x",
								player.getLocation().getBlockX());
						getConfig().set("Guards.y",
								player.getLocation().getBlockY());
						getConfig().set("Guards.z",
								player.getLocation().getBlockZ());
						saveConfig();
						player.sendMessage(ChatColor.GOLD + "Guards spawn set");

						return true;

					} else if (args[0].equalsIgnoreCase("addartifact")) {

						int newArtifact = getConfig().getInt("NewestArtifact") + 1;
						getConfig().set("Artifact." + newArtifact + ".x",
								player.getLocation().getBlockX());
						getConfig().set("Artifact." + newArtifact + ".y",
								player.getLocation().getBlockY());
						getConfig().set("Artifact." + newArtifact + ".z",
								player.getLocation().getBlockZ());
						getConfig().set("NewestArtifact", newArtifact++);
						saveConfig();
						player.sendMessage("Added artifact spawn point at feet");

						return true;

					} else if (args[0].equalsIgnoreCase("spawnartifact")) {

						int thievesX = getConfig().getInt("Artifact.1.x");
						int thievesY = getConfig().getInt("Artifact.1.y");
						int thievesZ = getConfig().getInt("Artifact.1.z");
						Location artifact = new Location(player.getWorld(), thievesX, thievesY, thievesZ);
						Block artifactBlock = player.getWorld().getBlockAt(artifact);
						artifactBlock.setType(Material.DRAGON_EGG);

						return true;

					}

					else {
						player.sendMessage(ChatColor.RED + "Not a valid command!");
						return true;
					}


				}
			}
		}
		return true;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (gameBegun == true) {
			Location location = player.getLocation();
			Location standingOn = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());
			// check if player moves a block
			if((event.getTo().getBlockX() != event.getFrom().getBlockX()) || (event.getTo().getBlockY() != event.getFrom().getBlockY()) || (event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {

				// remove invisibility from players on movement into the light
				if(player.getLocation().getBlock().getLightLevel() > 5){
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}

				// default sneak chance is 1 in 3 this is modified by sneaking or sprinting, may also add skill points to reduce the chance
				int sneakChance = 3;
				if (player.isSneaking() == true) {
					sneakChance = 6; // a sneaking player has less chance of creaking
				} else if (player.isSprinting()) {
					sneakChance = 2; // a sprinting player has more chance of making noise
				}

				//if player is standing on logs do random creaks
				if (0 + (int)(Math.random() * ((sneakChance - 0) + sneakChance)) == 1) {
					if(standingOn.getBlock().getType() == Material.LOG) {
						if (0 + (int)(Math.random() * ((1 - 0) + 1)) == 0) {
							location.getWorld().playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5F, 0.0F);
						} else {
							location.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.5F, 0.0F);
						}
					}
				} 

			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Projectile) {
			Projectile projectile = (Projectile)entity;
			//if the projectile is an egg get the entities within 3 blocks and give them blindness where the projectile hits
			if(projectile instanceof Egg) {
				for(Entity e : projectile.getNearbyEntities(2, 2, 2)) {
					if (e instanceof Player) {
						Player player = (Player) e;
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,0));
					}
				}
				//play a smoke a firework sound where the projectile hits
				projectile.getWorld().playEffect(projectile.getLocation(), Effect.SMOKE, 1); 
				projectile.getWorld().playSound(projectile.getLocation(), Sound.FIREWORK_TWINKLE, 0.1F, 1F);
			}
		}

	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		//stop thrown eggs from hatching chickens
		if (event.getSpawnReason() == SpawnReason.EGG)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();


		ItemStack i = player.getItemInHand();
		ItemMeta meta = i.getItemMeta();
		String name = meta.getDisplayName();
		if (name != null && name.equalsIgnoreCase("Kit Selector")) {
			if(i.getType().equals(Material.NETHER_STAR)){

				if(Teams.isThief(player) == true){

					player.openInventory(Items.createClassMenu("Thief"));
				}

				if(Teams.isThief(player) == false){
					player.openInventory(Items.createClassMenu("Guard"));
				}


			}
		}


		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.DRAGON_EGG) {
				event.setCancelled(true);
				//check player is a thief
				// if (Teams.isThief(player) == true) {

				player.sendMessage("You have picked up an artifact!");

				ItemStack artifact = Items.createArtifact();
				if (player.getInventory().contains(artifact) != true) {
					event.getClickedBlock().setType(Material.AIR); //Remove Dragon Egg
					player.getInventory().addItem(new ItemStack(artifact)); //Give thief dragon egg item
					player.updateInventory();
				} else {
					player.sendMessage("You are already holding an artifact, you should capture this first before getting another!");
				}
			}
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item.getType() == Material.BLAZE_POWDER) {
				HelixLocationEffect helixEffect = new HelixLocationEffect(effectManager, player.getLocation());
				helixEffect.radius = 5;
				helixEffect.period = 5 * 20;
				helixEffect.iterations = 1;
				if (checkCooldown(fireOfRevealing, player, 20, "fire of revealing") == true) {
					helixEffect.start();
					player.getWorld().playSound(player.getLocation(), Sound.FIRE, 1F, 1F);
					fireOfRevealing.put(player.getName(), System.currentTimeMillis());
					for(Entity e : player.getNearbyEntities(5, 5, 5)) {
						if (e instanceof Player) {
							Player target = (Player) e;
							if (target.hasPotionEffect(PotionEffectType.INVISIBILITY) == true) {
								target.sendMessage(ChatColor.GOLD + player.getName() + " has made you visible!");
							}
							target.removePotionEffect(PotionEffectType.INVISIBILITY);
							shadowMeldCooldown.put(player.getName(), System.currentTimeMillis());
						}
					}
				}

			} else if (item.getType() == Material.COAL) {
				Location location = player.getLocation();

				if (checkCooldown(shadowMeldCooldown, player, 10, "shadowmeld") == true) {
					if(Teams.isThief(player) == true) { //only give thieves invisibility
						//if player is in the dark hide them
						if(player.getLocation().getBlock().getLightLevel() < 6) {
							//remove potion to reset timer
							player.removePotionEffect(PotionEffectType.INVISIBILITY);
							//re-add potion effect to player
							player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,400,0));
							location.getWorld().playEffect(location, Effect.SMOKE, 1);
							player.sendMessage(ChatColor.GOLD + "You have shadowmelded!");
							shadowMeldCooldown.put(player.getName(), System.currentTimeMillis());
						} else {
							player.sendMessage(ChatColor.GOLD + "It is too light to shadowmeld here!");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getPlayer();
		
		
			Player pl = (Player) event.getEntity().getKiller();
			sqlf.addKill(pl);
			sqlf.addDeath(player);
		if (player.getInventory().contains(Material.DRAGON_EGG)) {
			int artifacts = itemsInInventory(player.getInventory(), Material.DRAGON_EGG);
			if (artifacts>0) {
				if (artifacts == 1) {
					Bukkit.broadcastMessage(player.getName() +" had " + artifacts + " artifact");
				} else {
					Bukkit.broadcastMessage(player.getName() +" had " + artifacts + " artifacts");
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		Location blockPlaced = event.getBlock().getLocation();
		Location placedOn = blockPlaced;
		placedOn.setY(blockPlaced.getY()-1);
		Block block = placedOn.getBlock();
		if (block.getType() == Material.BEDROCK && event.getBlockPlaced().getType() == Material.DRAGON_EGG) {
			artifactsScore.setScore(artifactsScore.getScore() + 1);
			event.setCancelled(true);
			ItemStack artifact = Items.createArtifact();

			if (player.getInventory().contains(artifact)){
				player.getInventory().remove(artifact);
				player.updateInventory();
				Bukkit.broadcastMessage("The thieves have captured an artifact!");
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;
		event.setCancelled(true);

	}

	public int itemsInInventory(Inventory inventory, Material... search) {
		List<Material> wanted = Arrays.asList(search);
		int found = 0;

		for(ItemStack item : inventory.getContents()) {
			if(item != null && wanted.contains(item.getType()))
				found += item.getAmount();
		}

		return found;
	}

	public boolean checkCooldown(HashMap<String, Long> cooldowns, Player player, int cooldownTime, String action) {
		if(cooldowns.containsKey(player.getName())) {
			long secondsLeft = ((cooldowns.get(player.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
			if(secondsLeft>0) {
				// Still cooling down
				player.sendMessage(ChatColor.GOLD + "You cannot use " + action + " for another "+ secondsLeft +" seconds!");
				return false;
			}
		}
		// No cooldown found or cooldown has expired, save new cooldown
		return true;
	}

	public void startGame() {
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if(countdowntimer != -1) {
					if(countdowntimer != 0) {
						if (countdowntimer == 30) {
							Bukkit.broadcastMessage(ChatColor.GOLD + "Game starting in " + countdowntimer + " seconds");
						} else if (countdowntimer == 15) {
							Bukkit.broadcastMessage(ChatColor.GOLD + "Game starting in " + countdowntimer + " seconds");
						} else if (countdowntimer <= 5) {
							Bukkit.broadcastMessage(ChatColor.GOLD + "Game starting in " + countdowntimer + " seconds");
						}
						countdowntimer--;
					} else {
						Bukkit.broadcastMessage(ChatColor.GOLD + "Let the game begin!");



						int thievesX = getConfig().getInt("Thieves.x");
						int thievesY = getConfig().getInt("Thieves.y");
						int thievesZ = getConfig().getInt("Thieves.z");

						int guardsX = getConfig().getInt("Guards.x");
						int guardsY = getConfig().getInt("Guards.y");
						int guardsZ = getConfig().getInt("Guards.z");

						// move all thieves to their base
						for(String thieves : Teams.getThieves()) {
							Player player = Bukkit.getServer().getPlayer(thieves);
							player.sendMessage(ChatColor.GOLD + "Teleported to theives base");
							player.teleport(new Location(player.getWorld(), thievesX, thievesY, thievesZ));
							giveKits(player);
							if(player.getInventory().contains(Material.NETHER_STAR)){
								player.getInventory().remove(Material.NETHER_STAR);
							}
						}

						// move all guards to their base
						for(String guards : Teams.getGuards()) {
							Player player = Bukkit.getServer().getPlayer(guards);
							Bukkit.getServer().getPlayer(guards).sendMessage(ChatColor.GOLD + "Teleported to guards base");
							giveKits(player);

							player.teleport(new Location(player.getWorld(), guardsX, guardsY, guardsZ));

							if(player.getInventory().contains(Material.NETHER_STAR)){
								player.getInventory().remove(Material.NETHER_STAR);
							}

						}
						gameBegun = true;
						countdowntimer--; // set count down timer to -1 so the scheduler does not run any further.
					}
				}
			}

		}, 200L, 20L);

	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event)
	{
		Player player = (Player)event.getWhoClicked();

		if (event.getInventory().getTitle().contains("Pick Your Guard Class")) {
			event.setCancelled(true);
			player.updateInventory();
			player.closeInventory();
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory destInvent = e.getInventory();
		Integer slotClicked = e.getRawSlot();
		if( slotClicked < destInvent.getSize() ) {
			// slot clicked was in the remote container, not in the player
			String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
			if (e.getInventory().getTitle().equalsIgnoreCase("Pick Your Guard Class") || e.getInventory().getTitle().equalsIgnoreCase("Pick Your Thief Class")) {
				if (e.getCurrentItem().getItemMeta() == null) return;			
				//Guards
				if (itemName.contains("Guard")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Standard Guard kit!");
					selectedKit.put(p.getName(), "Default");
					p.closeInventory();
					return;
				} else if (itemName.contains("Scout")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Scout kit!");
					selectedKit.put(p.getName(), "Scout");
					p.closeInventory();
					return;
				} else if (itemName.contains("Heavy")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Heavy kit!");
					selectedKit.put(p.getName(), "Heavy");
					p.closeInventory();
					return;
				} else if (itemName.contains("Mage")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Mage kit!");
					p.closeInventory();
					selectedKit.put(p.getName(), "Mage");
					return;
				} else if (itemName.contains("Smoke")) { //Thieves
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Smoke kit!");
					selectedKit.put(p.getName(), "Smoke");
					p.closeInventory();
					return;
				} else if (itemName.contains("Thief")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Standard Thief kit!");
					selectedKit.put(p.getName(), "Default");
					p.closeInventory();
					return;
				} else if (itemName.contains("Shadow Step")) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.GREEN + "You selected the Blink kit!");
					selectedKit.put(p.getName(), "Blink");
					p.closeInventory();
					return;
				}
			}
		} else {
			// slot clicked was in the player, not the remote container
		}

	}

	public void giveKits(Player p){

		if(selectedKit.get(p.getName()) == "Scout"){
			p.getInventory().addItem(Items.createThiefDagger("dash"));
			p.sendMessage(ChatColor.BLUE + "Kit recieved!");

			return;

		} else if(selectedKit.get(p.getName()) == "Smoke"){
			p.getInventory().addItem(Items.createThiefDagger(null));
			p.getInventory().addItem(Items.createEgg(16));
			p.sendMessage(ChatColor.BLUE + "Kit recieved!");

			return;

		} else if(selectedKit.get(p.getName()) == "Blink"){
			p.getInventory().addItem(Items.createThiefDagger("shadow step"));
			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;

		} else if(selectedKit.get(p.getName()) == "Scout"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;

		} else if(selectedKit.get(p.getName()) == "Heavy"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;

		} else if(selectedKit.get(p.getName()) == "Mage"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;

		} else {

			if (Teams.isThief(p) == true) {
				p.getInventory().addItem(Items.createThiefDagger("special ability"));
			}
			p.sendMessage(ChatColor.RED + "No kit selected, applying default.");

			return;

		}

	}



}
