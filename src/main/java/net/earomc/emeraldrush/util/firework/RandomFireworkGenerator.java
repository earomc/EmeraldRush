package net.earomc.emeraldrush.util.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomFireworkGenerator {

    private static final List<Color> NATURAL_FIREWORK_COLORS = Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.SILVER, Color.WHITE);

    public FireworkMeta randomizeMeta(FireworkMeta meta) {
        meta.setPower(randomPower());
        int randomAmountOfEffects = random().nextInt(1, 5);
        for (int i = 0; i < randomAmountOfEffects; i++) {
            meta.addEffect(randomEffect());
        }
        return meta;
    }

    private int randomPower() {
        return random().nextInt(1, 3);
    }

    private FireworkEffect randomEffect() {
        return FireworkEffect.builder()
                .withColor(randomColorsSubList())
                .withFade(randomColorsSubList())
                .flicker(random().nextBoolean())
                .trail(random().nextBoolean())
                .build();
    }

    private List<Color> randomColorsSubList() {
        List<Color> colors = RandomFireworkGenerator.NATURAL_FIREWORK_COLORS;
        List<Color> ret = new ArrayList<>(colors.size());
        int pickAmount = random().nextInt(1, colors.size() + 1);// how many colors to pick from the colors list. At least one up to the full colors.size. Bound is exclusive, that's why we add 1.
        for (int i = 0; i < pickAmount; i++) {
            Color pick = colors.get(random().nextInt(colors.size()));
            if (!ret.contains(pick))
                ret.add(pick);
        }
        return ret;
    }

    private ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

}
