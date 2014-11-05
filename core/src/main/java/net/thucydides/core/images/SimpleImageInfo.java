package net.thucydides.core.images;

/*
 *	SimpleImageInfo.java
 *
 *	@version 0.1
 *	@author  Jaimon Mathew <http://www.jaimon.co.uk>
 *
 *	A Java class to determine image width, height and MIME types for a number of image file formats without loading the whole image data.
 *
 *	Revision history
 *	0.1 - 29/Jan/2011 - Initial version created
 *
 *  -------------------------------------------------------------------------------

 	This code is licensed under the Apache License, Version 2.0 (the "License");
 	You may not use this file except in compliance with the License.

 	You may obtain a copy of the License at

 	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 *  -------------------------------------------------------------------------------
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("all")
public class SimpleImageInfo {
	private int height;
	private int width;

	public SimpleImageInfo(final File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			processStream(is);
		} finally {
			is.close();
		}
	}

	private void processStream(final InputStream is) throws IOException {
		int c1 = is.read();
		int c2 = is.read();
		int c3 = is.read();

		width = height = -1;

		if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
			is.skip(15);
			width = readInt(is,2);
			is.skip(2);
			height = readInt(is,2);
		} else {
			throw new IOException("Unsupported image type");
		}
	}

	private int readInt(final InputStream is, final int noOfBytes) throws IOException {
		int ret = 0;
		int sv = (noOfBytes - 1) * 8;
		int cnt = -8;
		for(int i=0;i<noOfBytes;i++) {
			ret |= is.read() << sv;
			sv += cnt;
		}
		return ret;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}