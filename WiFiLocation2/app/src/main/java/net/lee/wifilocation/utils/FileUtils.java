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

    //�ó�Ա���������洢SD��Ŀ¼
    private String SDCardRoot;

    public FileUtils()
    {
        //�õ���ǰ�ⲿ�洢�豸���ֻ�SD����Ŀ¼�ľ���·����
        // ֮��Ҫ���ļ��洢��SD����ĳ���ط���ֻ���ں������Ŀ¼
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }

    /**
     * ��SD���ϴ����ļ�
     * @param fileName
     * @param dir
     * @return
     * @throws IOException
     */
    public File createFileInSDCard(String fileName,String dir)throws IOException
    {
        //��SD��������һ���ļ���������SD����·��+SD�µ�Ŀ¼dir+�ָ���+�ļ�������
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        System.out.println("createFileInSDCard:file----->" + file);

        //ע������ֻ�Ǵ�����һ�����󣬾�����ļ���û�б�����
        file.createNewFile();
        return file;
    }

    /**
     * ��SD���ϴ�����Ŀ¼�����ڴ���ض����ļ�
     * @param dir
     * @return
     */
    public File createSDDir(String dir)
    {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        //������һ��������mkdirs�����������ش�������
        System.out.println("createSDDir--->" + dirFile.mkdirs());

        //��ʵ���ص�File����Ҳû�У�����ֻ��Ϊ�˴���һ��Ŀ¼
        return dirFile;
    }

    /**
     * �ж�SD���ϵ��ļ��Ƿ���ڣ�ע����Ҫ������·��
     * @param fileName
     * @param path
     * @return
     */
    public boolean isFileExist(String fileName,String path)
    {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        //��exists()���ж�
        return file.exists();
    }

    /**
     * ��InputStream���������д�뵽SD����
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
            //�ȴ���Ŀ¼(������ȴ���Ŀ¼ֱ�Ӵ�������·�����ļ�����ô����)
            createSDDir(path);
            //����path·���´�����Ӧ���ļ�
            //ע���ʱ���ļ��ǿյģ���Ҫ������д��Ż�������
            file = createFileInSDCard(fileName, path);
            //���ļ����Ϲܵ�FileOutputStream׼��������д����
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int temp;
            //�����ݴ�inputStream�ж�ȡ���������浽buffer��
            while((temp = inputStream.read(buffer)) != -1)
            {
                //��buffer���������д�뵽֮ǰ������file��
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
     * ��ָ��Ŀ¼path�е������ļ���ȡ����
     * @param path
     * @return
     */
    public List<File> getFiles(String path)
    {
        List<File> list = new ArrayList<File>();
        //����Ϊʲô��Ҫ�Ӹ��ָ�����SDCardRoot�����Ѿ����ˣ�ȥ���в��У�
        //����
        File file = new File(SDCardRoot + File.separator + path);
        //�Ӹ�·��path����ȡ�����е��ļ�����list����
        File[] files = file.listFiles();
        list = Arrays.asList(files);

        return list;
    }
    /**
     * ��ָ��Ŀ¼path�е�ָ���ļ���ȡ����
     * @param path ·��
     * @param fileName �ļ�����
     * @return
     */
    public File getFile(String path,String fileName)
    {
        File file = new File(SDCardRoot +  path);
        //�Ӹ�·��path����ȡ�����е��ļ�������������
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
     * ����url�����ļ���sd������
     * @param urlStr �ļ���url·��
     * @param path  �ļ�sd���ϵ�Ŀ¼
     * @param fileName �ļ�������
     * @return -1�����������ļ�����
     *          0�����������ļ��ɹ�
     *          1�������ļ��Ѿ�����
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
            //��url�õ�inputStream
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
     * ����URL�õ�������
     * @param urlStr �ļ���url
     * @return ������
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
