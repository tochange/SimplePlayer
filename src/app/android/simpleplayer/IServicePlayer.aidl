package app.android.simpleplayer;
interface IServicePlayer{
	void play(int progress,String name);
	void pause();
	void stop();
	void changeTo(String name);
	int getDuration();
	int getCurrentPosition(boolean playing);
	void seekTo(int current);
	boolean isPlaying();
	boolean isCompleted();
	void release();
	void reset();
	boolean setLoop(boolean loop,String name);
}