package it.intecs.pisa.common.stream;

import java.io.InputStream;
import java.io.PushbackInputStream;

public class XmlDirectivesFilterStream extends PushbackInputStream
 {

	public XmlDirectivesFilterStream(InputStream arg0) {
		super(arg0,3);
		//removing all directives
		removeDirectivesFromStream();
	}

	private void removeDirectivesFromStream() {
		byte[] buf=null;
		byte current;
		
		try {
			buf=new byte[2];
			
			read(buf);
			if((buf[0]=='<' && buf[1]=='?')==false)
			{
				unread(buf[1]);
				unread(buf[0]);
				return;
			}
			
			//finding the end of directive
			do
			{
				current=(byte) read();
			}
			while(available()>0 && current!='>');
			
			//	finding next directive
			do
			{
				current=(byte) read();
			}
			while(available()>0 && current!='<');
			
			if(available()>0)
				{
					unread(current);
					this.removeDirectivesFromStream();
				}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

	
}
