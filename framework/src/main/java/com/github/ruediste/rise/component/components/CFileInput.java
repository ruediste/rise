package com.github.ruediste.rise.component.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.component.tree.RelationsComponent;

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
        public String getSubmittedFileName();

        /**
         * Gets the content of this part as an <tt>InputStream</tt>
         * 
         * @return The content of this part as an <tt>InputStream</tt>
         * @throws IOException
         *             If an error occurs in retrieving the contet as an
         *             <tt>InputStream</tt>
         */
        public InputStream getInputStream() throws IOException;
    }

    private final List<UploadedFile> uploadedFiles = new ArrayList<CFileInput.UploadedFile>();

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

}
