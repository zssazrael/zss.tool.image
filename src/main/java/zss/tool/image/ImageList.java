package zss.tool.image;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import zss.tool.Version;

@Version("2017.05.23")
public class ImageList extends LinkedList<BufferedImage> {
    private static final long serialVersionUID = 20160409015120537L;

    public ImageList(final BufferedImage image) {
        add(image);
    }

    public ImageList() {
    }
}
