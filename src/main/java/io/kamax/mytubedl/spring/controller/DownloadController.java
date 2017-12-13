package io.kamax.mytubedl.spring.controller;

import io.kamax.mytubedl.MyTubeDl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class DownloadController {

    @Autowired
    private MyTubeDl mtdl;

    @RequestMapping(method = POST, path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(HttpServletResponse res, @RequestParam String url) throws IOException, InterruptedException, TimeoutException {
        URL target = new URL(url);
        String filename = mtdl.download(target);
        File file = new File(filename);

        res.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
        res.setContentLengthLong(FileUtils.sizeOf(file));
        IOUtils.copy(new FileInputStream(filename), res.getOutputStream());
        res.flushBuffer();

        FileUtils.deleteQuietly(file);
    }

}
