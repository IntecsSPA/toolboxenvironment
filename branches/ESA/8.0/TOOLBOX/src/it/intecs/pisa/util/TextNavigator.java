/* 
 *
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 *  File Name:         $RCSfile: TextNavigator.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.util;

import java.util.Vector;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import java.awt.Point;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * This class allows navigation operations and data extraction on a given text.
 * The API of this class corresponds to the set of navigation and extraction tags of the TOOLBOX configuration language. This class uses jakarta-regexp-1.2 as a search engine.
 */

public class TextNavigator {

  private static class Tokenization {

    private static final int START = 0;
    private static final int END = 1;

    private static String getSeparatorRegexp(String separators, boolean greedy) {
      StringBuffer buffer = new StringBuffer("(");
      char c;
      for (int index = 0; index < separators.length(); index++) {
        if ((c = separators.charAt(index)) == BACKSLASH) {
          if (separators.charAt(index + 1) == 'b') {
            buffer.append(' ');
          } else {
            buffer.append(separators.substring(index, index + 2));
          }
          index++;
        } else {
          buffer.append(c);
        }
        buffer.append('|');
      }
      buffer.delete(buffer.length() - 1, buffer.length()).append(")+");
      if (!greedy)
        buffer.append('?');
      return buffer.toString();
    }

    private static LinkedList getOccurrences(String text, String pattern) {
      LinkedList result = new LinkedList();
      RE re = getRE(pattern);
      int index = 0;
      int matchStart;
      while (re.match(text, index)) {
        matchStart = re.getParenStart(0);
        index = matchStart + re.getParenLength(0);
        result.add(new int[] {matchStart, index});
      }
      return result;
    }

    private final int[] edges;

    Tokenization(String text, String separators, boolean greedy) {
      LinkedList separations = getOccurrences(text, getSeparatorRegexp(separators, greedy));
      int textLength = text.length();
      if (separations.isEmpty()) {
        edges = new int[] {0, 0, textLength, textLength};
        return;
      }
      if (((int[]) separations.getFirst())[START] > 0)
        separations.addFirst(new int[] {0, 0});
      if (((int[]) separations.getLast())[END] < textLength)
        separations.addLast(new int[] {textLength, textLength});
      edges = new int[separations.size() * 2];
      int[] separation;
      int index = 0;
      Iterator iterator = separations.iterator();
      while (iterator.hasNext()) {
        separation = (int[]) iterator.next();
        edges[index++] = separation[START];
        edges[index++] = separation[END];
      }
    }

    boolean isInField(int index) {
      int i;
      for (i = 1; i < edges.length && edges[i] <= index; i += 2);
      return edges[i - 1] > index;
    }

    int getFieldIndex(int index) {
      int i;
      for (i = 1; i < edges.length && edges[i] <= index; i += 2);
      return (i - 3) / 2;
    }

    int getFieldStart(int index) {
      return edges[fieldIndexBound(index) * 2 + 1];
    }

    int getFieldEnd(int index) {
      return edges[fieldIndexBound(index) * 2 + 2];
    }

    int getFieldCount() {
      return (edges.length - 2) / 2;
    }

    private int fieldIndexBound(int index) {
      if (index < 0)
        return 0;
      if (index >= getFieldCount())
        return getFieldCount() - 1;
      return index;
    }

  }

  public static final int START = 0;
  public static final int COLUMN = 1;
  public static final int END = 2;
  public static final char BACKSLASH = '\\';

  public static void main(String[] args) throws Exception {
    Tokenization tokenization = new Tokenization(args[0], args[1], false);
    for (int index = 0; index < tokenization.getFieldCount(); System.out.println(tokenization.getFieldEnd(index++)));
  }

  private static RE getRE(String pattern) {
    try {return new RE(pattern);}
    catch (RESyntaxException e) {throw new RuntimeException(e.getMessage());}
  }

  private final String text;
  private final Vector lines = new Vector();
  private final int[] lineStart;
  private int currentLine = 0;
  private int currentColumn = 0;
  private final Hashtable marks = new Hashtable();

  /**
   * Builds a text navigator from a character stream.
   * The initial current position is first line first character.
   */

