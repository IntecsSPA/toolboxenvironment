/* 
 *
 *  Developed By:      Intecs  S.P.A.
 *  File Name:         $RCSfile: Queue.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:25 $
 *
 */
package it.intecs.pisa.toolbox.util;
public class Queue {
  private static final int CONTENT = 0;
  private static final int NEXT = 1;
  private Object[] head, tail;
  public void enqueue(Object o) {
    Object[] newTail = {o, null};
    if (head == null)
      head = newTail;
    else
      tail[NEXT] = newTail;
    tail = newTail;
  }
  public Object dequeue() {
    Object o = head[CONTENT];
    head = (Object[])head[NEXT];
    return o;
  }
  public Object first() {
    return head[CONTENT];
  }
  public boolean isEmpty() {
    return head == null;
  }
}
