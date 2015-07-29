/*
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.unifile;

import com.hippo.yorozuya.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class DocumentRandomReadFile extends UniRandomReadFile {

    private DocumentFile mDocumentFile;
    private InputStream mInputStream;
    private long position;

    public DocumentRandomReadFile(DocumentFile documentFile) throws IOException {
        mDocumentFile =documentFile;
        mInputStream = documentFile.openInputStream();
        position = 0;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        InputStream is = mInputStream;
        if (is == null) {
            throw new IOException("DocumentRandomReadFile is closed");
        }

        int count = 0;
        int n;
        while (byteCount > 0 && -1 != (n = is.read(buffer, byteOffset, byteCount))) {
            count += n;
            byteOffset += n;
            byteCount -= n;
        }
        position += count;

        return count;
    }

    @Override
    public void seek(long offset) throws IOException {
        InputStream is = mInputStream;
        if (is == null) {
            throw new IOException("DocumentRandomReadFile is closed");
        }

        long actuallySkip;
        if (offset < position) {
            IOUtils.closeQuietly(is);
            mInputStream = mDocumentFile.openInputStream();
            actuallySkip = mInputStream.skip(offset);
            position = actuallySkip;
        } else if (offset > position) {
            actuallySkip = mInputStream.skip(offset - position);
            position += actuallySkip;
        }
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public long length() throws IOException {
        return mDocumentFile.length();
    }

    @Override
    public void close() throws IOException {
        mDocumentFile = null;
        IOUtils.closeQuietly(mInputStream);
    }
}
