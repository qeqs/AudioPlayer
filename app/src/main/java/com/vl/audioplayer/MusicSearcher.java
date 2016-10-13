package com.vl.audioplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by kvakin on 10.10.2016.
 */

public class MusicSearcher {

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
        return "/mnt/sdcard";//System.getenv("EXTERNAL_STORAGE");
    }



}

