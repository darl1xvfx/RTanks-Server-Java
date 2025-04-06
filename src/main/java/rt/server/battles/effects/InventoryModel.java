package rt.server.battles.effects;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import rt.server.battles.BattleController;
import rt.server.battles.effects.effects.ArmorEffect;
import rt.server.battles.effects.effects.DoubleDamageEffect;
import rt.server.battles.effects.effects.Effect;
import rt.server.battles.effects.effects.HealthEffect;
import rt.server.battles.effects.effects.MineEffect;
import rt.server.battles.effects.effects.NitroEffect;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class InventoryModel {
	
	public BattleController controller;
	private boolean isHealthOnCooldown = false;
	private boolean isArmorOnCooldown = false;
	private boolean isDamageOnCooldown = false;
	private boolean isNitroOnCooldown = false;
	private boolean isMineOnCooldown = false;
	
	public InventoryModel(BattleController c) {
		this.controller = c;
	}
	
    public void init() {
    	if (this.controller.battle.getBattleEntity().withoutInventory) {
    		return;
    	};
    	new Command(Commands.InitInventory, JSON.parseInventory(Arrays.asList(JSON.parseInventoryItem(new HealthInventoryItem()), JSON.parseInventoryItem(new ArmrInventoryItem()), JSON.parseInventoryItem(new DoubleDamageInventoryItem()), JSON.parseInventoryItem(new NitroInventoryItem()), JSON.parseInventoryItem(new MineInventoryItem())))).send(this.controller.client);
    }

	public void onActivateItem(String id, boolean fromInventory, Vector3 pos) {
		Effect effect = null;
		if (id.equals("health") && this.isHealthOnCooldown) {
			return;
		}
		if (id.equals("armor") && this.isArmorOnCooldown) {
			return;
		}
		if (id.equals("double_damage") && this.isDamageOnCooldown) {
			return;
		}
		if (id.equals("nitro") && this.isNitroOnCooldown) {
			return;
		}
		if (id.equals("mine") && this.isMineOnCooldown) {
			return;
		}
		if (fromInventory) {
			this.activateCooldown(id);
		}
		if (this.controller.tank.isUsedEffect(id)) {
			this.controller.tank.removeUsedEffect(id);
		}
		switch (id) {
            case "health": {
    	        effect = new HealthEffect();
    	        break;
            }
            case "armor": {
    	        effect = new ArmorEffect();
    	        break;
            }
	        case "double_damage": {
	    	    effect = new DoubleDamageEffect();
	    	    break;
	        }
		    case "nitro": {
		    	effect = new NitroEffect();
		    	break;
		    }
	        case "mine": {
	    	    effect = new MineEffect();
	    	    break;
	        }
		}
		effect.activate(controller, fromInventory, pos);
    	new Command(Commands.ActivateItem, id).send(this.controller.client);
    	new Command(Commands.ChangeEffectTime, id, effect.getDurationTime()).send(this.controller.client);
		this.controller.battle.send2Battle(new Command(Commands.EnableEffect, this.controller.client.user.username, effect.getID(), effect.getDurationTime()));
	}

	private void activateCooldown(String id) {
		switch (id) {
           case "health": {
        	   new Command(Commands.ActivateCooldown, "mine", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "armor", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "double_damage", 15000).send(this.controller.client);
        	   this.isMineOnCooldown = true;
        	   this.isDamageOnCooldown = true;
        	   this.isArmorOnCooldown = true;
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
		           isMineOnCooldown = false;
		           isDamageOnCooldown = false;
		           isArmorOnCooldown = false;		
				}        		   
        	   }, 15000);
	           break;
           }
           case "armor": {
        	   new Command(Commands.ActivateCooldown, "nitro", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "armor", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "double_damage", 30000).send(this.controller.client);
        	   this.isNitroOnCooldown = true;
        	   this.isDamageOnCooldown = true;
        	   this.isArmorOnCooldown = true;
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
				   isNitroOnCooldown = false;
		           isArmorOnCooldown = false;		
				}        		   
        	   }, 15000);
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
		           isDamageOnCooldown = false;	
				}        		   
        	   }, 30000);
	           break;
           }
           case "double_damage": {
        	   new Command(Commands.ActivateCooldown, "nitro", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "armor", 30000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "double_damage", 15000).send(this.controller.client);
        	   this.isNitroOnCooldown = true;
        	   this.isDamageOnCooldown = true;
        	   this.isArmorOnCooldown = true;
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
				   isNitroOnCooldown = false;
				   isDamageOnCooldown = false;		
				}        		   
        	   }, 15000);
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
		           isArmorOnCooldown = false;	
				}        		   
        	   }, 30000);
    	       break;
           }
	       case "nitro": {
        	   new Command(Commands.ActivateCooldown, "nitro", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "armor", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "double_damage", 15000).send(this.controller.client);
        	   this.isNitroOnCooldown = true;
        	   this.isDamageOnCooldown = true;
        	   this.isArmorOnCooldown = true;
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
				   isNitroOnCooldown = false;
		           isDamageOnCooldown = false;
		           isArmorOnCooldown = false;		
				}        		   
        	   }, 15000);
	    	   break;
	       }
           case "mine": {
        	   new Command(Commands.ActivateCooldown, "health", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "armor", 15000).send(this.controller.client);
        	   new Command(Commands.ActivateCooldown, "double_damage", 15000).send(this.controller.client);
        	   this.isHealthOnCooldown = true;
        	   this.isDamageOnCooldown = true;
        	   this.isArmorOnCooldown = true;
        	   new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
				   isHealthOnCooldown = false;
		           isDamageOnCooldown = false;
		           isArmorOnCooldown = false;		
				}        		   
        	   }, 15000);
    	       break;
           }
	    }
	}
}
