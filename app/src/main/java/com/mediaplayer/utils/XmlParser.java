package com.mediaplayer.utils;

import android.os.Environment;

import com.mediaplayer.model.Image;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imishev on 15.10.2014 г..
 */
public class XmlParser {

    public static Image xmlParserImage(int imagesId) throws XmlPullParserException, IOException {
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser parser = xmlFactoryObject.newPullParser();

        parser.setInput(new FileInputStream(Constants.STORAGE_PATH + "/program/images.xml"), null);

        Image image = null;
        String curText = null;
        List<String> paths = new ArrayList<String>();
        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(Constants.IMAGE)) {
                        if (image != null && image.getId() == imagesId) {
                            image.setPaths(paths);
                            return image;
                        }
                        paths.clear();
                        image = new Image();
                    }
                    break;
                case XmlPullParser.TEXT:
                    curText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase(Constants.ID)) {
                        image.setId(Integer.valueOf(curText));
                    } else if (tagName.equalsIgnoreCase(Constants.PATH)) {
                        paths.add(curText);
                    }
                    break;
            }

            event = parser.next();
        }

        return null;
    }
}