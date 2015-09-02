import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Shea Polansky
 * UpdateThread: updates a game of life simulation, synchronized with other threads.
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

  /**
   * Begins updating its portion of the board in a loop, waiting for other threads to finish after each update
   */
  @Override
  public void run()
  {
    main:
    while (!hasEnded)
    {
      final int gridWidth = source.length - 2, gridHeight = source[0].length - 2;
      final int numCellsToProcess = gridWidth / threadCount + 1;
      for (int i = 0; i < gridHeight; i ++)
      {
        int curThreadIndex = (threadIndex + i) % threadCount;
        int stopAt = Math.min((curThreadIndex + 1) * numCellsToProcess, gridWidth);
        for(int j = curThreadIndex * numCellsToProcess; j < stopAt; j++)
        {
          if (hasEnded) break main;
          int neighborCount = 0;
          boolean ret;
          for (int k = 0; k < neighborDeltas.length; k += 2)
          {
            if (source[i + 1 + neighborDeltas[k]][j + 1 + neighborDeltas[k + 1]]) neighborCount++; //+1 accounts for border cells
          }
          if (source[i + 1][j + 1])
          {
            destination[i + 1][j + 1] = neighborCount >= 2 && neighborCount <= 3;
          }
          else destination[i + 1][j + 1] = neighborCount == 3;
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

  /**
   * Signals to the thread that it should exit at the next opportunity
   */
  public void end()
  {
    hasEnded = true;
  }
}
