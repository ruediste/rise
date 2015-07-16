package com.github.ruediste.rise.component.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.google.common.io.ByteStreams;

@DefaultTemplate(CFileInputTemplate.class)
public class CFileInput extends RelationsComponent<CFileInput> {

    public interface UploadedFile {
        /**
         * Gets the file name specified by the client
         *
         * @return the submitted file name
         *
         * @since Servlet 3.1
         */
        String getSubmittedFileName();

        /**
         * Gets the content of this part as an <tt>InputStream</tt>
         * 
         * @return The content of this part as an <tt>InputStream</tt>
         * @throws IOException
         *             If an error occurs in retrieving the contet as an
         *             <tt>InputStream</tt>
         */
        InputStream getInputStream() throws IOException;

        default byte[] getBytes() {
            try {
                return ByteStreams.toByteArray(getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Unable to load data of file "
                        + getSubmittedFileName(), e);
            }
        }
    }

    private final List<UploadedFile> uploadedFiles = new ArrayList<CFileInput.UploadedFile>();

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

}
