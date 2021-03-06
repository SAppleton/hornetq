package org.hornetq.tests;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public abstract class CoreUnitTestCase extends Assert
{
   public static void assertEqualsByteArrays(final byte[] expected, final byte[] actual)
   {
      for (int i = 0; i < expected.length; i++)
      {
         Assert.assertEquals("byte at index " + i, expected[i], actual[i]);
      }
   }

   /**
    * Asserts that latch completes within a (rather large interval).
    * <p>
    * Use this instead of just calling {@code latch.await()}. Otherwise your test may hang the whole
    * test run if it fails to count-down the latch.
    * @param latch
    * @throws InterruptedException
    */
   public static void waitForLatch(CountDownLatch latch) throws InterruptedException
   {
      assertTrue("Latch has got to return within a minute", latch.await(1, TimeUnit.MINUTES));
   }
}
