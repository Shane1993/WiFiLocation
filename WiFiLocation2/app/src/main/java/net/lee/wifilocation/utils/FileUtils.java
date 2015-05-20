package net.lee.wifilocation.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LEE on 2015/5/20.
 */
public class FileUtils {

    //该成员变量用来存储SD卡目录
    private String SDCardRoot;

    public FileUtils()
    {
        //得到当前外部存储设备即手机SD卡的目录的绝对路径，
        // 之后要将文件存储到SD卡的某个地方，只需在后面加上目录
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }

    /**
     * 在SD卡上创建文件
     * @param fileName
     * @param dir
     * @return
     * @throws IOException
     */
    public File createFileInSDCard(String fileName,String dir)throws IOException
    {
        //在SD卡上生成一个文件，参数是SD卡的路径+SD下的目录dir+分隔符+文件的名字
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        System.out.println("createFileInSDCard:file----->" + file);

        //注意上面只是创建了一个对象，具体的文件还没有被创建
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建子目录，用于存放特定的文件
     * @param dir
     * @return
     */
    public File createSDDir(String dir)
    {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        //和上面一样，调用mkdirs方法才真正地创建出来
        System.out.println("createSDDir--->" + dirFile.mkdirs());

        //其实返回的File对象也没有，这里只是为了创建一个目录
        return dirFile;
    }

    /**
     * 判断SD卡上的文件是否存在，注意需要到绝对路径
     * @param fileName
     * @param path
     * @return
     */
    public boolean isFileExist(String fileName,String path)
    {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        //用exists()来判断
        return file.exists();
    }

    /**
     * 将InputStream里面的数据写入到SD卡中
     * @param path
     * @param fileName
     * @param inputStream
     * @return
     */
    public File writeToSDFromInput(String path,String fileName, InputStream inputStream)
    {
        File file = null;
        OutputStream outputStream = null;
        try
        {
            //先创建目录(如果不先创建目录直接创建绝对路径的文件会怎么样？)
            createSDDir(path);
            //再在path路径下创建相应的文件
            //注意此时该文件是空的，需要将数据写入才会有数据
            file = createFileInSDCard(fileName, path);
            //将文件加上管道FileOutputStream准备往里面写数据
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int temp;
            //把数据从inputStream中读取出来，缓存到buffer中
            while((temp = inputStream.read(buffer)) != -1)
            {
                //将buffer缓存的数据写入到之前创建的file中
                outputStream.write(buffer, 0, temp);
            }
            outputStream.flush();

        }catch(IOException e)
        {
            e.printStackTrace();
        }finally {
            try
            {
                inputStream.close();
                if (outputStream != null) {
                    outputStream.close();
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 将指定目录path中的所有文件读取出来
     * @param path
     * @return
     */
    public List<File> getFiles(String path)
    {
        List<File> list = new ArrayList<File>();
        //这里为什么还要加个分隔符？SDCardRoot明明已经有了，去掉行不行？
        //答：行
        File file = new File(SDCardRoot + File.separator + path);
        //从该路径path里面取出所有的文件放入list里面
        File[] files = file.listFiles();
        list = Arrays.asList(files);

        return list;
    }
    /**
     * 将指定目录path中的指定文件读取出来
     * @param path 路径
     * @param fileName 文件名字
     * @return
     */
    public File getFile(String path,String fileName)
    {
        File file = new File(SDCardRoot +  path);
        //从该路径path里面取出所有的文件放入数组里面
        File[] files = file.listFiles();
        for(int i=0;i<files.length;i++){
            if(files[i].getName().equals(fileName))
            {
                return files[i];
            }
        }

        return null;
    }

    /**
     * 根据url下载文件到sd卡当中
     * @param urlStr 文件的url路径
     * @param path  文件sd卡上的目录
     * @param fileName 文件的名字
     * @return -1：代表下载文件出错
     *          0：代表下载文件成功
     *          1：代表文件已经存在
     */
    public int downFile(String urlStr,String path,String fileName)
    {
        FileUtils fileUtils = new FileUtils();
        if (fileUtils.isFileExist(fileName,path))
        {
            return 1;
        }
        else
        {
            InputStream inputStream;
            //由url得到inputStream
            inputStream = getInputStreamFromUrl(urlStr);
            File resultFile = fileUtils.writeToSDFromInput(path,fileName,inputStream);

            if(resultFile == null)
            {
                return -1;
            }
        }

        return 0;

    }

    /**
     * 根据URL得到输入流
     * @param urlStr 文件的url
     * @return 输入流
     */
    public InputStream getInputStreamFromUrl(String urlStr) {

        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            inputStream = urlConn.getInputStream();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return inputStream;
    }

}
