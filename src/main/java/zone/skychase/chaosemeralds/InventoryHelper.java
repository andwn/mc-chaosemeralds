package zone.skychase.chaosemeralds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryHelper {
    public static Boolean playerHasItem(PlayerEntity player, String itemName) {
        for(ItemStack stack : player.inventory.main) {
            if(stack.getItem().getTranslationKey().equals(itemName)) return true;
        }
        return false;
    }

    public static int playerItemIndex(PlayerEntity player, String itemName) {
        for(ItemStack stack : player.inventory.main) {
            if(stack.getItem().getTranslationKey().equals(itemName)) {
                return player.inventory.main.indexOf(stack);
            }
        }
        return -1;
    }

    public static int playerTotalItem(PlayerEntity player, String itemName) {
        int total = 0;
        for(ItemStack stack : player.inventory.main) {
            if(stack.getItem().getTranslationKey().equals(itemName)) {
                total += stack.getCount();
            }
        }
        return total;
    }
}
