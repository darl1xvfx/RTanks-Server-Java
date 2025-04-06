package rt.server.utils;

public class Rank
{
    public int min;
    public int max;
    public String name;
    public int prize;
    
    public Rank(final int min, final int max, final String name, final int prize) {
        this.min = min;
        this.max = max;
        this.name = name;
        this.prize = prize;
    }
}