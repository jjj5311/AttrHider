package com.civpvp.attrhider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AttrHider
  extends JavaPlugin
  implements Listener
{
  private ProtocolManager protocolManager;
  
  public void onEnable()
  {
    registerPacketListeners();
    Bukkit.getPluginManager().registerEvents(this, this);
  }
  
  private void registerPacketListeners()
  {
    this.protocolManager = ProtocolLibrary.getProtocolManager();
    
    this.protocolManager.addPacketListener(new PacketAdapter(this, new PacketType[] { PacketType.Play.Server.ENTITY_EQUIPMENT })
    {
      public void onPacketSending(PacketEvent e)
      {
        try
        {
          PacketContainer p = e.getPacket();
          StructureModifier<ItemStack> items = p.getItemModifier();
          ItemStack i = (ItemStack)items.read(0);
          if (i != null)
          {
            AttrHider.this.adjustEnchantment(i);
            items.write(0, i);
          }
        }
        catch (FieldAccessException exception)
        {
          exception.printStackTrace();
        }
      }
    });
    this.protocolManager.addPacketListener(new PacketAdapter(this, new PacketType[] { PacketType.Play.Server.ENTITY_EFFECT })
    {
      public void onPacketSending(PacketEvent e)
      {
        try
        {
          PacketContainer p = e.getPacket();
          if (e.getPlayer().getEntityId() != ((Integer)p.getIntegers().read(0)).intValue()) {
            p.getShorts().write(0, Short.valueOf((short)420));
          }
        }
        catch (FieldAccessException exception)
        {
          exception.printStackTrace();
        }
      }
    });
    ProtocolLibrary.getProtocolManager().addPacketListener(
      new PacketAdapter(this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.ENTITY_METADATA })
      {
        public void onPacketSending(PacketEvent event)
        {
          try
          {
            Player observer = event.getPlayer();
            StructureModifier entityModifer = event.getPacket().getEntityModifier(observer.getWorld());
            Entity entity = (Entity)entityModifer.read(0);
            if ((entity != null) && (observer != entity) && ((entity instanceof LivingEntity)) && 
              ((!(entity instanceof EnderDragon)) || (!(entity instanceof Wither))) && (
              (entity.getPassenger() == null) || (entity.getPassenger() != observer)))
            {
              event.setPacket(event.getPacket().deepClone());
              StructureModifier watcher = event.getPacket()
                .getWatchableCollectionModifier();
              for (WrappedWatchableObject watch : (List)watcher.read(0)) {
                if ((watch.getIndex() == 6) && 
                  (((Float)watch.getValue()).floatValue() > 0.0F)) {
                  watch.setValue(
                    Float.valueOf(new Random().nextInt((int)((LivingEntity)entity).getMaxHealth()) + 
                    new Random().nextFloat()));
                }
              }
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      });
  }
  
  private ItemStack adjustEnchantment(ItemStack i)
  {
    if (i != null)
    {
      Material type = i.getData().getItemType();
      if ((type == Material.DIAMOND_HELMET) || 
        (type == Material.DIAMOND_CHESTPLATE) || 
        (type == Material.DIAMOND_LEGGINGS) || 
        (type == Material.DIAMOND_BOOTS) || 
        (type == Material.IRON_HELMET) || 
        (type == Material.IRON_CHESTPLATE) || 
        (type == Material.IRON_LEGGINGS) || 
        (type == Material.IRON_BOOTS) || 
        (type == Material.GOLD_HELMET) || 
        (type == Material.GOLD_CHESTPLATE) || 
        (type == Material.GOLD_LEGGINGS) || 
        (type == Material.GOLD_BOOTS) || 
        (type == Material.LEATHER_HELMET) || 
        (type == Material.LEATHER_CHESTPLATE) || 
        (type == Material.LEATHER_LEGGINGS) || 
        (type == Material.LEATHER_BOOTS) || 
        (type == Material.DIAMOND_SWORD) || 
        (type == Material.GOLD_SWORD) || 
        (type == Material.IRON_SWORD) || 
        (type == Material.STONE_SWORD) || 
        (type == Material.WOOD_SWORD) || 
        (type == Material.DIAMOND_AXE) || 
        (type == Material.GOLD_AXE) || 
        (type == Material.IRON_AXE) || 
        (type == Material.STONE_AXE) || 
        (type == Material.WOOD_AXE) || 
        (type == Material.DIAMOND_PICKAXE) || 
        (type == Material.GOLD_PICKAXE) || 
        (type == Material.IRON_PICKAXE) || 
        (type == Material.STONE_PICKAXE) || 
        (type == Material.WOOD_PICKAXE) || 
        (type == Material.DIAMOND_SPADE) || 
        (type == Material.GOLD_SPADE) || 
        (type == Material.IRON_SPADE) || 
        (type == Material.STONE_SPADE) || 
        (type == Material.WOOD_SPADE))
      {
        Object[] copy = i.getEnchantments().keySet().toArray();
        for (Object enchantment : copy) {
          i.removeEnchantment((Enchantment)enchantment);
        }
        i.setDurability((short)1);
        if (copy.length > 0) {
          i.addEnchantment(Enchantment.DURABILITY, 1);
        }
      }
    }
    return i;
  }
  
  @EventHandler
  public void onMount(final VehicleEnterEvent event)
  {
    if ((event.getEntered() instanceof Player)) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
      {
        public void run()
        {
          if ((event.getVehicle().isValid()) && (event.getEntered().isValid())) {
            AttrHider.this.protocolManager.updateEntity(event.getVehicle(), Arrays.asList(new Player[] { (Player)event.getEntered() }));
          }
        }
      });
    }
  }
}
