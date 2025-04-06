package rt.server.garage.lootbox;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import rt.server.client.ClientEntity;

public class LootboxService {
	
    private static final Pattern EXCLUDED_PRIZE_REGEX = Pattern.compile("^paint_.+|.+_xt$");
	private static List<Prize> prizes = new ArrayList<>(Arrays.asList(
                new Prize("Пакет 3500 кристаллов", "COMMON", 66936, "crystals_3500"),
                new Prize("Набор 125 двойного урона", "COMMON", 66938, "doubledamage_125"),
                new Prize("Набор 125 повышенной защиты", "COMMON", 66937, "armor_125"),
                new Prize("Набор 125 ускорений", "COMMON", 66941, "n2o_125"),
                new Prize("Набор 125 мин", "COMMON", 66930, "mine_125"),
                new Prize("Пакет 10 000 кристаллов", "UNCOMMON", 66945, "crystals_10000"),
                new Prize("Набор 125 ремкомплектов", "UNCOMMON", 66943, "health_125"),
                new Prize("Комплект 100 всех припасов", "UNCOMMON", 66945, "allsupplies_100"),
                new Prize("Краска Луноход", "EPIC", 66945, "paint_moonwalker"),
                new Prize("Набор 5 золотых ящиков", "UNCOMMON", 66945, "goldboxes_5"),
                new Prize("Пакет 25 000 кристаллов", "RARE", 66945, "crystals_25000"),
                new Prize("3 дня премиум аккаунта", "UNCOMMON", 66945, "premiumdays_3"),
                new Prize("Набор 10 золотых ящиков", "RARE", 66945, "goldboxes_10"),
                new Prize("Комплект 250 всех припасов", "RARE", 66945, "allsupplies_250"),
                new Prize("Пакет 100 000 кристаллов", "EPIC", 66945, "crystals_100000"),
                new Prize("10 дней премиум аккаунта", "RARE", 66945, "premiumdays_10"),
                new Prize("Пакет 300 000 кристаллов", "LEGENDARY", 66945, "crystals_300000"),
                new Prize("Гром ХТ", "EXOTIC", 66945, "thunder_xt"),
                new Prize("Пакет 1 000 000 кристаллов", "EXOTIC", 66945, "crystals_1000000")
    ));

	private static Map<String, Double> probabilities = new HashMap<String, Double>() {{
	    put("COMMON", 0.50);
	    put("UNCOMMON", 0.34);
	    put("RARE", 0.10);
	    put("EPIC", 0.05);
	    put("LEGENDARY", 0.02);
	    put("EXOTIC", 0.01);
	}};

	private static Map<String, Integer> categoryOrder = new HashMap<String, Integer>() {{
	    put("COMMON", 1);
	    put("UNCOMMON", 2);
	    put("RARE", 3);
	    put("EPIC", 4);
	    put("LEGENDARY", 5);
	    put("EXOTIC", 6);
	}};

	private static Map<String, Integer> prizeOrder = new HashMap<String, Integer>() {{
	    put("crystals_3500", 1);
	    put("doubledamage_125", 2);
	    put("armor_125", 3);
	    put("n2o_125", 4);
	    put("mine_125", 5);
	    put("crystals_10000", 7);
	    put("health_125", 8);
	    put("allsupplies_100", 9);
	    put("goldboxes_5", 10);
	    put("premiumdays_3", 11);
	    put("crystals_25000", 12);
	    put("goldboxes_10", 13);
	    put("allsupplies_250", 14);
	    put("premiumdays_10", 15);
	    put("paint_moonwalker", 16);
	    put("crystals_100000", 17);
	    put("paint_legendary", 18);
	    put("crystals_300000", 19);
	    put("thunder_xt", 20);
	    put("crystals_1000000", 21);
	}};

	public static List<LootboxPrize> getRandomReward(ClientEntity socket, int count) {
	    if (count > prizes.size()) {
	        throw new IllegalArgumentException("Requested count exceeds available elements.");
	    }
	    
	    List<Prize> selectedPrizes = new ArrayList<>();
	    Map<String, Integer> prizeCounts = new HashMap<>();
	    Random random = new Random();
	    Prize lastSelectedPrize = null;

	    while (selectedPrizes.size() < count) {
	        boolean isDuplicate = random.nextDouble() < 0.10;
	        boolean isTriplicate = random.nextDouble() < 0.05;

	        List<Prize> filteredPrizes;
	        final PrizeWrapper lastSelectedPrizeWrapper = new PrizeWrapper(lastSelectedPrize);

	        if ((isDuplicate && isTriplicate) && !selectedPrizes.isEmpty()) {
	            filteredPrizes = selectedPrizes.stream()
	                .filter(it -> (prizeCounts.getOrDefault(it.getId(), 0) < 3) &&
	                              (!isTriplicate || prizeCounts.getOrDefault(it.getId(), 0) < 3) &&
	                              !EXCLUDED_PRIZE_REGEX.matcher(it.getId()).find() &&
	                              (lastSelectedPrizeWrapper.getPrize() == null || !it.equals(lastSelectedPrizeWrapper.getPrize()))) 
	                .collect(Collectors.toList());
	        } else {
	            String rarity = selectRarity();
	            filteredPrizes = prizes.stream()
	                .filter(it -> it.getRarity().equals(rarity) && 
	                              (prizeCounts.getOrDefault(it.getId(), 0) < 3) &&
	                              selectedPrizes.stream().noneMatch(selectedPrize -> selectedPrize.getId().equals(it.getId())) &&
	                              !EXCLUDED_PRIZE_REGEX.matcher(it.getId()).find())
	                .collect(Collectors.toList());
	        }


	        if (!filteredPrizes.isEmpty()) {
	            Prize selectedPrize = filteredPrizes.get(random.nextInt(filteredPrizes.size()));
	            selectedPrizes.add(selectedPrize);
	            prizeCounts.put(selectedPrize.getId(), prizeCounts.getOrDefault(selectedPrize.getId(), 0) + 1);
	            lastSelectedPrize = selectedPrize;
	        }
	    }

	    selectedPrizes.sort(Comparator.comparingInt(it -> prizeOrder.getOrDefault(it.getId(), Integer.MAX_VALUE)));
	    return selectedPrizes.stream()
	        .map(prize -> new LootboxPrize(
	            prize.getRarity(),
	            1,
	            prize.getPreview(),
	            prize.getName()
	        ))
	        .sorted(Comparator.comparingInt(it -> categoryOrder.get(it.getCategory())))
	        .collect(Collectors.toList());
	}
	
	private static String selectRarity() {
	    Random rand = new Random();
	    double randomValue = rand.nextDouble();
	    double cumulativeProbability = 0.0;
	    
	    for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
	        String rarity = entry.getKey();
	        double probability = entry.getValue();
	        cumulativeProbability += probability;
	        if (randomValue < cumulativeProbability) {
	            return rarity;
	        }
	    }
	    return (String) probabilities.keySet().toArray()[probabilities.size() - 1];
	}

}

class PrizeWrapper {
    private Prize prize;

    public PrizeWrapper(Prize prize) {
        this.prize = prize;
    }

    public Prize getPrize() {
        return prize;
    }

    public void setPrize(Prize prize) {
        this.prize = prize;
    }
}


