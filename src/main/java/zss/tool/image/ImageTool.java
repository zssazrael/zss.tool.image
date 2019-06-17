package zss.tool.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zss.tool.HashTool;
import zss.tool.HexTool;
import zss.tool.IOTool;
import zss.tool.LoggedException;
import zss.tool.ResourceTool;
import zss.tool.Version;

@Version("2019.06.18")
public class ImageTool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTool.class);

    public static ImageList read(final InputStream stream)
    {
        final ImageInputStream input = new MemoryCacheImageInputStream(stream);
        try
        {
            return read(input);
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }
    }

    public static ImageList read(final ImageInputStream stream)
    {
        final Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);
        if (iterator.hasNext())
        {
            final ImageReader reader = iterator.next();
            try
            {
                reader.setInput(stream);
                return read(reader);
            }
            finally
            {
                reader.dispose();
            }
        }
        LOGGER.error("Unknown image format.");
        throw new LoggedException();
    }

    public static ImageList read(final ImageReader reader)
    {
        int index = 0;
        final ImageList list = new ImageList();
        while (true)
        {
            try
            {
                list.add(reader.read(index));
            }
            catch (IndexOutOfBoundsException e)
            {
                break;
            }
            catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
                throw new LoggedException();
            }

            index++;
        }
        return list;
    }

    public static ImageList read(final File file)
    {
        final InputStream stream = IOTool.newFileInputStream(file);
        try
        {
            return read(stream);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

    public static ImageList read(final String path)
    {
        final InputStream stream = ResourceTool.openStream(path);
        try
        {
            return read(stream);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

    public static byte[] md5(final ImageList list)
    {
        final MessageDigest digest = HashTool.newMD5MessageDigest();
        final byte[] data = new byte[4];
        final IntBuffer buffer = ByteBuffer.wrap(data).asIntBuffer();
        for (BufferedImage image : list)
        {
            final int width = image.getWidth();
            final int height = image.getHeight();
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    buffer.clear();
                    buffer.put(image.getRGB(x, y));
                    digest.update(data);
                }
            }
        }
        return digest.digest();
    }

    public static ImageWriter getImageWriterByMIMEType(final String mimeType)
    {
        final Iterator<ImageWriter> iterator = ImageIO.getImageWritersByMIMEType(mimeType);
        if (iterator.hasNext())
        {
            return iterator.next();
        }
        LOGGER.error("Unknown image format.");
        throw new LoggedException();
    }

    public static ImageReader getImageReaderByMIMEType(final String mimeType)
    {
        final Iterator<ImageReader> iterator = ImageIO.getImageReadersByMIMEType(mimeType);
        if (iterator.hasNext())
        {
            return iterator.next();
        }
        LOGGER.error("Unknown image format.");
        throw new LoggedException();
    }

    /**
     * 计算图片像素的 MD5
     *
     * @param image 图片
     * @return MD5
     */
    public static String md5(final BufferedImage image) {
        return HexTool.transform(md5(new ImageList(image)));
    }

    public static String fileName(final BufferedImage image) {
        final StringBuilder name = new StringBuilder();
        name.append(image.getWidth());
        name.append('x');
        name.append(image.getHeight());
        name.append('-');
        name.append(md5(image));
        return name.toString();
    }
    
    public static BufferedImage newBufferedImage(final Image image) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = bufferedImage.createGraphics();
        try {
            graphics.drawImage(image, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return bufferedImage;
    }
}
