package wang.wangby.utils;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.annotation.Remark;
import wang.wangby.annotation.api.Return;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {
    public static final String ENCODE = "UTF-8";
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static void copy(String from, String to) {
        try (RandomAccessFile fromFile = new RandomAccessFile(from, "rw");
             RandomAccessFile toFile = new RandomAccessFile(to, "rw");) {
            FileChannel fromChannel = fromFile.getChannel();
            FileChannel toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getText(Class clazz, String path) {
        try {
            InputStream in = clazz.getResourceAsStream(path);
            byte[] bs = read(in, in.available());
            return new String(bs, ENCODE);
        } catch (Exception ex) {
            throw new RuntimeException("读取文件出错:"+path,ex);
        }
    }

    public static String getText(String path) {
        try {
            Path p = Paths.get(path);
            byte[] bs = Files.readAllBytes(p);
            return new String(bs, ENCODE);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SneakyThrows
    public static byte[] getBytes(Class clazz, String path) {
        InputStream in = clazz.getResourceAsStream(path);
        return read(in, in.available());
    }


    public static byte[] read(InputStream ins, int length) {
        try {
            byte[] bytes = new byte[length];
            ins.read(bytes);
            return bytes;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void createFile(String fileName, String content) throws FileNotFoundException, IOException {
        @Cleanup
        OutputStream out = new FileOutputStream(fileName);
        out = new BufferedOutputStream(out);
        out.write(content.getBytes("UTF-8"));
    }

    //生成文本文件
    public static void createFile(String fileName, Object content) throws FileNotFoundException, IOException {
        @Cleanup
        OutputStream out = new FileOutputStream(fileName);
        out = new BufferedOutputStream(out);
        out.write(toByte(content));
    }

    public static byte[] toByte(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        @Cleanup
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        byte[] bs = bos.toByteArray();
        return bs;
    }

    public static Object toObject(byte[] bs) throws Exception {
        @Cleanup
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bs));
        ois = new ObjectInputStream(new ByteArrayInputStream(bs));
        return ois.readObject();
    }

    @SneakyThrows
    public static boolean makeDirs(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        path = path.replace("\\", "/");
        int begin = path.lastIndexOf('/');
        String dir = path.substring(0, begin);
        Files.createDirectories(Paths.get(dir));
        log.debug("创建目录:" + dir);
        return true;
    }

    /***
     * 通过完整路径获得文件名
     * @param  fullName 文件路径
     * @param   ext 是否要包含扩展名
     */
    public static String getFileName(String fullName, boolean ext) {
        int i = fullName.lastIndexOf('/');
        if (i == -1) {
            i = fullName.lastIndexOf('\\');
        }
        if (i == -1) {
            i = 0;
        }

        //如果需要扩展名直接截取分隔符后面所有数据
        //如果.在分隔符前面,就说明本身没有扩展名
        int point = fullName.lastIndexOf('.');
        if (point < i || ext) {
            return fullName.substring(i + 1);
        } else {
            return fullName.substring(i + 1, point);
        }
    }

    /**
     * 将文件名按linux方式格式化,只能格式化全路径名称,如果是目录去掉末尾的/
     * 如果是windows的文件名会直接去掉盘符,所以如果程序和文件不在同一个盘,格式化后将无法读取
     */
    public static String format(String fullname) {
        fullname=fullname.replace('\\','/');
        if(fullname.endsWith("/")){
            fullname=fullname.substring(0,fullname.length()-1);
        }
        int point=fullname.lastIndexOf(":");
        if(point!=-1){
            fullname=fullname.substring(point+1);
        }
        if(fullname.startsWith("/")){
            return fullname;
        }
        return "/"+fullname;
    }

    //遍历某个目录下的所有目录和文件
    public static void iterator(File root, FileConsumer fileConsumer){
        File[] list=root.listFiles();
        if(list==null){
            throw new RuntimeException("目录不存在:"+root.getAbsolutePath());
        }
        for(File f:list){
            if(!fileConsumer.apply(root,f)){
                return;
            }
            if(f.isDirectory()){
                iterator(f,fileConsumer);
            }
        }
    }

    //遍历某个目录下的所有文件
    public static void iteratorFile(File root, FileConsumer fileConsumer){
        File[] list=root.listFiles();
        if(list==null){
            throw new RuntimeException("找不到目录:"+root);
        }
        for(File f:list){
            if(f.isDirectory()){
                iterator(f,fileConsumer);
            }else{
                if(!fileConsumer.apply(root,f)){
                    return;
                }
            }
        }
    }

    public interface FileConsumer{
        @Remark("读取扫描到的文件")
        @Return(("是否继续遍历当前目录"))
        boolean apply(File dir, File file);
    }
}
