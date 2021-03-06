/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.hornetq.tests.timing.core.server.impl;
import org.junit.Before;
import org.junit.After;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import org.hornetq.api.core.SimpleString;
import org.hornetq.core.server.Consumer;
import org.hornetq.core.server.HandleStatus;
import org.hornetq.core.server.MessageReference;
import org.hornetq.core.server.impl.QueueImpl;
import org.hornetq.tests.unit.core.server.impl.fakes.FakeConsumer;
import org.hornetq.tests.util.UnitTestCase;

/**
 * @author <a href="ataylor@redhat.com">Andy Taylor</a>
 */
public class QueueImplTest extends UnitTestCase
{
   private static final SimpleString queue1 = new SimpleString("queue1");

   private static final long TIMEOUT = 10000;

   private ScheduledExecutorService scheduledExecutor;

   // private ExecutorService executor;

   @Override
   @Before
   public void setUp() throws Exception
   {
      super.setUp();

      scheduledExecutor = new ScheduledThreadPoolExecutor(1);
   }

   @Override
   @After
   public void tearDown() throws Exception
   {
      scheduledExecutor.shutdownNow();

      super.tearDown();
   }

   // The tests ----------------------------------------------------------------

   @Test
   public void testScheduledNoConsumer() throws Exception
   {
      QueueImpl queue = new QueueImpl(1,
                                  new SimpleString("address1"),
                                  new SimpleString("queue1"),
                                  null,
                                  null,
                                  false,
                                  true,
                                  scheduledExecutor,
                                  null,
                                  null,
                                  null,
                                  Executors.newSingleThreadExecutor());

      // Send one scheduled

      long now = System.currentTimeMillis();

      MessageReference ref1 = generateReference(queue, 1);
      ref1.setScheduledDeliveryTime(now + 7000);
      queue.addTail(ref1);

      // Send some non scheduled messages

      MessageReference ref2 = generateReference(queue, 2);
      queue.addTail(ref2);
      MessageReference ref3 = generateReference(queue, 3);
      queue.addTail(ref3);
      MessageReference ref4 = generateReference(queue, 4);
      queue.addTail(ref4);

      // Now send some more scheduled messages

      MessageReference ref5 = generateReference(queue, 5);
      ref5.setScheduledDeliveryTime(now + 5000);
      queue.addTail(ref5);

      MessageReference ref6 = generateReference(queue, 6);
      ref6.setScheduledDeliveryTime(now + 4000);
      queue.addTail(ref6);

      MessageReference ref7 = generateReference(queue, 7);
      ref7.setScheduledDeliveryTime(now + 3000);
      queue.addTail(ref7);

      MessageReference ref8 = generateReference(queue, 8);
      ref8.setScheduledDeliveryTime(now + 6000);
      queue.addTail(ref8);

      List<MessageReference> refs = new ArrayList<MessageReference>();

      // Scheduled refs are added back to *FRONT* of queue - otherwise if there were many messages in the queue
      // They may get stranded behind a big backlog

      refs.add(ref1);
      refs.add(ref8);
      refs.add(ref5);
      refs.add(ref6);
      refs.add(ref7);

      refs.add(ref2);
      refs.add(ref3);
      refs.add(ref4);

      Thread.sleep(7500);

      FakeConsumer consumer = new FakeConsumer();

      queue.addConsumer(consumer);

      queue.deliverNow();

      assertRefListsIdenticalRefs(refs, consumer.getReferences());
   }

   @Test
   public void testScheduled() throws Exception
   {
      QueueImpl queue = new QueueImpl(1,
                                  new SimpleString("address1"),
                                  new SimpleString("queue1"),
                                  null,
                                  null,
                                  false,
                                  true,
                                  scheduledExecutor,
                                  null,
                                  null,
                                  null,
                                  Executors.newSingleThreadExecutor());

      FakeConsumer consumer = null;


      // Send one scheduled

      long now = System.currentTimeMillis();

      MessageReference ref1 = generateReference(queue, 1);
      ref1.setScheduledDeliveryTime(now + 7000);
      queue.addTail(ref1);

      // Send some non scheduled messages

      MessageReference ref2 = generateReference(queue, 2);
      queue.addTail(ref2);
      MessageReference ref3 = generateReference(queue, 3);
      queue.addTail(ref3);
      MessageReference ref4 = generateReference(queue, 4);
      queue.addTail(ref4);

      // Now send some more scheduled messages

      MessageReference ref5 = generateReference(queue, 5);
      ref5.setScheduledDeliveryTime(now + 5000);
      queue.addTail(ref5);

      MessageReference ref6 = generateReference(queue, 6);
      ref6.setScheduledDeliveryTime(now + 4000);
      queue.addTail(ref6);

      MessageReference ref7 = generateReference(queue, 7);
      ref7.setScheduledDeliveryTime(now + 3000);
      queue.addTail(ref7);

      MessageReference ref8 = generateReference(queue, 8);
      ref8.setScheduledDeliveryTime(now + 6000);
      queue.addTail(ref8);

      consumer = new FakeConsumer();

      queue.addConsumer(consumer);

      queue.deliverNow();


      List<MessageReference> refs = new ArrayList<MessageReference>();

      refs.add(ref2);
      refs.add(ref3);
      refs.add(ref4);

      assertRefListsIdenticalRefs(refs, consumer.getReferences());

      refs.clear();
      consumer.getReferences().clear();

      MessageReference ref = consumer.waitForNextReference(QueueImplTest.TIMEOUT);
      Assert.assertEquals(ref7, ref);
      long now2 = System.currentTimeMillis();
      Assert.assertTrue(now2 - now >= 3000);

      ref = consumer.waitForNextReference(QueueImplTest.TIMEOUT);
      Assert.assertEquals(ref6, ref);
      now2 = System.currentTimeMillis();
      Assert.assertTrue(now2 - now >= 4000);

      ref = consumer.waitForNextReference(QueueImplTest.TIMEOUT);
      Assert.assertEquals(ref5, ref);
      now2 = System.currentTimeMillis();
      Assert.assertTrue(now2 - now >= 5000);

      ref = consumer.waitForNextReference(QueueImplTest.TIMEOUT);
      Assert.assertEquals(ref8, ref);
      now2 = System.currentTimeMillis();
      Assert.assertTrue(now2 - now >= 6000);

      ref = consumer.waitForNextReference(QueueImplTest.TIMEOUT);
      Assert.assertEquals(ref1, ref);
      now2 = System.currentTimeMillis();
      Assert.assertTrue(now2 - now >= 7000);

      Assert.assertTrue(consumer.getReferences().isEmpty());
   }

   @Test
   public void testDeliveryScheduled() throws Exception
   {
      final CountDownLatch countDownLatch = new CountDownLatch(1);
      Consumer consumer = new FakeConsumer()
      {
         @Override
         public synchronized HandleStatus handle(final MessageReference reference)
         {
            countDownLatch.countDown();
            return HandleStatus.HANDLED;
         }
      };
      QueueImpl queue = new QueueImpl(1,
                                  new SimpleString("address1"),
                                  QueueImplTest.queue1,
                                  null,
                                  null,
                                  false,
                                  true,
                                  scheduledExecutor,
                                  null,
                                  null,
                                  null,
                                  Executors.newSingleThreadExecutor());
      MessageReference messageReference = generateReference(queue, 1);
      queue.addConsumer(consumer);
      messageReference.setScheduledDeliveryTime(System.currentTimeMillis() + 2000);
      queue.addHead(messageReference);

      boolean gotLatch = countDownLatch.await(3000, TimeUnit.MILLISECONDS);
      Assert.assertTrue(gotLatch);
   }

}
