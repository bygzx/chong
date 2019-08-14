package com.weixin;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;

/**
 * @author bygzx
 * @date 2019/4/29 14:46
 **/
public class XstreamUtil {
    //xstream扩展
    private static XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点都增加CDATA标记
                boolean cdata = true;

                @Override
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });



    public String object2Xml(Object obj,Object child,String alias,String aliasForChild){
        xstream.alias(alias, obj.getClass());
        xstream.alias(aliasForChild, child.getClass());
        String xml=xstream.toXML(obj);
        return xml;
    }

    public String object2Xml(Object obj,String alias){
        xstream.alias(alias, obj.getClass());

        String xml=xstream.toXML(obj);
        return xml;
    }

    /**
     * xml转成bean泛型方法
     * @param resultXml
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T xmlToBean(String resultXml, Class clazz) {
        // XStream对象设置默认安全防护，同时设置允许的类
        XStream stream = new XStream(new DomDriver());
//        XStream.setupDefaultSecurity(stream);
//        stream.allowTypes(new Class[]{clazz});
        stream.processAnnotations(new Class[]{clazz});
//        stream.setMode(XStream.NO_REFERENCES);
        stream.alias("xml", clazz);
        return (T) stream.fromXML(resultXml);
    }
}
