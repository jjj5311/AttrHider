package com.civpvp.attrhider;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.*;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;

/**
 * Uses ProtocolLib to strip away stuff that should never have been sent in the first place
 * such as enchantment, durability and potion duration information.
 * @author Squeenix
 *
 */
public class AttrHider extends JavaPlugin {
	private ProtocolManager protocolManager;

	@Override
	public void onEnable() {
	    registerPacketListeners();
	}
	
	private void registerPacketListeners(){
		protocolManager = ProtocolLibrary.getProtocolManager();
	    //Strips armour 
	    protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT){
	    	@Override
	    	public void onPacketSending(PacketEvent e){
	    		try{
		    		PacketContainer p = e.getPacket();
		    		StructureModifier<ItemStack> items = p.getItemModifier();
		    		ItemStack i = items.read(0);
		    		if(i!=null){
		    			adjustEnchantment(i);
		    			items.write(0, i);
		    		}
		    		
	    		} catch (FieldAccessException exception){ //Should catch if the packet is the wrong type
	    			exception.printStackTrace();
	    		}
	    	}
	    });
	    
	    //Strips potion duration length and sets it to 420 ticks so you can blaze it
	    protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EFFECT){
	    	@Override
	    	public void onPacketSending(PacketEvent e){
	    		try{
		    		PacketContainer p = e.getPacket();
		    		if(e.getPlayer().getEntityId()!=p.getIntegers().read(0)){ //Make sure it's not the player
		    			p.getShorts().write(0, (short)420);
		    		}
		    		
	    		} catch (FieldAccessException exception){ 
	    			exception.printStackTrace();
	    		}
	    	}
	    });
	}
	
	private ItemStack adjustEnchantment(ItemStack i){
		if(i!=null){
			Material type = i.getData().getItemType();
			/* Only applying to commonly enchanted items because 
			 * Items such as potions and wood rely on damage values for appearance
			 */
			if(type == Material.DIAMOND_HELMET 
			|| type == Material.DIAMOND_CHESTPLATE
			|| type == Material.DIAMOND_LEGGINGS
			|| type == Material.DIAMOND_BOOTS
			|| type == Material.IRON_HELMET 
			|| type == Material.IRON_CHESTPLATE
			|| type == Material.IRON_LEGGINGS
			|| type == Material.IRON_BOOTS
			|| type == Material.GOLD_HELMET 
			|| type == Material.GOLD_CHESTPLATE
			|| type == Material.GOLD_LEGGINGS
			|| type == Material.GOLD_BOOTS
			|| type == Material.LEATHER_HELMET 
			|| type == Material.LEATHER_CHESTPLATE
			|| type == Material.LEATHER_LEGGINGS
			|| type == Material.LEATHER_BOOTS
			|| type == Material.DIAMOND_SWORD
			|| type == Material.GOLD_SWORD
			|| type == Material.IRON_SWORD
			|| type == Material.STONE_SWORD
			|| type == Material.WOOD_SWORD
			|| type == Material.DIAMOND_AXE
			|| type == Material.GOLD_AXE
			|| type == Material.IRON_AXE
			|| type == Material.STONE_AXE
			|| type == Material.WOOD_AXE
			|| type == Material.DIAMOND_PICKAXE
			|| type == Material.GOLD_PICKAXE
			|| type == Material.IRON_PICKAXE
			|| type == Material.STONE_PICKAXE
			|| type == Material.WOOD_PICKAXE
			|| type == Material.DIAMOND_SPADE
			|| type == Material.GOLD_SPADE
			|| type == Material.IRON_SPADE
			|| type == Material.STONE_SPADE
			|| type == Material.WOOD_SPADE){
				Object[] copy = i.getEnchantments().keySet().toArray();
			
				for(Object enchantment : copy){
					i.removeEnchantment((Enchantment)enchantment);
				}
				i.setDurability((short)1);
				if(copy.length>0){
					i.addEnchantment(Enchantment.DURABILITY, 1);
				}
			}
		}
		return i;
	}
}
