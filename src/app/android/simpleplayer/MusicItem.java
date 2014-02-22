package app.android.simpleplayer;

public class MusicItem

{
    public MusicItem(int i, String s, int p, int r, boolean pl, boolean r1)
    {
        id = i;
        name = s;
        progress = p;
        rank = r;
        playing = pl;
        repeated = r1;
    }

    public int getRank()
    {
        return rank;
    }

    public String getName()
    {
        return name;
    }

    public int id;

    public String name;

    public int progress;

    public int duration;

    public int rank;

    public boolean playing;

    public boolean repeated;

    public boolean visible;

    public boolean delete;

    public short hight;

}