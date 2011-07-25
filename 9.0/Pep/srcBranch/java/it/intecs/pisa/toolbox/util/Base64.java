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
 *  File Name:         $RCSfile: Base64.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:25 $
 *
 */
package it.intecs.pisa.toolbox.util;

import java.io.*;
import java.util.*;

public class Base64 {

  private static final String INVALID_CHAR = "Invalid Base64 character: ";
  private static final String UNEXPECTED_EOF = "Unexpected end of Base64 stream";

  private static final int[] CHAR_TABLE = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '+', '/'
  };

  private static final int[] INDEX_TABLE;

  static {

    INDEX_TABLE = new int[256];
    Arrays.fill(INDEX_TABLE, -1);
    int base1 = 'A';
    int base2 = 'a' - 26;
    int base3 = '0' - 52;
    int c;
    for (c = 'A'; c <= 'Z'; c++)
      INDEX_TABLE[c] = c - base1;
    for (c = 'a'; c <= 'z'; c++)
      INDEX_TABLE[c] = c - base2;
    for (c = '0'; c <= '9'; c++)
      INDEX_TABLE[c] = c - base3;
    INDEX_TABLE['+'] = 62;
    INDEX_TABLE['/'] = 63;

  }

  private static final int PAD = '=';

  public static void encode(byte[] b, int off, int len, OutputStream out) throws IOException {
    while (len > 0) {
      out.write(CHAR_TABLE[(b[off] & 0xFF) >> 2]);
      switch (len) {
        case 1:
          out.write(CHAR_TABLE[(b[off] & 0x3) << 4]);
          out.write(PAD);
          out.write(PAD);
          break;
        case 2:
          out.write(CHAR_TABLE[((b[off] & 0x3) << 4) | ((b[off + 1] & 0xFF) >> 4)]);
          out.write(CHAR_TABLE[(b[off + 1] & 0xF) << 2]);
          out.write(PAD);
          break;
        default:
          out.write(CHAR_TABLE[((b[off] & 0x3) << 4) | ((b[off + 1] & 0xFF) >> 4)]);
          out.write(CHAR_TABLE[((b[off + 1] & 0xF) << 2) | ((b[off + 2] & 0xFF) >> 6)]);
          out.write(CHAR_TABLE[b[off + 2] & 0x3F]);
          break;
      }
      off += 3;
      len -= 3;
    }
  }

  public static int decode(byte[] b, int off, int len, InputStream in, byte[] remainder) throws IOException {
    if (len == 0)
      return 0;
    int result = 0;
    int c, index1, index2, index3, index4;
    if ((c = in.read()) < 0)
      return -1;
    for (;;) {

      index1 = INDEX_TABLE[c];
      if (index1 < 0)
        throw new IOException(INVALID_CHAR + (char)c);

      if ((c = in.read()) < 0)
        throw new IOException(UNEXPECTED_EOF);

      index2 = INDEX_TABLE[c];
      if (index2 < 0)
        throw new IOException(INVALID_CHAR + (char)c);
      b[off++] = (byte)((index1 << 2) | ((index2 & 0x3F) >> 4));
      result++;

      if ((c = in.read()) < 0)
        throw new IOException(UNEXPECTED_EOF);

      if (c == PAD) {
        if ((c = in.read()) < 0)
          throw new IOException(UNEXPECTED_EOF);
        if (c != PAD)
          throw new IOException(INVALID_CHAR + (char)c);
        if (result >= len || (c = in.read()) < 0)
          break;
        continue;
      }
      index3 = INDEX_TABLE[c];
      if (index3 < 0)
        throw new IOException(INVALID_CHAR + (char)c);
      if (result < len)
        b[off++] = (byte)(((index2 & 0xF) << 4) | ((index3 & 0x3F) >> 2));
      else
        remainder[result - len] = (byte)(((index2 & 0xF) << 4) | ((index3 & 0x3F) >> 2));
      result++;

      if ((c = in.read()) < 0)
        throw new IOException(UNEXPECTED_EOF);

      if (c == PAD) {
        if (result >= len || (c = in.read()) < 0)
          break;
        continue;
      }
      index4 = INDEX_TABLE[c];
      if (index4 < 0)
        throw new IOException(INVALID_CHAR + (char)c);
      if (result < len)
        b[off++] = (byte)(((index3 & 0x3) << 6) | index4);
      else
        remainder[result - len] =  (byte)(((index3 & 0x3) << 6) | index4);
      result++;

      if (result >= len || (c = in.read()) < 0)
        break;

    }
    return result;
  }

}
