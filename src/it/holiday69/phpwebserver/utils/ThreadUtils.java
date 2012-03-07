/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.holiday69.phpwebserver.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stefano Fratini <stefano.fratini@yeahpoint.com>
 */
public class ThreadUtils {

  public static List<Thread> getFullThreadList(boolean includeDaemons) {

    List<Thread> list = new ArrayList<Thread>();

    // Find the root thread group
    ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
    while (root.getParent() != null) {
        root = root.getParent();
    }

    // Visit each thread group
    visit(list, root, 0, includeDaemons);

    // returns the full list
    return list;
  }

  // This method recursively visits all thread groups under `group'.
  private static void visit(List<Thread> list, ThreadGroup group, int level, boolean includeDaemons) {

    // Get threads in `group'
    int numThreads = group.activeCount();
    Thread[] threads = new Thread[numThreads*2];
    numThreads = group.enumerate(threads, false);

    // Enumerate each thread in `group'
    for (int i=0; i<numThreads; i++) {
        // Get thread
      if(!includeDaemons && threads[i].isDaemon())
        continue;

      list.add(threads[i]);
    }

    // Get thread subgroups of `group'
    int numGroups = group.activeGroupCount();
    ThreadGroup[] groups = new ThreadGroup[numGroups*2];
    numGroups = group.enumerate(groups, false);

    // Recursively visit each subgroup
    for (int i=0; i<numGroups; i++) {
        visit(list, groups[i], level+1, includeDaemons);
    }
  }
}
