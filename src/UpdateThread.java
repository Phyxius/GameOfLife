import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Shea Polansky
 * Class name and description goes here
 */
public class UpdateThread extends Thread
{
  private static final int[] neighborDeltas = new int[] { 0, 1, 0, -1, 1, 0, 1, 1, 1, -1, -1, 0, -1, 1, -1, -1 };
  private boolean[][] source;
  private boolean[][] destination;
  private final int threadCount, threadIndex;
  private final CyclicBarrier synchronizationBarrier;
  private boolean hasEnded = false;

  public UpdateThread(boolean[][] source, boolean[][] destination, int threadCount, int threadIndex, CyclicBarrier synchronizationBarrier)
  {
    this.source = source;
    this.destination = destination;
    this.threadCount = threadCount;
    this.threadIndex = threadIndex;
    this.synchronizationBarrier = synchronizationBarrier;
  }

  @Override
  public void run()
  {
    main:
    while (!hasEnded)
    {
      final int gridWidth = source.length - 2, gridHeight = source[0].length - 2;
      for (int i = 0; i < gridHeight; i ++)
      {
        int stopAt = Math.min((threadIndex + 1) * (gridWidth / threadCount + 1), gridWidth);
        for(int j = threadIndex * (gridWidth / threadCount + 1); j < stopAt; j++)
        {
          if (hasEnded) break main;
          destination[i + 1][j + 1] = getNextCellState(i, j);
        }
      }
      boolean[][] temp = source;
      source = destination;
      destination = temp;
      try
      {
        synchronizationBarrier.await();
      }
      catch(InterruptedException | BrokenBarrierException e)
      {
        break;
      }
    }
  }

  private int getLivingNeighborsCount(int x, int y)
  {
    int count = 0;
    for (int i = 0; i < neighborDeltas.length; i += 2)
    {
      if (source[x + 1 + neighborDeltas[i]][y + 1 + neighborDeltas[1 + 1]]) count++; //+1 accounts for border cells
    }
    return count;
  }

  private boolean getNextCellState(int x, int y)
  {
    int neighborCount = getLivingNeighborsCount(x, y);
    if (source[x + 1][y + 1]) return neighborCount >= 2 && neighborCount <= 3; //+1 to account for border cells
    return neighborCount == 3;
  }

  public void end()
  {
    hasEnded = true;
  }
}