  public TextNavigator(Reader in) throws IOException {
    BufferedReader bufIn = new BufferedReader(in);
    StringBuffer buffer = new StringBuffer();
    for (String line = bufIn.readLine(); line != null; line = bufIn.readLine()) {
      lines.add(line);
      buffer.append(line).append('\n');
    }
    text = buffer.toString();
    lineStart = new int[lines.size()];
    lineStart[0] = 0;
    for (int index = 0; index < lineStart.length - 1; index++) {
      lineStart[index + 1] = lineStart[index] + line(index).length() + 1;
    }
  }

  private String line(int index) {
    return (String) lines.elementAt(index);
  }

  private String line() {
    return line(currentLine);
  }

  private int lineLength() {
    return line().length();
  }

  private int getIndex() {
    return lineStart[currentLine] + currentColumn;
  }

  private Point getLocation(int index)  {
    int idx;
    for (idx = 0; idx < lineStart.length && lineStart[idx] <= index; idx++);
    idx--;
    return new Point(idx, index - lineStart[idx]);
  }

  private int boundColumn(int desiredColumn) {
    int lineLength = lineLength();
    return (desiredColumn < 0) ? 0 : ((desiredColumn > lineLength) ? lineLength : desiredColumn);
  }

  public void horizontalMove(int delta, boolean absolute) {
    if (absolute) {
      if (delta >= 0) {
        gotoLineStart();
      } else {
        gotoLineEnd();
      }
    }
    currentColumn = boundColumn(currentColumn + delta);
  }

  public void verticalMove (int delta, boolean absolute, int position) {
    int currentColumn = this.currentColumn;
    if (absolute) {
      if (delta >= 0) {
        gotoFileStart();
      } else {
        gotoFileEnd();
      }
    }
    int desiredLine = currentLine + delta;
    int lineNumber = lines.size();
    currentLine = (desiredLine < 0) ? 0 : ((desiredLine >= lineNumber) ? lineNumber - 1 : desiredLine);
    switch (position) {
    case START:
      this.currentColumn = 0;
      break;
    case END:
      this.currentColumn = lineLength();
      break;
    default:
      this.currentColumn = boundColumn(currentColumn);
    }
  }

  public void gotoLineStart() {
    currentColumn = 0;
  }

  public void gotoLineEnd() {
    currentColumn = lineLength();
  }

  public void gotoFileStart() {
    currentLine = currentColumn = 0;
  }

  public void gotoFileEnd() {
    currentLine = lines.size() - 1;
    currentColumn = lineLength();
  }

  public int getCurrentLine() {
    return currentLine + 1;
  }

  public int getCurrentColumn() {
    return currentColumn + 1;
  }

  public void mark(String markName) {
    marks.put(markName, new Point(currentLine, currentColumn));
  }

  public void goTo(String markName) {
    Point newLocation = (Point) marks.get(markName);
    if (newLocation != null) {
      currentLine = newLocation.x;
      currentColumn = newLocation.y;
    }
  }

  public String extract(String startName, String endName) {
    Point start = (Point) marks.get(startName);
    Point end = (Point) marks.get(endName);
    int startIndex = lineStart[start.x] + start.y;
    int endIndex = lineStart[end.x] + end.y;
    return
      startIndex < endIndex
      ? text.substring(startIndex, endIndex)
      : text.substring(endIndex, startIndex);
  }

  public void fieldMove(int delta, String separators, boolean greedySeparators, boolean absolute, int position) {
    Tokenization tokenization = new Tokenization(line(), separators, greedySeparators);
    if (!absolute && !tokenization.isInField(currentColumn) && delta == 0)
      return;
    int newFieldIndex =
       absolute
       ?
       ((delta >= 0) ? (delta - 1) : (tokenization.getFieldCount() + delta))
       :
       tokenization.getFieldIndex(currentColumn) + delta;
    switch (position) {
    case START:
      currentColumn = tokenization.getFieldStart(newFieldIndex);
      break;
    case END:
      currentColumn = tokenization.getFieldEnd(newFieldIndex);
      break;
    }
  }

  public boolean search(String pattern, int delta, boolean absolute) {
    if (delta <= 0)
      return false;
    RE re = getRE(pattern);
    boolean result = false;
    int index = absolute ? 0 : getIndex();
    while (delta-- > 0 && (result = re.match(text, index))) {
      index = re.getParenStart(0) + re.getParenLength(0);
    }
    if (!result)
      return false;
    Point newLocation = getLocation(re.getParenStart(0));
    currentLine = newLocation.x;
    currentColumn = newLocation.y;
    return true;
  }

}
