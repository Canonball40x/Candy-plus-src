package cc.candy.candymod.module.misc;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MathUtil;
import cc.candy.candymod.utils.Timer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class Spammer extends Module {
    public Setting<Float> delay = register(new Setting("Delay" , 50.0F , 100.0F , 1.0F));
    public Setting<type> spam = register(new Setting("Type" , type.chinese));
    public Setting<Boolean> suffix = register(new Setting("Suffix" , true));
    public Timer timer = new Timer();
    public enum type {
        chinese ,
        korean ,
        amongus
    }

    public final String chinese = "\u8FD9\u662F\u4E00\u9996\u7231\u60C5\u4E4B\u6B4C\u5417\uFF1F \u6216\u8005\u662F\u4E00\u9996\u5173\u4E8E\u68A6\u60F3\u7684\u6B4C\uFF1F\u65E0\u8BBA\u600E\u6837\uFF0C\u4ED6\u4EEC\u90FD\u5F88\u96BE\u8FFD\u4E0A\u3002 \u800C\u6211\u4EEC\u5219\u7EE7\u7EED\u5F98\u5F8A\u3002 \u68A6\u60F3\u603B\u662F\u5728\u540E\u9762\u3002 \u53EA\u6709\u5F53\u6211\u4EEC\u8FFD\u4E0A\u4ED6\u4EEC\u65F6\uFF0C\u6211\u4EEC\u624D\u80FD\u4ECE\u6B63\u9762\u770B\u5230\u4ED6\u4EEC\u3002 \u53EA\u6709\u5230\u90A3\u65F6\uFF0C\u6211\u4EEC\u624D\u610F\u8BC6\u5230\u8FD9\u5F20\u8138\u662F\u6211\u4EEC\u81EA\u5DF1\u7684\u3002 \"\u7EAF\u6D01\u5C31\u50CF\u73AB\u7470\uFF0C\u7F8E\u4E3D\u800C\u523A\u773C\u3002 \u51C0\u5316\u4F60\u7684\u68A6\u60F3 ............. \u4F60\u7684\u8FFD\u6355\u884C\u52A8\u5C06\u6301\u7EED\u591A\u4E45\uFF1F";
    public final String korean = "\uD480 \uCFE4 \uAE30 \uC8FC \uCFC4 \uB450\uB974 \uACE0 \uCE5C \uBA48\uCE6B \uBD80\uC0C1 \uD751\uC778 \uD3F0 \uB378 \uB974 \uC751\uC6A9 \uC138 \uC6B0\uACE0 \uD2B8 \uB8CC\uCF00 \uAC00\uC2A4 \uB2E4\uC774\uB8E8 \uC9C4\uC758 \uC815 \uB9CC\uB370 \uC624\uC608 \uD06C\uB8E8 \uACE0 \uAC00\uC2A4 \uB208\uCFE0 \uBAA8\uC2A4 \uBB38 \uC1A1 \uC2B9\uAE30 \uC870\uC544\uC138\u3002\uAD6D\uB3C4 \uCD94\uC99D \uBC14\uB2E4\uC5D0\uC11C \uAC00\uC2DC \uAD11\uC120\uC774 \uC6C3\uC74C. \uBE44\uC2A4\uB4EC\uD788 \uB4A4\uCABD\uC758 \uC5F4\uBCD1\uC740 \uD1F4\uACE0\uB97C \uAC70\uB4ED\uD55C\uB2E4.\n";
    public final String[] amongus = new String[]{
            "\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u28C0\u28C0\u28D0\u2840\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804\u2804" +
                    "\u2804\u2804\u28A0\u2804\u28E0\u28F6\u28FF\u28FF\u28FF\u283F\u283F\u28DB\u28C2\u28C0\u28C0\u2852\u2836\u28F6\u28E4\u28E4\u28EC\u28C0\u2840\u2804\u2880\u2804\u2804\u2804\u2804\u2804\u2804\u2804" +
                    "\u2804\u2804\u2880\u28FE\u28FF\u28FF\u28FF\u285F\u28A1\u28BE\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28F6\u28CC\u283B\u28FF\u28FF\u28FF\u28FF\u28F7\u28E6\u28C4\u2840\u2804\u2804\u2804\u2804\u2804" +
                    "\u2804\u2804\u28C8\u28C9\u285B\u28FF\u28FF\u28FF\u284C\u2887\u28BB\u28FF\u28FF\u28FF\u28FF\u28FF\u283F\u281B\u28E1\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28E6\u28C4\u2804\u2804\u2804" +
                    "\u2804\u283A\u281F\u28C9\u28F4\u287F\u281B\u28E9\u28FE\u28CE\u2833\u283F\u281B\u28CB\u28E9\u28F4\u28FE\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28C6\u2804\u2804" +
                    "\u2804\u2804\u2804\u2818\u288B\u28F4\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u2846\u2804" +
                    "\u2804\u2804\u2880\u2880\u28FE\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u2847\u2804" +
                    "\u2804\u2804\u2804\u28FE\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u2803\u28C0" +
                    "\u2804\u2804\u2804\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u287F\u2803\u2818\u281B" +
                    "\u2804\u2804\u2804\u28BB\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u281F\u280B\u28C0\u28C0\u28E0\u28E4" +
                    "\u2804\u2804\u28C0\u28C0\u2859\u283B\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u28FF\u283F\u289B\u28E9\u2824\u283E\u2804\u281B\u280B\u2809\u2889" +
                    "\u2804\u283A\u283F\u281B\u281B\u2803\u2804\u2809\u2819\u281B\u281B\u283B\u283F\u283F\u283F\u281F\u281B\u281B\u281B\u2809\u2801\u2804\u2804\u28C0\u28C0\u28E0\u28E4\u28E0\u28F4\u28F6\u28FC\u28FF"
    };

    public int index = 0;

    public Spammer(){
        super("Spammer" , Categories.MISC , false , false);
    }

    public void onEnable(){
        index = 0;
    }

    public void onUpdate(){
        if(timer == null) timer = new Timer();
        if(timer.passedDms(delay.getValue())){
            timer.reset();

            String msg = "";
            if(spam.getValue() == type.chinese){
                msg += chinese;
            }
            if(spam.getValue() == type.korean){
                msg += korean;
            }
            if(spam.getValue() == type.amongus){
                msg += amongus[index];
                index++;
                if(amongus.length <= index) index = 0;
            }

            if(suffix.getValue()) msg += MathUtil.getRandom(1000 , 10000);

            mc.player.connection.sendPacket(new CPacketChatMessage(msg));
        }
    }
}
