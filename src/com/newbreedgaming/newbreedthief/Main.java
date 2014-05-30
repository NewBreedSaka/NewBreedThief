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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
	public HashMap<String, Long> fireOfRevealing = new HashMap<String, Long>();
	public HashMap<String, Long> shadowMeldCooldown = new HashMap<String, Long>();


	public HashMap<String, String> selectedKit = new HashMap<String, String>();
	
	
	//ArrayList<Player> thiefplayers;
	//ArrayList<Player> guardplayers;

	//ArrayList<Player> standardthiefitems;
	//ArrayList<Player> smokeitems;
	//ArrayList<Player> blinkitems;


	//ArrayList<Player> standardguarditems;
	//ArrayList<Player> scoutitems;
	//ArrayList<Player> heavyitems;
	//ArrayList<Player> mageitems;



	ItemStack kitselector;

	Inventory guardInv;
	Inventory thiefInv;
	ItemStack standard, scout, heavy, mage;
	ItemStack standard1, smoke, blink;

	Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		getConfig().options().copyDefaults(true);

		kitselector = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = kitselector.getItemMeta();
		im.setDisplayName("Kit Selector");
		im.setLore(Arrays.asList("Choose your kit!"));
		kitselector.setItemMeta(im);
		
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
			

			if(p.getInventory().contains(Material.NETHER_STAR)){
				p.getInventory().remove(Material.NETHER_STAR);
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

			if(player.getInventory().contains(Material.NETHER_STAR)){
				player.getInventory().remove(Material.NETHER_STAR);
			}
		
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage("§6Welcome to thief");

		player.setScoreboard(board);

		//add player to team with less players
		if(Teams.getGuards().size() < Teams.getThieves().size()) {
			Teams.addToTeam(TeamType.GUARD, player, thiefTeam, guardTeam);
			player.getInventory().addItem(kitselector);

		} else {
			Teams.addToTeam(TeamType.THIEF, player, thiefTeam, guardTeam);
			player.getInventory().addItem(kitselector);
		}

		// If enough players joined start game
		if(Bukkit.getOnlinePlayers().length >= Integer.parseInt(getConfig().getString("Playerstostart"))) {
			startGame();
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.isOp() == true) {
				if (commandLabel.equalsIgnoreCase("setthievesspawn")) {
					getConfig().set("Thieves.x",
							player.getLocation().getBlockX());
					getConfig().set("Thieves.y",
							player.getLocation().getBlockY());
					getConfig().set("Thieves.z",
							player.getLocation().getBlockZ());
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "Thieves spawn set");
					return true;
				} else if (commandLabel.equalsIgnoreCase("setguardsspawn")) {
					getConfig().set("Guards.x",
							player.getLocation().getBlockX());
					getConfig().set("Guards.y",
							player.getLocation().getBlockY());
					getConfig().set("Guards.z",
							player.getLocation().getBlockZ());
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "Gaurds spawn set");
					return true;
				} else if (commandLabel.equalsIgnoreCase("addartifact")) {
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
				} else if (commandLabel.equalsIgnoreCase("spawnartifact")) {
					int thievesX = getConfig().getInt("Artifact.1.x");
					int thievesY = getConfig().getInt("Artifact.1.y");
					int thievesZ = getConfig().getInt("Artifact.1.z");
					Location artifact = new Location(player.getWorld(), thievesX, thievesY, thievesZ);
					Block artifactBlock = player.getWorld().getBlockAt(artifact);
					artifactBlock.setType(Material.DRAGON_EGG);
					return true;
				}
			}
		}
		return false;
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

		guardInv = Bukkit.createInventory(null, 9, "Pick Your Guard Class"); 

		standard = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta im = standard.getItemMeta();
		im.setLore(Arrays.asList(ChatColor.DARK_PURPLE + " Normal armour, no access to walkways ability grants strength"));
		im.setDisplayName(ChatColor.AQUA + "StandardGuard");
		standard.setItemMeta(im);		


		scout = new ItemStack(Material.POTION);
		ItemMeta im1 = scout.getItemMeta();
		im1.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Use Thief pathways, and has dash ability"));
		im1.setDisplayName(ChatColor.AQUA + "Scout");
		scout.setItemMeta(im1);	

		heavy = new ItemStack(Material.BREAD);
		ItemMeta im2 = heavy.getItemMeta();
		im2.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Increase Health"));
		im2.setDisplayName(ChatColor.AQUA + "Heavy");
		heavy.setItemMeta(im2);	

		mage = new ItemStack(Material.FIRE);
		ItemMeta im3 = mage.getItemMeta();
		im3.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Use the fire of revealing spell to un-shadowmeld thieves"));
		im3.setDisplayName(ChatColor.AQUA + "Mage");
		mage.setItemMeta(im3);	


		guardInv.addItem(new ItemStack(standard));
		guardInv.addItem(new ItemStack(scout));
		guardInv.addItem(new ItemStack(heavy));
		guardInv.addItem(new ItemStack(mage));

		thiefInv = Bukkit.createInventory(null, 9, "Pick Your Thief Class"); 

		standard1 = new ItemStack(Material.FEATHER);
		ItemMeta im4 = standard1.getItemMeta();
		im4.setDisplayName(ChatColor.AQUA + "StandardThief");
		im4.setLore(Arrays.asList(ChatColor.DARK_PURPLE + " Backstabs Guards "));
		standard1.setItemMeta(im4);		


		smoke = new ItemStack(Material.EGG);
		ItemMeta im12 = smoke.getItemMeta();
		im12.setDisplayName(ChatColor.AQUA + "Smoke");
		im12.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Access to smoke eggs which blind guards to make killing easier"));
		smoke.setItemMeta(im12);	

		blink = new ItemStack(Material.COMPASS);
		ItemMeta im23 = blink.getItemMeta();
		im23.setDisplayName(ChatColor.AQUA + "Blink");
		im23.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "May jump several blocks towards the nearest guard it a teleportation style "));
		blink.setItemMeta(im23);	




		thiefInv.addItem(new ItemStack(standard1));
		thiefInv.addItem(new ItemStack(smoke));
		thiefInv.addItem(new ItemStack(blink));




		Player player = event.getPlayer();
		ItemStack item = event.getItem();


		ItemStack i = player.getItemInHand();
		ItemMeta meta = i.getItemMeta();
		String name = meta.getDisplayName();
		if (name != null && name.equalsIgnoreCase("Kit Selector")) {
			if(i.getType().equals(Material.NETHER_STAR)){


				if(Teams.isThief(player) == true){
					player.openInventory(thiefInv);
				}

				if(Teams.isThief(player) == false){
					player.openInventory(guardInv);
				}


			}
		}


		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.DRAGON_EGG) {
				event.setCancelled(true);
				//check player is a thief
				// if (Teams.isThief(player) == true) {

				player.sendMessage("You have picked up an artifact!");

				ItemStack artifact = createArtifact();
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
			ItemStack artifact = createArtifact();

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

	public ItemStack createArtifact() {
		ItemStack artifact = new ItemStack(Material.DRAGON_EGG, 1);
		ItemMeta artifactMeta = artifact.getItemMeta();
		artifactMeta.setDisplayName("§6Artifact");
		ArrayList<String> artifactLore = new ArrayList<String>();
		artifactLore.add("§7Get this back to the thieves guild!");
		artifactMeta.setLore(artifactLore);
		artifact.setItemMeta(artifactMeta);
		return(artifact);
	}

	public ItemStack createEgg(int amount) {
		ItemStack item = new ItemStack(Material.EGG, amount);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§5Blinding Bomb");
		ArrayList<String> itemLore = new ArrayList<String>();
		itemLore.add("§7Throw this at the guards to blind them!");
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return(item);
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
							if (selectedKit.containsKey(player) == false){
								selectedKit.put(player.getName(), "Default");
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
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		String iName = e.getCurrentItem().getItemMeta().getDisplayName();
		if (e.getInventory().getTitle().equalsIgnoreCase("Pick Your Guard Class") || e.getInventory().getTitle().equalsIgnoreCase("Pick Your Thief Class")) {
			if (e.getCurrentItem().getItemMeta() == null) return;
			
			//Guards
			if (iName.contains("Standard Guard")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Standard Guard kit!");
				selectedKit.put(p.getName(), "Default");
				p.closeInventory();
				return;
			} else if (iName.contains("Scout")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Scout kit!");
				selectedKit.put(p.getName(), "Scout");
				p.closeInventory();
				return;
			} else if (iName.contains("Heavy")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Heavy kit!");
				selectedKit.put(p.getName(), "Heavy");
				p.closeInventory();
				return;
			} else if (iName.contains("Mage")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Mage kit!");
				p.closeInventory();
				selectedKit.put(p.getName(), "Mage");
				return;
			} else if (iName.contains("Smoke")) { //Thieves
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Smoke kit!");
				selectedKit.put(p.getName(), "Smoke");
				p.closeInventory();
				return;
			} else if (iName.contains("StandardThief")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected StandardThief kit!");
				selectedKit.put(p.getName(), "Default");
				p.closeInventory();
				return;
			} else if (iName.contains("Blink")) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.GREEN + "You selected Blink kit!");
				selectedKit.put(p.getName(), "Blink");
				p.closeInventory();
				return;
			}

		}
	}

	public void giveKits(Player p){
		

		if(selectedKit.get(p) == "Scout"){
			ItemStack speeder = new ItemStack(Material.FEATHER);
			ItemMeta speederim = speeder.getItemMeta();
			speederim.setDisplayName(ChatColor.AQUA + "Speed Boost");
			speederim.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Access to dash , speed boost, jump boost"));
			speeder.setItemMeta(speederim); 
			p.getInventory().addItem(speeder);
			p.sendMessage(ChatColor.BLUE + "Kit recieved!");

			return;

		} else if(selectedKit.get(p) == "Smoke"){

			ItemStack smoker = new ItemStack(Material.EGG);
			ItemMeta smokerim = smoker.getItemMeta();
			smokerim.setDisplayName(ChatColor.AQUA + "Smoke Eggs");
			smokerim.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Blind Guards!"));
			smoker.setItemMeta(smokerim);
			p.getInventory().addItem(smoker);
			p.sendMessage(ChatColor.BLUE + "Kit recieved!");
			
			return;
			
		} else if(selectedKit.get(p) == "Blink"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");
			
			return;
			
		} else if(selectedKit.get(p) == "Scout"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");
			return;
		} else if(selectedKit.get(p) == "Heavy"){

			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;
		} else if(selectedKit.get(p) == "Mage"){
			p.sendMessage(ChatColor.RED + "Kit comming soon!");

			return;

		} else if (selectedKit.get(p) == "Default") {
			p.sendMessage(ChatColor.RED + "No kit selected, applying default.");
			return;
		}
		
	}
	
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e ){
		Player player = (Player) e.getPlayer();
		
			if(player.getInventory().contains(Material.NETHER_STAR)){
				player.getInventory().remove(Material.NETHER_STAR);
			}
		
	}

}
