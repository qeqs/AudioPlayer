package com.vl.audioplayer.service;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MusicSearcher  {

        //классы для работы с регулярными выражениями
        private Pattern p = null;
        private Matcher m = null;

        //общий размер найденных файлов
        private long totalLength = 0;
        //общее количество найденных файлов
        private long filesNumber = 0;



        public MusicSearcher() {
        }

        public long getDirectorySize() {
            return totalLength;
        }

        public long getFilesNumber() {
            return filesNumber;
        }

        /*
        Проверяет, соответствует ли имя файла заданному
        регулярному выражению. Возвращает true, если найденный
        объект соответствует регулярному выражению, false - в
        противном случае.
        */
        private boolean accept(String name) {
            //если регулярное выражение не задано...
            if(p == null) {
                //...значит объект подходит
                return true;
            }
            //создаем Matcher
            m = p.matcher(name);
            //выполняем проверку
            if(m.matches()) {
                return true;
            }
            else {
                return false;
            }
        }
        String mask1;
        public List find(String startPath, String mask)
                throws Exception {
            //проверка параметров
            if(startPath == null || mask == null) {
                throw new Exception("Ошибка: не заданы параметры поиска");
            }
            mask1 = mask;
            File topDirectory = new File(startPath);
            if(!topDirectory.exists()) {
                throw new Exception("Ошибка: указанный путь не существует");
            }
            //если задано регулярное выражение, создаем Pattern
            if(!mask.equals("")) {
                p = Pattern.compile(mask,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }
            //обнуляем все счетчики
            filesNumber = 0;
            totalLength = 0;
            //создаем список результатов
            ArrayList res = new ArrayList();

            //выполняем поиск
            search(topDirectory, res);

            //присваиваем null шаблону, т.к. при следующем вызове find...
            //регулярное выражение может быть не задано
            p = null;
            //возвращаем результат
            return res;
        }

        /*
        Этот метод выполняет поиск объектов заданного типа.
        Если, в процессе поиска, встречает вложенную директорию, то рекурсивно вызывает сам себя.
        Результаты поиска сохраняются в параметре res.
        Текущая директория - topDirectory.
        */
        private void search(File topDirectory, List res) {
            //получаем список всех объектов в текущей директории
            if(topDirectory==null)return;

            File[] list = topDirectory.listFiles();
            if(list==null)return;
            //просматриваем все объекты по-очереди
            for(int i = 0; i < list.length; i++) {
                //если это директория (папка)...
                if(list[i].isDirectory()) {
                    //выполняем поиск во вложенных директориях
                    search(list[i], res);
                }
                //если это файл
                else {
                    //...выполняем проверку на соответствие регулярному выражению...
                    if(list[i].getAbsolutePath().endsWith(mask1))//(accept(list[i].getAbsolutePath()))
                    {
                        //...добавляем текущий объект в список результатов,
                        //и обновляем значения счетчиков
                        filesNumber++;
                        totalLength += list[i].length();
                        res.add(list[i]);
                    }
                }
            }
        }
    public static String getExternalSdCardPath() {
        return System.getenv("EXTERNAL_STORAGE");
    }


    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

    /**
     * Raturns all available SD-Cards in the system (include emulated)
     *
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standart way to get it.
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public static String[] getStorageDirectories()
    {
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if(TextUtils.isEmpty(rawEmulatedStorageTarget))
        {
            // Device has physical external storage; use plain paths.
            if(TextUtils.isEmpty(rawExternalStorage))
            {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            }
            else
            {
                rv.add(rawExternalStorage);
            }
        }
        else
        {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                rawUserId = "";
            }
            else
            {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try
                {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                }
                catch(NumberFormatException ignored)
                {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if(TextUtils.isEmpty(rawUserId))
            {
                rv.add(rawEmulatedStorageTarget);
            }
            else
            {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
        {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        rv.add("/storage");
        return rv.toArray(new String[rv.size()]);
    }


}

