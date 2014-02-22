package app.android.simpleplayer.db;


import java.util.List;

public interface AfterWriteDbCallBack
{
    void refresh(List<String> mWithoutAnyMp3PathList);
}