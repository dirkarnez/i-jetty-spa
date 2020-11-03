package org.mortbay.ijetty.handler;

import android.os.Environment;
import android.util.Log;

import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.eclipse.jetty.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SinglePageApplicationHandler extends org.eclipse.jetty.server.handler.DefaultHandler {
    private static final String TAG = SinglePageApplicationHandler.class.getName();

    public static String WEB_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/spa";

    public SinglePageApplicationHandler() {
        super();
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (response.isCommitted() || baseRequest.isHandled())
            return;

        baseRequest.setHandled(true);

        String method = request.getMethod();

//        if (!method.equals(HttpMethods.GET) || !request.getRequestURI().equals("/")) {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//
//        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        String requestURI = request.getRequestURI();
        requestURI = StringUtil.replace(requestURI, "<", "&lt;");
        requestURI = StringUtil.replace(requestURI, ">", "&gt;");
        //String contentType = getContentType(requestURI);

        OutputStream out = response.getOutputStream();
        //ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);

        byte[] barr = null;
        String contentType = getContentType(requestURI);
        if (!requestURI.equals("/") && !MimeTypes.TEXT_HTML_UTF_8.equals(contentType)) {
            barr = readFile(WEB_DIR_PATH + requestURI);
        }

        if (barr == null) {
            barr = readFile(WEB_DIR_PATH + "/index.html");
        }

        out.write(barr);
        response.setContentLength(barr.length);
        response.setContentType(contentType);

//        Server server = getServer();
//        Handler[] handlers = server == null ? null : server.getChildHandlersByClass(ContextHandler.class);

//        int i = 0;
//        for (; handlers != null && i < handlers.length; i++) {
//            if (i == 0)
//                writer.write("<p>Available contexts are: </p><ul>");
//
//            ContextHandler context = (ContextHandler) handlers[i];
//            if (context.isRunning()) {
//                writer.write("<li><a href=\"");
//                if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0)
//                    writer.write("http://" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
//                writer.write(context.getContextPath());
//                if (context.getContextPath().length() > 1 && context.getContextPath().endsWith("/"))
//                    writer.write("/");
//                writer.write("\">");
//                writer.write(context.getContextPath());
//                if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0)
//                    writer.write("&nbsp;@&nbsp;" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
//                writer.write("&nbsp;--->&nbsp;");
//                writer.write(context.toString());
//                writer.write("</a></li>\n");
//            } else {
//                writer.write("<li>");
//                writer.write(context.getContextPath());
//                if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0)
//                    writer.write("&nbsp;@&nbsp;" + context.getVirtualHosts()[0] + ":" + request.getLocalPort());
//                writer.write("&nbsp;--->&nbsp;");
//                writer.write(context.toString());
//                if (context.isFailed())
//                    writer.write(" [failed]");
//                if (context.isStopped())
//                    writer.write(" [stopped]");
//                writer.write("</li>\n");
//            }
//
//            if (i == handlers.length - 1)
//                writer.write("</ul>\n");
//        }


        //writer.flush();
        //response.setContentLength(writer.size());

        //writer.writeTo(out);
        out.close();
    }

    public String readFileAsString(String fileName) {
        try {
            File iFile = new File(fileName);
            if (iFile.exists()) {
                FileInputStream fis = new FileInputStream(fileName);
                byte[] buffer = new byte[10];
                StringBuilder sb = new StringBuilder();
                while (fis.read(buffer) != -1) {
                    sb.append(new String(buffer));
                    buffer = new byte[10];
                }
                fis.close();
                return sb.toString();
            } else {
                return null;
            }
        } catch (Exception er) {
            return null;
        }
    }

    public byte[] readFile(String fileName) {
        try {
            File iFile = new File(fileName);
            if (!iFile.exists()) {
                return null;
            }

            FileInputStream fis = new FileInputStream(fileName);
            byte[] buffer = new byte[fis.available()];
            while (fis.read(buffer) != -1);
            fis.close();
            return buffer;
        } catch (Exception er) {
            return null;
        }
    }

    public static String getContentType(String path) {
        int index = path.lastIndexOf(".");
        if (index > -1) {
            String extension = path.substring(index + 1);
            switch (extension) {
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "gif":
                    return "image/gif";
                case "svg":
                    return "image/svg+xml";
                case "png":
                    return "image/png";
                case "woff":
                    return "font/woff";
                case "woff2":
                    return "font/woff2";
                case "ttf":
                    return "font/ttf";
                case "js":
                    return "application/javascript; charset=utf-8";
                case "json":
                    return MimeTypes.TEXT_JSON_UTF_8;
                case "html":
                    return MimeTypes.TEXT_HTML_UTF_8;
                default:
                    return "text/plain";
            }
        }
        // it is a route then
        return MimeTypes.TEXT_HTML_UTF_8;
    }
}