/* 
 *
 *  Developed By:      Intecs  S.P.A.
 *  File Name:         $RCSfile: Stack.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:25 $
 *
 */
package it.intecs.pisa.toolbox.util;
public class Stack {
  private static final int CONTENT = 0;
  private static final int NEXT = 1;
  private Object[] top;
  public void push(Object o) {
    top = new Object[] {o, top};
  }
  public Object pop() {
    Object o = top[CONTENT];
    top = (Object[])top[NEXT];
    return o;
  }
  public Object peek() {
    return top[CONTENT];
  }
  public boolean isEmpty() {
    return top == null;
  }
}
