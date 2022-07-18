package cc.candy.candymod.module.misc;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FakePlayer extends Module {
    public Setting<Boolean> copyInv = register(new Setting("CopyInv" , true));
    private final String name = "Hiyokomame844";
    private EntityOtherPlayerMP _fakePlayer;

    public static String getUuid(String name) {
        JsonParser parser = new JsonParser();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if (UUIDJson.isEmpty()) {
                return "invalid name";
            }
            JsonObject UUIDObject = (JsonObject) parser.parse(UUIDJson);
            return FakePlayer.reformatUuid(UUIDObject.get("id").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private static String reformatUuid(String uuid) {
        String longUuid = "";
        longUuid = longUuid + uuid.substring(1, 9) + "-";
        longUuid = longUuid + uuid.substring(9, 13) + "-";
        longUuid = longUuid + uuid.substring(13, 17) + "-";
        longUuid = longUuid + uuid.substring(17, 21) + "-";
        longUuid = longUuid + uuid.substring(21, 33);
        return longUuid;
    }

    public FakePlayer()
    {
        super("FakePlayer" , Categories.MISC , false , false);
    }

    //summon fake player
    @Override
    public void onEnable()
    {
        if (mc.player == null) {
            this.disable();
            return;
        }
        this._fakePlayer = null;
        if (mc.player != null) {
            this._fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03"), this.name));
            this._fakePlayer.copyLocationAndAnglesFrom(mc.player);
            this._fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            if(copyInv.getValue()) this._fakePlayer.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-100, this._fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        if (mc.world != null && mc.player != null) {
            super.onDisable();
            try {
                mc.world.removeEntity(this._fakePlayer);
            }
            catch(Exception exception) {}
        }
    }
}
