package zss.tool.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zss.tool.IOTool;
import zss.tool.LoggedException;
import zss.tool.Version;

@Version("2016-04-09")
public class PNGTool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PNGTool.class);

    public static String MIME_TYPE = "image/png";

    public static void write(final File file, final BufferedImage image)
    {
        final OutputStream stream = IOTool.newFileOutputStream(file);
        try
        {
            write(stream, image);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void write(final ImageOutputStream stream, BufferedImage image)
    {
        final ImageWriter writer = newWriter();
        try
        {
            writer.setOutput(stream);
            writer.write(image);
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
            throw new LoggedException();
        }
        finally
        {
            writer.dispose();
        }
    }

    public static void write(final OutputStream stream, BufferedImage image)
    {
        final ImageOutputStream output = new MemoryCacheImageOutputStream(stream);
        try
        {
            write(output, image);
        }
        finally
        {
            IOUtils.closeQuietly(output);
        }
    }

    public static ImageWriter newWriter()
    {
        return ImageTool.getImageWriterByMIMEType(MIME_TYPE);
    }

    public static ImageReader newReader()
    {
        return ImageTool.getImageReaderByMIMEType(MIME_TYPE);
    }
}
