package com.newbreedgaming.newbreedthief;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class Teams {

	private static List<String> thieves = new ArrayList<String>();
	private static List<String> guards = new ArrayList<String>();
	 
	  public static void addToTeam(TeamType type, Player player, Team thiefTeam, Team guardTeam) {
		  if(isInTeam(player)) {
			  player.sendMessage("You are already in a team!");
			  return;
		  }
		  switch (type) {
		  case THIEF:
			  thieves.add(player.getName());
			  thiefTeam.addPlayer(Bukkit.getOfflinePlayer(player.getName()));
			  break;
		  case GUARD:
			  guards.add(player.getName());
			  guardTeam.addPlayer(Bukkit.getOfflinePlayer(player.getName()));
			  break;
		  }
		  player.sendMessage(ChatColor.GOLD + "Added to " + type.name()+ " team!");
	  }
	  
	  public static boolean isInTeam(Player player) {
		  return thieves.contains(player.getName()) || guards.contains(player.getName());
	  }
	  
	  public static boolean isThief(Player player) {
		  return thieves.contains(player.getName());
	  }
	  
	  public static void clearTeams() {
		 thieves.clear(); 
		 guards.clear();
	  }
	  
	  public static List<String> getThieves() {
		  return thieves;
	  }
	  
	  public static List<String> getGuards() {
		  return guards;
	  }
	  
	  public static List<String> getAllPlayersInTeams() {
		  List<String> combinedTeams = new ArrayList<String>();
		  combinedTeams.addAll(thieves);
		  combinedTeams.addAll(guards);
		  return combinedTeams;
	  }
	  
	  public static TeamType getTeamType(Player player) {
		  if(!isInTeam(player)){
			  return null;
		  }
		  return (thieves.contains(player.getName()) ? TeamType.THIEF : TeamType.GUARD);
	  }
}
