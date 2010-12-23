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
 *  File Name:         $RCSfile: Base64OutputStream.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:25 $
 *
 */
package it.intecs.pisa.toolbox.util;

import java.io.*;

public class Base64OutputStream extends FilterOutputStream {

  private final byte[] remainder = new byte[3];
  private int remainderSize;

  public Base64OutputStream(OutputStream out) {
    super(out);
  }

  public void close() throws IOException {
    flushBase64();
    out.close();
  }

  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    if (remainderSize > 0) {
      while (len > 0 && remainderSize < 3) {
        remainder[remainderSize++] = b[off++];
        len--;
      }
      checkRemainder();
    }
    if (len > 0) {
      int mod = len % 3;
      if (len / 3 > 0)
        Base64.encode(b, off, len - mod, out);
      while (mod > 0)
        remainder[remainderSize++] = b[off + len - mod--];
    }
  }

  public void write(int b) throws IOException {
    remainder[remainderSize++] = (byte)b;
    checkRemainder();
  }

  public void flushBase64() throws IOException {
    if (remainderSize > 0) {
      Base64.encode(remainder, 0, remainderSize, out);
      remainderSize = 0;
    }
    out.flush();
  }

  private void checkRemainder() throws IOException {
    if (remainderSize == 3) {
      Base64.encode(remainder, 0, 3, out);
      remainderSize = 0;
    }
  }

}
