package com.github.ruediste.rise.component.components;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ruediste.rise.component.components.CFileInput.UploadedFile;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CFileInputTemplate
        extends BootstrapComponentTemplateBase<CFileInput> {

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    ObjectMapper mapper;

    @Override
    public void doRender(CFileInput component, BootstrapRiseCanvas<?> html) {

        html.input().rCOMPONENT_ATTRIBUTES(component).TYPE("file")
                .CLASS("rise_fileinput").MULTIPLE("multiple")
                .DATA("upload-url", getAjaxUrl(component) + "/upload")
                .DATA("delete-url", getAjaxUrl(component) + "/delete")
                // .DATA("show-upload", "false")
                ;
        if (!component.getUploadedFiles().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (UploadedFile file : component.getUploadedFiles())
                sb.append("<div class='file-preview-text'>"
                        + "<h2><i class='glyphicon glyphicon-file'></i></h2>"
                        + file.getSubmittedFileName() + "</div>");
            html.DATA("initial-preview", sb.toString());
        }
    }

    private static class Response {
        private String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    @Override
    public HttpRenderResult handleAjaxRequest(CFileInput component,
            String suffix) throws Throwable {
        if ("upload".equals(suffix)) {
            // read submitted files
            for (Part part : coreRequestInfo.getServletRequest().getParts()) {
                if (part.getSubmittedFileName() != null) {
                    component.getUploadedFiles().add(new UploadedFile() {

                        @Override
                        public String getSubmittedFileName() {
                            return part.getSubmittedFileName();
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return part.getInputStream();
                        }
                    });
                }
            }

            // send response
            HttpServletResponse resp = coreRequestInfo.getServletResponse();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("utf-8");
            mapper.writeValue(resp.getWriter(), new Response());
            return null;
        } else if ("delete".equals(suffix)) {
            // TODO: does not work yet
            System.out.println("Delete!");
            return null;
        }
        throw new RuntimeException("unknown suffis " + suffix);
    }
}
