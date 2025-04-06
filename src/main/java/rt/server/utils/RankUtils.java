package rt.server.utils;

import rt.server.logger.Logger;

public class RankUtils
{
    private static Rank[] ranks;
    
    public static void init() {
    	Logger.log(Logger.INFO, "Init RankUtils...");
       (ranks = new Rank[30])[0] = new Rank(0, 99, "Новобранец", 0);
        ranks[1] = new Rank(100, 499, "Рядовой",10);
        ranks[2] = new Rank(500, 1499, "Ефрейтор",40);
        ranks[3] = new Rank(1500, 3699, "Капрал",120);
        ranks[4] = new Rank(3700, 7099, "Мастер-капрал",230);
        ranks[5] = new Rank(7100, 12299, "Сержант",420);
        ranks[6] = new Rank(12300, 19999, "Штаб-сержант",740);
        ranks[7] = new Rank(20000, 28999, "Мастер-сержант",950);
        ranks[8] = new Rank(29000, 40999, "Первый сержант",1400);
        ranks[9] = new Rank(41000, 56999, "Сержант-майор",2000);
        ranks[10] = new Rank(57000, 75999, "Уорэент-офицер 1",2500);
        ranks[11] = new Rank(76000, 97999, "Уорэент-офицер 2",3100);
        ranks[12] = new Rank(98000, 124999, "Уорэент-офицер 3",3900);
        ranks[13] = new Rank(125000, 155999, "Уорэент-офицер 4",4600);
        ranks[14] = new Rank(156000, 191999, "Уорэент-офицер 5",5600);
        ranks[15] = new Rank(192000, 232999, "Младшый лейтенант",6600);
        ranks[16] = new Rank(233000, 279999, "Лейтенант",7900);
        ranks[17] = new Rank(280000, 331999, "Старший лейтенант",8900);
        ranks[18] = new Rank(332000, 389999, "Капитан",10000);
        ranks[19] = new Rank(390000, 454999, "Майор",12000);
        ranks[20] = new Rank(455000, 526999, "Подполковник",14000);
        ranks[21] = new Rank(527000, 605999, "Полковник",16000);
        ranks[22] = new Rank(606000, 691999, "Бригадир",17000);
        ranks[23] = new Rank(692000, 786999, "Генерал-майор",20000);
        ranks[24] = new Rank(787000, 888999, "Генерал-лейнетант",22000);
        ranks[25] = new Rank(889000, 999999, "Генерал",24000);
        ranks[26] = new Rank(1000000, 1121999, "Маршал",28000);
        ranks[27] = new Rank(1122000, 1254999, "Фельдмаршал",31000);
        ranks[28] = new Rank(1255000, 1399999, "Командор",34000);
        ranks[29] = new Rank(1400000, -1, "Генералиссимус",37000);
      }
    
    public static int getUpdateNumber(final int score) {
        final Rank temp = getRankByScore(score);
        final int rang;
        rang = getNumberRank(temp) - 1;
        int result = 0;
        try {
            result = (int)((score - RankUtils.ranks[rang - 1].max) * 1.0 / (temp.max - RankUtils.ranks[rang - 1].max) * 10000.0);
        }
        catch (Exception e) {
            result = (int)((score - 0) * 1.0 / (temp.max - 0) * 10000.0);
        }
        if (score > RankUtils.ranks[RankUtils.ranks.length - 1].min - 1) {
            result = 10000;
        }
        else if (score < 0) {
            result = 0;
        }
        return result;
    }
    
    public static int getNumberRank(final Rank rank) {
        for (int i = 0; i < RankUtils.ranks.length; ++i) {
            if (RankUtils.ranks[i] == rank) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public static Rank getRankByScore(final int score) {
        Rank temp = RankUtils.ranks[0];
        if (score >= RankUtils.ranks[29].max) {
            temp = RankUtils.ranks[29];
        }
        Rank[] ranks;
        for (int length = (ranks = RankUtils.ranks).length, i = 0; i < length; ++i) {
            final Rank rank = ranks[i];
            if (score >= rank.min && score <= rank.max) {
                temp = rank;
            }
        }
        return temp;
    }
    
    public static Rank getRankByIndex(final int index) {
        return RankUtils.ranks[index];
    }
    
    public static int stringToInt(final String src) {
        try {
            int tempelate = Integer.parseInt(src);
            if (tempelate <= 0) {
                tempelate = 5000000;
            }
            return (tempelate >= RankUtils.ranks[30].min) ? RankUtils.ranks[30].min : tempelate;
        }
        catch (Exception ex) {
            return 50000000;
        }
    }
}
