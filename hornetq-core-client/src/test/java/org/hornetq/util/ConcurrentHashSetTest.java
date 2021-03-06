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

package org.hornetq.util;
import org.junit.Before;
import org.junit.After;

import org.junit.Test;

import java.util.Iterator;

import org.junit.Assert;


import org.hornetq.tests.util.RandomUtil;
import org.hornetq.utils.ConcurrentHashSet;
import org.hornetq.utils.ConcurrentSet;

/**
 * @author <a href="mailto:jmesnil@redhat.com">Jeff Mesnil</a>
 *
 *
 */
public class ConcurrentHashSetTest extends Assert
{
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   private ConcurrentSet<String> set;

   private String element;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   @Test
   public void testAdd() throws Exception
   {
      Assert.assertTrue(set.add(element));
      Assert.assertFalse(set.add(element));
   }

   @Test
   public void testAddIfAbsent() throws Exception
   {
      Assert.assertTrue(set.addIfAbsent(element));
      Assert.assertFalse(set.addIfAbsent(element));
   }

   @Test
   public void testRemove() throws Exception
   {
      Assert.assertTrue(set.add(element));

      Assert.assertTrue(set.remove(element));
      Assert.assertFalse(set.remove(element));
   }

   @Test
   public void testContains() throws Exception
   {
      Assert.assertFalse(set.contains(element));

      Assert.assertTrue(set.add(element));
      Assert.assertTrue(set.contains(element));

      Assert.assertTrue(set.remove(element));
      Assert.assertFalse(set.contains(element));
   }

   @Test
   public void testSize() throws Exception
   {
      Assert.assertEquals(0, set.size());

      Assert.assertTrue(set.add(element));
      Assert.assertEquals(1, set.size());

      Assert.assertTrue(set.remove(element));
      Assert.assertEquals(0, set.size());
   }

   @Test
   public void testClear() throws Exception
   {
      Assert.assertTrue(set.add(element));

      Assert.assertTrue(set.contains(element));
      set.clear();
      Assert.assertFalse(set.contains(element));
   }

   @Test
   public void testIsEmpty() throws Exception
   {
      Assert.assertTrue(set.isEmpty());

      Assert.assertTrue(set.add(element));
      Assert.assertFalse(set.isEmpty());

      set.clear();
      Assert.assertTrue(set.isEmpty());
   }

   @Test
   public void testIterator() throws Exception
   {
      set.add(element);

      Iterator<String> iterator = set.iterator();
      while (iterator.hasNext())
      {
         String e = iterator.next();
         Assert.assertEquals(element, e);
      }
   }

   // TestCase overrides --------------------------------------------

   @Before
   public void setUp() throws Exception
   {


      set = new ConcurrentHashSet<String>();
      element = RandomUtil.randomString();
   }

   @After
   public void tearDown() throws Exception
   {
      set = null;
      element = null;


   }
   // Package protected ---------------------------------------------

   // Protected -----------------------------------------------------

   // Private -------------------------------------------------------

   // Inner classes -------------------------------------------------
}
